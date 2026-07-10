package pkg.b.logic

import org.scalatest.funsuite.AnyFunSuite
import pkg.c.data.generalStructures.RegistrationRequestStatus

class RegistrationRequestServiceTest extends AnyFunSuite:

  private val service = new RegistrationRequestService()

  test("submitRequest crea correttamente una richiesta valida nello stato Pending"):
    val result = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi.test@email.it",
      phone = "3331234567",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    assert(result.isRight)

    val request = result.toOption.get

    assert(request.name == "Mario")
    assert(request.surname == "Rossi")
    assert(request.email == "mario.rossi.test@email.it")
    assert(request.status == RegistrationRequestStatus.Pending)
    assert(request.id.nonEmpty)

  test("submitRequest rifiuta nome cognome o email mancanti"):
    val result = service.submitRequest(
      name = "",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    assert(result == Left("Nome, cognome ed email sono obbligatori"))

  test("submitRequest rifiuta una email non valida"):
    val result = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "email-non-valida",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    assert(result == Left("Email non valida"))

  test("submitRequest rifiuta un ruolo mancante"):
    val result = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "",
      requestedRole = "",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    assert(result == Left("Il ruolo richiesto è obbligatorio"))

  test("submitRequest rifiuta un'area mancante"):
    val result = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "",
      assignment = "Impiegato"
    )

    assert(result == Left("L'area di appartenenza è obbligatoria"))

  test("submitRequest rifiuta un incarico mancante"):
    val result = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = ""
    )

    assert(result == Left("L'incarico è obbligatorio"))

  test("getPendingRequests restituisce solamente richieste Pending"):
    val requests = service.getPendingRequests

    assert(
      requests.forall(
        _.status == RegistrationRequestStatus.Pending
      )
    )

  test("approveRequest cambia lo stato della richiesta in Approved"):
    val created = service.submitRequest(
      name = "Luigi",
      surname = "Bianchi",
      email = "luigi.bianchi.approve.test@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Segreteria",
      assignment = "Impiegato"
    )

    assert(created.isRight)

    val requestId = created.toOption.get.id
    val result = service.approveRequest(requestId)

    assert(result.isRight)
    assert(
      result.toOption.get.status ==
        RegistrationRequestStatus.Approved
    )

  test("rejectRequest cambia lo stato della richiesta in Rejected"):
    val created = service.submitRequest(
      name = "Anna",
      surname = "Verdi",
      email = "anna.verdi.reject.test@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Amministrazione",
      assignment = "Impiegata"
    )

    assert(created.isRight)

    val requestId = created.toOption.get.id
    val result = service.rejectRequest(requestId)

    assert(result.isRight)
    assert(
      result.toOption.get.status ==
        RegistrationRequestStatus.Rejected
    )

  test("approveRequest restituisce errore se la richiesta non esiste"):
    val result = service.approveRequest("id-che-non-esiste")

    assert(result == Left("Richiesta non trovata"))

  test("rejectRequest restituisce errore se la richiesta non esiste"):
    val result = service.rejectRequest("id-che-non-esiste")

    assert(result == Left("Richiesta non trovata"))