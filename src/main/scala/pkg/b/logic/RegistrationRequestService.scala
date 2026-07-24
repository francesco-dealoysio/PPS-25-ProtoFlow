package pkg.b.logic

import pkg.d.util.IdGen
import pkg.d.util.Util.{inDatabaseFilePathName, inIdsFilePathName, md5}

import scala.util.Random

/** Esito dell'approvazione: la richiesta aggiornata, l'account creato e la password generata (in chiaro, solo per il report). */
case class RegistrationApproval(
                                  request: Registration,
                                  account: Account,
                                  generatedPassword: String
                                )

class RegistrationRequestService(
                                   private val pendingFilePath: String = inDatabaseFilePathName("registrations.xml"),
                                   private val acceptedFilePath: String = inDatabaseFilePathName("registrations_accepted.xml"),
                                   private val rejectedFilePath: String = inDatabaseFilePathName("registrations_rejected.xml"),
                                   private val accountsFilePathName: String = inDatabaseFilePathName("accounts.xml")
                                 ):

  private val registrationLogic = new Registration()

  def pendingRequestsFilePath: String = pendingFilePath
  def acceptedRequestsFilePath: String = acceptedFilePath
  def rejectedRequestsFilePath: String = rejectedFilePath

  def submitRequest(
                     name: String,
                     surname: String,
                     email: String,
                     phone: String,
                     requestedRole: String,
                     requestedArea: String,
                     assignment: String
                   ): Either[String, Registration] =

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
      val request =
        Registration(
          id = IdGen(inIdsFilePathName("registrationId")),
          surname = surname.trim,
          name = name.trim,
          email = email.trim,
          phone = phone.trim,
          role = requestedRole.trim,
          area = requestedArea.trim,
          assignment = assignment.trim,
          date = RegistrationDates.now(),
          state = "Pending"
        )

      if registrationLogic.recordInsert(request, pendingFilePath) then
        Right(request)
      else
        Left("Errore durante il salvataggio della richiesta")

  def getPendingRequests: List[Registration] =
    registrationLogic
      .getRecordsByFilter[Registration](_.getState == "Pending", pendingFilePath)
      .toList

  /**
   * Genera un account dai dati della richiesta, lo inserisce in accounts.xml e sposta
   * la richiesta da "in attesa" ad "accettate", tracciando operatore e data di esecuzione.
   */
  def approveRequest(id: String, operatorUsername: String): Either[String, RegistrationApproval] =
    findPending(id) match
      case None =>
        Left("Richiesta non trovata")

      case Some(request) =>
        val accountLogic = new Account()
        val existingAccounts = accountLogic.getRecords[Account](accountsFilePathName)
        val username = generateUsername(request, existingAccounts)
        val plainPassword = generatePassword()

        val account =
          Account(
            id = IdGen(inIdsFilePathName("accountId")),
            surname = request.getSurname,
            name = request.getName,
            email = request.getEmail,
            phone = request.getPhone,
            role = mapRequestedRole(request.getRole),
            area = request.getArea,
            assignment = request.getAssignment,
            username = username,
            password = md5(plainPassword)
          )

        if !accountLogic.recordInsert(account, accountsFilePathName) then
          Left("Errore durante la creazione dell'account")
        else
          val processedRequest =
            request.copy(
              state = "Approved",
              processedBy = operatorUsername,
              processedDate = RegistrationDates.now(),
              assignedUsername = username
            )

          moveRequest(id, processedRequest, acceptedFilePath) match
            case Right(moved) =>
              Right(RegistrationApproval(moved, account, plainPassword))

            case Left(error) =>
              Left(error)

  /** Rifiuta la richiesta, richiedendo una motivazione, e la sposta da "in attesa" a "rifiutate". */
  def rejectRequest(id: String, operatorUsername: String, motivation: String): Either[String, Registration] =
    if motivation.trim.isEmpty then
      Left("La motivazione del rifiuto è obbligatoria")
    else
      findPending(id) match
        case None =>
          Left("Richiesta non trovata")

        case Some(request) =>
          val processedRequest =
            request.copy(
              state = "Rejected",
              processedBy = operatorUsername,
              processedDate = RegistrationDates.now(),
              motivation = motivation.trim
            )

          moveRequest(id, processedRequest, rejectedFilePath)

  private def findPending(id: String): Option[Registration] =
    registrationLogic
      .getRecordsByFilter[Registration](_.getId == id, pendingFilePath)
      .headOption

  private def moveRequest(id: String, processedRequest: Registration, destinationFilePath: String): Either[String, Registration] =
    if !registrationLogic.recordDelete(id, pendingFilePath) then
      Left("Errore durante l'aggiornamento della richiesta")
    else if !registrationLogic.recordInsert(processedRequest, destinationFilePath) then
      Left("Errore durante l'aggiornamento della richiesta")
    else
      Right(processedRequest)

  private def mapRequestedRole(requestedRole: String): String =
    requestedRole.trim.toLowerCase match
      case "amministratore" => "admin"
      case "operatore protocollo" => "oper"
      case "viewer" => "viewer"
      case other => other

  private def generateUsername(request: Registration, existingAccounts: Seq[Account]): String =
    val base =
      (request.getName.take(1) + request.getSurname)
        .toLowerCase
        .replaceAll("[^a-z]", "")

    val existingUsernames = existingAccounts.map(_.getUsername.toLowerCase).toSet

    LazyList.iterate(1)(_ + 1)
      .map(index => if index == 1 then base else s"$base$index")
      .find(candidate => !existingUsernames.contains(candidate))
      .getOrElse(base)

  private def generatePassword(): String =
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
    (1 to 10).map(_ => chars(Random.nextInt(chars.length))).mkString
