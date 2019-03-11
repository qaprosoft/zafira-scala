package com.qaprosoft.reporter

import java.io.StringWriter
import java.util
import java.util.{Date, UUID}

import org.scalatest.events._
import org.scalatest.Reporter
import com.qaprosoft.zafira.client.ZafiraClient
import com.qaprosoft.zafira.config.CIConfig._
import com.qaprosoft.zafira.config._
import com.qaprosoft.zafira.models.db.TestRun.Initiator
import com.qaprosoft.zafira.models.dto.config.ConfigurationType
import com.qaprosoft.zafira.models.dto.user.UserType
import com.qaprosoft.zafira.models.dto._
import javax.xml.bind.{JAXBContext, JAXBException}
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import com.qaprosoft.zafira.models.db.Status
import org.testng.SkipException

class ZafiraReporter extends Reporter with Util {

  private val LOGGER = LoggerFactory.getLogger(classOf[ZafiraReporter])

  var parentJob: JobType = null
  var user: UserType = null
  var suite: TestSuiteType = null
  var run:TestRunType = null
  var test:TestType = null
  var testRunResults: util.List[TestType] = new  util.ArrayList[TestType]
  var registeredTests: util.Map[String, TestType] = new util.HashMap[String, TestType]

  var classesToRerun: util.Set[String] = new util.HashSet[String]
  val marshaller = JAXBContext.newInstance(classOf[ConfigurationType]).createMarshaller
  val configurator = Class.forName(ZAFIRA_CONFIGURATOR).newInstance.asInstanceOf[IConfigurator]

  private val threadCiTestId = new ThreadLocal[String]
  private val threadTest = new ThreadLocal[TestType]

  val zafiraClient:ZafiraClient = initializeZafira

  def apply(event: Event) {
    event match {

      case event: TestStarting => onTestStart(event)
      case event: TestSucceeded => onTestSuccess(event)
      case event: TestIgnored => println(event.testName + "\n...test ignored")
      case event: TestPending => println(event.testName + "\n...test pending")
      case event: TestFailed => onTestFailure(event)

      case event: SuiteStarting =>  println(event.suiteName + "\n...suite starting")
      case event: SuiteCompleted => println(event.suiteName + "\n...suite completed")
      case event: SuiteAborted => println(event.suiteName + "\n...suite aborted")

      case event: InfoProvided => println(event.nameInfo + "\n...info provided")

      case event: RunStarting =>  onStart(event)
      case event: RunStopped => println(event.threadName + "\n...run stopped")
      case event: RunAborted => println(event.threadName + "\n...run aborted")
      case event: RunCompleted => onFinish(event)

      case _ =>
    }
  }


  /**
    * Reads zafira.properties and creates zafira client.
    *
    * @return if initialization success
    */
  private def initializeZafira():ZafiraClient = {
    val zc = new ZafiraClient(ZAFIRA_URL)
    try {
      if (ZAFIRA_ENABLED) {
        ZAFIRA_ENABLED = zc.isAvailable
        if (ZAFIRA_ENABLED) {
          val auth = zc.refreshToken(ZAFIRA_ACCESS_TOKEN)
          if (auth.getStatus.equals(200)) zc.setAuthToken(auth.getObject.getType + " " + auth.getObject.getAccessToken)
          else ZAFIRA_ENABLED = false
        }
        LOGGER.info("Zafira is " + (if (ZAFIRA_ENABLED) "available"
        else "unavailable"))

      }
    } catch {
      case e: NoSuchElementException =>
        LOGGER.error("Unable to find config property: ", e)
    }
    zc
  }

