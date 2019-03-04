package com.qaprosoft.reporter
import java.net.URL

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities, LocalFileDetector, RemoteWebDriver}
import org.openqa.selenium.support.ThreadGuard
import org.scalatest._
import org.scalatest.selenium.{Driver, WebBrowser}

trait ChromeSuite extends TestSuite with WebBrowser with Driver with Util with BeforeAndAfterAll{
  this: Suite with WebBrowser with Driver =>

  lazy val seleniumGridConfig = seleniumGrid

  var number = 0

  implicit lazy val webDriver: WebDriver = {
    val driver = seleniumGridConfig.enabled.toBoolean match {
      case true => {
        // Use selenium grid
        val capability: DesiredCapabilities = DesiredCapabilities.chrome()
        val options = new ChromeOptions
        //Jenkins has this with out any special work, locally however i needed to add this
        //This was preventing JS Popups
        options.addArguments("--disable-popup-blocking")
        options.addArguments("--disable-gpu")
        capability.setCapability(ChromeOptions.CAPABILITY, options)
        capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
        capability.setCapability("enableVideo", seleniumGrid.enableVideo.toBoolean)
        capability.setCapability("enableVNC", seleniumGrid.enableVnc.toBoolean)

        try {
          suiteLogger.info("Try to create Grid WebDriver.")
          gridWebDriver(capability)
        } catch {
          case e: Throwable => {
            suiteLogger.warn("Grid WebDriver failed, try again.")
            try {
              gridWebDriver(capability)
            } catch {
              case e: Throwable => {
                suiteLogger.warn("Grid WebDriver failed 2x, try 3rd time.")
                gridWebDriver(capability)
              }
            }
          }
        }

      }
      case _ => {
        // Don't use selenium grid
        WebDriverPool.pool.get.borrowObject()
      }
    }
    suiteLogger.info(s"CHECKING OUT DRIVER:${driver.hashCode()}")

    ThreadGuard.protect(driver)
  }

  private[this] def releaseWebDriver(webDriver : WebDriver) = {
    try {
      webDriver.quit()
    } catch {
      case _: Exception => suiteLogger.info("[URGENT] could not de-allocate webdriver. Could be Selenium Grid issue")
    }
  }

  override def afterAll(): Unit ={
    super.afterAll()
    seleniumGridConfig.enabled.toBoolean match {
      case true => releaseWebDriver(webDriver)
      case _ => WebDriverPool.pool.get.returnObject(webDriver)
    }
    suiteLogger.info(s"CHECKED IN DRIVER:${webDriver.hashCode()}")
  }

  /**
    * Create a Selenium Grid WebDriver
    *
    * @param capability
    * @return
    */
  def gridWebDriver(capability: DesiredCapabilities): WebDriver = {
    val remoteWebDriver: RemoteWebDriver = new RemoteWebDriver(new URL(seleniumGridUrl(seleniumGridConfig.webdriverPath)), capability)
    remoteWebDriver.setFileDetector(new LocalFileDetector())
    logSelenoidVncLink(remoteWebDriver)
    remoteWebDriver
  }

  def logSelenoidVncLink(remoteWebDriver: RemoteWebDriver ) = {
    val rawSessionId = remoteWebDriver.getSessionId.toString
    println(s"UI Session ID: $rawSessionId")
  }

}
