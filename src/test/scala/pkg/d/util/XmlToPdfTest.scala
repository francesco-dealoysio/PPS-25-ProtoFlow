package pkg.d.util

import org.junit.*
import org.junit.Assert.*
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class XmlToPdfTest:

  private val projectFolder: Path = Paths.get(System.getProperty("user.dir"))
  private val testFolder: Path = projectFolder.resolve("target/xml-to-pdf-test")
  private val printsFolder: Path = projectFolder.resolve("protoflow/prints")
  private val xmlPath: Path = testFolder.resolve("test-accounts.xml")
  private val listPdfPath: Path = printsFolder.resolve("test-accounts-list.pdf")
  private val detailsPdfPath: Path = printsFolder.resolve("test-account-details.pdf")

  @Test
  def testPrintListGeneratesPdf(): Unit =
    prepareTestXml()
    Files.deleteIfExists(listPdfPath)

    val result = XmlToPdf.printList(xmlPath.toString, "test-accounts-list.pdf", "Elenco account di test")

    assertTrue(result)
    assertTrue(Files.exists(listPdfPath))
    assertTrue(Files.isRegularFile(listPdfPath))
    assertTrue(Files.size(listPdfPath) > 0)

    println(s"\nPDF elenco account creato in:\n${listPdfPath.toAbsolutePath}")

  @Test
  def testPrintDetailsGeneratesPdf(): Unit =
    prepareTestXml()
    Files.deleteIfExists(detailsPdfPath)

    val result = XmlToPdf.printDetails(xmlPath.toString, "2", "test-account-details.pdf", "Scheda account di test")

    assertTrue(result)
    assertTrue(Files.exists(detailsPdfPath))
    assertTrue(Files.isRegularFile(detailsPdfPath))
    assertTrue(Files.size(detailsPdfPath) > 0)

    println(s"\nPDF scheda account creato in:\n${detailsPdfPath.toAbsolutePath}")

  @Test
  def testPrintDetailsReturnsFalseIfAccountDoesNotExist(): Unit =
    prepareTestXml()
    val missingPdfPath = printsFolder.resolve("missing-account.pdf")
    Files.deleteIfExists(missingPdfPath)

    val result = XmlToPdf.printDetails(xmlPath.toString, "999", "missing-account.pdf", "Account inesistente")

    assertFalse(result)
    assertFalse(Files.exists(missingPdfPath))

  @Test
  def testPrintListReturnsFalseIfNoAccountsExist(): Unit =
    Files.createDirectories(testFolder)
    val emptyXmlPath = testFolder.resolve("empty-accounts.xml")
    Files.writeString(emptyXmlPath, "<accounts></accounts>", StandardCharsets.UTF_8)

    val emptyPdfPath = printsFolder.resolve("empty-accounts.pdf")
    Files.deleteIfExists(emptyPdfPath)

    val result = XmlToPdf.printList(emptyXmlPath.toString, "empty-accounts.pdf", "Elenco account vuoto")

    assertFalse(result)
    assertFalse(Files.exists(emptyPdfPath))

  private def prepareTestXml(): Unit =
    Files.createDirectories(testFolder)
    Files.createDirectories(printsFolder)

    val xmlContent =
      """<accounts>
        |  <record><id>1</id><name>Mario</name><surname>Rossi</surname><username>mario.rossi</username><password>password1</password><email>mario.rossi@example.it</email><phone>3331111111</phone><role>admin</role><area>Amministrazione</area><assignment>Responsabile</assignment></record>
        |  <record><id>2</id><name>Anna</name><surname>Bianchi</surname><username>anna.bianchi</username><password>password2</password><email>anna.bianchi@example.it</email><phone>3332222222</phone><role>oper</role><area>Protocollo</area><assignment>Operatrice</assignment></record>
        |  <record><id>3</id><name>Luca</name><surname>Verdi</surname><username>luca.verdi</username><password>password3</password><email>luca.verdi@example.it</email><phone>3333333333</phone><role>viewer</role><area>Archivio</area><assignment>Consultazione</assignment></record>
        |</accounts>""".stripMargin

    Files.writeString(xmlPath, xmlContent, StandardCharsets.UTF_8)