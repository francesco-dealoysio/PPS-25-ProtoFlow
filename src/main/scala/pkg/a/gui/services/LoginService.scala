package pkg.a.gui.services

import pkg.b.logic.{Account, AccessLog, Role}
import pkg.d.util.DateTime
import pkg.d.util.Util.cipher

object LoginService:

  enum LoginError:
    case EmptyCredentials
    case InvalidCredentials
    case UnknownRole(role: String)

  def login(username: String, password: String): Either[LoginError, Account] =
    val cleanUsername = username.trim
    val cleanPassword = password.trim

    if cleanUsername.isEmpty || cleanPassword.isEmpty then
      Left(LoginError.EmptyCredentials)
    else
      checkCredentials(cleanUsername, cleanPassword) match
        case Some(account) if !roleExists(account.getRole) =>
          Left(LoginError.UnknownRole(account.getRole))

        case Some(account) =>
          AccessLog().writeAccessLog(
            account.getUsername,
            account.getRole,
            DateTime.localDate,
            DateTime.localTime
          )
          Right(account)

        case None =>
          Left(LoginError.InvalidCredentials)

  private def checkCredentials(username: String, password: String): Option[Account] =
    Account().getRecords[Account]().find(account => account.getUsername == username && account.getPassword == cipher(password))

  private def roleExists(role: String): Boolean =
    new Role()
      .getRecords[Role]()
      .exists(_.getRole.trim.equalsIgnoreCase(role.trim))