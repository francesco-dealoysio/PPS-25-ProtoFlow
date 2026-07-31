package pkg.a.gui

import org.scalatest.funsuite.AnyFunSuite
import pkg.a.gui.structures.AccountViewModel
import pkg.b.logic.Account

class AccountViewModelTest extends AnyFunSuite:

  private val viewModel = AccountViewModel()

  test("un account completo e corretto deve essere valido"):
    val accountToValidate = validForm()

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.isEmpty)
    assert(viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("il campo Cognome è obbligatorio"):
    val accountToValidate = validForm().copy(surname = "")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.SurnameRequiredError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("il campo Cognome contenente solo spazi non è valido"):
    val accountToValidate = validForm().copy(surname = "   ")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.SurnameRequiredError))

  test("il campo Nome è obbligatorio"):
    val accountToValidate = validForm().copy(name = "")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.NameRequiredError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("il campo Email è obbligatorio"):
    val accountToValidate = validForm().copy(email = "")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.EmailRequiredError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("un'email senza chiocciola non è valida"):
    val accountToValidate = validForm().copy(email = "francesco.studio.unibo.it")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.EmailInvalidError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("un'email senza dominio non è valida"):
    val accountToValidate = validForm().copy(email = "francesco@unibo")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.EmailInvalidError))

  test("il campo Ruolo è obbligatorio"):
    val accountToValidate = validForm().copy(role = "")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.RoleRequiredError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("il campo Username è obbligatorio"):
    val accountToValidate = validForm().copy(username = "")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.UsernameRequiredError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("il campo Password è obbligatorio in fase di aggiunta"):
    val accountToValidate = validForm()

    val errors = viewModel.validate(accountToValidate, "", existingAccounts, requirePassword = true)

    assert(errors.contains(AccountViewModel.PasswordRequiredError))
    assert(!viewModel.isValid(accountToValidate, "", existingAccounts, requirePassword = true))

  test("il campo Password può essere lasciato vuoto in fase di modifica"):
    val accountToValidate = validForm()

    val errors = viewModel.validate(accountToValidate, "", existingAccounts, requirePassword = false)

    assert(errors.isEmpty)
    assert(viewModel.isValid(accountToValidate, "", existingAccounts, requirePassword = false))

  test("un account già esistente con lo stesso username non deve essere valido"):
    val accountToValidate = validForm().copy(username = "rosma")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.DuplicateUsernameError))
    assert(!viewModel.isValid(accountToValidate, "topolino", existingAccounts))

  test("il controllo dei duplicati ignora maiuscole e minuscole"):
    val accountToValidate = validForm().copy(username = "ROSMA")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.DuplicateUsernameError))

  test("il controllo dei duplicati ignora gli spazi iniziali e finali"):
    val accountToValidate = validForm().copy(username = "  rosma  ")

    val errors = viewModel.validate(accountToValidate, "topolino", existingAccounts)

    assert(errors.contains(AccountViewModel.DuplicateUsernameError))

  test("durante la modifica l'account non deve essere considerato duplicato di se stesso"):
    val accountToValidate = validForm().copy(id = "1", username = "frank")

    val errors = viewModel.validate(
      account = accountToValidate,
      rawPassword = "",
      existingAccounts = existingAccounts,
      currentAccountId = Some("1"),
      requirePassword = false
    )

    assert(errors.isEmpty)
    assert(viewModel.isValid(
      account = accountToValidate,
      rawPassword = "",
      existingAccounts = existingAccounts,
      currentAccountId = Some("1"),
      requirePassword = false
    ))

  test("durante la modifica lo username di un altro account deve essere considerato duplicato"):
    val accountToValidate = validForm().copy(id = "1", username = "rosma")

    val errors = viewModel.validate(
      account = accountToValidate,
      rawPassword = "",
      existingAccounts = existingAccounts,
      currentAccountId = Some("1"),
      requirePassword = false
    )

    assert(errors.contains(AccountViewModel.DuplicateUsernameError))
    assert(!viewModel.isValid(
      account = accountToValidate,
      rawPassword = "",
      existingAccounts = existingAccounts,
      currentAccountId = Some("1"),
      requirePassword = false
    ))

  test("se tutti i campi obbligatori sono vuoti vengono restituiti tutti gli errori"):
    val accountToValidate = Account()

    val errors = viewModel.validate(accountToValidate, "", existingAccounts)

    assert(errors.contains(AccountViewModel.SurnameRequiredError))
    assert(errors.contains(AccountViewModel.NameRequiredError))
    assert(errors.contains(AccountViewModel.EmailRequiredError))
    assert(errors.contains(AccountViewModel.RoleRequiredError))
    assert(errors.contains(AccountViewModel.UsernameRequiredError))
    assert(errors.contains(AccountViewModel.PasswordRequiredError))

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
      Account(id = "1", surname = "de aloysio", name = "francesco", username = "frank"),
      Account(id = "2", surname = "rossi", name = "mario", username = "rosma")
    )
