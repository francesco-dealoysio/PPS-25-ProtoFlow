ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "PPS-25-ProtoFlow"
  )

Compile / run / mainClass := Some("pkg.RunApp")
Compile / packageBin / mainClass := Some("pkg.RunApp")

Test / parallelExecution := false
Test / test := (Test / testOnly).toTask(" pkg.AllTestsSuite").value

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "org.apache.pdfbox" % "pdfbox" % "2.0.30",
  "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0",
  "junit" % "junit" % "4.13.2" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)