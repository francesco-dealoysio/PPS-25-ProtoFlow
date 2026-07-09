package pkg.b.logic

import pkg.c.data.generalStructures.RegistrationRequestStatus
import pkg.c.data.guiStructures.RegistrationRequest
import pkg.c.data.xmlManagement.RegistrationRequestRepository

import java.time.LocalDateTime

class RegistrationRequestService(repository: RegistrationRequestRepository):

  def submitRequest(
                     name: String,
                     surname: String,
                     email: String,
                     phone: String,
                     requestedRole: String,
                     requestedArea: String,
                     assignment: String
                   ): Either[String, RegistrationRequest] =

    if name.trim.isEmpty || surname.trim.isEmpty || email.trim.isEmpty then
      Left("Nome, cognome ed email sono obbligatori")
    else if requestedRole.trim.isEmpty then
      Left("Il ruolo richiesto è obbligatorio")
    else if requestedArea.trim.isEmpty then
      Left("L'area di appartenenza è obbligatoria")
    else if assignment.trim.isEmpty then
      Left("L'incarico è obbligatorio")
    else if !email.contains("@") then
      Left("Email non valida")
    else
      val request = RegistrationRequest(
        name = name.trim,
        surname = surname.trim,
        email = email.trim,
        phone = phone.trim,
        requestedRole = requestedRole.trim,
        requestedArea = requestedArea.trim,
        assignment = assignment.trim,
        requestDate = LocalDateTime.now(),
        status = RegistrationRequestStatus.Pending
      )

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