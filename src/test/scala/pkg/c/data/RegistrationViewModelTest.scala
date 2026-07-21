package pkg.c.data

import org.scalatest.funsuite.AnyFunSuite
import pkg.c.data.generalStructures.RegistrationRequestStatus
import pkg.c.data.guiStructures.{RegistrationRequest, RegistrationViewModel}
import java.time.LocalDateTime

class RegistrationViewModelTest extends AnyFunSuite:

  private val viewModel = RegistrationViewModel()
  
  test("una richiesta di registrazione completa e corretta deve essere valida"):
    assertValid(validRequest())
    assertValid(validRequest().copy(phone = "")) // Il campo Telefono non è obbligatorio

  test("validazione campi obbligatori singoli"):
    assertInvalid(validRequest().copy(name = ""))("Il campo 'Nome' è obbligatorio.")
    assertInvalid(validRequest().copy(surname = ""))("Il campo 'Cognome' è obbligatorio.")
    assertInvalid(validRequest().copy(email = ""))("Il campo 'Indirizzo email' è obbligatorio.")
    assertInvalid(validRequest().copy(requestedRole = ""))("Il campo 'Ruolo richiesto' è obbligatorio.")
    assertInvalid(validRequest().copy(requestedArea = ""))("Il campo 'Area/Settore di appartenenza' è obbligatorio.")
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
      requestedRole = "",
      requestedArea = "",
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

  private def assertValid(request: RegistrationRequest): Unit =
    val errors = viewModel.validate(request)
    assert(errors.isEmpty)
    assert(viewModel.isValid(request))

  private def assertInvalid(request: RegistrationRequest)(expectedErrors: String*): Unit =
    val errors = viewModel.validate(request)
    assert(errors.size == expectedErrors.size)
    expectedErrors.foreach(err => assert(errors.contains(err)))
    assert(!viewModel.isValid(request))

  private def validRequest(): RegistrationRequest =
    RegistrationRequest(
      id = "test-request-1",
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "3331234567",
      requestedRole = "Operatore Protocollo",
      requestedArea = "Urbanistica",
      assignment = "Addetto protocollo",
      requestDate = LocalDateTime.of(2026, 7, 10, 10, 0),
      status = RegistrationRequestStatus.Pending
    )