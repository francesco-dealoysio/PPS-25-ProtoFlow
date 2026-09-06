package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class ArchivedDocumentTest:

  private var xmlFilePathName: String = _
  private var archivedDocument1: ArchivedDocument = _
  private var archivedDocument2: ArchivedDocument = _
  private var archivedDocument3: ArchivedDocument = _
  private var empty: ArchivedDocument = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test-archived.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new ArchivedDocument

    archivedDocument1 =
      ArchivedDocument(
        id = "1",
        documentDate = "2026-07-10",
        documentProtocol = "2024/002342/F.D.",
        documentType = "email",
        sender = "ACEA",
        recipient = "UNUCI/Tesoreria",
        subject = "Bollette energia elettrica",
        remarks = "Alla attenzione del Tesoriere",
        loadedDate = "2026-07-10",
        loadedTime = "22:20:00",
        loadedBy = "Rossi",
        protocolNumber = "2026/1",
        registeredDate = "2026-07-11",
        registeredTime = "09:00:00",
        registeredBy = "Bianchi",
        archivedDate = "2026-07-12",
        archivedTime = "10:00:00",
        archivedBy = "Verdi",
        archiveLocation = "Archivio A - Scaffale 2"
      )

    archivedDocument2 =
      archivedDocument1.copy(
        id = "2",
        protocolNumber = "2026/2",
        sender = "INPS",
        archiveLocation = "Archivio B - Scaffale 1"
      )

    archivedDocument3 =
      archivedDocument1.copy(
        id = "3",
        protocolNumber = "2026/3",
        documentType = "letter",
        sender = "Comune di Milano"
      )

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(xmlFilePathName))

  @Test
  def testGetRecordsInexistentXmlFile(): Unit =
    assertEquals(Seq.empty[ArchivedDocument], ArchivedDocument().getRecords[ArchivedDocument]("path inesistente"))

  @Test
  def testGetRecordsEmptyXmlFile(): Unit =
    assertEquals(Seq.empty[ArchivedDocument], ArchivedDocument().getRecords[ArchivedDocument](xmlFilePathName))

  @Test
  def testGetRecordsFound(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    ArchivedDocument().recordInsert(archivedDocument2, xmlFilePathName)
    assertEquals(List(archivedDocument1, archivedDocument2), ArchivedDocument().getRecords[ArchivedDocument](xmlFilePathName))

  @Test
  def testGetRecordByIdFound(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    val result = ArchivedDocument().getRecordById[ArchivedDocument]("1", xmlFilePathName)
    assertEquals(archivedDocument1, result)

  @Test
  def testGetRecordByIdInexistent(): Unit =
    val result = ArchivedDocument().getRecordById[ArchivedDocument]("999", xmlFilePathName)
    assertEquals(empty, result)

  @Test
  def testGetRecordsByFilter(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    ArchivedDocument().recordInsert(archivedDocument2, xmlFilePathName)
    ArchivedDocument().recordInsert(archivedDocument3, xmlFilePathName)
    val result = ArchivedDocument().getRecordsByFilter[ArchivedDocument](_.getDocumentType == "email", xmlFilePathName)
    assertEquals(Seq(archivedDocument1, archivedDocument2), result)

  @Test
  def testRecordInsert(): Unit =
    val inserted = ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    val result = ArchivedDocument().getRecordById[ArchivedDocument]("1", xmlFilePathName)
    assertTrue(inserted)
    assertEquals(archivedDocument1, result)

  @Test
  def testRecordInsertDuplicateId(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    val duplicate = archivedDocument1.copy()
    assertFalse(ArchivedDocument().recordInsert(duplicate, xmlFilePathName))

  @Test
  def testRecordUpdate(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    val updated = archivedDocument1.copy()
    updated.setArchiveLocation("Archivio C - Scaffale 5")
    assertTrue(ArchivedDocument().recordUpdate(updated, xmlFilePathName))
    val result = ArchivedDocument().getRecordById[ArchivedDocument]("1", xmlFilePathName)
    assertEquals("Archivio C - Scaffale 5", result.getArchiveLocation)

  @Test
  def testRecordDelete(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    assertTrue(ArchivedDocument().recordDelete(archivedDocument1.getId, xmlFilePathName))
    assertEquals(empty, ArchivedDocument().getRecordById[ArchivedDocument](archivedDocument1.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInexistentId(): Unit =
    ArchivedDocument().recordInsert(archivedDocument1, xmlFilePathName)
    assertFalse(ArchivedDocument().recordDelete("999", xmlFilePathName))