package com.qaprosoft.reporter

import java.io.StringWriter
import java.util
import java.util.{Date, UUID}

import org.scalatest.events._
import org.scalatest._
import com.qaprosoft.zafira.client.ZafiraClient
import com.qaprosoft.zafira.config.CIConfig._
import com.qaprosoft.zafira.config._
import com.qaprosoft.zafira.models.db.TestRun.Initiator
import com.qaprosoft.zafira.models.dto.config.ConfigurationType
import com.qaprosoft.zafira.models.dto.user.UserType
import com.qaprosoft.zafira.models.dto.TestType
import com.qaprosoft.zafira.models.dto._
import javax.xml.bind.{JAXBContext, JAXBException}
import org.apache.commons.lang3.StringUtils
import com.qaprosoft.zafira.models.db.Status
import org.apache.log4j.Logger

class ZafiraReporter extends Reporter with Constants {

  val LOGGER = Logger.getLogger(this.getClass)

  var parentJob: JobType = _
  var user: UserType = new UserType
  var suite: TestSuiteType = _
  var run:TestRunType = _
  var test:TestType = _
  var registeredTests: util.Map[String, TestType] = new util.HashMap[String, TestType]
  val testNamesRerun = new util.ArrayList[String]()

  val marshaller = JAXBContext.newInstance(classOf[ConfigurationType]).createMarshaller
  val configurator = Class.forName(ZAFIRA_CONFIGURATOR).newInstance.asInstanceOf[IConfigurator]

  private val threadCiTestId = new ThreadLocal[String]
  private val threadTest = new ThreadLocal[TestType]

  val zafiraClient:ZafiraClient = initializeZafira

  def apply(event: Event) {
    event match {

      case event: TestStarting => onTestStart(event)
      case event: TestSucceeded => onTestFinish(event)
      case event: TestIgnored => onTestFinish(event)
      case event: TestPending =>  onTestFinish(event)
      case event: TestFailed => onTestFinish(event)
      case event: TestCanceled => onTestFinish(event)
      case event: SuiteAborted => onTestFinish(event)
      case event: RunStarting =>  onStart(event)
      case event: RunCompleted => onFinish(event)

      case default => LOGGER.info("Event " + event.getClass + " is not supported")
    }
  }

