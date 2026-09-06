package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class RoleTest:

  private var xmlFilePathName: String = _
  private var role1: Role = _
  private var role2: Role = _
  private var role3: Role = _
  private var empty: Role = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    empty = new Role

    role1 = Role(
      id = "1",
      role = "admin",
      name = "Amministratore",
      description = "Amministrazione"
    )

    role2 = Role(
      id = "2",
      role = "oper",
      name = "Operatore",
      description = "Protocollazione"
    )

    role3 = Role(
      id = "3",
      role = "viewer",
      name = "Visualizzatore",
      description = "Visualizzazione"
    )

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testGetRecordsInexistentXmlFile(): Unit =
    assertEquals(Role().getRecords[Role]("path inesistente"), Seq.empty[Role])

  @Test
  def testGetRecordsEmptyXmlFile(): Unit =
    assertEquals(Role().getRecords[Role](xmlFilePathName), Seq.empty[Role])

  @Test
  def testGetRecordsFound(): Unit =
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    assertEquals(Role().getRecords[Role](xmlFilePathName), List(role1, role2))

  @Test
  def testGetRecordByIdInexistentXmlFile(): Unit =
    assertEquals(Role().getRecordById[Role]("2", "path inesistente"), empty)

  @Test
  def testGetRecordByIdEmptyXmlFile(): Unit =
    assertEquals(Role().getRecordById[Role]("2", xmlFilePathName), empty)

  @Test
  def testGetRecordByIdFoundRecord(): Unit =
    Role().recordInsert[Role](role2, xmlFilePathName)
    assertEquals(Role().getRecordById[Role]("2", xmlFilePathName), role2)

  @Test
  def testGetRecordsIdInexistentId(): Unit =
    assertEquals(Role().getRecordById[Role]("?", xmlFilePathName), empty)

  @Test
  def testGetRecordsByFilter(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    val record = role1.copy()
    record.setId("4")
    record.setRole("contabile")
    record.setDescription("Protocollazione")
    Role().recordInsert[Role](record, xmlFilePathName)
    assertEquals(Role().getRecordsByFilter[Role](a => a.getDescription == "Protocollazione", xmlFilePathName), Seq(role2, record))

  @Test
  def testRecordInsertInexistentXmlFile(): Unit =
    Role().recordInsert[Role](role1, "path inesistente")
    val record = Role().getRecordById[Role]("1", xmlFilePathName)
    assertNotEquals(record, role1)

  @Test
  def testRecordInsert(): Unit =
    Role().recordInsert[Role](role1, xmlFilePathName)
    val record = Role().getRecordById[Role]("1", xmlFilePathName)
    assertEquals(record, role1)

  @Test
  def testRecordInsertDuplicateId(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    val record = role1.copy()
    assertFalse(Role().recordInsert[Role](record, xmlFilePathName))
  
  @Test
  def testRecordInsertDuplicateRole(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    val record = role1.copy()
    record.setId("100")
    assertFalse(Role().recordInsert[Role](record, xmlFilePathName))

  @Test
  def testRecordUpdateInexistentXmlFile(): Unit =
    Role().recordInsert[Role](role1, xmlFilePathName)
    assertEquals(Role().getRecordById[Role]("1", xmlFilePathName).getDescription, "Amministrazione")
    val record = Role().getRecordById[Role]("1", xmlFilePathName)
    record.setDescription("Gestione")
    Role().recordUpdate[Role](record, "path inesistente")
    assertNotEquals(Role().getRecordById[Role]("1", xmlFilePathName).getDescription, "Gestione")

  @Test
  def testRecordUpdateEmptyXmlFile(): Unit =
    cleanXmlFile(xmlFilePathName)
    val record = role1.copy()
    record.setDescription("Gestione")
    Role().recordUpdate[Role](record, xmlFilePathName)
    assertNotEquals(Role().getRecordById[Role]("1", xmlFilePathName).getDescription, "Gestione")

  @Test
  def testRecordUpdateInexistentId(): Unit =
    val record = Role().getRecordById[Role]("1", xmlFilePathName)
    record.setDescription("Gestione")
    record.setId("?")
    Role().recordUpdate[Role](record, xmlFilePathName)
    val recordUpdated = Role().getRecordById[Role]("?", xmlFilePathName)
    assertEquals(recordUpdated, empty)

  @Test
  def testRecordUpdateDuplicateRole(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    val record = role1.copy()
    record.setRole(role2.getRole)
    assertFalse(Role().recordUpdate[Role](record, xmlFilePathName))

  @Test
  def testRecordUpdate(): Unit =
    Role().recordInsert[Role](role1, xmlFilePathName)
    assertEquals("Amministrazione", Role().getRecordById[Role]("1", xmlFilePathName).getDescription)
    val record = Role().getRecordById[Role]("1", xmlFilePathName)
    record.setDescription("Gestione")
    Role().recordUpdate[Role](record, xmlFilePathName)
    assertEquals(Role().getRecordById[Role]("1", xmlFilePathName).getDescription, "Gestione")

  @Test
  def testRecordDelete(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    val record = Role().getRecordById[Role]("2", xmlFilePathName)
    assertEquals(record.getId, "2")
    Role().recordDelete(record.getId, xmlFilePathName)
    assertEquals(Role().getRecordById[Role](record.getId, xmlFilePathName), empty)

  @Test
  def testRecordDeleteAdminRole(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    assertFalse(Role().recordDelete(role1.getId, xmlFilePathName))
    assertEquals(Role().getRecordById[Role](role1.getId, xmlFilePathName), role1)

  @Test
  def testRecordDeleteInesistentXmlFile(): Unit =
    val record = role1.copy()
    assertFalse(Role().recordDelete(record.getId, "path inesistente"))

  @Test
  def testRecordDeleteEmptyXmlFile(): Unit =
    cleanXmlFile(xmlFilePathName)
    val record = role1.copy()
    assertFalse(Role().recordDelete(record.getId, xmlFilePathName))

  @Test
  def testRecordDeleteInesistentId(): Unit =
    cleanXmlFile(xmlFilePathName)
    Role().recordInsert[Role](role1, xmlFilePathName)
    Role().recordInsert[Role](role2, xmlFilePathName)
    Role().recordInsert[Role](role3, xmlFilePathName)
    val record = role1.copy()
    record.setId("100")
    assertFalse(Role().recordDelete(record.getId, xmlFilePathName))
