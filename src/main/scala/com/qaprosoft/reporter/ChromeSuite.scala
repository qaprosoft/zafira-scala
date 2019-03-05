package com.qaprosoft.reporter

import java.util

import org.apache.commons.pool.PoolableObjectFactory
import org.apache.commons.pool.impl.GenericObjectPool
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ThreadGuard
import org.scalatest._
import org.scalatest.selenium.{Driver, WebBrowser}
import org.slf4j.ext.XLoggerFactory


trait ChromeSuite extends TestSuite with WebBrowser with Driver with Util with BeforeAndAfterAll{
  this: Suite with WebBrowser with Driver =>

  implicit lazy val webDriver: WebDriver = {
    val driver = WebDriverPool.pool.get.borrowObject()
    ThreadGuard.protect(driver)
  }

  object WebDriverPool {
    var pool: Option[GenericObjectPool[WebDriver]] =  {
      Some(new GenericObjectPool[WebDriver](new RemoteWebDriverFactory, 2))
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
}
