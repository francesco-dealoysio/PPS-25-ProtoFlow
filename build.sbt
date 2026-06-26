ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "PPS-25-ProtoFlow"
  )

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0"
)

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
