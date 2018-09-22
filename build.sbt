import sbt.CrossVersion
import sbt.Keys.{scalacOptions, _}

val V = new {
  val scala = "2.12.4"

  val scalameta = "1.8.0"
  val scalametaParadise = "3.0.0-M11"

  val scalaLogging = "3.5.0"
  val logback = "1.1.7"

  val scalatest = "3.0.1"
}

name := "scalberto"
version := "1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := V.scala,
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked", // Enable additional warnings where generated code depends on assumptions
    "-language:higherKinds",
    "-language:reflectiveCalls",
    "-Ywarn-numeric-widen", // Warn when numerics are widened
    "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are are unused
    "-Ywarn-unused-import", // Warn when imports are unused
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused
  ),
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,
    "ch.qos.logback" % "logback-classic" % V.logback % "test",
    "org.scalatest" %% "scalatest" % V.scalatest % Test
  )
)

lazy val macroParadiseSettings = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % V.scalametaParadise cross CrossVersion.full),
  scalacOptions ++= Seq("-language:experimental.macros", "-Xplugin-require:macroparadise"),
  scalacOptions in(Compile, console) ~= (_ filterNot (_ contains "paradise")), // macroparadise plugin doesn't work in repl
)

lazy val macroDefSettings = macroParadiseSettings ++ Seq(
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % V.scala,
    "org.scalameta" %% "scalameta" % V.scalameta
  ),
  scalacOptions ++= Seq(
    "-Dscalberto.macro.debug=true"
  )
)

lazy val core = project // (project in file("core"))
  .settings(commonSettings)

lazy val macros = project // (project in file("macros"))
  .dependsOn(core)
  .settings(commonSettings, macroDefSettings)

lazy val tests = project //(project in file("tests"))
  .dependsOn(core, macros)
  .settings(commonSettings, macroParadiseSettings)

lazy val all = (project in file(".")).aggregate(core, macros, tests)
