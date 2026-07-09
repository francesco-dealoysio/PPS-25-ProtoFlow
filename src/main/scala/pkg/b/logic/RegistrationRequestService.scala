package pkg.b.logic

import pkg.c.data.*
import pkg.c.data.generalStructures.RegistrationRequestStatus
import pkg.c.data.guiStructures.RegistrationRequest
import pkg.c.data.xmlManagement.RegistrationRequestRepository

import java.time.LocalDateTime
import java.util.UUID

// GUI -> Service -> Repository -> XML
class RegistrationRequestService(repository: RegistrationRequestRepository):

  //Nella gui mi aspetto un pulsante [ Invia richiesta ]
  def submitRequest(
                     name: String,
                     surname: String,
                     email: String,
                     requestedRole: String,
                     requestedArea: String
                   ): Either[String, RegistrationRequest] =

  //Elenco errori sul rifiuto (cosa vogliamo far vedere?)
    if name.trim.isEmpty || surname.trim.isEmpty || email.trim.isEmpty then
      Left("Nome, cognome ed email sono obbligatori")
    else if !email.contains("@") then
      Left("Email non valida")
    else
      //Costruisco oggetto
      val request = RegistrationRequest(
        id = UUID.randomUUID().toString,
        name = name,
        surname = surname,
        email = email,
        requestedRole = requestedRole,
        requestedArea = requestedArea,
        requestDate = LocalDateTime.now(),
        status = RegistrationRequestStatus.Pending
      )

      //Se tutto ok, salvo richiesta
      Right(repository.save(request))

  def getPendingRequests: List[RegistrationRequest] =
    repository.findPending()

  def approveRequest(id: String): Either[String, RegistrationRequest] =
    repository.findById(id) match
      case Some(request) =>
        repository.update(request.copy(status = RegistrationRequestStatus.Approved))
      case None =>
        Left("Richiesta non trovata")

  def rejectRequest(id: String): Either[String, RegistrationRequest] =
    repository.findById(id) match
      case Some(request) =>
        repository.update(request.copy(status = RegistrationRequestStatus.Rejected))
      case None =>
        Left("Richiesta non trovata")