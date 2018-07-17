import sbt.Keys.{scalacOptions, _}

val V = new {
  val scala = "2.12.4"
}

name := "bismuth"
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
    //"-language:existentials",
    "-Ywarn-numeric-widen", // Warn when numerics are widened
    "-Ywarn-unused", // Warn when local and private vals, vars, defs, and types are are unused
    "-Ywarn-unused-import", // Warn when imports are unused
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused
  ),
  libraryDependencies ++= commonDependencies
)

lazy val commonDependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

lazy val core = project // (project in file("core"))
  .settings(
  commonSettings,
  scalacOptions += "-language:experimental.macros",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % V.scala
)

lazy val tests = project //(project in file("tests"))
  .dependsOn(core)
  .settings(
    commonSettings,
      scalacOptions += "-Dbismuth.macro.debug=true",
  )

lazy val all = (project in file(".")).aggregate(core, tests)
