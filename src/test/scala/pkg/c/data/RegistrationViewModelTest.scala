package pkg.c.data

import org.scalatest.funsuite.AnyFunSuite
import pkg.c.data.guiStructures.{RegistrationRequest, RegistrationViewModel}

class RegistrationViewModelTest extends AnyFunSuite:

  private val viewModel = RegistrationViewModel()

  test("una richiesta di registrazione completa e corretta deve essere valida"):
    val request = RegistrationRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "3331234567",
      requestedRole = "Operatore Protocollo",
      area = "Urbanistica",
      assignment = "Addetto protocollo"
    )

    val errors = viewModel.validate(request)

    assert(errors.isEmpty)
    assert(viewModel.isValid(request))

  test("il campo Nome è obbligatorio"):
    val request = validRequest().copy(name = "")

    val errors = viewModel.validate(request)

    assert(errors.contains("Il campo 'Nome' è obbligatorio."))
    assert(!viewModel.isValid(request))

  test("il campo Cognome è obbligatorio"):
    val request = validRequest().copy(surname = "")

    val errors = viewModel.validate(request)

    assert(errors.contains("Il campo 'Cognome' è obbligatorio."))
    assert(!viewModel.isValid(request))

  test("il campo Indirizzo email è obbligatorio"):
    val request = validRequest().copy(email = "")

    val errors = viewModel.validate(request)

    assert(errors.contains("Il campo 'Indirizzo email' è obbligatorio."))
    assert(!viewModel.isValid(request))

  test("l'email deve avere un formato valido"):
    val request = validRequest().copy(email = "email-non-valida")

    val errors = viewModel.validate(request)

    assert(errors.contains("L'indirizzo email non ha un formato valido."))
    assert(!viewModel.isValid(request))

  test("il campo Ruolo richiesto è obbligatorio"):
    val request = validRequest().copy(requestedRole = "")

    val errors = viewModel.validate(request)

    assert(errors.contains("Il campo 'Ruolo richiesto' è obbligatorio."))
    assert(!viewModel.isValid(request))

  test("il campo Area o Settore di appartenenza è obbligatorio"):
    val request = validRequest().copy(area = "")

    val errors = viewModel.validate(request)

    assert(errors.contains("Il campo 'Area/Settore di appartenenza' è obbligatorio."))
    assert(!viewModel.isValid(request))

  test("il campo Incarico è obbligatorio"):
    val request = validRequest().copy(assignment = "")

    val errors = viewModel.validate(request)

    assert(errors.contains("Il campo 'Incarico' è obbligatorio."))
    assert(!viewModel.isValid(request))

  test("il campo Telefono non è obbligatorio"):
    val request = validRequest().copy(phone = "")

    val errors = viewModel.validate(request)

    assert(errors.isEmpty)
    assert(viewModel.isValid(request))

  test("se più campi obbligatori sono vuoti, vengono restituiti più errori"):
    val request = validRequest().copy(
      name = "",
      surname = "",
      email = "",
      requestedRole = "",
      area = "",
      assignment = ""
    )

    val errors = viewModel.validate(request)

    assert(errors.size == 6)
    assert(errors.contains("Il campo 'Nome' è obbligatorio."))
    assert(errors.contains("Il campo 'Cognome' è obbligatorio."))
    assert(errors.contains("Il campo 'Indirizzo email' è obbligatorio."))
    assert(errors.contains("Il campo 'Ruolo richiesto' è obbligatorio."))
    assert(errors.contains("Il campo 'Area/Settore di appartenenza' è obbligatorio."))
    assert(errors.contains("Il campo 'Incarico' è obbligatorio."))
    assert(!viewModel.isValid(request))

  private def validRequest(): RegistrationRequest =
    RegistrationRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "3331234567",
      requestedRole = "Operatore Protocollo",
      area = "Urbanistica",
      assignment = "Addetto protocollo"
    )