name := """promptpayments"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "redis.clients" % "jedis" % "2.8.1",
  javaWs,
  filters,
  "org.assertj" % "assertj-core" % "3.4.1",
  "io.mikael" % "urlbuilder" % "2.0.7",
  "org.assertj" % "assertj-core" % "3.5.2" % "test",
  "com.typesafe.play" % "play-java-jdbc_2.11" % "2.5.8",
  "org.flywaydb" % "flyway-core" % "4.0.3",
  "org.mockito" % "mockito-core" % "1.+"
)

lazy val `promptpayments` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"