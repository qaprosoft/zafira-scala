name := "zafira-scala"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
    "com.qaprosoft" % "zafira-client" % "3.3.46",
    "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test",
    "com.sun.jersey" % "jersey-core" % "1.19",
    "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
    "com.typesafe" % "config" % "1.2.1"
)
