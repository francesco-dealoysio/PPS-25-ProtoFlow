ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "PPS-25-ProtoFlow"
  )

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "com.github.librepdf" % "openpdf" % "3.0.5",
  "junit" % "junit" % "4.13.2" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test
