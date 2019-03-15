package com.qaprosoft.reporter

import java.net.URL
import java.util

import org.apache.commons.pool.PoolableObjectFactory
import org.apache.commons.pool.impl.GenericObjectPool
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities, LocalFileDetector, RemoteWebDriver}
import org.openqa.selenium.support.ThreadGuard
import org.scalatest._
import org.scalatest.selenium.{Driver, WebBrowser}
import org.slf4j.ext.XLoggerFactory


trait ChromeSuite extends TestSuite with WebBrowser with Driver with Util with BeforeAndAfterAll{
  this: Suite with WebBrowser with Driver =>

  lazy val seleniumGridConfig = seleniumGrid

  object seleniumGrid {
    lazy val enabled = sys.props.getOrElse("seleniumGridEnabled", false.toString)

    lazy val selenium_url = sys.props.getOrElse("SELENIUM_URL", "http://qpsdemo:kqyQZC54WZ2A@stage.qaprosoft.com:4444/wd/hub")
  }
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


  def gridWebDriver(capability: DesiredCapabilities): WebDriver = {
    val remoteWebDriver: RemoteWebDriver = new RemoteWebDriver(new URL(seleniumGrid.selenium_url), capability)
    remoteWebDriver.setFileDetector(new LocalFileDetector())
    logSelenoidVncLink(remoteWebDriver)
    remoteWebDriver
  }

  def logSelenoidVncLink(remoteWebDriver: RemoteWebDriver ) = {
    val rawSessionId = remoteWebDriver.getSessionId.toString
    println(s"UI Session ID: $rawSessionId")

  }


object WebDriverPool {
  lazy val seleniumGridConfig = seleniumGrid

  var pool: Option[GenericObjectPool[WebDriver]] = if (seleniumGridConfig.enabled.toBoolean) {
    None
  } else {
    val maxActive = sys.props.getOrElse("dataloader.threats.number", "6").toInt
    Some(new GenericObjectPool[WebDriver](new RemoteWebDriverFactory, maxActive))
  }
}

  class RemoteWebDriverFactory extends PoolableObjectFactory[WebDriver] {
    val logger = XLoggerFactory.getXLogger(this.getClass)

    override def destroyObject(obj: WebDriver): Unit = {
      logger.info(s"Destroying DRIVER: ${obj.hashCode()}")
      obj.quit()
    }

    override def validateObject(obj: WebDriver): Boolean = {
      //Unsure how to handle this. I'm guessing we should obj.getWindowHandles.size()>0
      //However when i did that it started to throw.

      true
    }

    override def activateObject(obj: WebDriver): Unit = {

    }

    override def passivateObject(obj: WebDriver): Unit = {
      //clear cookies when object is put back into the pool
      obj.navigate().to("chrome://settings-frame/clearBrowserData")
      obj.findElement(By.id("clear-browser-data-commit")).click()
      obj.navigate().to("about:blank")
    }

    override def makeObject(): WebDriver = {
      val options = new ChromeOptions

      // Specify directory for downloading

      val chromePref: util.HashMap[String, Object] = new util.HashMap();
      chromePref.put("download.default_directory", sys.props.getOrElse("download.folder", "~/Downloads"));
      options.setExperimentalOption("prefs", chromePref);

      //Jenkins has this with out any special work, locally however i needed to add this
      //This was preventing JS Popups
      options.addArguments("--disable-popup-blocking")
      val driver = new ChromeDriver(options)

      driver
    }
  }

  override def withFixture(test: NoArgTest) = {
    var outcome:Outcome = null
    if(RUN_TESTS.contains(test.name)) {
      println(test.name + " is reruning")
      outcome = super.withFixture(test)
    } else println(test.name + " is already passed")
    outcome
  }

}
