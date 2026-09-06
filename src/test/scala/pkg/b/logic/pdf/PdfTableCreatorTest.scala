package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.*
import org.junit.Assert.*

import java.nio.file.{Files, Path, Paths}
import scala.util.Using

class PdfTableCreatorTest:

  private val testFolder: Path = Paths.get(System.getProperty("user.dir")).resolve("target/pdf-test")
  private val pdfPath: Path = testFolder.resolve("table.pdf")

  @Before
  def setUp(): Unit =
    Files.createDirectories(testFolder)
    Files.deleteIfExists(pdfPath)

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(pdfPath)

  @Test
  def testCreateTablePdf(): Unit =
    val result =
      PdfTableCreator.createTablePdf(
        pdfPathName = pdfPath.toString,
        title = "Elenco account",
        headers = Seq("Nome", "Cognome", "Email"),
        rows = Seq(
          Seq("Mario", "Rossi", "mario@test.it"),
          Seq("Luca", "Bianchi", "luca@test.it")
        )
      )

    assertTrue(result)
    assertTrue(PdfVerifier.isPdf(pdfPath.toString))

    val text = pdfText(pdfPath)

    assertTrue(text.contains("Elenco account"))
    assertTrue(text.contains("Mario"))
    assertTrue(text.contains("Rossi"))
    assertTrue(text.contains("Luca"))

  @Test
  def testTablePdfIsLandscape(): Unit =
    PdfTableCreator.createTablePdf(pdfPath.toString, "Tabella", Seq("Nome", "Cognome"), Seq(Seq("Mario", "Rossi")))

    Using.resource(PDDocument.load(pdfPath.toFile)): document =>
      val page = document.getPage(0)
      assertTrue(page.getMediaBox.getWidth > page.getMediaBox.getHeight)

  @Test
  def testCreateTablePdfReturnsFalseWithEmptyRows(): Unit =
    val result = PdfTableCreator.createTablePdf(pdfPath.toString, "Tabella", Seq("Nome", "Cognome"), Seq.empty)
    assertFalse(result)
    assertFalse(Files.exists(pdfPath))

  @Test
  def testCreateTablePdfReturnsFalseWithInvalidRow(): Unit =
    val result = PdfTableCreator.createTablePdf(pdfPath.toString, "Tabella", Seq("Nome", "Cognome"), Seq(Seq("Mario")))
    assertFalse(result)
    assertFalse(Files.exists(pdfPath))

  @Test
  def testCreateMultipageTablePdf(): Unit =
    val rows =
      (1 to 150).map: index =>
        Seq(s"Nome $index", s"Cognome $index")

    val result = PdfTableCreator.createTablePdf(pdfPath.toString, "Elenco multipagina", Seq("Nome", "Cognome"), rows)
    assertTrue(result)

    Using.resource(PDDocument.load(pdfPath.toFile)): document =>
      assertTrue(document.getNumberOfPages > 1)

  private def pdfText(path: Path): String =
    Using.resource(PDDocument.load(path.toFile)): document =>
      new PDFTextStripper().getText(document)