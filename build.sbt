
name := "zafira-scala"

organization := "com.qaprosoft"

version := "0.3-SNAPSHOT"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5",
  "com.qaprosoft" % "zafira-client" % "3.3.51",
  "commons-beanutils" % "commons-beanutils" % "1.9.3",
  "log4j" % "log4j" % "1.2.12",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "com.sun.jersey" % "jersey-core" % "1.19.4",
  "com.google.guava".%("guava") % "19.0",
  "org.apache.commons" % "commons-configuration2" % "2.1",
  "javax.mail" % "mail" % "1.4.7",
  "javax.mail" % "javax.mail-api" % "1.5.5",
  "commons-io" % "commons-io" % "2.6"

)

credentials += Credentials(
  sys.props.getOrElse("realm", null),
  sys.props.getOrElse("host", null),
  sys.props.getOrElse("username", null),
  sys.props.getOrElse("password", null)
)

publishTo := Some("Sonatype Nexus Repository Manager" at "https://ci.qaprosoft.com/nexus/content/repositories/snapshots")
