
name := "zafira-scala"

version := "0.1"

scalaVersion := "2.12.7"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-C", "com.qaprosoft.reporter.ZafiraReporter")

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.12" % "3.0.5",
    "org.scalactic" %% "scalactic" % "3.0.5",
    "com.sun.jersey" % "jersey-core" % "1.19",
    "com.typesafe" % "config" % "1.2.1",
    "org.seleniumhq.selenium" % "selenium-java" % "3.11.0" % "test",
    "org.apache.commons" % "commons-lang3" % "3.0",
    "org.apache.commons" % "commons-configuration2" % "2.4",
    "javax.xml.bind" % "jaxb-api" % "2.3.1",
    "com.sun.xml.bind" % "jaxb-impl" % "2.3.2",
    "com.sun.xml.bind" % "jaxb-core" % "2.3.0.1",
    "javax.mail" % "mail" % "1.4",
    "commons-io" % "commons-io" % "2.6",
    "org.apache.httpcomponents" % "httpclient" % "4.5.7",
    "org.codehaus.jettison" % "jettison" % "1.4.0",
    "com.qaprosoft" % "zafira" % "3.3.51",
    "com.qaprosoft" % "zafira-models" % "3.3.51",
    "com.qaprosoft" % "zafira-client" % "3.3.51",
    "commons-beanutils" % "commons-beanutils" % "1.9.3",
    "org.seleniumhq.selenium" % "selenium-java" % "3.11.0",
    "commons-pool" % "commons-pool" % "1.6",
//    "ch.qos.logback" % "logback-classic" % "1.2.3",
//    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
      "log4j" % "log4j" % "1.2.17"


)
