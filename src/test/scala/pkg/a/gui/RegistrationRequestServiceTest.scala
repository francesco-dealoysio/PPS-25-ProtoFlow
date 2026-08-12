package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.{Account, RegistrationRequestService}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

class RegistrationRequestServiceTest:

  private var tempDirectory: Path = _
  private var tempXmlFile: Path = _
  private var tempAcceptedXmlFile: Path = _
  private var tempRejectedXmlFile: Path = _
  private var tempAccountsXmlFile: Path = _
  private var service: RegistrationRequestService = _

  @Before
  def setUp(): Unit =
    tempDirectory = Files.createTempDirectory("protoflow-registration-test-")
    tempXmlFile = tempDirectory.resolve("registrations.xml")
    tempAcceptedXmlFile = tempDirectory.resolve("registrationsAccepted.xml")
    tempRejectedXmlFile = tempDirectory.resolve("registrationsRejected.xml")
    tempAccountsXmlFile = tempDirectory.resolve("accounts.xml")

    val emptyRequestsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<registrationRequests>\n</registrationRequests>\n"
    val emptyAccountsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<accounts>\n</accounts>\n"

    Seq(tempXmlFile, tempAcceptedXmlFile, tempRejectedXmlFile).foreach(file => Files.writeString(file, emptyRequestsXml, StandardCharsets.UTF_8))
    Files.writeString(tempAccountsXmlFile, emptyAccountsXml, StandardCharsets.UTF_8)

    service = new RegistrationRequestService(
      tempXmlFile.toString, tempAcceptedXmlFile.toString, tempRejectedXmlFile.toString, tempAccountsXmlFile.toString
    )

  @After
  def tearDown(): Unit =
    Seq(tempXmlFile, tempAcceptedXmlFile, tempRejectedXmlFile, tempAccountsXmlFile).foreach(file => if file != null then Files.deleteIfExists(file))
    if tempDirectory != null then Files.deleteIfExists(tempDirectory)

  private def submitLuigi(): String =
    service.submitRequest("Luigi", "Bianchi", "luigi.approve@email.it", "", "Viewer", "Segreteria", "Impiegato").toOption.get.getId

  @Test
  def testSubmitRequestCreatesPendingRequest(): Unit =
    val result = service.submitRequest("Mario", "Rossi", "mario.rossi@email.it", "3331234567", "Viewer", "Personale", "Impiegato")
    assertTrue(result.isRight)

    val request = result.toOption.get
    assertEquals("Mario", request.getName)
    assertEquals("Rossi", request.getSurname)
    assertEquals("Pending", request.getState)
    assertTrue(request.getId.nonEmpty)

  @Test
  def testSubmitRequestStoresRequestInTemporaryFile(): Unit =
    val created = service.submitRequest("Mario", "Rossi", "mario.repository@email.it", "", "Viewer", "Personale", "Impiegato")
    assertTrue(created.isRight)

    val pendingRequests = service.getPendingRequests
    assertEquals(1, pendingRequests.size)
    assertEquals("mario.repository@email.it", pendingRequests.head.getEmail)

  @Test
  def testSubmitRequestRejectsMissingNameSurnameOrEmail(): Unit =
    val result = service.submitRequest("", "Rossi", "mario.rossi@email.it", "", "Viewer", "Personale", "Impiegato")
    assertEquals(Left("Nome, cognome ed email sono obbligatori"), result)

  @Test
  def testSubmitRequestRejectsInvalidEmail(): Unit =
    val result = service.submitRequest("Mario", "Rossi", "email-non-valida", "", "Viewer", "Personale", "Impiegato")
    assertEquals(Left("Email non valida"), result)

  @Test
  def testSubmitRequestRejectsMissingRole(): Unit =
    val result = service.submitRequest("Mario", "Rossi", "mario.rossi@email.it", "", "", "Personale", "Impiegato")
    assertEquals(Left("Il ruolo richiesto è obbligatorio"), result)

  @Test
  def testSubmitRequestRejectsMissingArea(): Unit =
    val result = service.submitRequest("Mario", "Rossi", "mario.rossi@email.it", "", "Viewer", "", "Impiegato")
    assertEquals(Left("L'area di appartenenza è obbligatoria"), result)

  @Test
  def testSubmitRequestRejectsMissingAssignment(): Unit =
    val result = service.submitRequest("Mario", "Rossi", "mario.rossi@email.it", "", "Viewer", "Personale", "")
    assertEquals(Left("L'incarico è obbligatorio"), result)

  @Test
  def testGetPendingRequestsReturnsOnlyPendingRequests(): Unit =
    service.submitRequest("Mario", "Rossi", "mario.pending@email.it", "", "Viewer", "Personale", "Impiegato")
    val requests = service.getPendingRequests
    assertTrue(requests.nonEmpty)
    assertTrue(requests.forall(_.getState == "Pending"))

  @Test
  def testApproveRequestCreatesAccountAndMovesRequestToAccepted(): Unit =
    val requestId = submitLuigi()
    val result = service.approveRequest(requestId, operatorUsername = "admin")
    assertTrue(result.isRight)

    val approval = result.toOption.get
    assertEquals("Approved", approval.request.getState)
    assertEquals("admin", approval.request.getProcessedBy)
    assertTrue(approval.request.getProcessedDate.nonEmpty)
    assertEquals(approval.account.getUsername, approval.request.getAssignedUsername)
    assertEquals("Viewer", approval.account.getRole)
    assertTrue(approval.generatedPassword.nonEmpty)
    assertTrue(service.getPendingRequests.isEmpty)

    val storedAccounts = new Account().getRecords[Account](tempAccountsXmlFile.toString)
    assertTrue(storedAccounts.exists(_.getUsername == approval.account.getUsername))

  @Test
  def testRejectRequestRequiresMotivation(): Unit =
    val requestId = submitLuigi()
    val result = service.rejectRequest(requestId, operatorUsername = "admin", motivation = "")
    assertEquals(Left("La motivazione del rifiuto è obbligatoria"), result)
    assertTrue(service.getPendingRequests.nonEmpty)

  @Test
  def testRejectRequestMovesRequestToRejected(): Unit =
    val created = service.submitRequest("Anna", "Verdi", "anna.reject@email.it", "", "Viewer", "Amministrazione", "Impiegata")
    val requestId = created.toOption.get.getId
    val result = service.rejectRequest(requestId, operatorUsername = "admin", motivation = "Dati incompleti")

    assertTrue(result.isRight)
    val rejected = result.toOption.get
    assertEquals("Rejected", rejected.getState)
    assertEquals("admin", rejected.getProcessedBy)
    assertTrue(rejected.getProcessedDate.nonEmpty)
    assertEquals("Dati incompleti", rejected.getMotivation)
    assertTrue(service.getPendingRequests.isEmpty)

  @Test
  def testApproveRequestReturnsErrorForMissingId(): Unit =
    val result = service.approveRequest("id-inesistente", operatorUsername = "admin")
    assertEquals(Left("Richiesta non trovata"), result)

  @Test
  def testRejectRequestReturnsErrorForMissingId(): Unit =
    val result = service.rejectRequest("id-inesistente", operatorUsername = "admin", motivation = "Motivazione")
    assertEquals(Left("Richiesta non trovata"), result)