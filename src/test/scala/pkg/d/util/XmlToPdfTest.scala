package pkg.d.util

import org.scalatest.funsuite.AnyFunSuite

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class XmlToPdfTest extends AnyFunSuite:

  private val projectFolder: Path = Paths.get(System.getProperty("user.dir"))
  private val testFolder: Path = projectFolder.resolve("target/xml-to-pdf-test")
  private val printsFolder: Path = projectFolder.resolve("protoflow/prints")
  private val xmlPath: Path = testFolder.resolve("test-accounts.xml")
  private val listPdfPath: Path = printsFolder.resolve("test-accounts-list.pdf")
  private val detailsPdfPath: Path = printsFolder.resolve("test-account-details.pdf")

  test("printList genera il PDF con l'elenco degli account"):
    prepareTestXml()
    Files.deleteIfExists(listPdfPath)

    val result =
      XmlToPdf.printList(
        xmlPath = xmlPath.toString,
        pdfFileName = "test-accounts-list.pdf",
        title = "Elenco account di test"
      )

    assert(result)
    assert(Files.exists(listPdfPath))
    assert(Files.isRegularFile(listPdfPath))
    assert(Files.size(listPdfPath) > 0)

    println()
    println("PDF elenco account creato in:")
    println(listPdfPath.toAbsolutePath)

  test("printDetails genera il PDF con la scheda di un account"):
    prepareTestXml()
    Files.deleteIfExists(detailsPdfPath)

    val result =
      XmlToPdf.printDetails(
        xmlPath = xmlPath.toString,
        recordId = "2",
        pdfFileName = "test-account-details.pdf",
        title = "Scheda account di test"
      )

    assert(result)
    assert(Files.exists(detailsPdfPath))
    assert(Files.isRegularFile(detailsPdfPath))
    assert(Files.size(detailsPdfPath) > 0)

    println()
    println("PDF scheda account creato in:")
    println(detailsPdfPath.toAbsolutePath)

  test("printDetails restituisce false se l'account non esiste"):
    prepareTestXml()

    val missingPdfPath = printsFolder.resolve("missing-account.pdf")

    Files.deleteIfExists(missingPdfPath)

    val result =
      XmlToPdf.printDetails(
        xmlPath = xmlPath.toString,
        recordId = "999",
        pdfFileName = "missing-account.pdf",
        title = "Account inesistente"
      )

    assert(!result)
    assert(!Files.exists(missingPdfPath))

  test("printList restituisce false se non sono presenti account"):
    Files.createDirectories(testFolder)

    val emptyXmlPath = testFolder.resolve("empty-accounts.xml")

    Files.writeString(
      emptyXmlPath,
      "<accounts></accounts>",
      StandardCharsets.UTF_8
    )

    val emptyPdfPath = printsFolder.resolve("empty-accounts.pdf")

    Files.deleteIfExists(emptyPdfPath)

    val result =
      XmlToPdf.printList(
        xmlPath = emptyXmlPath.toString,
        pdfFileName = "empty-accounts.pdf",
        title = "Elenco account vuoto"
      )

    assert(!result)
    assert(!Files.exists(emptyPdfPath))

  private def prepareTestXml(): Unit =
    Files.createDirectories(testFolder)
    Files.createDirectories(printsFolder)

    val xmlContent =
      """
        |<accounts>
        |  <record>
        |    <id>1</id>
        |    <name>Mario</name>
        |    <surname>Rossi</surname>
        |    <username>mario.rossi</username>
        |    <password>password1</password>
        |    <email>mario.rossi@example.it</email>
        |    <phone>3331111111</phone>
        |    <role>admin</role>
        |    <area>Amministrazione</area>
        |    <assignment>Responsabile</assignment>
        |  </record>
        |
        |  <record>
        |    <id>2</id>
        |    <name>Anna</name>
        |    <surname>Bianchi</surname>
        |    <username>anna.bianchi</username>
        |    <password>password2</password>
        |    <email>anna.bianchi@example.it</email>
        |    <phone>3332222222</phone>
        |    <role>oper</role>
        |    <area>Protocollo</area>
        |    <assignment>Operatrice</assignment>
        |  </record>
        |
        |  <record>
        |    <id>3</id>
        |    <name>Luca</name>
        |    <surname>Verdi</surname>
        |    <username>luca.verdi</username>
        |    <password>password3</password>
        |    <email>luca.verdi@example.it</email>
        |    <phone>3333333333</phone>
        |    <role>viewer</role>
        |    <area>Archivio</area>
        |    <assignment>Consultazione</assignment>
        |  </record>
        |</accounts>
        |""".stripMargin

    Files.writeString(
      xmlPath,
      xmlContent,
      StandardCharsets.UTF_8
    )