import com.qaprosoft.zafira.client.ZafiraClient
import com.qaprosoft.zafira.models.dto.user.UserType

class ScalaZafiraClient(var url: String) {

  val zafiraClient = new ZafiraClient(url)

  def login(username: String, password: String) = {
    this.zafiraClient.login(username, password)
  }

  def refreshToken(token: String) = {
    this.zafiraClient.refreshToken(token)
  }

  def createUser(user: UserType) = {
    this.zafiraClient.createUser(user)
  }

  def setToken(token: String)= {
    this.zafiraClient.setAuthToken("Bearer " + token)
  }

}
