package pkg.a.gui.services

import pkg.b.logic.{Account, Registration}
import pkg.d.util.Util.{cipher, inDatabaseFilePathName, inIdsFilePathName}
import pkg.d.util.{DateTime, IdGen}
import pkg.a.gui.validators.RegistrationValidator

import scala.util.Random

case class RegistrationApproval(
                                  request: Registration,
                                  account: Account,
                                  generatedPassword: String
                                )

final class RegistrationRequestService(
                                   private val registrationsFilePath: String = inDatabaseFilePathName("registrations.xml"),
                                   private val accountsFilePathName: String = inDatabaseFilePathName("accounts.xml")
                                 ):

  private val registrationLogic = new Registration()
  private val accountLogic = new Account()
  private val registrationValidator = new RegistrationValidator()

  def requestsFilePath: String = registrationsFilePath

  def submitRequest(
                     name: String,
                     surname: String,
                     email: String,
                     phone: String,
                     requestedRole: String,
                     requestedArea: String,
                     assignment: String
                   ): Either[String, Registration] =

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
        date = DateTime.localDateTime,
        state = "Pending"
      )

    val errors = registrationValidator.validate(request)

    if errors.nonEmpty then
      Left(errors.head)
    else if registrationLogic.recordInsert(request, registrationsFilePath) then
      Right(request)
    else
      Left("Errore durante il salvataggio della richiesta")

  def getPendingRequests: List[Registration] =
    registrationLogic
      .getRecordsByFilter[Registration](_.getState == "Pending", registrationsFilePath)
      .toList

  def approveRequest(id: String, operatorUsername: String): Either[String, RegistrationApproval] =
    findPending(id) match
      case None =>
        Left("Richiesta non trovata")

      case Some(request) =>
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
            role = request.getRole.trim,
            area = request.getArea,
            assignment = request.getAssignment,
            username = username,
            password = cipher(plainPassword)
          )

        if !accountLogic.recordInsert(account, accountsFilePathName) then
          Left("Errore durante la creazione dell'account")
        else
          val processedRequest =
            request.copy(
              state = "Approved",
              processedBy = operatorUsername,
              processedDate = DateTime.localDateTime,
              assignedUsername = username
            )

          updateRequestState(processedRequest) match
            case Right(updated) =>
              Right(RegistrationApproval(updated, account, plainPassword))

            case Left(error) =>
              rollbackAccount(account)
              Left(error)

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
              processedDate = DateTime.localDateTime,
              motivation = motivation.trim
            )

          updateRequestState(processedRequest)

  private def rollbackAccount(account: Account): Unit =
    accountLogic.recordDelete(account.getId, accountsFilePathName)

  private def findPending(id: String): Option[Registration] =
    registrationLogic
      .getRecordsByFilter[Registration](request => request.getId == id && request.getState == "Pending", registrationsFilePath)
      .headOption

  private def updateRequestState(processedRequest: Registration): Either[String, Registration] =
    if registrationLogic.recordUpdate(processedRequest, registrationsFilePath) then
      Right(processedRequest)
    else
      Left("Errore durante l'aggiornamento della richiesta")

  private def generateUsername(request: Registration, existingAccounts: Seq[Account]): String =
    val base =
      (request.getName.take(1) + request.getSurname)
        .toLowerCase
        .replaceAll("[^a-z]", "")

    val existingUsernames = existingAccounts.map(_.getUsername.trim).toSet

    LazyList.iterate(1)(_ + 1)
      .map(index => if index == 1 then base else s"$base$index")
      .find(candidate => !existingUsernames.contains(candidate))
      .getOrElse(base)

  private def generatePassword(): String =
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"
    (1 to 10).map(_ => chars(Random.nextInt(chars.length))).mkString
