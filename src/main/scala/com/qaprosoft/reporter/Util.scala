package com.qaprosoft.reporter

import java.util.UUID

trait Util {

  var ZAFIRA_ENABLED = sys.props.getOrElse("zafira_enabled", true).toString.toBoolean
  val ZAFIRA_URL = sys.props.getOrElse("zafira_service_url", null)
  val ZAFIRA_ACCESS_TOKEN = sys.props.getOrElse("zafira_access_token",null)
  val ZAFIRA_PROJECT = sys.props.getOrElse("zafira_project", null)
  var ZAFIRA_RERUN_FAILURES = sys.props.getOrElse("rerun_failures", false).toString.toBoolean
  val ZAFIRA_REPORT_EMAILS = sys.props.getOrElse("zafira_report_emails", true).toString.toBoolean
  val ZAFIRA_REPORT_SHOW_STACKTRACE = sys.props.getOrElse("zafira_report_show_stacktrace", true).toString.toBoolean
  val ZAFIRA_CONFIGURATOR = sys.props.getOrElse("zafira_configurator", "com.qaprosoft.zafira.config.DefaultConfigurator")
  val ZAFIRA_RUN_ID_PARAM = "zafira_run_id"
  val SUITE_NAME =  sys.props.getOrElse("suite_name",null)

  var CI_URL = sys.props.getOrElse("ci_url",null)
  var CI_RUN_ID = sys.props.getOrElse("ci_run_id", UUID.randomUUID.toString)
  var CI_BUILD = sys.props.getOrElse("ci_build", null)
  var CI_BUILD_CAUSE = sys.props.getOrElse("ci_build_cause", null)
  var CI_PARENT_URL = sys.props.getOrElse("ci_parent_url", null)
  var CI_PARENT_BUILD = sys.props.getOrElse("ci_parent_build", null)

  var GIT_BRANCH = sys.props.getOrElse("git_branch", null)
  var GIT_COMMIT = sys.props.getOrElse("git_commit", null)
  var GIT_URL = sys.props.getOrElse("git_url", null)

  var JIRA_SUITE_ID = sys.props.getOrElse("jira_suite_id", null)

}
