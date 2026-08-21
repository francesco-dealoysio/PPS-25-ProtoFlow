package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Account
import pkg.a.gui.text.UiText.Validation.Account.*
import pkg.a.gui.validation.AccountValidator

class AccountValidatorTest:

  private val validator = AccountValidator()

  @Test
  def testValidCompleteAccount(): Unit =
    val accountToValidate = validForm()
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.isEmpty)
    assertTrue(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testSurnameRequired(): Unit =
    val accountToValidate = validForm().copy(surname = "")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(SurnameRequired))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testSurnameOnlySpacesInvalid(): Unit =
    val accountToValidate = validForm().copy(surname = "   ")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(SurnameRequired))

  @Test
  def testNameRequired(): Unit =
    val accountToValidate = validForm().copy(name = "")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(NameRequired))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testEmailRequired(): Unit =
    val accountToValidate = validForm().copy(email = "")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(EmailRequired))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testEmailWithoutAtInvalid(): Unit =
    val accountToValidate = validForm().copy(email = "francesco.studio.unibo.it")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(EmailInvalid))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testEmailWithoutDomainInvalid(): Unit =
    val accountToValidate = validForm().copy(email = "francesco@unibo")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(EmailInvalid))

  @Test
  def testRoleRequired(): Unit =
    val accountToValidate = validForm().copy(role = "")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(RoleRequired))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testUsernameRequired(): Unit =
    val accountToValidate = validForm().copy(username = "")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(UsernameRequired))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testPasswordRequiredWhenAddingAccount(): Unit =
    val accountToValidate = validForm()
    val errors = validator.validate(accountToValidate, "", existingAccounts)
    assertTrue(errors.contains(PasswordRequired))
    assertFalse(validator.isValid(accountToValidate, "", existingAccounts))

  @Test
  def testPasswordCanBeEmptyWhenEditingAccount(): Unit =
    val accountToValidate = validForm()
    val errors = validator.validate(accountToValidate, "", existingAccounts, requirePassword = false)
    assertTrue(errors.isEmpty)
    assertTrue(validator.isValid(accountToValidate, "", existingAccounts, requirePassword = false))

  @Test
  def testDuplicateUsernameInvalid(): Unit =
    val accountToValidate = validForm().copy(username = "rosma")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(DuplicateUsername))
    assertFalse(validator.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testUsernamesWithDifferentCaseAreAllowed(): Unit =
    val accountToValidate = validForm().copy(username = "ROSMA")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertFalse(errors.contains(DuplicateUsername))
    assertTrue(errors.isEmpty)

  @Test
  def testDuplicateUsernameIgnoresSpaces(): Unit =
    val accountToValidate = validForm().copy(username = "  rosma  ")
    val errors = validator.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(DuplicateUsername))

  @Test
  def testEditingAccountDoesNotConsiderItselfDuplicate(): Unit =
    val accountToValidate = validForm().copy(id = "1", username = "frank")
    val errors = validator.validate(accountToValidate, "", existingAccounts, Some("1"), false)
    assertTrue(errors.isEmpty)
    assertTrue(validator.isValid(accountToValidate, "", existingAccounts, Some("1"), false))

  @Test
  def testEditingAccountDetectsAnotherDuplicateUsername(): Unit =
    val accountToValidate = validForm().copy(id = "1", username = "rosma")
    val errors = validator.validate(accountToValidate, "", existingAccounts, Some("1"), false)
    assertTrue(errors.contains(DuplicateUsername))
    assertFalse(validator.isValid(accountToValidate, "", existingAccounts, Some("1"), false))

  @Test
  def testAllRequiredFieldsEmptyReturnAllErrors(): Unit =
    val accountToValidate = Account()
    val errors = validator.validate(accountToValidate, "", existingAccounts)

    assertTrue(errors.contains(SurnameRequired))
    assertTrue(errors.contains(NameRequired))
    assertTrue(errors.contains(EmailRequired))
    assertTrue(errors.contains(RoleRequired))
    assertTrue(errors.contains(UsernameRequired))
    assertTrue(errors.contains(PasswordRequired))

  private def validForm(): Account =
    Account(
      surname = "de aloysio",
      name = "francesco",
      email = "francesco.dealoysio@studio.unibo.it",
      phone = "06/11111111",
      role = "admin",
      area = "presidenza",
      assignment = "presidente",
      username = "franci"
    )

  private def existingAccounts: Seq[Account] =
    Seq(
      Account(
        id = "1",
        surname = "de aloysio",
        name = "francesco",
        username = "frank"
      ),
      Account(
        id = "2",
        surname = "rossi",
        name = "mario",
        username = "rosma"
      )
    )