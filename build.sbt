name := """promptpayments"""

version := "1.0-SNAPSHOT"

resolvers += "GOV.UK Notify" at "https://dl.bintray.com/gov-uk-notify/maven/"

libraryDependencies ++= Seq(
  "redis.clients" % "jedis" % "2.8.1",
  javaWs,
  filters,
  "org.assertj" % "assertj-core" % "3.4.1",
  "io.mikael" % "urlbuilder" % "2.0.7",
  "org.assertj" % "assertj-core" % "3.5.2" % "test",
  "com.typesafe.play" % "play-java-jdbc_2.11" % "2.5.8",
  "org.flywaydb" % "flyway-core" % "4.0.3",
  "org.mockito" % "mockito-core" % "1.+",
  "org.postgresql" % "postgresql" % "9.4.1211.jre7",
  "uk.gov.service.notify" % "notifications-java-client" % "2.2.0-RELEASE"
)


lazy val `promptpayments` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"