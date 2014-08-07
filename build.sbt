name := "play2-reactivemongo-mocks"

lazy val targetPlayReactiveMongoVersion = "0.11.0-SNAPSHOT"

version := targetPlayReactiveMongoVersion

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "org.mockito"         %   "mockito-all"           % "1.9.0",
    "org.reactivemongo"   %%  "play2-reactivemongo"   % targetPlayReactiveMongoVersion
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
				    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases")

jacoco.settings


