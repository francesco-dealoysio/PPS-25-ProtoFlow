package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty
import pkg.d.util.Util.md5

class AccountTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Account

  val account1 = Account(
    "1",
    "de aloysio",
    "francesco",
    "francesco.dealoysio@studio.unibo.it",
    "06/11111111",
    "admin",
    "presidenza",
    "presidente",
    "frank",
    md5("topolino")
  )

  val account2 = Account(
    "2",
    "rossi",
    "mario",
    "mario.rossi@gmail.com",
    "06/22222222",
    "oper",
    "amministrazione",
    "contabile",
    "rosma",
    md5("pippo")
  )

  val account3 = Account(
    "3",
    "bianchi",
    "giovanni",
    "giovanni.bianchi@gmail.com",
    "06/33333333",
    "viewer",
    "segreteria",
    "assistente",
    "condor",
    md5("paperino")
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Account().getRecords("path inesistente"), Seq.empty[Account])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Account().getRecords(xmlFilePathName), Seq.empty[Account])

  @Test
  def testGetRecordsFound: Unit =
    Account().recordInsert(account1, xmlFilePathName)
    Account().recordInsert(account2, xmlFilePathName)
    assertEquals(Account().getRecords(xmlFilePathName), List(account1, account2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Account().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Account().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Account().recordInsert(account2, xmlFilePathName)
    assertEquals(Account().getRecordById("2", xmlFilePathName), account2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Account().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Account().recordInsert(account1, "path inesistente")
    val record = Account().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, account1)

  @Test
  def testRecordInsert: Unit =
    Account().recordInsert(account1, xmlFilePathName)
    val record = Account().getRecordById("1", xmlFilePathName)
    assertEquals(record, account1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert(account1, xmlFilePathName)
    Account().recordInsert(account2, xmlFilePathName)
    Account().recordInsert(account3, xmlFilePathName)
    val record = account1.copy()
    record.setUsername("sconosciuto")
    assertFalse(Account().recordInsert(record, xmlFilePathName))

  @Test
  def testRecordInsertDuplicateUsername: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert(account1, xmlFilePathName)
    Account().recordInsert(account2, xmlFilePathName)
    Account().recordInsert(account3, xmlFilePathName)
    val record = account1.copy()
    record.setId("100")
    assertFalse(Account().recordInsert(record, xmlFilePathName))

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Account().recordInsert(account1, xmlFilePathName)
    assertEquals(Account().getRecordById("1", xmlFilePathName).getPhone, "06/11111111")
    val record = Account().getRecordById("1")
    record.setPhone("06/12345678")
    Account().recordUpdate(record, "path inesistente")
    assertNotEquals(Account().getRecordById("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = account1.copy()
    record.setPhone("06/12345678")
    Account().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Account().getRecordById("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Account().getRecordById("1", xmlFilePathName)
    record.setPhone("06/87654321")
    record.setId("?")
    Account().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Account().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Account().getRecordById("1", xmlFilePathName)
    assertEquals(account1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateUsername: Unit =
    val readRecord = Account().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, account1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Account().recordInsert(account1, xmlFilePathName)
    assertEquals("06/11111111", Account().getRecordById("1", xmlFilePathName).getPhone)
    val record = Account().getRecordById("1")
    record.setPhone("06/12345678")
    Account().recordUpdate(record, xmlFilePathName)
    assertEquals(Account().getRecordById("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert(account1, xmlFilePathName)
    Account().recordInsert(account2, xmlFilePathName)
    Account().recordInsert(account3, xmlFilePathName)
    val record = Account().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    assertTrue(Account().recordDelete(record.getId, xmlFilePathName))
    assertEquals(Account().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    val record = account1.copy()
    assertFalse(Account().recordDelete(record.getId, "path inesistente"))

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = account1.copy()
    assertFalse(Account().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert(account1, xmlFilePathName)
    Account().recordInsert(account2, xmlFilePathName)
    Account().recordInsert(account3, xmlFilePathName)
    val record = account1.copy()
    record.setId("100")
    assertFalse(Account().recordDelete(record.getId, xmlFilePathName))



