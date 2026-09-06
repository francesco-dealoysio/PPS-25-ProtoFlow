package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class AccessLogTest:

  private var xmlFilePathName: String = _
  private var accessLog1: AccessLog = _
  private var accessLog2: AccessLog = _
  private var accessLog3: AccessLog = _
  private var empty: AccessLog = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new AccessLog

    accessLog1 = AccessLog("1", "admin1", "admin", "2026-07-10", "22:19:13.86")
    accessLog2 = AccessLog("2", "oper1", "oper", "2026-07-10", "22:20:01.00")
    accessLog3 = AccessLog("3", "viewer1", "viewer", "2026-07-10", "22:21:45.12")

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile(): Unit =
    assertEquals(AccessLog().getRecords[AccessLog]("path inesistente"), Seq.empty[AccessLog])

  @Test
  def testGetRecordsEmptyXmlFile(): Unit =
    assertEquals(AccessLog().getRecords[AccessLog](xmlFilePathName), Seq.empty[AccessLog])

  @Test
  def testGetRecordsFound(): Unit =
    AccessLog().recordInsert[AccessLog](accessLog1, xmlFilePathName)
    AccessLog().recordInsert[AccessLog](accessLog2, xmlFilePathName)
    assertEquals(AccessLog().getRecords[AccessLog](xmlFilePathName), List(accessLog1, accessLog2))

  @Test
  def testGetRecordByIdFoundRecord(): Unit =
    AccessLog().recordInsert[AccessLog](accessLog2, xmlFilePathName)
    assertEquals(AccessLog().getRecordById[AccessLog]("2", xmlFilePathName), accessLog2)

  @Test
  def testGetRecordByIdInexistentId(): Unit =
    assertEquals(AccessLog().getRecordById[AccessLog]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter(): Unit =
    cleanXmlFile(xmlFilePathName)
    AccessLog().recordInsert[AccessLog](accessLog1, xmlFilePathName)
    AccessLog().recordInsert[AccessLog](accessLog2, xmlFilePathName)
    AccessLog().recordInsert[AccessLog](accessLog3, xmlFilePathName)
    assertEquals(
      AccessLog().getRecordsByFilter[AccessLog](_.getRole == "admin", xmlFilePathName),
      Seq(accessLog1)
    )

  @Test
  def testRecordInsertInexistentXmlFile(): Unit =
    assertFalse(AccessLog().recordInsert[AccessLog](accessLog1, "path inesistente"))

  @Test
  def testRecordInsert(): Unit =
    AccessLog().recordInsert[AccessLog](accessLog1, xmlFilePathName)
    assertEquals(AccessLog().getRecordById[AccessLog]("1", xmlFilePathName), accessLog1)

  @Test
  def testRecordInsertDuplicateId(): Unit =
    cleanXmlFile(xmlFilePathName)
    AccessLog().recordInsert[AccessLog](accessLog1, xmlFilePathName)
    assertFalse(AccessLog().recordInsert[AccessLog](accessLog1.copy(), xmlFilePathName))

  @Test
  def testRecordUpdate(): Unit =
    AccessLog().recordInsert[AccessLog](accessLog1, xmlFilePathName)
    val record = accessLog1.copy()
    record.setRole("oper")
    assertTrue(AccessLog().recordUpdate[AccessLog](record, xmlFilePathName))
    assertEquals("oper", AccessLog().getRecordById[AccessLog]("1", xmlFilePathName).getRole)

  @Test
  def testRecordDelete(): Unit =
    cleanXmlFile(xmlFilePathName)
    AccessLog().recordInsert[AccessLog](accessLog1, xmlFilePathName)
    AccessLog().recordDelete(accessLog1.getId, xmlFilePathName)
    assertEquals(AccessLog().getRecordById[AccessLog](accessLog1.getId, xmlFilePathName), empty)

  @Test
  def testWriteAccessLog(): Unit =
    assertTrue(AccessLog().writeAccessLog("mrossi", "admin", "2026-08-18", "10:00:00.00"))
