
name := "zafira-scala"

version := "0.1"

scalaVersion := "2.12.7"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-C", "com.qaprosoft.reporter.ZafiraReporter")

resolvers += "Qaprosoft Snapshots" at "https://ci.qaprosoft.com/nexus/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.seleniumhq.selenium" % "selenium-java" % "3.14.0",
  "com.qaprosoft" % "zafira" % "3.3.51" pomOnly,
  "com.qaprosoft" % "zafira-client" % "3.3.51",
  "commons-pool" % "commons-pool" % "1.6",
  "commons-beanutils" % "commons-beanutils" % "1.9.3",
  "log4j" % "log4j" % "1.2.12",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5" % Test,
  "com.sun.jersey" % "jersey-core" % "1.19.4",
  "com.google.guava".%("guava") % "19.0",
  "org.apache.commons" % "commons-configuration2" % "2.1",
  "javax.mail" % "mail" % "1.4.7",
  "javax.mail" % "javax.mail-api" % "1.5.5",
  "commons-io" % "commons-io" % "2.6"

)
