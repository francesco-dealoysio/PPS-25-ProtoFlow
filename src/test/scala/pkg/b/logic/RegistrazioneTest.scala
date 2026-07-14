package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty

class RegistrazioneTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Registrazione

  val registrazione1 = Registrazione(
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

  val registrazione2 = Registrazione(
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

  val registrazione3 = Registrazione(
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
    assertEquals(Registrazione().getRecords("path inesistente"), Seq.empty[Registrazione])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Registrazione().getRecords(xmlFilePathName), Seq.empty[Registrazione])

  @Test
  def testGetRecordsFound: Unit =
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    Registrazione().recordInsert(registrazione2, xmlFilePathName)
    assertEquals(Registrazione().getRecords(xmlFilePathName), List(registrazione1, registrazione2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Registrazione().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Registrazione().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Registrazione().recordInsert(registrazione2, xmlFilePathName)
    assertEquals(Registrazione().getRecordById("2", xmlFilePathName), registrazione2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Registrazione().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Registrazione().recordInsert(registrazione1, "path inesistente")
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, registrazione1)

  @Test
  def testRecordInsert: Unit =
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    assertEquals(record, registrazione1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    assertEquals(record, registrazione1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateUsername: Unit =
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    assertEquals(record, registrazione1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    assertEquals(Registrazione().getRecordById("1", xmlFilePathName).getTelefono, "06/11111111")
    val record = Registrazione().getRecordById("1")
    record.setTelefono("06/12345678")
    Registrazione().recordUpdate(record, "path inesistente")
    assertNotEquals(Registrazione().getRecordById("1", xmlFilePathName).getTelefono, "06/12345678")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = registrazione1.copy()
    record.setTelefono("06/12345678")
    Registrazione().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Registrazione().getRecordById("1", xmlFilePathName).getTelefono, "06/12345678")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    record.setTelefono("06/87654321")
    record.setId("?")
    Registrazione().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Registrazione().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Registrazione().getRecordById("1", xmlFilePathName)
    assertEquals(registrazione1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateUsername: Unit =
    val readRecord = Registrazione().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, registrazione1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    assertEquals("06/11111111", Registrazione().getRecordById("1", xmlFilePathName).getTelefono)
    val record = Registrazione().getRecordById("1")
    record.setTelefono("06/12345678")
    Registrazione().recordUpdate(record, xmlFilePathName)
    assertEquals(Registrazione().getRecordById("1", xmlFilePathName).getTelefono, "06/12345678")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Registrazione().recordInsert(registrazione1, xmlFilePathName)
    Registrazione().recordInsert(registrazione2, xmlFilePathName)
    Registrazione().recordInsert(registrazione3, xmlFilePathName)
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Registrazione().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Registrazione().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    Registrazione().recordInsert(registrazione1, "path inesistente")
    val record = Registrazione().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, registrazione1)
    assertTrue(false)

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    assertTrue(false)

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    assertTrue(false)



