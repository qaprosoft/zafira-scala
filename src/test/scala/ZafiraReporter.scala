import org.scalatest.events._
import org.scalatest._

class ZafiraReporter extends Reporter {
  val config = new Settings()
  val zafiraUrl = config.envOrElseConfig("zafira.base.url")
  val zafiraAdminToken = config.envOrElseConfig("zafira.admin.token")

  val client = new ScalaZafiraClient(zafiraUrl)
  val sessionToken = client.refreshToken(zafiraAdminToken)

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