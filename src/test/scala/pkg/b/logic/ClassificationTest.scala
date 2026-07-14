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

  val classifica1 = Classification(
    "1",
    "amministrazione"
  )

  val classifica2 = Classification(
    "2",
    "personale"
  )

  val classifica3 = Classification(
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
    Classification().recordInsert(classifica1, xmlFilePathName)
    Classification().recordInsert(classifica2, xmlFilePathName)
    assertEquals(Classification().getRecords(xmlFilePathName), List(classifica1, classifica2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Classification().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Classification().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Classification().recordInsert(classifica2, xmlFilePathName)
    assertEquals(Classification().getRecordById("2", xmlFilePathName), classifica2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Classification().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Classification().recordInsert(classifica1, "path inesistente")
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, classifica1)

  @Test
  def testRecordInsert: Unit =
    Classification().recordInsert(classifica1, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record, classifica1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Classification().recordInsert(classifica1, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record, classifica1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateClassification: Unit =
    Classification().recordInsert(classifica1, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record, classifica1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Classification().recordInsert(classifica1, xmlFilePathName)
    assertEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "amministrazione")
    val record = Classification().getRecordById("1")
    record.setClassification("informatica")
    Classification().recordUpdate(record, "path inesistente")
    assertNotEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "informatica")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = classifica1.copy()
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
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(classifica1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateRuolo: Unit =
    val readRecord = Role().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, classifica1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Classification().recordInsert(classifica1, xmlFilePathName)
    assertEquals("amministrazione", Classification().getRecordById("1", xmlFilePathName).getClassification)
    val record = Classification().getRecordById("1")
    record.setClassification("informatica")
    Classification().recordUpdate(record, xmlFilePathName)
    assertEquals(Classification().getRecordById("1", xmlFilePathName).getClassification, "informatica")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert(classifica1, xmlFilePathName)
    Classification().recordInsert(classifica2, xmlFilePathName)
    Classification().recordInsert(classifica3, xmlFilePathName)
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Classification().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Classification().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    Classification().recordInsert(classifica1, "path inesistente")
    val record = Classification().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, classifica1)
    assertTrue(false)

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    assertTrue(false)

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    assertTrue(false)
