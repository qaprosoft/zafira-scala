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


}
