package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, insertElemIntoXML, searchFieldValue}
import pkg.c.data.Properties.getPropsFileProperty

class ClassificaTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  createEmptyXmlFile(xmlFilePathName, "test_records")

  val empty = new Classifica

  val classifica1 = Classifica(
    "1",
    "amministrazione"
  )

  val classifica2 = Classifica(
    "2",
    "personale"
  )

  val classifica3 = Classifica(
    "3",
    "segreteria"
  )

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(Classifica().getRecords("path inesistente"), Seq.empty[Classifica])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(Classifica().getRecords(xmlFilePathName), Seq.empty[Classifica])

  @Test
  def testGetRecordsFound: Unit =
    Classifica().recordInsert(classifica1, xmlFilePathName)
    Classifica().recordInsert(classifica2, xmlFilePathName)
    assertEquals(Classifica().getRecords(xmlFilePathName), List(classifica1, classifica2))

  @Test
  def testGetRecordByIdInexistentXmlFile: Unit =
    assertEquals(Classifica().getRecordById("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile: Unit =
    assertEquals(Classifica().getRecordById("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    Classifica().recordInsert(classifica2, xmlFilePathName)
    assertEquals(Classifica().getRecordById("2", xmlFilePathName), classifica2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(Classifica().getRecordById("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(false)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    Classifica().recordInsert(classifica1, "path inesistente")
    val record = Classifica().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, classifica1)

  @Test
  def testRecordInsert: Unit =
    Classifica().recordInsert(classifica1, xmlFilePathName)
    val record = Classifica().getRecordById("1", xmlFilePathName)
    assertEquals(record, classifica1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    Classifica().recordInsert(classifica1, xmlFilePathName)
    val record = Classifica().getRecordById("1", xmlFilePathName)
    assertEquals(record, classifica1)
    assertTrue(false)

  @Test
  def testRecordInsertDuplicateClassifica: Unit =
    Classifica().recordInsert(classifica1, xmlFilePathName)
    val record = Classifica().getRecordById("1", xmlFilePathName)
    assertEquals(record, classifica1)
    assertTrue(false)

  @Test
  def testGetRecordUpdateInexistentXmlFile: Unit =
    Classifica().recordInsert(classifica1, xmlFilePathName)
    assertEquals(Classifica().getRecordById("1", xmlFilePathName).getClassifica, "amministrazione")
    val record = Classifica().getRecordById("1")
    record.setClassifica("informatica")
    Classifica().recordUpdate(record, "path inesistente")
    assertNotEquals(Classifica().getRecordById("1", xmlFilePathName).getClassifica, "informatica")

  @Test
  def testGetRecordUpdateEmptyXmlFile: Unit =
    cleanXmlFile(xmlFilePathName)
    val record = classifica1.copy()
    record.setClassifica("informatica")
    Classifica().recordUpdate(record, xmlFilePathName)
    assertNotEquals(Classifica().getRecordById("1", xmlFilePathName).getClassifica, "informatica")

  @Test
  def testGetRecordUpdateInexistentId: Unit =
    val record = Classifica().getRecordById("1", xmlFilePathName)
    record.setClassifica("informatica")
    record.setId("?")
    Classifica().recordUpdate(record, xmlFilePathName)
    val recordUpdated = Classifica().getRecordById("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testGetRecordUpdateDuplicateId: Unit =
    val readRecord = Classifica().getRecordById("1", xmlFilePathName)
    assertEquals(classifica1, readRecord)
    assertTrue(false)

  @Test
  def testGetRecordUpdateDuplicateRuolo: Unit =
    val readRecord = Ruolo().getRecordById("1", xmlFilePathName)
    assertEquals(readRecord, classifica1)
    assertTrue(false)

  @Test
  def testGetRecordUpdate: Unit =
    Classifica().recordInsert(classifica1, xmlFilePathName)
    assertEquals("amministrazione", Classifica().getRecordById("1", xmlFilePathName).getClassifica)
    val record = Classifica().getRecordById("1")
    record.setClassifica("informatica")
    Classifica().recordUpdate(record, xmlFilePathName)
    assertEquals(Classifica().getRecordById("1", xmlFilePathName).getClassifica, "informatica")

  @Test
  def testGetRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    Classifica().recordInsert(classifica1, xmlFilePathName)
    Classifica().recordInsert(classifica2, xmlFilePathName)
    Classifica().recordInsert(classifica3, xmlFilePathName)
    val record = Classifica().getRecordById("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    Classifica().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Classifica().getRecordById(record.getId, xmlFilePathName), empty)

  @Test
  def testGetRecordDeleteInesistentXmlFile: Unit =
    Classifica().recordInsert(classifica1, "path inesistente")
    val record = Classifica().getRecordById("1", xmlFilePathName)
    assertNotEquals(record, classifica1)
    assertTrue(false)

  @Test
  def testGetRecordDeleteEmptyXmlFile: Unit =
    assertTrue(false)

  @Test
  def testGetRecordDeleteInesistentId: Unit =
    assertTrue(false)
