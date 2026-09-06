package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Role
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class RoleServiceTest:

  private var xmlFilePathName: String = _
  private var idFilePathName: String = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("roleServiceTest.xml")
    idFilePathName = inTestFilePathName("roleServiceId")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    Files.deleteIfExists(Paths.get(idFilePathName))

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(xmlFilePathName))
    Files.deleteIfExists(Paths.get(idFilePathName))

  @Test
  def testAddRole(): Unit =
    val result = RoleService.addRole(
      role = "oper",
      name = "Operatore",
      description = "Gestione protocollazione",
      rolesFilePathName = xmlFilePathName,
      roleIdFilePathName = idFilePathName
    )

    assertTrue(result.isRight)
    val role = result.toOption.get
    assertEquals("oper", role.getRole)
    assertEquals("Operatore", role.getName)
    assertEquals("Gestione protocollazione", role.getDescription)
    assertEquals(role, Role().getRecordById[Role](role.getId, xmlFilePathName))

  @Test
  def testAddRoleDuplicateRole(): Unit =
    RoleService.addRole(
      "oper",
      "Operatore",
      "Gestione protocollazione",
      xmlFilePathName,
      idFilePathName
    )

    val result = RoleService.addRole(
      "oper",
      "Altro operatore",
      "Altra descrizione",
      xmlFilePathName,
      idFilePathName
    )

    assertEquals(Left("Errore durante l'inserimento del ruolo"), result)