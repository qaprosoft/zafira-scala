## ScalaTest Zafira Reporter


This Project is a ZafiraReporter for ScalaTest projects.

To use the reporter:
* add this library to your project as dependency
  * SBT
   >      resolvers ++= Seq(
   >        "Qaprosoft" at "https://ci.qaprosoft.com/nexus/content/repositories/snapshots/"
   >      )
   >
   >      libraryDependencies += "com.qaprosoft" %% "zafira-scala" % "${version}" % Test,
   >

  * Maven
   >
   >      <dependency>
   >        <groupId>com.qaprosoft</groupId>
   >        <artifactId>zafira-scala</artifactId>
   >        <version>${version}</version>
   >        <scope>test</scope>
   >      </dependency>



* set reporter in your build tool:
  * _maven_
  (Configuration options / reporters):
  `com.qaprosoft.reporter.ZafiraReporter`
  http://www.scalatest.org/user_guide/using_the_scalatest_maven_plugin
  * _sbt_

   In your build.sbt
  ```scala
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-C", "com.qaprosoft.reporter.ZafiraReporter")
  ```
  ```scala
    libraryDependencies += "com.qaprosoft" %% "zafira-scala" % ${version} % Test)
    ```

In order to post results to the Zafira service you have to specify the following parameters as System properties:
 ```
------ Zafira integration ------

zafira_enabled=false/true
zafira_service_url=
zafira_access_token=
zafira_project=
zafira_rerun_failures=false/true
zafira_report_emails=false/true
 ```
  ```
 ------ CI integration ------
ci_url=
ci_run_id=
ci_build=
ci_build_cause=
ci_parent_url=
ci_parent_build=
ci_user_id=
ci_user_first_name=
ci_user_last_name=
ci_user_email=
 ```
  ```
 ------ Git integration ------
git_branch=
git_commit=
git_url=
 ```
  ```
------ JIRA integration ------
jira_suite_id
 ```

