package pkg.b.logic.pdf

import org.junit.Assert.*
import org.junit.*
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class PdfVerifierTest:

  private val testFolder: Path = Paths.get(System.getProperty("user.dir")).resolve("target/pdf-test")
  private val pdfPath = testFolder.resolve("verified.pdf")
  private val textPath = testFolder.resolve("not-pdf.txt")

  @Before
  def setUp(): Unit =
    Files.createDirectories(testFolder)

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(pdfPath)
    Files.deleteIfExists(textPath)

  @Test
  def testExistingPdfIsRecognized(): Unit =
    PdfDetailsCreator.createDetailsPdf(pdfPath.toString, "Test", Seq("Nome" -> "Mario"))
    assertTrue(PdfVerifier.isPdf(pdfPath.toString))

  @Test
  def testTextFileIsNotPdf(): Unit =
    Files.writeString(textPath, "Questo non e un PDF", StandardCharsets.UTF_8)
    assertFalse(PdfVerifier.isPdf(textPath.toString))

  @Test
  def testMissingFileIsNotPdf(): Unit =
    assertFalse(PdfVerifier.isPdf(testFolder.resolve("missing.pdf").toString))