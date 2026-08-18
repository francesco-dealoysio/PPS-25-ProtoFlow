package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
import pkg.d.util.Util.inTestFilePathName
import java.nio.file.{Files, Paths}

class ClassificationTest:

  private var xmlFilePathName: String = _
  private var classification1: Classification = _
  private var classification2: Classification = _
  private var classification3: Classification = _
  private var empty: Classification = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new Classification

    classification1 = Classification(
      "1",
      "amministrazione"
    )

    classification2 = Classification(
      "2",
      "personale"
    )

    classification3 = Classification(
      "3",
      "segreteria"
    )

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile(): Unit =
    val result = Classification().getRecords[Classification]("path inesistente")
    assertEquals(Seq.empty[Classification], result)

  @Test
  def testGetRecordsEmptyXmlFile(): Unit =
    assertEquals(Classification().getRecords[Classification](xmlFilePathName), Seq.empty[Classification])

  @Test
  def testGetRecordsFound(): Unit =
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    assertEquals(Classification().getRecords[Classification](xmlFilePathName), List(classification1, classification2))

  @Test
  def testGetRecordByIdInexistentXmlFile(): Unit =
    val result = Classification().getRecordById[Classification]("2", "path inesistente")
    assertEquals(empty, result)

  @Test
  def testGetRecordByIdEmptyXmlFile(): Unit =
    val result = Classification().getRecordById[Classification]("1", xmlFilePathName)
    assertEquals(empty, result)

  @Test
  def testGetRecordByIdFoundRecord(): Unit =
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    assertEquals(Classification().getRecordById[Classification]("2", xmlFilePathName), classification2)

  @Test
  def testGetRecordsIdInexistentId(): Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    val result = Classification().getRecordById[Classification]("100", xmlFilePathName)
    assertEquals(empty, result)

  @Test
  def testGetRecordsByFilter(): Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    Classification().recordInsert[Classification](classification3, xmlFilePathName)
    val sequence = Seq(classification2, classification3)
    assertEquals(Classification().getRecordsByFilter[Classification](o => o.getId.toInt >= 2, xmlFilePathName), sequence)

  @Test
  def testRecordInsertInexistentXmlFile(): Unit =
    Classification().recordInsert[Classification](classification1, "path inesistente")
    val record = Classification().getRecordById[Classification]("1", xmlFilePathName)
    assertNotEquals(record, classification1)

  @Test
  def testRecordInsert(): Unit =
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    val record = Classification().getRecordById[Classification]("1", xmlFilePathName)
    assertEquals(record, classification1)

  @Test
  def testRecordInsertDuplicateId(): Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    Classification().recordInsert[Classification](classification3, xmlFilePathName)
    val record = classification1.copy()
    record.setClassification("sconosciuta")
    assertFalse(Classification().recordInsert[Classification](record, xmlFilePathName))

  @Test
  def testRecordInsertDuplicateClassification(): Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    Classification().recordInsert[Classification](classification3, xmlFilePathName)
    val record = classification1.copy()
    record.setId("100")
    assertFalse(Classification().recordInsert[Classification](record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile(): Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    val record = Classification().getRecordById[Classification]("1", xmlFilePathName)
    assertEquals("amministrazione", record.getClassification)
    record.setClassification("informatica")
    assertFalse(Classification().recordUpdate(record, "path inesistente"))
    val unchangedRecord = Classification().getRecordById[Classification]("1", xmlFilePathName)
    assertEquals("amministrazione", unchangedRecord.getClassification)

  @Test
  def testRecordUpdateEmptyXmlFile(): Unit =
    cleanXmlFile(xmlFilePathName)
    val record = classification1.copy()
    record.setClassification("informatica")
    assertFalse(Classification().recordUpdate(record, xmlFilePathName))
    val result = Classification().getRecordById[Classification]("1", xmlFilePathName)
    assertEquals(empty, result)

  @Test
  def testRecordUpdateInexistentId(): Unit =
    val updated = classification1.copy(id = "100")
    assertFalse(Classification().recordUpdate(updated, xmlFilePathName))
    val result = Classification().getRecordById[Classification]("100", xmlFilePathName)
    assertEquals(empty, result)

  @Test
  def testRecordUpdateDuplicateClassification(): Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    Classification().recordInsert[Classification](classification3, xmlFilePathName)
    val record = classification1.copy()
    record.setClassification(classification2.getClassification)
    assertFalse(Classification().recordUpdate[Classification](record, xmlFilePathName))

  @Test
  def testRecordUpdate(): Unit =
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    assertEquals("amministrazione", Classification().getRecordById[Classification]("1", xmlFilePathName).getClassification)
    val record = classification1.copy()
    record.setClassification("informatica")
    assertTrue(Classification().recordUpdate[Classification](record, xmlFilePathName))

  @Test
  def testRecordDelete(): Unit =
    Classification().recordInsert(classification1, xmlFilePathName)
    assertTrue(Classification().recordDelete(classification1.getId, xmlFilePathName))
    val result = Classification().getRecordById[Classification](classification1.getId, xmlFilePathName)
    assertEquals(empty, result)

  @Test
  def testRecordDeleteInesistentXmlFile(): Unit =
    val record = classification1.copy()
    assertFalse(Classification().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile(): Unit =
    cleanXmlFile(xmlFilePathName)
    val record = classification1.copy()
    assertFalse(Classification().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId(): Unit =
    cleanXmlFile(xmlFilePathName)
    Classification().recordInsert[Classification](classification1, xmlFilePathName)
    Classification().recordInsert[Classification](classification2, xmlFilePathName)
    Classification().recordInsert[Classification](classification3, xmlFilePathName)
    val record = classification1.copy()
    record.setId("100")
    assertFalse(Classification().recordDelete(record.getId, xmlFilePathName))