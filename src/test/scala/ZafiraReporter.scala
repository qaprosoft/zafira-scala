import com.qaprosoft.zafira.models.db.Status
import com.qaprosoft.zafira.models.dto.{TagType, TestCaseType, TestSuiteType}
import com.qaprosoft.zafira.models.dto.user.UserType
import org.scalatest.events._
import org.scalatest._

class ZafiraReporter extends Reporter {
  var testSuite: TestSuiteType = _
  var testRunUser: UserType = _
  var testCase: TestCaseType = _

  val config = new Settings()
  val zafiraUrl: String = config.envOrElseConfig("zafira.base.url")
  val zafiraAdminToken: String = config.envOrElseConfig("zafira.admin.token")

  val client = new ScalaZafiraClient(zafiraUrl)
  val sessionToken = client.refreshToken(zafiraAdminToken)

  client.setToken(sessionToken.getObject.getAccessToken)


  def beforeStart() = {

    val projectName = config.envOrElseConfig("project.name")
    val suiteOwnerName = config.envOrElseConfig("suite.owner.name")
    val suiteFileName = config.envOrElseConfig("suite.file.name")
    val suiteName = config.envOrElseConfig("suite.name")

    val jobUrl = config.envOrElseConfig("job.url") // workaround for now

    // Override project if specified in config or in env
    client.zafiraClient.initProject(projectName)

    // Register user who initiated test run
    testRunUser = client.getTestRunUser()

    // Register test suite  with suite owner
    val suiteOwner = client.getSuteOwner(suiteOwnerName)
    testSuite = client.registerTestSuite(suiteName, suiteFileName, suiteOwner)

    // Register job that triggers test run
    client.registerCIJob(jobUrl, suiteOwner)
  }
  def onTestStart(testClass: Option[String], testName: String) = {
    testCase = client.registerTestCase(testSuite, testClass.get, testName)

    // there will be part to look for already registered test run
    // for now runId will be 1
    val runId: Long = 1

    // set run count to 1 for now
    val runCount: Int = 1

    // xml config will be "" for now
    val configuration: String = ""

    // dependsonmethods will be empty list for now
    val dependsonmethods: Array[String] = Array("")

    // ci id will be ""
    val threadCiTestId: String = ""

    // empty tags set for now
    val tags: Set[TagType] = Set()

    // register new test
    val testParams: String = "" // not sure scalatest has params of a testcase
    val group: String = testClass.get
    client.registerTestStart(testName, group, Status.IN_PROGRESS, testParams, runId, testCase.getId, runCount, configuration, dependsonmethods, threadCiTestId, tags)

  }

  def apply(event: Event) = {
    event match {
      case event: RunStarting => beforeStart()
      case event: TestStarting => onTestStart(event.suiteClassName, event.testName)
      case event: TestSucceeded => println(event.testName + "\n...test succeeded")
      case event: TestFailed => println(event.testName + "\n...test failed")
      case _ =>
    }
  }
}


class ExampleSuite extends FunSuite {
  test("CustomReporter Pass")  {
    assert(1===1)
  }
  test("CustomReporter Fail")  {
    assert(1===2)
  }
}