  def onStart(event: RunStarting): Unit = {
  // Exit on initialization failure
  if (!ZAFIRA_ENABLED) return
  try {

    zafiraClient.initProject(ZAFIRA_PROJECT)
    val user = zafiraClient.getUserProfile.getObject

    val suiteOwner = zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)

     suite = zafiraClient.registerTestSuite(event.threadName,event.threadName, suiteOwner.getId)

    val job: JobType = zafiraClient.registerJob(ciConfig.getCiUrl, suiteOwner.getId)

    // Register upstream job if required
    var anonymous: UserType = null
    if (BuildCasue.UPSTREAMTRIGGER == ciConfig.getCiBuildCause) {
      anonymous = zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)
      parentJob = zafiraClient.registerJob(ciConfig.getCiParentUrl, anonymous.getId)
    }
    // Searching for existing test run with same CI run id in case of rerun
    if (!StringUtils.isEmpty(ciConfig.getCiRunId)) {
      val response = zafiraClient.getTestRunByCiRunId(ciConfig.getCiRunId)
      run = response.getObject
    }
    if (run != null) {
      // Already discovered run with the same CI_RUN_ID, it is re-run functionality!
      // Reset build number for re-run to map to the latest rerun build
      run.setBuildNumber(ciConfig.getCiBuild)

      // Reset testRun config for rerun in case of queued tests
      run.setConfigXML(convertToXML(configurator.getConfiguration))

      // Re-register test run to reset status onto in progress
      val response = zafiraClient.startTestRun(run)
      run = response.getObject
      testRunResults = zafiraClient.getTestRunResults(run.getId).getObject.asInstanceOf[util.List[TestType]]
      testRunResults.forEach({test => registeredTests.put(test.getName, test)
        if (test.isNeedRerun) classesToRerun.add(test.getTestClass)
        })
      if (ZAFIRA_RERUN_FAILURES) LOGGER.info("It is has not implemented for scala yet") //ExcludeTestsForRerun.excludeTestsForRerun(suiteContext, testRunResults, configurator);
    }
    else {
      if (ZAFIRA_RERUN_FAILURES) {
        LOGGER.error("Unable to find data in Zafira Reporting Service with CI_RUN_ID: '" + ciConfig.getCiRunId + "'.\n" + "Rerun failures featrure will be disabled!")
        ZAFIRA_RERUN_FAILURES = false
      }
      // Register new test run
      ciConfig.getCiBuildCause match {
        case BuildCasue.UPSTREAMTRIGGER =>
          run = zafiraClient.registerTestRunUPSTREAM_JOB(suite.getId, convertToXML(configurator.getConfiguration), job.getId, parentJob.getId, ciConfig, Initiator.UPSTREAM_JOB, JIRA_SUITE_ID)
        case BuildCasue.TIMERTRIGGER =>
        case BuildCasue.SCMTRIGGER =>
          run = zafiraClient.registerTestRunBySCHEDULER(suite.getId, convertToXML(configurator.getConfiguration), job.getId, ciConfig, Initiator.SCHEDULER, JIRA_SUITE_ID)
        case BuildCasue.MANUALTRIGGER =>
          run = zafiraClient.registerTestRunByHUMAN(suite.getId, user.getId, convertToXML(configurator.getConfiguration), job.getId, ciConfig, Initiator.HUMAN, JIRA_SUITE_ID)
        case _ =>
          throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL + " due to the misses build cause: '" + ciConfig.getCiBuildCause + "'")
      }
    }

    if (run == null) {
      throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL)
    }
    else {
      System.setProperty(ZAFIRA_RUN_ID_PARAM, run.getId.toString)
      println(run.getId.toString)
    }
    Runtime.getRuntime.addShutdownHook(new TestRunShutdownHook(zafiraClient, run))
  } catch {
    case e: Throwable =>
      ZAFIRA_ENABLED = false
      LOGGER.error("Undefined error during test run registration!", e.printStackTrace())
  }

  }




  /**
    * Marshals configuration bean to XML.
    *
    * @param config bean
    * @return XML representation of configuration bean
    */
  private def convertToXML(config: ConfigurationType):String = {
    val stringWriter = new StringWriter
    try {
      if (config != null) marshaller.marshal(config, stringWriter)
      else marshaller.marshal(new ConfigurationType, stringWriter)
    }
    catch {
      case e: Throwable =>
        LOGGER.error("Unable to convert config to XML!", e)
    }
    stringWriter.toString
  }

  class TestRunShutdownHook(var zc: ZafiraClient, var testRun: TestRunType) extends Thread {
    override def run(): Unit = {
      if (testRun != null) {
        val aborted = zc.abortTestRun(testRun.getId)
        LOGGER.info("TestRunShutdownHook was executed with result: " + aborted)
      }
    }
  }


  def onFinish(event: RunCompleted): Unit = {
    if (!ZAFIRA_ENABLED) return
    try { // Reset configuration to store for example updated at run-time app_version etc
      run.setConfigXML(convertToXML(configurator.getConfiguration))
      zafiraClient.registerTestRunResults(run)
    } catch {
      case e: Throwable =>
        LOGGER.error("Unable to finish test run correctly", e.printStackTrace())
    }
  }


  def onTestStart(event: TestStarting): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {

      var startedTest:TestType = null
      val testName = event.testName

      // If method owner is not specified then try to use suite owner. If both are not declared then ANONYMOUS will be used.
      val primaryOwnerName:String = zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER).getUsername
      val primaryOwner: UserType= zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)

      val secondaryOwnerName:String  =  zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER).getUsername
      val secondaryOwner: UserType =  zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)
      val testClass = event.suiteClassName.get
      val testMethod = event.testName

      val testCase:TestCaseType = zafiraClient.registerTestCase(suite.getId, primaryOwner.getId, secondaryOwner.getId,testClass, testMethod)
      // Search already registered test!
      println("4")
