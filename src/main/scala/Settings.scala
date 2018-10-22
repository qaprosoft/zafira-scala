import com.typesafe.config.ConfigFactory

import scala.util.Properties

class Settings (fileNameOption: Option[String] = None) {
  val config = fileNameOption.fold(ifEmpty = ConfigFactory.load())(file => ConfigFactory.parseResources(file))

  /**
    * load configurations in the following order:
    * 1) From properly named environment variables
    * 2) From command line paramenters
    * 3) From the configuration file.
    * It will replace dots in provieded env variable name with underscores and try to find it in env
    * if there is no such varible in env it will take it from config file
    * @param name String param name to look for
    * @return the value of param
    */
  def envOrElseConfig(name: String): String = {
    Properties.envOrElse(
      name.toUpperCase.replaceAll("""\.""", "_"),
      config.getString(name)
    )
  }
}