  def onStart(event: RunStarting): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {
      zafiraClient.initProject(ZAFIRA_PROJECT)
      user = zafiraClient.getUserProfile.getObject
      val suiteOwner = zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)
      suite = zafiraClient.registerTestSuite(SUITE_NAME,SUITE_NAME, suiteOwner.getId)
      val job: JobType = zafiraClient.registerJob(ciConfig.getCiUrl, suiteOwner.getId)
      var anonymous: UserType = null
      if (BuildCasue.UPSTREAMTRIGGER == ciConfig.getCiBuildCause) {
        anonymous = zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)
        parentJob = zafiraClient.registerJob(ciConfig.getCiParentUrl, anonymous.getId)
      }

      if (!StringUtils.isEmpty(ciConfig.getCiRunId)) {
        val response = zafiraClient.getTestRunByCiRunId(ciConfig.getCiRunId)
        run = response.getObject
      }
      if (run != null) {
        run.setBuildNumber(ciConfig.getCiBuild)
        run.setConfigXML(convertToXML(configurator.getConfiguration))
        val response = zafiraClient.startTestRun(run)
        run = response.getObject
        val testRunResults:Array[TestType] = zafiraClient.getTestRunResults(run.getId).getObject
        testRunResults.foreach(test => {
          registeredTests.put(test.getName, test)
        })
        if (ZAFIRA_RERUN_FAILURES) {
          for (test <- testRunResults) {
            if (test.isNeedRerun) testNamesRerun.add(test.getName)
          }
          LOGGER.info("Tests need rerun " + testNamesRerun.toString)
          LOGGER.error("Rerun failures functionality is not supported")
          ZAFIRA_RERUN_FAILURES = false
          }
      }
      else {
        if (ZAFIRA_RERUN_FAILURES) {
          LOGGER.error("Unable to find data in Zafira Reporting Service with CI_RUN_ID: '" + ciConfig.getCiRunId + "'.\n" + "Rerun failures featrure will be disabled!")
          ZAFIRA_RERUN_FAILURES = false
        }
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

      if (run == null) throw new RuntimeException("Unable to register test run for zafira service: " + ZAFIRA_URL)
      else {
        System.setProperty(ZAFIRA_RUN_ID_PARAM, run.getId.toString)
      }

      Runtime.getRuntime.addShutdownHook(new TestRunShutdownHook(zafiraClient, run))
    } catch {
      case e: Throwable =>
        ZAFIRA_ENABLED = false
        LOGGER.error("Undefined error during test run registration!", e)
    }

  }

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

  def onFinish(event: RunCompleted): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {
      run.setConfigXML(convertToXML(configurator.getConfiguration))
      zafiraClient.registerTestRunResults(run)
    } catch {
      case e: Throwable =>
        LOGGER.error("Unable to finish test run correctly", e)
    }
  }


  def onTestStart(event: TestStarting): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {
      var startedTest:TestType = null
      val testName = event.testName

      val primaryOwner: UserType= zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)

      val secondaryOwner: UserType =  zafiraClient.getUserOrAnonymousIfNotFound(ZafiraClient.DEFAULT_USER)
      val testClass = event.suiteClassName.get
      val testMethod = event.testName

      val testCase:TestCaseType = zafiraClient.registerTestCase(suite.getId, primaryOwner.getId, secondaryOwner.getId,testClass, testMethod)
            if (registeredTests.containsKey(testName)) {
              startedTest = registeredTests.get(testName)
              if (ZAFIRA_RERUN_FAILURES && !startedTest.isNeedRerun) throw new RuntimeException("ALREADY_PASSED: " + testName)
              startedTest.setFinishTime(event.timeStamp)
              startedTest.setStartTime(new Date().getTime)
              startedTest.setCiTestId(getThreadCiTestId)
              startedTest = zafiraClient.registerTestRestart(startedTest)
            }
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
      case e: Throwable =>
        LOGGER.error("Undefined error during test case/method start!", e)
    }
  }

  val getThreadCiTestId: String = {
    if (StringUtils.isEmpty(threadCiTestId.get)) threadCiTestId.set(UUID.randomUUID.toString)
    threadCiTestId.get
  }

  def onTestFinish(event: Event): Unit = {
    if (!ZAFIRA_ENABLED) return
    try {
      val rs = zafiraClient.finishTest(populateTestResult(event))
      if ((!rs.getStatus.equals(200))  && rs.getObject == null) throw new RuntimeException("Unable to register test " + rs.getObject.getName + " for zafira service: " + ZAFIRA_URL)
    } catch {
      case e: Throwable =>
        LOGGER.error("Undefined error during test case/method finish!", e)
    }
  }

  @throws[JAXBException]
  private def populateTestResult(event:Event):TestType = {
    var testName:String = null
    var message:String = null
    var finishTime = 0L
    var status:Status = Status.UNKNOWN

    event match {
      case event: TestFailed =>
        testName = event.testName
        message = getFullStackTrace(event)
        finishTime = event.timeStamp
        status =  Status.FAILED

      case event: TestSucceeded =>
        testName = event.testName
        finishTime = event.timeStamp
        status =  Status.PASSED

      case event: TestPending =>
        testName = event.testName
        finishTime = event.timeStamp
        status =  Status.QUEUED

      case event: TestIgnored =>
        testName = event.testName
        finishTime = event.timeStamp
        status =  Status.SKIPPED

      case event: TestCanceled =>
        testName = event.testName
        finishTime = event.timeStamp
        message = event.message
        status =  Status.ABORTED

      case default => LOGGER.info("Event " + event.getClass + " is not supported")

    }

    val threadId = Thread.currentThread.getId
    val test = threadTest.get

    LOGGER.debug("testName registered with current thread is: " + testName)
    if (test == null) throw new RuntimeException("Unable to find TestType result to mark test as finished! name: '" + testName + "'; threadId: " + threadId)
    test.setStatus(status)
    test.setMessage(message)
    test.setFinishTime(finishTime)
    threadTest.remove()
    threadCiTestId.remove()
    test
  }

  private def getFullStackTrace(event: TestFailed):String = {
    val sb = new StringBuilder
    if (event.throwable.get != null) {
      sb.append(event.message).append("\n")
      val elems = event.throwable.get.getStackTrace
      for (elem <- elems) {
        sb.append("\n").append(elem.toString)
      }
    }
    if (!StringUtils.isEmpty(sb.toString)) {
      sb.toString
    }
    else null
  }

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

  class TestRunShutdownHook(var zc: ZafiraClient, var testRun: TestRunType) extends Thread {
    override def run(): Unit = {
      if (testRun != null) {
        val aborted = zc.abortTestRun(testRun.getId)
        LOGGER.info("TestRunShutdownHook was executed with result: " + aborted)
      }
    }
  }


}

