package com.qaprosoft.reporter

import java.util

import com.typesafe.config.ConfigFactory
import org.apache.commons.configuration2.CombinedConfiguration
import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.FileBasedConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.SystemConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.commons.configuration2.tree.MergeCombiner
import java.util.{NoSuchElementException, UUID}

import com.qaprosoft.zafira.config.CIConfig
import org.apache.commons.pool.PoolableObjectFactory
import org.apache.commons.pool.impl.GenericObjectPool
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.slf4j.ext.XLoggerFactory


trait Util {

  val conf = ConfigFactory.load()

  private val zafira = "app.zafira."
  private val zafiraEnabled = "zafira_enabled"
  private val zafiraServiceUrl = "zafira_service_url"
  private val zafiraAccessToken = "zafira_access_token"
  private val zafiraProject = "zafira_project"
  private val zafiraRerunFailures = "zafira_rerun_failures"
  private val zafiraReportEmail = "zafira_report_emails"
  private val zafiraReportShowStacktrace = "zafira_report_show_stacktrace"
  private val zafiraConfigurator = "zafira_configurator"
  val ZAFIRA_RUN_ID_PARAM = "zafira_run_id"
  val ZAFIRA_PROJECT_PARAM = "zafira_project"
  val ZAFIRA_PROPERTIES = "zafira.properties"

  private val ci = "app.ci."
  private val ciUrl = "ci_url"
  private val ciRunId = "ci_run_id"
  private val ciBuild = "ci_build"
  private val ciBuildCause = "ci_build_cause"
  private val ciParentUrl = "ci_parent_url"
  private val ciParentBuild = "ci_parent_build"

  private val git = "app.git."
  private val gitBranch = "git_branch"
  private val gitCommit = "git_commit"
  private val gitUrl = "git_url"

  private val jira = "app.jira."
  private val jiraSuiteId = "jira_suite_id"

  def getStringParam(path: String, property: String): String = {
    val param = System.getProperty(property)
    if (param != null) param
    else conf.getString(path)
  }

  def getBooleanParam(path: String, property: String): Boolean = {
    val param = System.getProperty(property)
    if (param != null) param.asInstanceOf[Boolean]
    else conf.getBoolean(path)
  }

  var ZAFIRA_ENABLED = getBooleanParam(zafira + zafiraEnabled, zafiraEnabled)
  val ZAFIRA_URL = getStringParam(zafira + zafiraServiceUrl, zafiraServiceUrl)
  val ZAFIRA_ACCESS_TOKEN = getStringParam(zafira + zafiraAccessToken, zafiraAccessToken)
  val ZAFIRA_PROJECT = getStringParam(zafira + zafiraProject, zafiraProject)
  var ZAFIRA_RERUN_FAILURES = getBooleanParam(zafira + zafiraRerunFailures, zafiraRerunFailures)
  val ZAFIRA_REPORT_EMAILS = getStringParam(zafira + zafiraReportEmail, zafiraReportEmail)
  val ZAFIRA_REPORT_SHOW_STACKTRACE = getBooleanParam(zafira + zafiraReportShowStacktrace, zafiraReportShowStacktrace)
  val ZAFIRA_CONFIGURATOR = getStringParam(zafira + zafiraConfigurator, zafiraConfigurator)

  var CI_URL = getStringParam(ci + ciUrl, ciUrl)
  val CI_RUN_ID = getStringParam(ci + ciRunId, UUID.randomUUID.toString)
  val CI_BUILD = getStringParam(ci + ciBuild, ciBuild)
  val CI_BUILD_CAUSE = getStringParam(ci + ciBuildCause, ciBuildCause)
  val CI_PARENT_URL = getStringParam(ci + ciParentUrl, ciParentUrl)
  val CI_PARENT_BUILD = getStringParam(ci + ciParentBuild, ciParentBuild)

  val GIT_BRANCH = getStringParam(git + gitBranch, gitBranch)
  val GIT_COMMIT = getStringParam(git + gitCommit, gitCommit)
  val GIT_URL = getStringParam(git + gitUrl, gitUrl)

  var JIRA_SUITE_ID = getStringParam(jira + jiraSuiteId, jiraSuiteId)


