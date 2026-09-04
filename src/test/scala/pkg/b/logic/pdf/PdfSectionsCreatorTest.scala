package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.Assert.*
import org.junit.*
import pkg.b.logic.pdf.PdfSectionsCreator.Section

import java.nio.file.{Files, Path, Paths}
import scala.util.Using

class PdfSectionsCreatorTest:

  private val testFolder: Path = Paths.get(System.getProperty("user.dir")).resolve("target/pdf-test")
  private val pdfPath: Path = testFolder.resolve("sections.pdf")

  @Before
  def setUp(): Unit =
    Files.createDirectories(testFolder)
    Files.deleteIfExists(pdfPath)

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(pdfPath)

  @Test
  def testCreateSectionsPdf(): Unit =
    val sections =
      Seq(
        Section(
          title = "Registrazioni",
          headers = Seq("Totali", "Approvate", "Rifiutate"),
          rows = Seq(Seq("10", "7", "3"))
        ),
        Section(
          title = "Documenti per mese",
          headers = Seq("Mese", "Conteggio"),
          rows = Seq(Seq("2026-07", "5"), Seq("2026-08", "8"))
        )
      )

    val result = PdfSectionsCreator.createSectionsPdf(pdfPath.toString, "Statistiche", sections)
    assertTrue(result)
    assertTrue(PdfVerifier.isPdf(pdfPath.toString))

    val text = pdfText(pdfPath)

    assertTrue(text.contains("Statistiche"))
    assertTrue(text.contains("Registrazioni"))
    assertTrue(text.contains("Documenti per mese"))
    assertTrue(text.contains("2026-07"))

  @Test
  def testCreateSectionsPdfReturnsFalseWithNoSections(): Unit =
    val result = PdfSectionsCreator.createSectionsPdf(pdfPath.toString, "Statistiche", Seq.empty)
    assertFalse(result)
    assertFalse(Files.exists(pdfPath))

  @Test
  def testCreateSectionsPdfReturnsFalseWhenAllSectionsAreEmpty(): Unit =
    val result = PdfSectionsCreator.createSectionsPdf(pdfPath.toString, "Statistiche", Seq(Section("Vuota", Seq("Nome"), Seq.empty)))
    assertFalse(result)

  @Test
  def testCreateSectionsPdfReturnsFalseWithInvalidRow(): Unit =
    val result = PdfSectionsCreator.createSectionsPdf(pdfPath.toString, "Statistiche", Seq(Section("Test", Seq("Nome", "Valore"), Seq(Seq("Mario")))))
    assertFalse(result)

  @Test
  def testCreateMultipageSectionsPdf(): Unit =
    val rows =
      (1 to 100).map: index =>
        Seq(s"Utente $index", index.toString)

    val result = PdfSectionsCreator.createSectionsPdf(pdfPath.toString, "Statistiche", Seq(Section("Accessi per utente", Seq("Utente", "Accessi"), rows)))
    assertTrue(result)

    Using.resource(PDDocument.load(pdfPath.toFile)): document =>
      assertTrue(document.getNumberOfPages > 1)

  private def pdfText(path: Path): String =
    Using.resource(PDDocument.load(path.toFile)): document =>
      new PDFTextStripper().getText(document)