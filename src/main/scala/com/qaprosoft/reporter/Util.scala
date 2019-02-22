package com.qaprosoft.reporter

import com.qaprosoft.zafira.config.CIConfig
import java.util.UUID

import com.typesafe.config.ConfigFactory

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

    def getStringParam(path: String, property: String):String = {
      val param =  System.getProperty(property)
      if (param != null) param
      else conf.getString(path)
    }

  def getBooleanParam(path: String, property: String):Boolean = {
    val param =  System.getProperty(property)
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
  val CI_RUN_ID = getStringParam(ci + ciRunId,  UUID.randomUUID.toString)
  val CI_BUILD = getStringParam(ci + ciBuild, ciBuild)
  val CI_BUILD_CAUSE = getStringParam(ci + ciBuildCause, ciBuildCause)
  val CI_PARENT_URL = getStringParam(ci + ciParentUrl, ciParentUrl)
  val CI_PARENT_BUILD = getStringParam(ci + ciParentBuild, ciParentBuild)

  val GIT_BRANCH = getStringParam(git + gitBranch, gitBranch)
  val GIT_COMMIT = getStringParam(git + gitCommit, gitCommit)
  val GIT_URL = getStringParam(git + gitUrl, gitUrl)

  val JIRA_SUITE_ID = getStringParam(jira + jiraSuiteId, jiraSuiteId)


  val ciConfig:CIConfig = {
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

}
