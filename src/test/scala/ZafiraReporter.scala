import org.scalatest.events._
import org.scalatest._

class ZafiraReporter extends Reporter {

  val adminDefaultToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicGFzc3dvcmQiOiI2RzgzSnE4dXNQQ3JocjRPVT" +
    "ZJZnVYdC85engxSWY3NCIsInRlbmFudCI6InphZmlyYSIsImV4cCI6MTMwMzg3NDMwMjk3fQ.e97p9ZV1CWtvQm_Oo" +
    "wEwiWbcZc8JFZELal1a7Y_4aCFHkGgXA4UUw47zDdatn3P1n7lhHp1P2NJGCpYvYrAg9g"
  val zafiraDefaultURL = "http://demo.qaprosoft.com/zafira-ws"

  val zafiraAdminToken: Option[String] = sys.env.get("ZAFIRA_ADMIN_TOKEN")
  val zafiraUrl: Option[String] = sys.env.get("ZAFIRA_BASE_URI")

  val client = new ScalaZafiraClient(zafiraUrl.getOrElse(zafiraDefaultURL))
  val sessionToken = client.refreshToken(zafiraAdminToken.getOrElse(adminDefaultToken))

  client.setToken(sessionToken.getObject.getAccessToken)


  def apply(event: Event) {
    event match {
      case event: TestSucceeded => println(event.testName + "\n...test succeeded")
      case event: TestFailed => println(event.testName + "\n...test failed")
      case _ =>
    }
  }
}

//Create trait to hook custom reporter into tests as workaround before will be added to sbt
trait ReporterHook extends Suite with BeforeAndAfterAll {
  override def run(testName: Option[String], args: Args) : Status = {
    val rep = new ZafiraReporter()
    super.run(testName, args.copy(reporter = rep))
  }
}

//Create tests with custom hook to have report
class CustomReporter extends FunSuite with ReporterHook{
  test("CustomReporter Pass")  {
    assert(1===1)
  }
  test("CustomReporter Fail")  {
    assert(1===2)
  }
}