  val ciConfig: CIConfig = {
    val ci = new CIConfig
    ci.setCiRunId(CI_RUN_ID)
    ci.setCiUrl(CI_URL)
    ci.setCiBuild(CI_BUILD)
    ci.setCiBuildCause(CI_BUILD_CAUSE)
    ci.setCiParentUrl(CI_PARENT_URL)
    ci.setCiParentBuild(CI_PARENT_BUILD)

    ci.setGitBranch(GIT_BRANCH)
    ci.setGitCommit(GIT_COMMIT)
    ci.setGitUrl(GIT_URL)
    ci
  }


  val suiteLogger = XLoggerFactory.getXLogger(this.getClass)


  //  val ciConfig:CIConfig = {
  //
  //      val config:CombinedConfiguration = new CombinedConfiguration(new MergeCombiner)
  //      config.setThrowExceptionOnMissing(true)
  //      config.addConfiguration(new SystemConfiguration)
  //      config.addConfiguration(
  //        new FileBasedConfigurationBuilder[FileBasedConfiguration](classOf[PropertiesConfiguration])
  //          .configure(new Parameters().properties.setFileName(ZAFIRA_PROPERTIES))
  //          .getConfiguration)
  //
  //      val ci = new CIConfig
  //      ci.setCiRunId(config.getString("ci_run_id", UUID.randomUUID.toString))
  //      ci.setCiUrl(config.getString("ci_url", "http://localhost:8080/job/unavailable"))
  //      ci.setCiBuild(config.getString("ci_build", null))
  //      ci.setCiBuildCause(config.getString("ci_build_cause", "MANUALTRIGGER"))
  //      ci.setCiParentUrl(config.getString("ci_parent_url", null))
  //      ci.setCiParentBuild(config.getString("ci_parent_build", null))
  //
  //      ci.setGitBranch(config.getString("git_branch", null))
  //      ci.setGitCommit(config.getString("git_commit", null))
  //      ci.setGitUrl(config.getString("git_url", null))
  //
  //      JIRA_SUITE_ID = config.getString("jira_suite_id", null)
  //
  //    ci
  //  }

  object seleniumGrid {
    lazy val enabled = sys.props.getOrElse("seleniumGrid.enabled", false.toString)
    lazy val protocol = sys.props.getOrElse("seleniumGrid.protocol", "http")
    lazy val hostname = sys.props.getOrElse("seleniumGrid.hostname", "selenium-hub.intranet.solarmosaic.com")
    lazy val port = sys.props.getOrElse("seleniumGrid.port", "4444")
    lazy val webdriverPath = sys.props.getOrElse("seleniumGrid.webdriverPath", "/wd/hub")
    lazy val enableVideo = sys.props.getOrElse("seleniumGrid.enableVideo", false.toString)
    lazy val enableVnc = sys.props.getOrElse("seleniumGrid.enableVnc", false.toString)
  }

  object download {
    lazy val folder = sys.props.getOrElse("download.folder", "~/Downloads")
  }


  object WebDriverPool {
    //TODO: inject this errywhere instead of a static object
    lazy val seleniumGridConfig = seleniumGrid

    var pool: Option[GenericObjectPool[WebDriver]] = if (seleniumGridConfig.enabled.toBoolean) {
      // dont use pool for grid
      None
    } else {
      // Limit to 6 threads
      val maxActive = sys.props.getOrElse("dataloader.threats.number", "6").toInt
      Some(new GenericObjectPool[WebDriver](new RemoteWebDriverFactory, maxActive))
    }
  }

  /**
    * Builds a URL for seleniumGrid from the relative path, using the settings from Config.
    */
  def seleniumGridUrl(path: String): String = {
    seleniumGrid.protocol + "://" + seleniumGrid.hostname + ":" + seleniumGrid.port + path
  }

  //  /**
  //    * Builds a URL to trigger jobs from Jenkins, using the settings from Config.
  //    */
  //  def jenkinsJobUrl(path: String): String = {
  //    jenkins.protocol + "://" + jenkins.hostname + "/"+ path
  //  }

  /**
    * Developer HINTS:
    * Exceptions in [[validateObject()]], [[activateObject()]], [[passivateObject()]]
    * get swallowed. When these methods throw, the pool assumes the object is bad and destroys it.
    *
    */
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
      chromePref.put("download.default_directory", download.folder);
      options.setExperimentalOption("prefs", chromePref);

      //Jenkins has this with out any special work, locally however i needed to add this
      //This was preventing JS Popups
      options.addArguments("--disable-popup-blocking")
      val driver = new ChromeDriver(options)

      driver
    }
  }

}
