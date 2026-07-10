package pkg.b.logic

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import pkg.c.data.generalStructures.RegistrationRequestStatus
import pkg.c.data.xmlManagement.RegistrationRequestRepository

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

class RegistrationRequestServiceTest
  extends AnyFunSuite
    with BeforeAndAfterEach:

  private var tempDirectory: Path = _
  private var tempXmlFile: Path = _
  private var service: RegistrationRequestService = _

  override protected def beforeEach(): Unit =
    super.beforeEach()

    tempDirectory =
      Files.createTempDirectory("protoflow-registration-test-")

    tempXmlFile =
      tempDirectory.resolve("registrationRequests.xml")

    Files.writeString(
      tempXmlFile,
      """<?xml version="1.0" encoding="UTF-8"?>
        |<registrationRequests>
        |</registrationRequests>
        |""".stripMargin,
      StandardCharsets.UTF_8
    )

    val repository =
      new RegistrationRequestRepository(
        tempXmlFile.toString
      )

    service =
      new RegistrationRequestService(repository)

  override protected def afterEach(): Unit =
    try
      if tempXmlFile != null then
        Files.deleteIfExists(tempXmlFile)

      if tempDirectory != null then
        Files.deleteIfExists(tempDirectory)
    finally
      super.afterEach()

  test("submitRequest crea una richiesta valida nello stato Pending"):
    val result = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.rossi@email.it",
      phone = "3331234567",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    assert(result.isRight)

    val request = result.toOption.get

    assert(request.name == "Mario")
    assert(request.surname == "Rossi")
    assert(request.status == RegistrationRequestStatus.Pending)
    assert(request.id.nonEmpty)

  test("submitRequest salva la richiesta nel repository temporaneo"):
    val created = service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.repository@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    assert(created.isRight)

    val pendingRequests =
      service.getPendingRequests

    assert(pendingRequests.size == 1)
    assert(
      pendingRequests.head.email ==
        "mario.repository@email.it"
    )

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

    assert(
      result ==
        Left("Nome, cognome ed email sono obbligatori")
    )

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

    assert(
      result ==
        Left("Il ruolo richiesto è obbligatorio")
    )

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

    assert(
      result ==
        Left("L'area di appartenenza è obbligatoria")
    )

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

    assert(
      result ==
        Left("L'incarico è obbligatorio")
    )

  test("getPendingRequests restituisce solo richieste Pending"):
    service.submitRequest(
      name = "Mario",
      surname = "Rossi",
      email = "mario.pending@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Personale",
      assignment = "Impiegato"
    )

    val requests =
      service.getPendingRequests

    assert(requests.nonEmpty)
    assert(
      requests.forall(
        _.status == RegistrationRequestStatus.Pending
      )
    )

  test("approveRequest cambia lo stato in Approved"):
    val created = service.submitRequest(
      name = "Luigi",
      surname = "Bianchi",
      email = "luigi.approve@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Segreteria",
      assignment = "Impiegato"
    )

    val requestId =
      created.toOption.get.id

    val result =
      service.approveRequest(requestId)

    assert(result.isRight)
    assert(
      result.toOption.get.status ==
        RegistrationRequestStatus.Approved
    )

    assert(service.getPendingRequests.isEmpty)

  test("rejectRequest cambia lo stato in Rejected"):
    val created = service.submitRequest(
      name = "Anna",
      surname = "Verdi",
      email = "anna.reject@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Amministrazione",
      assignment = "Impiegata"
    )

    val requestId =
      created.toOption.get.id

    val result =
      service.rejectRequest(requestId)

    assert(result.isRight)
    assert(
      result.toOption.get.status ==
        RegistrationRequestStatus.Rejected
    )

    assert(service.getPendingRequests.isEmpty)

  test("approveRequest restituisce errore per id inesistente"):
    val result =
      service.approveRequest("id-inesistente")

    assert(result == Left("Richiesta non trovata"))

  test("rejectRequest restituisce errore per id inesistente"):
    val result =
      service.rejectRequest("id-inesistente")

    assert(result == Left("Richiesta non trovata"))