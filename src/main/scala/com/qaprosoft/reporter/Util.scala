package com.qaprosoft.reporter

import java.util.UUID
import com.qaprosoft.zafira.config.CIConfig
import org.slf4j.ext.XLoggerFactory

trait Util {

  var ZAFIRA_ENABLED = sys.props.getOrElse("zafira_enabled", true).asInstanceOf[Boolean]
  val ZAFIRA_URL = sys.props.getOrElse("zafira_service_url", "https://stage.qaprosoft.farm/zafira-ws")
  val ZAFIRA_ACCESS_TOKEN = sys.props.getOrElse("zafira_access_token","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicGFzc3dvcmQiOiJzclZmUitMVWNhVGdJbFR1SURNVmNlL1EzVVlvbmNuOCIsInRlbmFudCI6InN0YWdlIiwiZXhwIjoxMzAzODY5OTU2MjJ9.tAVXNCOCbEisbo5Uvp73w-s1XsYGtDeKL2pbYBhSCFtEgNR7CW82CSAROOdb7iPkGyCA9f-b9AtFqeBt_ICDPg")
  val ZAFIRA_PROJECT = sys.props.getOrElse("zafira_project", "Scala")
  var ZAFIRA_RERUN_FAILURES = sys.props.getOrElse("zafira_rerun_failures", false).asInstanceOf[Boolean]
  val ZAFIRA_REPORT_EMAILS = sys.props.getOrElse("zafira_report_emails", false).asInstanceOf[Boolean]
  val ZAFIRA_REPORT_SHOW_STACKTRACE = sys.props.getOrElse("zafira_report_show_stacktrace", true).asInstanceOf[Boolean]
  val ZAFIRA_CONFIGURATOR = sys.props.getOrElse("zafira_configurator", "com.qaprosoft.zafira.config.DefaultConfigurator")
  val ZAFIRA_RUN_ID_PARAM = "zafira_run_id"

  var CI_URL = sys.props.getOrElse("ci_url",null)
 // var CI_URL = sys.props.getOrElse("ci_url", "https://stage.qaprosoft.com/jenkins/job/ScalaTest/job/ScalaTest/")
  var CI_RUN_ID = sys.props.getOrElse("ci_run_id", UUID.randomUUID.toString)
  var CI_BUILD = sys.props.getOrElse("ci_build", null)
  var CI_BUILD_CAUSE = sys.props.getOrElse("ci_build_cause", "MANUALTRIGGER")
  var CI_PARENT_URL = sys.props.getOrElse("ci_parent_url", null)
  var CI_PARENT_BUILD = sys.props.getOrElse("ci_parent_build", null)

  var GIT_BRANCH = sys.props.getOrElse("git_branch", "ddev")
  var GIT_COMMIT = sys.props.getOrElse("git_commit", null)
  var GIT_URL = sys.props.getOrElse("git_url", "https://github.com/qaprosoft/zafira-scala")

  var JIRA_SUITE_ID = sys.props.getOrElse("ci_build", null)

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

}
