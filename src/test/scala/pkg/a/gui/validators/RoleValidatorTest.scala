package pkg.a.gui.validators

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.text.UiText.Validation.Role.*
import pkg.b.logic.Role

class RoleValidatorTest:

  private val validator = RoleValidator()

  @Test
  def testValidRole(): Unit =
    assertValid(validRole())

  @Test
  def testRequiredFields(): Unit =
    assertInvalid(Role())(RoleRequired, NameRequired, DescriptionRequired)

  @Test
  def testDuplicateRoleAndName(): Unit =
    assertInvalid(validRole().copy(role = "  ADMIN  "))(DuplicateRole)
    assertInvalid(validRole().copy(name = "  amministratore  "))(DuplicateRoleName)

  @Test
  def testEditDoesNotConsiderCurrentRoleDuplicate(): Unit =
    val role = Role(
      id = "1",
      role = "admin",
      name = "Amministratore",
      description = "Descrizione aggiornata"
    )
    assertValid(role, currentId = Some("1"))


  private def assertValid(role: Role, currentId: Option[String] = None): Unit =
    val errors = validator.validate(role, existingRoles, currentId)
    assertTrue(errors.isEmpty)
    assertTrue(validator.isValid(role, existingRoles, currentId))

  private def assertInvalid(role: Role, currentId: Option[String] = None)(expectedErrors: String*): Unit =
    val errors = validator.validate(role, existingRoles, currentId)
    assertEquals(expectedErrors.size, errors.size)
    expectedErrors.foreach(error => assertTrue(errors.contains(error)))
    assertFalse(validator.isValid(role, existingRoles, currentId))

  private def validRole(): Role =
    Role(
      role = "auditor",
      name = "Revisore",
      description = "Controllo documenti"
    )

  private def existingRoles: Seq[Role] =
    Seq(
      Role(
        id = "1",
        role = "admin",
        name = "Amministratore",
        description = "Amministrazione"
      ),
      Role(
        id = "2",
        role = "oper",
        name = "Operatore",
        description = "Protocollazione"
      )
    )