import sbt.*

object Dependencies {
  object Cats {
    private val catsVersion    = "2.12.0"
    private val effectVersion  = "3.5.7"
    private val taglessVersion = "0.16.3"
    private val org            = "org.typelevel"

    val core    = org %% "cats-core"         % catsVersion
    val effect  = org %% "cats-effect"       % effectVersion
    val tagless = org %% "cats-tagless-core" % taglessVersion

    val all: Seq[ModuleID] = Seq(core, effect, tagless)
  }

  object Logging {
    private val slf4jVersion        = "2.0.3"
    private val logbackVersion      = "1.4.4"
    private val catsLogVersion      = "2.5.0"
    private val scalaLoggingVersion = "3.9.5"

    val slf4jApi       = "org.slf4j"                   % "slf4j-api"       % slf4jVersion
    val logbackClassic = "ch.qos.logback"              % "logback-classic" % logbackVersion
    val logback        = "ch.qos.logback"              % "logback-core"    % logbackVersion
    val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"   % scalaLoggingVersion

    val log4cats     = "org.typelevel" %% "log4cats-core"  % catsLogVersion
    val log4catsSl4j = "org.typelevel" %% "log4cats-slf4j" % catsLogVersion

    val all: Seq[ModuleID] = Seq(slf4jApi, logback, scalaLogging, log4cats, log4catsSl4j)
  }

  object Specs {
    val scalaTest                   = "org.scalatest"     %% "scalatest"                                % "3.2.14"
    val scalaCheckPlus              = "org.scalatestplus" %% "scalacheck-1-15"                          % "3.2.11.0"
    val catsEffectTesting           = "com.codecommit"    %% "cats-effect-testing-scalatest"            % "0.5.4"
    val munitCatsEffect             = "org.typelevel"     %% "munit-cats-effect-3"                      % "1.0.7"
    val catsEffectTestingScalaCheck = "com.codecommit"    %% "cats-effect-testing-scalatest-scalacheck" % "0.5.4"
    val testLogging                 = "ch.qos.logback"     % "logback-classic"                          % "1.4.11"

    val itTests: Seq[ModuleID] = Seq(munitCatsEffect, testLogging).map(_ % Test)

    val all: Seq[ModuleID] =
      Seq(scalaTest, scalaCheckPlus, catsEffectTesting, catsEffectTestingScalaCheck, munitCatsEffect, testLogging).map(_ % Test)
  }
}
