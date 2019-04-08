## ScalaTest Zafira Reporter


This Project is a ZafiraReporter for ScalaTest projects.

To use the reporter:
* add this library to your project as dependency
  * SBT
   >      resolvers ++= Seq(
   >        "Qaprosoft" at ""
   >      )
   >
   >      libraryDependencies += "qaprosoft.com" %% "zafira-reporter" % "${version}" % "test"
   >

  * Maven
   >
   >      <dependency>
   >        <groupId>qaprosoft.com</groupId>
   >        <artifactId>zafira-reporter</artifactId>
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



