package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Account
import pkg.a.gui.text.UiText.Validation.Account.*
import pkg.a.gui.validation.AccountValidator

class AccountValidatorTest:

  private val viewModel = AccountValidator()

  @Test
  def testValidCompleteAccount(): Unit =
    val accountToValidate = validForm()
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.isEmpty)
    assertTrue(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testSurnameRequired(): Unit =
    val accountToValidate = validForm().copy(surname = "")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(SurnameRequired))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testSurnameOnlySpacesInvalid(): Unit =
    val accountToValidate = validForm().copy(surname = "   ")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(SurnameRequired))

  @Test
  def testNameRequired(): Unit =
    val accountToValidate = validForm().copy(name = "")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(NameRequired))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testEmailRequired(): Unit =
    val accountToValidate = validForm().copy(email = "")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(EmailRequired))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testEmailWithoutAtInvalid(): Unit =
    val accountToValidate = validForm().copy(email = "francesco.studio.unibo.it")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(EmailInvalid))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testEmailWithoutDomainInvalid(): Unit =
    val accountToValidate = validForm().copy(email = "francesco@unibo")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(EmailInvalid))

  @Test
  def testRoleRequired(): Unit =
    val accountToValidate = validForm().copy(role = "")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(RoleRequired))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testUsernameRequired(): Unit =
    val accountToValidate = validForm().copy(username = "")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(UsernameRequired))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testPasswordRequiredWhenAddingAccount(): Unit =
    val accountToValidate = validForm()
    val errors = viewModel.validate(accountToValidate, "", existingAccounts)
    assertTrue(errors.contains(PasswordRequired))
    assertFalse(viewModel.isValid(accountToValidate, "", existingAccounts))

  @Test
  def testPasswordCanBeEmptyWhenEditingAccount(): Unit =
    val accountToValidate = validForm()
    val errors = viewModel.validate(accountToValidate, "", existingAccounts, requirePassword = false)
    assertTrue(errors.isEmpty)
    assertTrue(viewModel.isValid(accountToValidate, "", existingAccounts, requirePassword = false))

  @Test
  def testDuplicateUsernameInvalid(): Unit =
    val accountToValidate = validForm().copy(username = "rosma")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(DuplicateUsername))
    assertFalse(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  @Test
  def testDuplicateUsernameIgnoresCase(): Unit =
    val accountToValidate = validForm().copy(username = "ROSMA")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(DuplicateUsername))

  @Test
  def testDuplicateUsernameIgnoresSpaces(): Unit =
    val accountToValidate = validForm().copy(username = "  rosma  ")
    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)
    assertTrue(errors.contains(DuplicateUsername))

  @Test
  def testEditingAccountDoesNotConsiderItselfDuplicate(): Unit =
    val accountToValidate = validForm().copy(id = "1", username = "frank")
    val errors = viewModel.validate(accountToValidate, "", existingAccounts, Some("1"), false)
    assertTrue(errors.isEmpty)
    assertTrue(viewModel.isValid(accountToValidate, "", existingAccounts, Some("1"), false))

  @Test
  def testEditingAccountDetectsAnotherDuplicateUsername(): Unit =
    val accountToValidate = validForm().copy(id = "1", username = "rosma")
    val errors = viewModel.validate(accountToValidate, "", existingAccounts, Some("1"), false)
    assertTrue(errors.contains(DuplicateUsername))
    assertFalse(viewModel.isValid(accountToValidate, "", existingAccounts, Some("1"), false))

  @Test
  def testAllRequiredFieldsEmptyReturnAllErrors(): Unit =
    val accountToValidate = Account()
    val errors = viewModel.validate(accountToValidate, "", existingAccounts)

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