package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
import pkg.d.util.Util.{inTestFilePathName, cipher}

class AccountTest:

  private var xmlFilePathName: String = _
  private var empty: Account = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new Account

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
    cipher("topolino")
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
    cipher("pippo")
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
    cipher("paperino")
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Account().getRecords[Account]("path inesistente"), Seq.empty[Account])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Account().getRecords[Account](xmlFilePathName), Seq.empty[Account])

  @Test
  def testGetRecordsFound: Unit =
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    assertEquals(Account().getRecords[Account](xmlFilePathName), Seq(account1, account2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Account().getRecordById[Account]("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Account().getRecordById[Account]("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Account().recordInsert[Account](account2, xmlFilePathName)
    assertEquals(Account().getRecordById[Account]("2", xmlFilePathName), account2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Account().getRecordById[Account]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    Account().recordInsert[Account](account3, xmlFilePathName)
    val record = account1.copy()
    record.setId("4")
    record.setRole("admin")
    record.setUsername("spider")
    Account().recordInsert[Account](record, xmlFilePathName)
    assertEquals(Account().getRecordsByFilter[Account](a => a.getRole == "admin", xmlFilePathName), Seq(account1, record))

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    assertFalse(Account().recordInsert[Account](account1, "path inesistente"))

  @Test
  def testRecordInsert: Unit =
    Account().recordInsert[Account](account1, xmlFilePathName)
    val record = Account().getRecordById[Account]("1", xmlFilePathName)
    assertEquals(record, account1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    Account().recordInsert[Account](account3, xmlFilePathName)
    val record = account1.copy()
    record.setUsername("sconosciuto")
    assertFalse(Account().recordInsert[Account](record, xmlFilePathName))

  @Test
  def testRecordInsertDuplicateUsername: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    Account().recordInsert[Account](account3, xmlFilePathName)
    val record = account1.copy()
    record.setId("100")
    assertFalse(Account().recordInsert[Account](record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile: Unit =
    Account().recordInsert[Account](account1, xmlFilePathName)
    val record = account1.copy()
    record.setPhone("06/12345678")
    Account().recordUpdate[Account](record, "path inesistente")
    assertNotEquals(Account().getRecordById[Account]("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = account1.copy()
    record.setPhone("06/12345678")
    Account().recordUpdate[Account](record, xmlFilePathName)
    assertNotEquals(Account().getRecordById[Account]("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testRecordUpdateInexistentId: Unit =
    val record = Account().getRecordById[Account]("1", xmlFilePathName)
    record.setPhone("06/87654321")
    record.setId("?")
    Account().recordUpdate[Account](record, xmlFilePathName)
    val recordUpdated = Account().getRecordById[Account]("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testRecordUpdateDuplicateUsername: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    Account().recordInsert[Account](account3, xmlFilePathName)
    val record = account1.copy()
    record.setUsername(account2.getUsername)
    assertFalse(Account().recordUpdate[Account](record, xmlFilePathName))

  @Test
  def testRecordUpdate: Unit =
    Account().recordInsert[Account](account1, xmlFilePathName)
    assertEquals("06/11111111", Account().getRecordById[Account]("1", xmlFilePathName).getPhone)
    val record = account1.copy()
    record.setPhone("06/12345678")
    assertTrue(Account().recordUpdate[Account](record, xmlFilePathName))

  @Test
  def testRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    Account().recordInsert[Account](account3, xmlFilePathName)
    val record = Account().getRecordById[Account]("1", xmlFilePathName)
    Account().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Account().getRecordById[Account](record.getId, xmlFilePathName), empty)

  @Test
  def testRecordDeleteInesistentXmlFile: Unit =
    val record = account1.copy()
    assertFalse(Account().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = account1.copy()
    assertFalse(Account().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert[Account](account1, xmlFilePathName)
    Account().recordInsert[Account](account2, xmlFilePathName)
    Account().recordInsert[Account](account3, xmlFilePathName)
    val record = account1.copy()
    record.setId("100")
    assertFalse(Account().recordDelete(record.getId, xmlFilePathName))

