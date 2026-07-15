package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty

class ClassificationTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Classification

  val classification1 = Classification(
    "1",
    "amministrazione"
  )

  val classification2 = Classification(
    "2",
    "personale"
  )

  val classification3 = Classification(
    "3",
    "segreteria"
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Classification().getRecords("path inesistente"), Seq.empty[Classification])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Classification().getRecords(xmlFilePathName), Seq.empty[Classification])

  @Test
  def testGetRecordsFound: Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    Classification().recordInsert(classification2, xmlFilePathName)
    assertEquals(Classification().getRecords(xmlFilePathName), List(classification1, classification2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Classification().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Classification().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Classification().recordInsert(classification2, xmlFilePathName)
    assertEquals(Classification().getRecordById("2", xmlFilePathName), classification2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Classification().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert(classification1, xmlFilePathName)
    Classification().recordInsert(classification2, xmlFilePathName)
    Classification().recordInsert(classification3, xmlFilePathName)
    val sequence = Seq(classification2, classification3)
    assertEquals(Classification().getRecordsByFilter[Classification](a => a.getId.toInt >= 2, xmlFilePathName, classOf[Classification]), sequence)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Classification().recordInsert(classification1, "path inesistente")
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, classification1)

  @Test
  def testRecordInsert: Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record, classification1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record, classification1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateClassification: Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record, classification1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    assertEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "amministrazione")
    val record = Classification().getRecordById("1")
    record.setClassification("informatica")
    Classification().recordUpdate(record, "path inesistente")
    assertNotEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "informatica")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = classification1.copy()
    record.setClassification("informatica")
    Classification().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "informatica")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Classification().getRecordById("1", xmlFilePathName)
    record.setClassification("informatica")
    record.setId("?")
    Classification().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Classification().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateClassification: Unit =
    val readRecord = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, classification1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    assertEquals("amministrazione", Classification().getRecordById("1", xmlFilePathName).getClassification)
    val record = Classification().getRecordById("1")
    record.setClassification("informatica")
    Classification().recordUpdate(record, xmlFilePathName)
    assertEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "informatica")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert(classification1, xmlFilePathName)
    Classification().recordInsert(classification2, xmlFilePathName)
    Classification().recordInsert(classification3, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Classification().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Classification().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    val record = classification1.copy()
    assertFalse(Classification().recordDelete(record.getId, "path inesistente"))

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = classification1.copy()
    assertFalse(Classification().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    Account().recordInsert(classification1, xmlFilePathName)
    Account().recordInsert(classification2, xmlFilePathName)
    Account().recordInsert(classification3, xmlFilePathName)
    val record = classification1.copy()
    record.setId("100")
    assertFalse(Account().recordDelete(record.getId, xmlFilePathName))