//      if (registeredTests.containsKey(testName)) {
//        println("5")
//        startedTest = registeredTests.get(testName)
//        // Skip already passed tests if rerun failures enabled
//        if (ZAFIRA_RERUN_FAILURES && !startedTest.isNeedRerun) throw new RuntimeException("ALREADY_PASSED: " + testName)
//        startedTest.setFinishTime(null)
//        startedTest.setStartTime(new Date().getTime)
//        startedTest.setCiTestId(getThreadCiTestId)
//        startedTest.setTags(null)
//        startedTest = zafiraClient.registerTestRestart(startedTest)
//      }
      if (startedTest == null) { //new test run registration
        val testArgs = event.testName
        var group = event.suiteClassName.get
        group = group.substring(0, group.lastIndexOf("."))
        val dependsOnMethods = null
        startedTest = zafiraClient.registerTestStart(testName, group, Status.IN_PROGRESS, testArgs, run.getId, testCase.getId, 0, convertToXML(configurator.getConfiguration), dependsOnMethods, getThreadCiTestId, configurator.getTestTags(null))
      }
      zafiraClient.registerWorkItems(startedTest.getId, configurator.getTestWorkItems(null))
      threadTest.set(startedTest)
      registeredTests.put(event.testName, startedTest)
    } catch {
      case e: SkipException =>
      case e: Throwable =>
        LOGGER.error("Undefined error during test case/method start!", e.printStackTrace())
    }
  }



  val getThreadCiTestId: String = {
    if (StringUtils.isEmpty(threadCiTestId.get)) threadCiTestId.set(UUID.randomUUID.toString)
    threadCiTestId.get
  }

  def onTestSuccess(event: TestSucceeded): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {
      val rs = zafiraClient.finishTest(populateTestResult(event, Status.PASSED))
      if ((!rs.getStatus.equals(200)) && rs.getObject == null) throw new RuntimeException("Unable to register test " + rs.getObject.getName + " for zafira service: " + ZAFIRA_URL)
    } catch {
      case e: Throwable =>
        LOGGER.error("Undefined error during test case/method finish!", e.printStackTrace())
    }
  }

  def onTestFailure(event: TestFailed): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {
      val rs = zafiraClient.finishTest(populateTestResult(event, Status.FAILED))
      if ((!rs.getStatus.equals(200))  && rs.getObject == null) throw new RuntimeException("Unable to register test " + rs.getObject.getName + " for zafira service: " + ZAFIRA_URL)
    } catch {
      case e: Throwable =>
        LOGGER.error("Undefined error during test case/method finish!", e.printStackTrace())
    }
  }

  @throws[JAXBException]
  private def populateTestResult(event:Event, status: Status) = {
    var testName:String = null
    var message:String = null
    //var duration:Long = 0L
    var finishTime = 0L

    event match {
      case event: TestFailed => {
        testName = event.testName
        message = event.message
        finishTime = event.timeStamp

      }
      case event: TestSucceeded => {
        testName = event.testName
        finishTime = event.timeStamp
        message = "test succeed"
      }
    }

    val threadId = Thread.currentThread.getId
    val test = threadTest.get
    //testByThread.get(threadId);


    LOGGER.debug("testName registered with current thread is: " + testName)
    if (test == null) throw new RuntimeException("Unable to find TestType result to mark test as finished! name: '" + testName + "'; threadId: " + threadId)
    test.setTestMetrics(configurator.getTestMetrics(null))
    test.setConfigXML(convertToXML(configurator.getConfiguration))
    test.setArtifacts(configurator.getArtifacts(null))
//    var testDetails = "testId: %d; testCaseId: %d; testRunId: %d; name: %s; thread: %s; status: %s, finishTime: %s \n message: %s"
//    var logMessage = String.format(testDetails, test.getId, test.getTestCaseId, test.getTestRunId, test.getName, threadId, status, finishTime, message)
 //   LOGGER.debug("Test details to finish registration:" + logMessage)
    test.setStatus(status)
    test.setMessage(message)
    test.setFinishTime(finishTime)
    threadTest.remove
    threadCiTestId.remove
    test
  }

  private def getFullStackTrace(event: TestFailed) = {
    val sb = new StringBuilder
    if (event.throwable.get == null) {
      sb.append(event.throwable.get.getMessage).append("\n")
      val elems = event.throwable.get.getStackTrace
      for (elem <- elems) {
        sb.append("\n").append(elem.toString)
      }
    }
    if (!StringUtils.isEmpty(sb.toString)) sb.toString
    else null
  }


}

