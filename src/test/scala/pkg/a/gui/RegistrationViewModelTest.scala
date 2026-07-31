package pkg.a.gui

import org.scalatest.funsuite.AnyFunSuite
import pkg.a.gui.structures.RegistrationViewModel
import pkg.b.logic.Registration

class RegistrationViewModelTest extends AnyFunSuite:

  private val viewModel = RegistrationViewModel()

  test("una richiesta di registrazione completa e corretta deve essere valida"):
    assertValid(validRequest())
    assertValid(validRequest().copy(phone = "")) // Il campo Telefono non è obbligatorio

  test("validazione campi obbligatori singoli"):
    assertInvalid(validRequest().copy(name = ""))("Il campo 'Nome' è obbligatorio.")
    assertInvalid(validRequest().copy(surname = ""))("Il campo 'Cognome' è obbligatorio.")
    assertInvalid(validRequest().copy(email = ""))("Il campo 'Indirizzo email' è obbligatorio.")
    assertInvalid(validRequest().copy(role = ""))("Il campo 'Ruolo richiesto' è obbligatorio.")
    assertInvalid(validRequest().copy(area = ""))("Il campo 'Area/Settore di appartenenza' è obbligatorio.")
    assertInvalid(validRequest().copy(assignment = ""))("Il campo 'Incarico' è obbligatorio.")

  test("l'email deve avere un formato valido"):
    assertInvalid(validRequest().copy(email = "email-non-valida"))(
      "L'indirizzo email non ha un formato valido."
    )

  test("se più campi obbligatori sono vuoti vengono restituiti tutti gli errori relativi"):
    val emptyRequest = validRequest().copy(
      name = "",
      surname = "",
      email = "",
      role = "",
      area = "",
      assignment = ""
    )

    assertInvalid(emptyRequest)(
      "Il campo 'Nome' è obbligatorio.",
      "Il campo 'Cognome' è obbligatorio.",
      "Il campo 'Indirizzo email' è obbligatorio.",
      "Il campo 'Ruolo richiesto' è obbligatorio.",
      "Il campo 'Area/Settore di appartenenza' è obbligatorio.",
      "Il campo 'Incarico' è obbligatorio."
    )

  // Test Helpers

  private def assertValid(request: Registration): Unit =
    val errors = viewModel.validate(request)
    assert(errors.isEmpty)
    assert(viewModel.isValid(request))

  private def assertInvalid(request: Registration)(expectedErrors: String*): Unit =
    val errors = viewModel.validate(request)
    assert(errors.size == expectedErrors.size)
    expectedErrors.foreach(err => assert(errors.contains(err)))
    assert(!viewModel.isValid(request))

  private def validRequest(): Registration =
    Registration(
      id = "test-request-1",
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "3331234567",
      role = "Operatore Protocollo",
      area = "Urbanistica",
      assignment = "Addetto protocollo",
      date = "2026-07-10T10:00:00.000",
      state = "Pending"
    )
