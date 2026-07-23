package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.d.util.Util.inTestFilePathName
import java.nio.file.{Files, Paths}

class ErrorLogTest:

  private var xmlFilePathName: String = _
  private var errorLog1: ErrorLog = _
  private var errorLog2: ErrorLog = _
  private var errorLog3: ErrorLog = _
  private var empty: ErrorLog = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new ErrorLog

    errorLog1 = ErrorLog(
      "1",
      "2026-07-10",
      "22:19:13.86",
      "pkg.d.util.Util",
      "inTestFilePathName",
      "23",
      "messaggio 1",
      "Stack trace 1"
    )

    errorLog2 = ErrorLog(
      "2",
      "2026-07-15",
      "22:19:13.86",
      "java.io.FileInputStream",
      "open0",
      "100",
      "messaggio 2",
      "Stack trace 2"
    )

    errorLog3 = ErrorLog(
      "3",
      "2026-07-20",
      "22:19:13.86",
      "pkg.d.util.Util",
      "md5",
      "115",
      "messaggio 3",
      "Stack trace 3"
    )

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(ErrorLog().getRecords[ErrorLog]("path inesistente"), Seq.empty[ErrorLog])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(ErrorLog().getRecords[ErrorLog](xmlFilePathName), Seq.empty[ErrorLog])

  @Test
  def testGetRecordsFound: Unit =
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    ErrorLog().recordInsert(errorLog2, xmlFilePathName)
    assertEquals(ErrorLog().getRecords[ErrorLog](xmlFilePathName), List(errorLog1, errorLog2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(ErrorLog().getRecordById[ErrorLog]("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(ErrorLog().getRecordById[ErrorLog]("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    ErrorLog().recordInsert(errorLog2, xmlFilePathName)
    assertEquals(ErrorLog().getRecordById[ErrorLog]("2", xmlFilePathName), errorLog2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(ErrorLog().getRecordById[ErrorLog]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    cleanXmlFile(xmlFilePathName)
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    ErrorLog().recordInsert(errorLog2, xmlFilePathName)
    ErrorLog().recordInsert(errorLog3, xmlFilePathName)
    val sequence = Seq(errorLog1, errorLog3)
    //assertEquals(ErrorLog().getRecordsByFilter[ErrorLog](a => a.getClass == "pkg.d.util.Util", xmlFilePathName, classOf[ErrorLog]), sequence)
    assertEquals(ErrorLog().getRecordsByFilter[ErrorLog](a => a.getClass == "pkg.d.util.Util", xmlFilePathName), sequence)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    assertFalse(ErrorLog().recordInsert(errorLog1, "path inesistente"))

  @Test
  def testRecordInsert: Unit =
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    val record = ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName)
    assertEquals(record, errorLog1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    cleanXmlFile(xmlFilePathName)
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    val record = errorLog1.copy()
    assertFalse(ErrorLog().recordInsert(record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile: Unit =
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    assertEquals(ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName).getLine, "23")
    val record = errorLog1.copy()
    record.setLine("78")
    ErrorLog().recordUpdate(record, "path inesistente")
    assertNotEquals(ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName).getLine, "78")

  @Test
  def testRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = errorLog1.copy()
    record.setLine("235")
    ErrorLog().recordUpdate(record, xmlFilePathName)
    assertNotEquals(ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName).getLine, "235")

  @Test
  def testRecordUpdateInexistentId: Unit =
    val record = ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName)
    record.setLine("100")
    record.setId("?")
    ErrorLog().recordUpdate(record, xmlFilePathName)
    val recordUpdated = ErrorLog().getRecordById[ErrorLog]("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testRecordUpdate: Unit =
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    assertEquals("23", ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName).getLine)
    val record = errorLog1.copy()
    record.setLine("150")
    assertTrue(ErrorLog().recordUpdate(record, xmlFilePathName))

  @Test
  def testRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    ErrorLog().recordInsert(errorLog2, xmlFilePathName)
    ErrorLog().recordInsert(errorLog3, xmlFilePathName)
    val record = ErrorLog().getRecordById[ErrorLog]("1", xmlFilePathName)
    ErrorLog().recordDelete(record.getId, xmlFilePathName)
    assertEquals(ErrorLog().getRecordById[ErrorLog](record.getId, xmlFilePathName), empty)

  @Test
  def testRecordDeleteInesistentXmlFile: Unit =
    val record = errorLog1.copy()
    assertFalse(ErrorLog().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = errorLog1.copy()
    assertFalse(ErrorLog().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    ErrorLog().recordInsert(errorLog1, xmlFilePathName)
    ErrorLog().recordInsert(errorLog2, xmlFilePathName)
    ErrorLog().recordInsert(errorLog3, xmlFilePathName)
    val record = errorLog1.copy()
    record.setId("100")
    assertFalse(ErrorLog().recordDelete(record.getId, xmlFilePathName))
