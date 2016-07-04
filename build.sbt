name := "play2-reactivemongo-mocks"

val mocksVersion = s"${sys.props.getOrElse("build.majorMinor", "0.7")}.${sys.props.getOrElse("build.version", "SNAPSHOT")}"

val targetPlayReactiveMongoVersion = "0.11.11"

val minimumSpecs2Version = "[3.6,)"

val minimumPlayVersion = "[2.5.0,)"


version := targetPlayReactiveMongoVersion + "_" + mocksVersion

scalaVersion := "2.11.7"

publishArtifact in (Compile, packageDoc) := false

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "ch.qos.logback"        % "logback-classic"         % "1.1.3",
    "org.mockito"           %  "mockito-all"            % "1.10.19",
    "org.reactivemongo"     %% "play2-reactivemongo"    % targetPlayReactiveMongoVersion,
    "com.typesafe.play"     %% "play"                   % minimumPlayVersion                            % "provided",
  //  "io.netty"              %  "netty"                  % "4.0.36.Final"                                % "provided",
    "org.specs2"            %% "specs2-core"            % minimumSpecs2Version							% "provided",
    "org.specs2"            %% "specs2-mock"            % minimumSpecs2Version							% "provided"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
				    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

seq(bintraySettings:_*)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalariformSettings



