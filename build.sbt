import Dependencies.*
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

lazy val commonSettings = Seq(scalacOptions := Seq("-language:higherKinds", "-language:postfixOps", "-feature"))

lazy val core = (project in file("core"))
  .configure(_.settings(commonSettings))
  .settings(
    name := "lynx",
    organization := "codedone",
    description := "lynx",
    scalaVersion := "2.13.13",
    resolvers ++= Seq(Resolver.mavenLocal, Resolver.jcenterRepo),
    publishTo := Some(Resolver.mavenLocal),
    publishMavenStyle := true,
    libraryDependencies ++= Cats.all ++ Logging.all ++ Specs.all
  )

lazy val it = (project in file("it"))
  .configure(_.settings(commonSettings))
  .settings(
    name := "it",
    organization := "codedone",
    description := "lynx",
    scalaVersion := "2.13.13",
    publish / skip := true,
    resolvers ++= Seq(Resolver.mavenLocal, Resolver.jcenterRepo),
    libraryDependencies ++= Specs.itTests
  )
  .dependsOn(core)

lazy val root = (project in file("."))
  .aggregate(core, it)
  .configure(_.settings(commonSettings))
  .settings(
    name := "lynx",
    organization := "codedone",
    description := "lynx",
    resolvers ++= Seq(Resolver.mavenLocal, Resolver.jcenterRepo),
    publishTo := Some(Resolver.mavenLocal),
    publishMavenStyle := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion
    )
  )
