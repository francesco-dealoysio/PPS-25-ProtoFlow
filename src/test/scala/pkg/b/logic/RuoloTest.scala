package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty
import pkg.d.util.Util.md5

class RuoloTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Ruolo
  
  val ruolo1 = Ruolo(
    "1",
    "admin",
    "Amministrazione"
  )
  
  val ruolo2 = Ruolo(
    "2",
    "oper",
    "Protocollazione"
  )
  
  val ruolo3 = Ruolo(
    "3",
    "viewer",
    "Visualizzazione"
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Ruolo().getRecords("path inesistente"), Seq.empty[Ruolo])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Ruolo().getRecords(xmlFilePathName), Seq.empty[Ruolo])

  @Test
  def testGetRecordsFound: Unit =
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    Ruolo().recordInsert(ruolo2, xmlFilePathName)
    assertEquals(Ruolo().getRecords(xmlFilePathName), List(ruolo1, ruolo2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Ruolo().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Ruolo().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Ruolo().recordInsert(ruolo2, xmlFilePathName)
    assertEquals(Ruolo().getRecordById("2", xmlFilePathName), ruolo2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Ruolo().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Ruolo().recordInsert(ruolo1, "path inesistente")
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, ruolo1)

  @Test
  def testRecordInsert: Unit =
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(record, ruolo1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(record, ruolo1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateRuolo: Unit =
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(record, ruolo1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    assertEquals(Ruolo().getRecordById("1", xmlFilePathName).getDescrizione, "Amministrazione")
    val record = Ruolo().getRecordById("1")
    record.setDescrizione("Gestione")
    Ruolo().recordUpdate(record, "path inesistente")
    assertNotEquals(Ruolo().getRecordById("1", xmlFilePathName).getDescrizione, "Gestione")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = ruolo1.copy()
    record.setDescrizione("Gestione")
    Ruolo().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Ruolo().getRecordById("1", xmlFilePathName).getDescrizione, "Gestione")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    record.setDescrizione("Gestione")
    record.setId("?")
    Ruolo().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Ruolo().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(ruolo1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateRuolo: Unit =
    val readRecord = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, ruolo1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    assertEquals("Amministrazione", Ruolo().getRecordById("1", xmlFilePathName).getDescrizione)
    val record = Ruolo().getRecordById("1")
    record.setDescrizione("Gestione")
    Ruolo().recordUpdate(record, xmlFilePathName)
    assertEquals(Ruolo().getRecordById("1", xmlFilePathName).getDescrizione, "Gestione")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Ruolo().recordInsert(ruolo1, xmlFilePathName)
    Ruolo().recordInsert(ruolo2, xmlFilePathName)
    Ruolo().recordInsert(ruolo3, xmlFilePathName)
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Ruolo().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Ruolo().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    Ruolo().recordInsert(ruolo1, "path inesistente")
    val record = Ruolo().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, ruolo1)
    assertTrue(false)

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    assertTrue(false)

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    assertTrue(false)
