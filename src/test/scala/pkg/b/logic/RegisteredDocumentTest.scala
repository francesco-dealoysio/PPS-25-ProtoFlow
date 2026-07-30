package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
import java.nio.file.{Files, Path}

class RegisteredDocumentTest:

  private var xmlFilePathName: String = _
  private var tempDirectory: Path = _
  private var registeredDocument1: RegisteredDocument = _
  private var registeredDocument2: RegisteredDocument = _
  private var registeredDocument3: RegisteredDocument = _
  private var empty: RegisteredDocument = _

  @Before
  def setUp(): Unit =
    tempDirectory = Files.createTempDirectory("protoflow-registered-document-test-")
    xmlFilePathName =
      tempDirectory
        .resolve("test.xml")
        .toString
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new RegisteredDocument

    registeredDocument1 = RegisteredDocument(
      "1",
      "2026-07-10",
      "22:19:13.86",
      "2024/002342/F.D.",
      "email",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "registered",
      "2026-07-10",
      "22:19:13.86",
      "Rossi",
      "2026/000001",
      "2026-07-11",
      "09:00:00.00",
      "Bianchi"
    )

    registeredDocument2 = RegisteredDocument(
      "2",
      "2026-07-10",
      "22:19:13.86",
      "2024/002342/F.D.",
      "letter",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "registered",
      "2026-07-10",
      "22:19:13.86",
      "Bianchi",
      "2026/000002",
      "2026-07-11",
      "09:00:00.00",
      "Neri"
    )

    registeredDocument3 = RegisteredDocument(
      "3",
      "2026-07-10",
      "22:19:13.86",
      "2024/002342/F.D.",
      "letter",
      "ACEA",
      "UNUCI/Tesoreria",
      "Bollette energia elettrica",
      "Alla attenzione del Tesoriere",
      "registered",
      "2026-07-10",
      "22:19:13.86",
      "Neri",
      "2026/000003",
      "2026-07-11",
      "09:00:00.00",
      "Bianchi"
    )

  @After
  def tearDown(): Unit =
    Option(xmlFilePathName).foreach: fileName =>
      Files.deleteIfExists(Path.of(fileName))

    Option(tempDirectory).foreach: directory =>
      Files.deleteIfExists(directory)

  @Test
  def testGetRecordsInexistentXmlFile: Unit =
    assertEquals(RegisteredDocument().getRecords[RegisteredDocument]("path inesistente"), Seq.empty[RegisteredDocument])

  @Test
  def testGetRecordsEmptyXmlFile: Unit =
    assertEquals(RegisteredDocument().getRecords[RegisteredDocument](xmlFilePathName), Seq.empty[RegisteredDocument])

  @Test
  def testGetRecordsFound: Unit =
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument2, xmlFilePathName)
    assertEquals(RegisteredDocument().getRecords[RegisteredDocument](xmlFilePathName), List(registeredDocument1, registeredDocument2))

  @Test
  def testGetRecordByIdFoundRecord: Unit =
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument2, xmlFilePathName)
    assertEquals(RegisteredDocument().getRecordById[RegisteredDocument]("2", xmlFilePathName), registeredDocument2)

  @Test
  def testGetRecordsIdInexistentId: Unit =
    assertEquals(RegisteredDocument().getRecordById[RegisteredDocument]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter: Unit =
    cleanXmlFile(xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument2, xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument3, xmlFilePathName)
    val sequence = Seq(registeredDocument2, registeredDocument3)
    assertEquals(RegisteredDocument().getRecordsByFilter[RegisteredDocument](a => a.getDocumentType == "letter", xmlFilePathName), sequence)

  @Test
  def testRecordInsertInexistentXmlFile: Unit =
    assertFalse(RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, "path inesistente"))

  @Test
  def testRecordInsert: Unit =
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    val record = RegisteredDocument().getRecordById[RegisteredDocument]("1", xmlFilePathName)
    assertEquals(record, registeredDocument1)

  @Test
  def testRecordInsertDuplicateId: Unit =
    cleanXmlFile(xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    val record = registeredDocument1.copy()
    assertFalse(RegisteredDocument().recordInsert[RegisteredDocument](record, xmlFilePathName))

  @Test
  def testRecordUpdate: Unit =
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    assertEquals("ACEA", RegisteredDocument().getRecordById[RegisteredDocument]("1", xmlFilePathName).getSender)
    val record = registeredDocument1.copy()
    record.setSender("SME")
    assertTrue(RegisteredDocument().recordUpdate[RegisteredDocument](record, xmlFilePathName))

  @Test
  def testRecordDelete: Unit =
    cleanXmlFile(xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument2, xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument3, xmlFilePathName)
    val record = RegisteredDocument().getRecordById[RegisteredDocument]("1", xmlFilePathName)
    assertEquals(record.getId, "1")
    RegisteredDocument().recordDelete(record.getId, xmlFilePathName)

  @Test
  def testRecordDeleteInesistentId: Unit =
    cleanXmlFile(xmlFilePathName)
    RegisteredDocument().recordInsert[RegisteredDocument](registeredDocument1, xmlFilePathName)
    val record = registeredDocument1.copy()
    record.setId("100")
    assertFalse(RegisteredDocument().recordDelete(record.getId, xmlFilePathName))
