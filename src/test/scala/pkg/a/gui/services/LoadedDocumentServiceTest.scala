package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.{LoadedDocument, RegisteredDocument}
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class LoadedDocumentServiceTest:

  private var loadedXmlFile: String = _
  private var registeredXmlFile: String = _
  private var loadedIdFile: String = _

  @Before
  def setUp(): Unit =
    loadedXmlFile = inTestFilePathName("loadedDocumentServiceTest.xml")
    registeredXmlFile = inTestFilePathName("registeredDocumentServiceTest.xml")
    loadedIdFile = inTestFilePathName("loadedDocumentServiceId")
    createEmptyXmlFile(loadedXmlFile, "test_records")
    createEmptyXmlFile(registeredXmlFile, "test_records")
    Files.deleteIfExists(Paths.get(loadedIdFile))

  @After
  def tearDown(): Unit =
    Seq(
      loadedXmlFile,
      registeredXmlFile,
      loadedIdFile
    ).foreach(file =>
      Files.deleteIfExists(Paths.get(file))
    )

  @Test
  def testAddLoadedDocument(): Unit =
    val result =
      LoadedDocumentService.addLoadedDocument(
        documentDate = "2026-09-02",
        documentProtocol = "PROT-001",
        documentType = "Email",
        sender = "ACEA",
        recipient = "Tesoreria",
        subject = "Bollette energia elettrica",
        remarks = "Urgente",
        operatorUsername = "rossi",
        loadedFilePathName = loadedXmlFile,
        loadedDocumentIdFilePathName = loadedIdFile
      )

    assertTrue(result.isRight)

    val document = result.toOption.get
    assertEquals("PROT-001", document.getDocumentProtocol)
    assertEquals("ACEA", document.getSender)
    assertEquals("rossi", document.getProcessedBy)
    assertEquals(document, LoadedDocument().getRecordById[LoadedDocument](document.getId, loadedXmlFile))

  @Test
  def testRegisterDocumentWithoutClassification(): Unit =
    val source = testLoadedDocument()

    val result =
      LoadedDocumentService.registerDocument(
        source = source,
        operatorUsername = "rossi",
        classification = "   ",
        loadedFilePathName = loadedXmlFile,
        registeredFilePathName = registeredXmlFile
      )
    assertEquals(Left("Seleziona una classifica"), result)

  @Test
  def testRegisterDocument(): Unit =
    val source = testLoadedDocument()
    LoadedDocument().recordInsert(source, loadedXmlFile)
    val result =
      LoadedDocumentService.registerDocument(
        source = source,
        operatorUsername = "rossi",
        classification = "Amministrazione",
        loadedFilePathName = loadedXmlFile,
        registeredFilePathName = registeredXmlFile
      )

    assertTrue(result.isRight)
    val registered = result.toOption.get
    assertEquals("Amministrazione", registered.getClassification)
    assertEquals("rossi", registered.getRegisteredBy)
    assertEquals(RegisteredDocument().getRecordById[RegisteredDocument](source.getId, registeredXmlFile), registered)
    assertEquals(LoadedDocument(), LoadedDocument().getRecordById[LoadedDocument](source.getId, loadedXmlFile))

  private def testLoadedDocument(): LoadedDocument =
    LoadedDocument(
      id = "1",
      documentDate = "2026-09-02",
      documentProtocol = "PROT-001",
      documentType = "Email",
      sender = "ACEA",
      recipient = "Tesoreria",
      subject = "Bollette energia elettrica",
      processedDate = "2026-09-02",
      processedTime = "10:30:00",
      processedBy = "bianchi"
    )