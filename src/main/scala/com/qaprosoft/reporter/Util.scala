package com.qaprosoft.reporter

import java.util.UUID

import com.qaprosoft.zafira.config.CIConfig
import org.apache.log4j.Logger

trait Util {

  val LOGGER = Logger.getLogger(this.getClass)

  var ZAFIRA_ENABLED = sys.props.getOrElse("zafira_enabled", true).toString.toBoolean
  val ZAFIRA_URL = sys.props.getOrElse("zafira_service_url", "http://demo.qaprosoft.com/zafira-ws")
  val ZAFIRA_ACCESS_TOKEN = sys.props.getOrElse("zafira_access_token","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicGFzc3dvcmQiOiJ6WHRoVy9CNS9CZk9QRW4xRktMVy8vbmpqbkZFSGJhZSIsInRlbmFudCI6InphZmlyYSIsImV4cCI6MTMwMzg2OTAyMzg3fQ.8xDrHUmtahzBrbyKrAX-Xkr9cUZXpfH5aC-rDh1oQZGWCfVME76YEsUPPoozOOfhHKg6AzV56w-BEq9UtGz_AA")
  val ZAFIRA_PROJECT = sys.props.getOrElse("zafira_project", "scala")
  var ZAFIRA_RERUN_FAILURES = sys.props.getOrElse("rerun_failures", false).toString.toBoolean
  val ZAFIRA_REPORT_EMAILS = sys.props.getOrElse("zafira_report_emails", true).toString.toBoolean
  val ZAFIRA_REPORT_SHOW_STACKTRACE = sys.props.getOrElse("zafira_report_show_stacktrace", true).toString.toBoolean
  val ZAFIRA_CONFIGURATOR = sys.props.getOrElse("zafira_configurator", "com.qaprosoft.zafira.config.DefaultConfigurator")
  val ZAFIRA_RUN_ID_PARAM = "zafira_run_id"
  val SUITE_NAME =  sys.props.getOrElse("suite_name","unknown")

  var CI_URL = sys.props.getOrElse("ci_url","http://demo.qaprosoft.com/jenkins/")
  var CI_RUN_ID = sys.props.getOrElse("ci_run_id", UUID.randomUUID.toString)
  var CI_BUILD = sys.props.getOrElse("ci_build", "1")
  var CI_BUILD_CAUSE = sys.props.getOrElse("ci_build_cause", "MANUALTRIGGER")
  var CI_PARENT_URL = sys.props.getOrElse("ci_parent_url", null)
  var CI_PARENT_BUILD = sys.props.getOrElse("ci_parent_build", null)

  var GIT_BRANCH = sys.props.getOrElse("git_branch", "reportter")
  var GIT_COMMIT = sys.props.getOrElse("git_commit", "")
  var GIT_URL = sys.props.getOrElse("git_url", "https://github.com/qaprosoft/zafira-scala")

  var JIRA_SUITE_ID = sys.props.getOrElse("jira_suite_id", null)

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

}
