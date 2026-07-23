package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class RegistrationTest:

  private var xmlFilePathName: String = _
  private var registration1: Registration = _
  private var registration2: Registration = _
  private var registration3: Registration = _
  private var empty: Registration = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new Registration

    registration1 = Registration(
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

    registration2 = Registration(
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

    registration3 = Registration(
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

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Registration().getRecords[Registration]("path inesistente"), Seq.empty[Registration])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Registration().getRecords[Registration](xmlFilePathName), Seq.empty[Registration])

  @Test
  def testGetRecordsFound: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    Registration().recordInsert(registration2, xmlFilePathName)
    assertEquals(Registration().getRecords[Registration](xmlFilePathName), List(registration1, registration2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Registration().getRecordById[Registration]("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Registration().getRecordById[Registration]("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Registration().recordInsert(registration2, xmlFilePathName)
    assertEquals(Registration().getRecordById[Registration]("2", xmlFilePathName), registration2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Registration().getRecordById[Registration]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    cleanXmlFile(xmlFilePathName)
    Registration().recordInsert(registration1, xmlFilePathName)
    Registration().recordInsert(registration2, xmlFilePathName)
    Registration().recordInsert(registration3, xmlFilePathName)
    val sequence = Seq(registration1, registration3)
    assertEquals(Registration().getRecordsByFilter[Registration](a => a.getArea == "segreteria", xmlFilePathName), sequence)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    assertFalse(Registration().recordInsert(registration1, "path inesistente"))

  @Test
  def testRecordInsert: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    val record = Registration().getRecordById[Registration]("1", xmlFilePathName)
    assertEquals(record, registration1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    cleanXmlFile(xmlFilePathName)
    Registration().recordInsert(registration1, xmlFilePathName)
    val record = registration1.copy()
    assertFalse(Registration().recordInsert(record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    assertEquals(Registration().getRecordById[Registration]("1", xmlFilePathName).getPhone, "06/11111111")
    val record = Registration().getRecordById[Registration]("1")
    record.setPhone("06/12345678")
    Registration().recordUpdate(record, "path inesistente")
    assertNotEquals(Registration().getRecordById[Registration]("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = registration1.copy()
    record.setPhone("06/12345678")
    Registration().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Registration().getRecordById[Registration]("1", xmlFilePathName).getPhone, "06/12345678")

  @Test
  def testRecordUpdateInexistentId: Unit =
    val record = Registration().getRecordById[Registration]("1", xmlFilePathName)
    record.setPhone("06/87654321")
    record.setId("?")
    Registration().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Registration().getRecordById[Registration]("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testRecordUpdate: Unit =
    Registration().recordInsert(registration1, xmlFilePathName)
    assertEquals("06/11111111", Registration().getRecordById[Registration]("1", xmlFilePathName).getPhone)
    val record = registration1.copy()
    record.setPhone("06/12345678")
    assertTrue(Registration().recordUpdate(record, xmlFilePathName))

  @Test
  def testRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Registration().recordInsert(registration1, xmlFilePathName)
    Registration().recordInsert(registration2, xmlFilePathName)
    Registration().recordInsert(registration3, xmlFilePathName)
    val record = Registration().getRecordById[Registration]("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Registration().recordDelete(record.getId, xmlFilePathName)
    //assertEquals(Registration().getRecordById[Registration](record.getId, xmlFilePathName), empty)

  @Test
  def testRecordDeleteInesistentXmlFile: Unit =
    val record = registration1.copy()
    assertFalse(Registration().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = registration1.copy()
    assertFalse(Registration().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    Registration().recordInsert(registration1, xmlFilePathName)
    Registration().recordInsert(registration2, xmlFilePathName)
    Registration().recordInsert(registration3, xmlFilePathName)
    val record = registration1.copy()
    record.setId("100")
    assertFalse(Registration().recordDelete(record.getId, xmlFilePathName))
