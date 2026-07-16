package pkg.d.util

import org.scalatest.funsuite.AnyFunSuite

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class XmlToPdfTest extends AnyFunSuite:

  private val testFolder: Path =
    Paths.get(
      System.getProperty("user.dir"),
      "target",
      "xml-to-pdf-test"
    )

  private val xmlPath: Path =
    testFolder.resolve("test-classifications.xml")

  private val printsFolder: Path =
    Paths.get(
      System.getProperty("user.dir"),
      "src",
      "main",
      "resources",
      "prints"
    )

  private val pdfPath: Path =
    printsFolder.resolve("test-classifications.pdf")

  test("deve generare un PDF visibile nella cartella resources prints"):

    Files.createDirectories(testFolder)
    Files.createDirectories(printsFolder)

    val xmlContent =
      """
        |<classifications>
        |  <record>
        |    <id>1</id>
        |    <classification>Amministrazione</classification>
        |    <description>Documenti amministrativi</description>
        |  </record>
        |
        |  <record>
        |    <id>2</id>
        |    <classification>Personale</classification>
        |    <description>Gestione del personale</description>
        |  </record>
        |
        |  <record>
        |    <id>3</id>
        |    <classification>Informatica</classification>
        |    <description>Gestione dei servizi informatici</description>
        |  </record>
        |</classifications>
        |""".stripMargin

    Files.writeString(
      xmlPath,
      xmlContent,
      StandardCharsets.UTF_8
    )

    /*
     * Elimina il PDF precedente, in modo da verificare
     * che sia realmente il test a ricrearlo.
     */
    Files.deleteIfExists(pdfPath)

    val result =
      XmlToPdf.print(
        xmlPath = xmlPath.toString,
        pdfFileName = "test-classifications.pdf",
        title = "Test stampa classifiche"
      )

    assert(result)
    assert(Files.exists(pdfPath))
    assert(Files.isRegularFile(pdfPath))
    assert(Files.size(pdfPath) > 0)

    println()
    println("PDF di test creato correttamente:")
    println(pdfPath.toAbsolutePath)