name := "play2-reactivemongo-mocks"

lazy val targetPlayReactiveMongoVersion = "0.11.0-SNAPSHOT"

version := targetPlayReactiveMongoVersion

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.11.1", "2.10.4")


organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.mockito"           %   "mockito-all"           % "1.9.0",
    "org.reactivemongo"     %%  "play2-reactivemongo"   % targetPlayReactiveMongoVersion,
    "com.typesafe.play"     %% "play"                   % "2.3.0"       % "provided",
    "org.specs2"            %% "specs2"                 % "2.3.12"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
				    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings


