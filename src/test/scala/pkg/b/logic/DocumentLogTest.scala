package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
import pkg.d.util.Util.inTestFilePathName
import java.nio.file.{Files, Paths}

class DocumentLogTest:

  private var xmlFilePathName: String = _
  private var documentLog1: DocumentLog = _
  private var documentLog2: DocumentLog = _
  private var documentLog3: DocumentLog = _
  private var empty: DocumentLog = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new DocumentLog

    documentLog1 = DocumentLog(
      "1",
      "3",
      "loading",
      "2026-07-10",
      "22:19:13.86",
      "Rossi"
    )

    documentLog2 = DocumentLog(
      "2",
      "3",
      "registering",
      "2026-07-10",
      "22:19:13.86",
      "Bianchi"
    )

    documentLog3 = DocumentLog(
      "3",
      "3",
      "archiving",
      "2026-07-10",
      "22:19:13.86",
      "Neri"
    )

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile(): Unit =
    assertEquals(DocumentLog().getRecords[DocumentLog]("path inesistente"), Seq.empty[DocumentLog])

  @Test
  def testGetRecordsEmptyXmlFile(): Unit =
    assertEquals(DocumentLog().getRecords[DocumentLog](xmlFilePathName), Seq.empty[DocumentLog])

  @Test
  def testGetRecordsFound(): Unit =
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog2, xmlFilePathName)
    assertEquals(DocumentLog().getRecords[DocumentLog](xmlFilePathName), List(documentLog1, documentLog2))

  @Test
  def testGetRecordByIdInexistentXmlFile(): Unit =
    assertEquals(DocumentLog().getRecordById[DocumentLog]("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile(): Unit =
    assertEquals(DocumentLog().getRecordById[DocumentLog]("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord(): Unit =
    DocumentLog().recordInsert[DocumentLog](documentLog2, xmlFilePathName)
    assertEquals(DocumentLog().getRecordById[DocumentLog]("2", xmlFilePathName), documentLog2)

  @Test
  def testGetRecordsIdInexistentId(): Unit =
    assertEquals(DocumentLog().getRecordById[DocumentLog]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter(): Unit =
    cleanXmlFile(xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog2, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog3, xmlFilePathName)
    val record = documentLog3.copy()
    record.setOperationType(documentLog1.getOperationType.toString)
    record.setId("4")
    DocumentLog().recordInsert[DocumentLog](record, xmlFilePathName)
    val sequence = Seq(documentLog1, record)
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](a => a.getOperationType == "loading", xmlFilePathName), sequence)

  @Test
  def testRecordInsertInexistentXmlFile(): Unit =
    assertFalse(DocumentLog().recordInsert[DocumentLog](documentLog1, "path inesistente"))

  @Test
  def testRecordInsert(): Unit =
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    val record = DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName)
    assertEquals(record, documentLog1)

  @Test
  def testRecordInsertDuplicateId(): Unit =
    cleanXmlFile(xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    val record = documentLog1.copy()
    assertFalse(DocumentLog().recordInsert[DocumentLog](record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile(): Unit =
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    assertEquals(DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName).getOperationType, "loading")
    val record = documentLog1.copy()
    record.setOperationType("registering")
    DocumentLog().recordUpdate[DocumentLog](record, "path inesistente")
    assertNotEquals(DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName).getOperationType, "registering")

  @Test
  def testRecordUpdateEmptyXmlFile(): Unit =
    cleanXmlFile(xmlFilePathName)
    val record = documentLog1.copy()
    record.setOperationType("registering")
    DocumentLog().recordUpdate[DocumentLog](record, xmlFilePathName)
    assertNotEquals(DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName).getOperationType, "registering")

  @Test
  def testRecordUpdateInexistentId(): Unit =
    val record = DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName)
    record.setOperationType("100")
    record.setId("?")
    DocumentLog().recordUpdate[DocumentLog](record, xmlFilePathName)
    val recordUpdated = DocumentLog().getRecordById[DocumentLog]("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testRecordUpdate(): Unit =
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    assertEquals("loading", DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName).getOperationType)
    val record = documentLog1.copy()
    record.setOperationType("registering")
    assertTrue(DocumentLog().recordUpdate[DocumentLog](record, xmlFilePathName))

  @Test
  def testRecordDelete(): Unit =
    cleanXmlFile(xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog2, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog3, xmlFilePathName)
    val record = DocumentLog().getRecordById[DocumentLog]("1", xmlFilePathName)
    DocumentLog().recordDelete(record.getId, xmlFilePathName)
    assertEquals(DocumentLog().getRecordById[DocumentLog](record.getId, xmlFilePathName), empty)

  @Test
  def testRecordDeleteInesistentXmlFile(): Unit =
    val record = documentLog1.copy()
    assertFalse(DocumentLog().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile(): Unit =
    cleanXmlFile(xmlFilePathName)
    val record = documentLog1.copy()
    assertFalse(DocumentLog().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId(): Unit =
    cleanXmlFile(xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog1, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog2, xmlFilePathName)
    DocumentLog().recordInsert[DocumentLog](documentLog3, xmlFilePathName)
    val record = documentLog1.copy()
    record.setId("100")
    assertFalse(DocumentLog().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testWriteDocumentOperationLog(): Unit =
    import pkg.b.logic.Account
    import pkg.d.util.Util.cipher
    val user = Account(
      "1",
      "Rossi",
      "Mario",
      "mario.rossi@studio.unibo.it",
      "06/12345678",
      "admin",
      "Ufficio informatica",
      "Tecnico informatico",
      "tecnico1",
      cipher("topolino")
    )
    val operationType = "loading"

    val loadedDocument = LoadedDocument(
      "1",
      "2026-07-10",
      "2024/002342/F.D.",
      "email",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "2026-07-10",
      "22:19:13.86",
      "Rossi"
    )

    assertTrue(loadedDocument.recordInsert(loadedDocument, xmlFilePathName))
