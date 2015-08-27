name := "play2-reactivemongo-mocks"

val mocksVersion = s"${sys.props.getOrElse("build.majorMinor", "0.3")}.${sys.props.getOrElse("build.version", "SNAPSHOT")}"

val targetPlayReactiveMongoVersion = "0.10.5.0.akka23"

version := targetPlayReactiveMongoVersion + "_" + mocksVersion

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.10.5")

publishArtifact in (Compile, packageDoc) := false

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.mockito"           %   "mockito-all"           % "1.9.0",
    "org.reactivemongo"     %%  "play2-reactivemongo"   % targetPlayReactiveMongoVersion,
    "com.typesafe.play"     %% "play"                   % "2.3.8"       % "provided",
    "org.specs2"            %% "specs2"                 % "2.3.12"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
				    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

seq(bintraySettings:_*)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalariformSettings



