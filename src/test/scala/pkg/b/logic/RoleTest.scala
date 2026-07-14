package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty
import pkg.d.util.Util.md5

class RoleTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Role
  
  val ruolo1 = Role(
    "1",
    "admin",
    "Amministrazione"
  )
  
  val ruolo2 = Role(
    "2",
    "oper",
    "Protocollazione"
  )
  
  val ruolo3 = Role(
    "3",
    "viewer",
    "Visualizzazione"
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Role().getRecords("path inesistente"), Seq.empty[Role])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Role().getRecords(xmlFilePathName), Seq.empty[Role])

  @Test
  def testGetRecordsFound: Unit =
    Role().recordInsert(ruolo1, xmlFilePathName)
    Role().recordInsert(ruolo2, xmlFilePathName)
    assertEquals(Role().getRecords(xmlFilePathName), List(ruolo1, ruolo2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Role().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Role().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Role().recordInsert(ruolo2, xmlFilePathName)
    assertEquals(Role().getRecordById("2", xmlFilePathName), ruolo2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Role().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Role().recordInsert(ruolo1, "path inesistente")
    val record = Role().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, ruolo1)

  @Test
  def testRecordInsert: Unit =
    Role().recordInsert(ruolo1, xmlFilePathName)
    val record = Role().getRecordById("1", xmlFilePathName)
    assertEquals(record, ruolo1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Role().recordInsert(ruolo1, xmlFilePathName)
    val record = Role().getRecordById("1", xmlFilePathName)
    assertEquals(record, ruolo1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateRole: Unit =
    Role().recordInsert(ruolo1, xmlFilePathName)
    val record = Role().getRecordById("1", xmlFilePathName)
    assertEquals(record, ruolo1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Role().recordInsert(ruolo1, xmlFilePathName)
    assertEquals(Role().getRecordById("1", xmlFilePathName).getDescription, "Amministrazione")
    val record = Role().getRecordById("1")
    record.setDescription("Gestione")
    Role().recordUpdate(record, "path inesistente")
    assertNotEquals(Role().getRecordById("1", xmlFilePathName).getDescription, "Gestione")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = ruolo1.copy()
    record.setDescription("Gestione")
    Role().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Role().getRecordById("1", xmlFilePathName).getDescription, "Gestione")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Role().getRecordById("1", xmlFilePathName)
    record.setDescription("Gestione")
    record.setId("?")
    Role().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Role().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Role().getRecordById("1", xmlFilePathName)
    assertEquals(ruolo1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateRole: Unit =
    val readRecord = Role().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, ruolo1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Role().recordInsert(ruolo1, xmlFilePathName)
    assertEquals("Amministrazione", Role().getRecordById("1", xmlFilePathName).getDescription)
    val record = Role().getRecordById("1")
    record.setDescription("Gestione")
    Role().recordUpdate(record, xmlFilePathName)
    assertEquals(Role().getRecordById("1", xmlFilePathName).getDescription, "Gestione")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert(ruolo1, xmlFilePathName)
    Role().recordInsert(ruolo2, xmlFilePathName)
    Role().recordInsert(ruolo3, xmlFilePathName)
    val record = Role().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Role().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Role().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    Role().recordInsert(ruolo1, "path inesistente")
    val record = Role().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, ruolo1)
    assertTrue(false)

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    assertTrue(false)

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    assertTrue(false)
