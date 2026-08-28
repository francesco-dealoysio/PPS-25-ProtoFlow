ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "PPS-25-ProtoFlow"
  )

Compile / mainClass := Some("pkg.RunApp")

Compile / packageBin / mappings ~= {
  _.filterNot { case (_, pathInJar) =>
    pathInJar.equalsIgnoreCase("META-INF/MANIFEST.MF")
  }
}

Test / parallelExecution := false

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "com.github.librepdf" % "openpdf" % "3.0.5",
  "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0",
  "org.apache.pdfbox" % "pdfbox" % "3.0.8",
  "junit" % "junit" % "4.13.2" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test

libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "2.0.30"
//libraryDependencies += "org.apache.pdfbox" % "pdfbox-tools" % "2.0.30" // For PDFPagePanel

//libraryDependencies += "org.icepdf.os" % "icepdf-core" % "7.2.2"
//libraryDependencies += "org.icepdf.os" % "icepdf-viewer" % "7.2.2"

//libraryDependencies += "org.icepdf.os" % "icepdf-core" % "7.1.0"   // Core PDF rendering
//libraryDependencies += "org.icepdf.os" % "icepdf-viewer" % "7.1.0"  // Optional Swing viewer

//libraryDependencies += "org.icepdf.os" % "icepdf-core" % "7.2.0"