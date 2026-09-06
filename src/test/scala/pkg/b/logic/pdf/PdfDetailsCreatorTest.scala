package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.*
import org.junit.Assert.*

import java.nio.file.{Files, Path, Paths}
import scala.util.Using

class PdfDetailsCreatorTest:

  private val testFolder: Path = Paths.get(System.getProperty("user.dir")).resolve("target/pdf-test")
  private val pdfPath: Path = testFolder.resolve("details.pdf")
  private val secondPdfPath: Path = testFolder.resolve("details-second.pdf")

  @Before
  def setUp(): Unit =
    Files.createDirectories(testFolder)
    Files.deleteIfExists(pdfPath)
    Files.deleteIfExists(secondPdfPath)

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(pdfPath)
    Files.deleteIfExists(secondPdfPath)

  @Test
  def testCreateDetailsPdf(): Unit =
    val result =
      PdfDetailsCreator.createDetailsPdf(
        pdfPath.toString,
        "Scheda account",
        Seq(
          "Nome" -> "Mario",
          "Cognome" -> "Rossi",
          "Email" -> "mario.rossi@test.it"
        )
      )

    assertTrue(result)
    assertTrue(Files.exists(pdfPath))
    assertTrue(PdfVerifier.isPdf(pdfPath.toString))
    val text = pdfText(pdfPath)
    assertTrue(text.contains("Scheda account"))
    assertTrue(text.contains("Mario"))
    assertTrue(text.contains("Rossi"))
    assertTrue(text.contains("mario.rossi@test.it"))

  @Test
  def testCreateTwoDetailsPdfsInSameSession(): Unit =
    val first = PdfDetailsCreator.createDetailsPdf(pdfPath.toString, "Prima scheda", Seq("Nome" -> "Mario"))
    val second = PdfDetailsCreator.createDetailsPdf(secondPdfPath.toString, "Seconda scheda", Seq("Nome" -> "Luca"))
    assertTrue(first)
    assertTrue(second)
    assertTrue(PdfVerifier.isPdf(pdfPath.toString))
    assertTrue(PdfVerifier.isPdf(secondPdfPath.toString))

  @Test
  def testCreateDetailsPdfReturnsFalseWithEmptyPath(): Unit =
    val result = PdfDetailsCreator.createDetailsPdf("", "Scheda", Seq("Nome" -> "Mario"))
    assertFalse(result)

  private def pdfText(path: Path): String =
    Using.resource(PDDocument.load(path.toFile)): document =>
      new PDFTextStripper().getText(document)
