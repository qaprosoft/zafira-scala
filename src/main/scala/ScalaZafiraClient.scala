import com.qaprosoft.zafira.client.ZafiraClient
import com.qaprosoft.zafira.models.dto.user.UserType

class ScalaZafiraClient(var url: String) {

  val zafiraClient = new ZafiraClient(url)

  def login(username: String, password: String): Unit = {
    this.zafiraClient.login(username, password)
  }

  def refreshToken(token: String): Unit = {
    this.zafiraClient.refreshToken(token)
  }

  def createUser(user: UserType): Unit = {
    this.zafiraClient.createUser(user)
  }

}
