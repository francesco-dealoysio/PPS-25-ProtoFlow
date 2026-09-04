package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.Assert.*
import org.junit.*
import pkg.b.logic.pdf.PdfDocumentSummaryCreator.SummaryData
import java.nio.file.{Files, Path, Paths}
import scala.util.Using

class PdfDocumentSummaryCreatorTest:

  private val testFolder: Path = Paths.get(System.getProperty("user.dir")).resolve("target/pdf-test")
  private val pdfPath: Path = testFolder.resolve("summary.pdf")

  @Before
  def setUp(): Unit =
    Files.createDirectories(testFolder)
    Files.deleteIfExists(pdfPath)

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(pdfPath)

  @Test
  def testCreateSummaryPdf(): Unit =
    val result = PdfDocumentSummaryCreator.createSummaryPdf(pdfPath.toString, summaryData)
    assertTrue(result)
    assertTrue(PdfVerifier.isPdf(pdfPath.toString))
    val text = pdfText(pdfPath)

    assertTrue(text.contains("ProtoFlow"))
    assertTrue(text.contains("Riepilogo gestione documento"))
    assertTrue(text.contains("DOC-001"))
    assertTrue(text.contains("Amministrazione"))
    assertTrue(text.contains("Protocollazione"))
    assertTrue(text.contains("mario"))

  @Test
  def testCreateSummaryPdfReturnsFalseWithoutHeaders(): Unit =
    val invalidData = summaryData.copy(phaseHeaders = Seq.empty)
    val result = PdfDocumentSummaryCreator.createSummaryPdf(pdfPath.toString, invalidData)
    assertFalse(result)
    assertFalse(Files.exists(pdfPath))

  @Test
  def testCreateSummaryPdfReturnsFalseWithInvalidRow(): Unit =
    val invalidData = summaryData.copy(phaseRows = Seq(Seq("Protocollazione")))
    val result = PdfDocumentSummaryCreator.createSummaryPdf(pdfPath.toString, invalidData)
    assertFalse(result)

  @Test
  def testCreateMultipageSummaryPdf(): Unit =
    val rows =
      (1 to 100).map: index =>
        Seq(s"Fase $index", "04/09/2026 15:00", "mario", "Completata")

    val result = PdfDocumentSummaryCreator.createSummaryPdf(pdfPath.toString, summaryData.copy(phaseRows = rows))
    assertTrue(result)

    Using.resource(PDDocument.load(pdfPath.toFile)): document =>
      assertTrue(document.getNumberOfPages > 1)

  private def summaryData: SummaryData =
    SummaryData(
      applicationTitle = "ProtoFlow",
      reportTitle = "Riepilogo gestione documento",
      generatedAtLabel = "Generato il",
      generatedAt = "04/09/2026 15:00",
      documentDataSectionTitle = "Dati documento",
      documentCodeLabel = "Codice",
      documentCode = "DOC-001",
      classificationLabel = "Classifica",
      classification = "Amministrazione",
      phasesSectionTitle = "Fasi di gestione",
      phaseHeaders = Seq("Fase", "Data e ora", "Operatore", "Esito"),
      phaseRows = Seq(Seq("Protocollazione", "04/09/2026 14:30", "mario", "Completata")),
      generatedByLabel = "Generato da",
      generatedBy = "admin",
      pageLabel = "Pagina",
      logoResourcePath = "/img/message.jpg"
    )

  private def pdfText(path: Path): String =
    Using.resource(PDDocument.load(path.toFile)): document =>
      new PDFTextStripper().getText(document)