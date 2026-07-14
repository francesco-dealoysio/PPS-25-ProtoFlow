package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty

class RegistrationTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Registration

  val registration1 = Registration(
    "1",
    "neri",
    "paolo",
    "paolo.neri@gmail.com",
    "06/11111111",
    "admin",
    "segreteria",
    "segretario",
    "13/07/2026",
    "",
    "",
    ""
  )

  val registration2 = Registration(
    "2",
    "rossi",
    "mario",
    "mario.rossi@gmail.com",
    "06/22222222",
    "oper",
    "amministrazione",
    "contabile",
    "05/06/2026",
    "",
    "",
    ""
  )

  val registration3 = Registration(
    "3",
    "bianchi",
    "giovanni",
    "giovanni.bianchi@gmail.com",
    "06/33333333",
    "viewer",
    "segreteria",
    "assistente",
    "condor",
    "",
    "",
    ""
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Registration().getRecords("path inesistente"), Seq.empty[Registration])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Registration().getRecords(xmlFilePathName), Seq.empty[Registration])

  @Test
  def testGetRecordsFound: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    Registration().recordInsert(registration2, xmlFilePathName)
    assertEquals(Registration().getRecords(xmlFilePathName), List(registration1, registration2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Registration().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Registration().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Registration().recordInsert(registration2, xmlFilePathName)
    assertEquals(Registration().getRecordById("2", xmlFilePathName), registration2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Registration().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Registration().recordInsert(registration1, "path inesistente")
    val record = Registration().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, registration1)

  @Test
  def testRecordInsert: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    val record = Registration().getRecordById("1", xmlFilePathName)
    assertEquals(record, registration1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    val record = Registration().getRecordById("1", xmlFilePathName)
    assertEquals(record, registration1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateUsername: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    val record = Registration().getRecordById("1", xmlFilePathName)
    assertEquals(record, registration1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    assertEquals(Registration().getRecordById("1", xmlFilePathName).getTelefono, "06/11111111")
    val record = Registration().getRecordById("1")
    record.setTelefono("06/12345678")
    Registration().recordUpdate(record, "path inesistente")
    assertNotEquals(Registration().getRecordById("1", xmlFilePathName).getTelefono, "06/12345678")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = registration1.copy()
    record.setTelefono("06/12345678")
    Registration().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Registration().getRecordById("1", xmlFilePathName).getTelefono, "06/12345678")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Registration().getRecordById("1", xmlFilePathName)
    record.setTelefono("06/87654321")
    record.setId("?")
    Registration().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Registration().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Registration().getRecordById("1", xmlFilePathName)
    assertEquals(registration1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateUsername: Unit =
    val readRecord = Registration().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, registration1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    assertEquals("06/11111111", Registration().getRecordById("1", xmlFilePathName).getTelefono)
    val record = Registration().getRecordById("1")
    record.setTelefono("06/12345678")
    Registration().recordUpdate(record, xmlFilePathName)
    assertEquals(Registration().getRecordById("1", xmlFilePathName).getTelefono, "06/12345678")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Registration().recordInsert(registration1, xmlFilePathName)
    Registration().recordInsert(registration2, xmlFilePathName)
    Registration().recordInsert(registration3, xmlFilePathName)
    val record = Registration().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Registration().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Registration().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    Registration().recordInsert(registration1, "path inesistente")
    val record = Registration().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, registration1)
    assertTrue(false)

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    assertTrue(false)

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    assertTrue(false)



