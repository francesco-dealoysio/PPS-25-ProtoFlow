package pkg.b.logic

import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

class RegistrationRequestServiceTest
  extends AnyFunSuite
    with BeforeAndAfterEach:

  private var tempDirectory: Path = _
  private var tempXmlFile: Path = _
  private var tempAcceptedXmlFile: Path = _
  private var tempRejectedXmlFile: Path = _
  private var tempAccountsXmlFile: Path = _
  private var service: RegistrationRequestService = _

  override protected def beforeEach(): Unit =
    super.beforeEach()

    tempDirectory =
      Files.createTempDirectory("protoflow-registration-test-")

    tempXmlFile = tempDirectory.resolve("registrations.xml")
    tempAcceptedXmlFile = tempDirectory.resolve("registrationsAccepted.xml")
    tempRejectedXmlFile = tempDirectory.resolve("registrationsRejected.xml")
    tempAccountsXmlFile = tempDirectory.resolve("accounts.xml")

    Seq(tempXmlFile, tempAcceptedXmlFile, tempRejectedXmlFile).foreach: file =>
      Files.writeString(
        file,
        """<?xml version="1.0" encoding="UTF-8"?>
          |<registrationRequests>
          |</registrationRequests>
          |""".stripMargin,
        StandardCharsets.UTF_8
      )

    Files.writeString(
      tempAccountsXmlFile,
      """<?xml version="1.0" encoding="UTF-8"?>
        |<accounts>
        |</accounts>
        |""".stripMargin,
      StandardCharsets.UTF_8
    )

    service =
      new RegistrationRequestService(
        tempXmlFile.toString,
        tempAcceptedXmlFile.toString,
        tempRejectedXmlFile.toString,
        tempAccountsXmlFile.toString
      )

  override protected def afterEach(): Unit =
    try
      Seq(tempXmlFile, tempAcceptedXmlFile, tempRejectedXmlFile, tempAccountsXmlFile)
        .foreach(file => if file != null then Files.deleteIfExists(file))

      if tempDirectory != null then
        Files.deleteIfExists(tempDirectory)
    finally
      super.afterEach()

  private def submitLuigi(): String =
    service.submitRequest(
      name = "Luigi",
      surname = "Bianchi",
      email = "luigi.approve@email.it",
      phone = "",
      requestedRole = "Viewer",
      requestedArea = "Segreteria",
      assignment = "Impiegato"
    ).toOption.get.getId

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

    assert(request.getName == "Mario")
    assert(request.getSurname == "Rossi")
    assert(request.getState == "Pending")
    assert(request.getId.nonEmpty)

  test("submitRequest salva la richiesta nel file temporaneo"):
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
      pendingRequests.head.getEmail ==
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
        _.getState == "Pending"
      )
    )

  test("approveRequest cambia lo stato in Approved, crea un account e sposta la richiesta tra le accettate"):
    val requestId = submitLuigi()

    val result =
      service.approveRequest(requestId, operatorUsername = "admin")

    assert(result.isRight)

    val approval = result.toOption.get

    assert(approval.request.getState == "Approved")
    assert(approval.request.getProcessedBy == "admin")
    assert(approval.request.getProcessedDate.nonEmpty)
    assert(approval.request.getAssignedUsername == approval.account.getUsername)
    assert(approval.account.getRole == "viewer")
    assert(approval.generatedPassword.nonEmpty)

    assert(service.getPendingRequests.isEmpty)

    val storedAccounts =
      new Account().getRecords[Account](tempAccountsXmlFile.toString)

    assert(storedAccounts.exists(_.getUsername == approval.account.getUsername))

  test("rejectRequest richiede una motivazione"):
    val requestId = submitLuigi()

    val result =
      service.rejectRequest(requestId, operatorUsername = "admin", motivation = "")

    assert(result == Left("La motivazione del rifiuto è obbligatoria"))
    assert(service.getPendingRequests.nonEmpty)

  test("rejectRequest cambia lo stato in Rejected, registra la motivazione e sposta la richiesta tra le rifiutate"):
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
      created.toOption.get.getId

    val result =
      service.rejectRequest(requestId, operatorUsername = "admin", motivation = "Dati incompleti")

    assert(result.isRight)

    val rejected = result.toOption.get

    assert(rejected.getState == "Rejected")
    assert(rejected.getProcessedBy == "admin")
    assert(rejected.getProcessedDate.nonEmpty)
    assert(rejected.getMotivation == "Dati incompleti")

    assert(service.getPendingRequests.isEmpty)

  test("approveRequest restituisce errore per id inesistente"):
    val result =
      service.approveRequest("id-inesistente", operatorUsername = "admin")

    assert(result == Left("Richiesta non trovata"))

  test("rejectRequest restituisce errore per id inesistente"):
    val result =
      service.rejectRequest("id-inesistente", operatorUsername = "admin", motivation = "Motivazione")

    assert(result == Left("Richiesta non trovata"))
