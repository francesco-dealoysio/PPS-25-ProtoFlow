package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.text.UiText.ArchivedDocuments.Errors as ArchiveErrors
import pkg.b.logic.{ArchivedDocument, RegisteredDocument}
import pkg.c.data.Xml.{createEmptyXmlFile, insertElemIntoXML}
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class ArchivedDocumentServiceTest:

  private var registeredXmlFile: String = _
  private var archivedXmlFile: String = _

  @Before
  def setUp(): Unit =
    registeredXmlFile = inTestFilePathName("archivedServiceRegisteredTest.xml")
    archivedXmlFile = inTestFilePathName("archivedServiceTest.xml")
    createEmptyXmlFile(registeredXmlFile, "test_records")
    createEmptyXmlFile(archivedXmlFile, "test_records")

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(registeredXmlFile))
    Files.deleteIfExists(Paths.get(archivedXmlFile))

  @Test
  def testArchiveDocument(): Unit =
    val source = registeredDocument()
    insertElemIntoXML(registeredXmlFile, source)
    val result =
      ArchivedDocumentService.archiveDocument(
        source = source,
        archivedDate = "2026-09-02",
        archivedTime = "14:30:00",
        operatorUsername = "rossi",
        archiveLocation = "Archivio A - Scaffale 2",
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    assertTrue(result.isRight)
    val archived = result.toOption.get
    assertEquals(source.getId, archived.getId)
    assertEquals(source.getProtocolNumber, archived.getProtocolNumber)
    assertEquals("Archivio A - Scaffale 2", archived.getArchiveLocation)
    assertEquals("rossi", archived.getArchivedBy)
    assertEquals(archived, ArchivedDocument().getRecordById[ArchivedDocument](source.getId, archivedXmlFile))
    assertEquals(RegisteredDocument(), RegisteredDocument().getRecordById[RegisteredDocument](source.getId, registeredXmlFile))

  @Test
  def testArchiveDocumentWithoutLocation(): Unit =
    val source = registeredDocument()

    val result =
      ArchivedDocumentService.archiveDocument(
        source = source,
        archivedDate = "2026-09-02",
        archivedTime = "14:30:00",
        operatorUsername = "rossi",
        archiveLocation = "",
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    assertEquals(Left(ArchiveErrors.ArchiveLocationRequired), result)

  @Test
  def testAlreadyArchivedDocument(): Unit =
    val source = registeredDocument()

    val alreadyArchived =
      ArchivedDocument(
        id = source.getId,
        documentDate = source.getDocumentDate,
        documentProtocol = source.getDocumentProtocol,
        documentType = source.getDocumentType,
        sender = source.getSender,
        recipient = source.getRecipient,
        subject = source.getSubject,
        remarks = source.getRemarks,
        loadedDate = source.getLoadedDate,
        loadedTime = source.getLoadedTime,
        loadedBy = source.getLoadedBy,
        protocolNumber = source.getProtocolNumber,
        registeredDate = source.getRegisteredDate,
        registeredTime = source.getRegisteredTime,
        registeredBy = source.getRegisteredBy,
        archivedDate = "2026-09-01",
        archivedTime = "10:00:00",
        archivedBy = "bianchi",
        archiveLocation = "Archivio A",
        classification = source.getClassification
      )

    insertElemIntoXML(archivedXmlFile, alreadyArchived)

    val result =
      ArchivedDocumentService.archiveDocument(
        source = source,
        archivedDate = "2026-09-02",
        archivedTime = "14:30:00",
        operatorUsername = "rossi",
        archiveLocation = "Archivio B",
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    assertEquals(Left("Il documento risulta già archiviato"), result)

  private def registeredDocument(): RegisteredDocument =
    RegisteredDocument(
      id = "1",
      documentDate = "2026-08-30",
      documentProtocol = "PROT-001",
      documentType = "Email",
      sender = "ACEA",
      recipient = "Tesoreria",
      subject = "Bollette energia elettrica",
      loadedDate = "2026-08-30",
      loadedTime = "09:00:00",
      loadedBy = "verdi",
      protocolNumber = "2026/1/AMM",
      registeredDate = "2026-08-31",
      registeredTime = "10:00:00",
      registeredBy = "bianchi",
      classification = "AMM"
    )