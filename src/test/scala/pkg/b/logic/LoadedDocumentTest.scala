package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.d.util.Util.inTestFilePathName
import java.nio.file.{Files, Paths}

class LoadedDocumentTest:
  
  private var xmlFilePathName: String = _
  private var loadedDocument1: LoadedDocument = _
  private var loadedDocument2: LoadedDocument = _
  private var loadedDocument3: LoadedDocument = _
  private var empty: LoadedDocument = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new LoadedDocument

    loadedDocument1 = LoadedDocument(
      "1",
      "2026-07-10",
//      "22:19:13.86",
      "2024/002342/F.D.",
      "email",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "loaded",
      "2026-07-10",
      "22:19:13.86",
      "Rossi"
    )

    loadedDocument2 = LoadedDocument(
      "2",
      "2026-07-10",
//      "22:19:13.86",
      "2024/002342/F.D.",
      "letter",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "loaded",
      "2026-07-10",
      "22:19:13.86",
      "Bianchi"
    )

    loadedDocument3 = LoadedDocument(
      "3",
      "2026-07-10",
//      "22:19:13.86",
      "2024/002342/F.D.",
      "letter",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "loaded",
      "2026-07-10",
      "22:19:13.86",
      "Neri"
    )

  @After
  def tearDown(): Unit =
    println
    //Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(LoadedDocument().getRecords[LoadedDocument]("path inesistente"), Seq.empty[LoadedDocument])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(LoadedDocument().getRecords[LoadedDocument](xmlFilePathName), Seq.empty[LoadedDocument])

  @Test
  def testGetRecordsFound: Unit =
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument2, xmlFilePathName)
    assertEquals(LoadedDocument().getRecords[LoadedDocument](xmlFilePathName), List(loadedDocument1, loadedDocument2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(LoadedDocument().getRecordById[LoadedDocument]("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(LoadedDocument().getRecordById[LoadedDocument]("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument2, xmlFilePathName)
    assertEquals(LoadedDocument().getRecordById[LoadedDocument]("2", xmlFilePathName), loadedDocument2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(LoadedDocument().getRecordById[LoadedDocument]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    cleanXmlFile(xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument2, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument3, xmlFilePathName)
    val sequence = Seq(loadedDocument2, loadedDocument3)
    assertEquals(LoadedDocument().getRecordsByFilter[LoadedDocument](a => a.getDocumentType == "letter", xmlFilePathName), sequence)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    assertFalse(LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, "path inesistente"))

  @Test
  def testRecordInsert: Unit =
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    val record = LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName)
    assertEquals(record, loadedDocument1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    cleanXmlFile(xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    val record = loadedDocument1.copy()
    assertFalse(LoadedDocument().recordInsert[LoadedDocument](record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile: Unit =
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    assertEquals(LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName).getProcessedBy, "Rossi")
    val record = LoadedDocument().getRecordById[LoadedDocument]("1")
    record.setProcessedBy("Bruni")
    LoadedDocument().recordUpdate[LoadedDocument](record, "path inesistente")
    assertNotEquals(LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName).getProcessedBy, "Bruni")

  @Test
  def testRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = loadedDocument1.copy()
    record.setSender("INPS")
    LoadedDocument().recordUpdate[LoadedDocument](record, xmlFilePathName)
    assertNotEquals(LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName).getSender, "INPS")

  @Test
  def testRecordUpdateInexistentId: Unit =
    val record = LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName)
    record.setDocumentType("package")
    record.setId("?")
    LoadedDocument().recordUpdate[LoadedDocument](record, xmlFilePathName)
    val recordUpdated = LoadedDocument().getRecordById[LoadedDocument]("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testRecordUpdate: Unit =
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    assertEquals("ACEA", LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName).getSender)
    val record = loadedDocument1.copy()
    record.setSender("SME")
    assertTrue(LoadedDocument().recordUpdate[LoadedDocument](record, xmlFilePathName))

  @Test
  def testRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument2, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument3, xmlFilePathName)
    val record = LoadedDocument().getRecordById[LoadedDocument]("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    LoadedDocument().recordDelete(record.getId, xmlFilePathName)

  @Test
  def testRecordDeleteInesistentXmlFile: Unit =
    val record = loadedDocument1.copy()
    assertFalse(LoadedDocument().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = loadedDocument1.copy()
    assertFalse(LoadedDocument().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument1, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument2, xmlFilePathName)
    LoadedDocument().recordInsert[LoadedDocument](loadedDocument3, xmlFilePathName)
    val record = loadedDocument1.copy()
    record.setId("100")
    assertFalse(LoadedDocument().recordDelete(record.getId, xmlFilePathName))
