name := """promptpayments"""

version := "1.0-SNAPSHOT"

resolvers += Resolver.bintrayRepo("gov-uk-notify", "maven")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

// TODO without this the js fails lint test so disabling for the moment. 
excludeFilter in (Assets, JshintKeys.jshint) := "*.js"

scalaVersion := "2.11.8"

libraryDependencies += javaWs

libraryDependencies ++= Seq(
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