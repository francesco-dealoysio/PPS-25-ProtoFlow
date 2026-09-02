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

    if credentialsAreEmpty(cleanUsername, cleanPassword) then
      Left(LoginError.EmptyCredentials)
    else
      authenticate(cleanUsername, cleanPassword)
        .toRight(LoginError.InvalidCredentials)
        .flatMap(validateRole)
        .map(registerAccess)

  private def credentialsAreEmpty(username: String, password: String): Boolean =
    username.isEmpty || password.isEmpty

  private def authenticate(username: String, password: String): Option[Account] =
    Account()
      .getRecords[Account]()
      .find(account => account.getUsername == username && account.getPassword == cipher(password))

  private def validateRole(account: Account): Either[LoginError, Account] =
    if roleExists(account.getRole) then
      Right(account)
    else
      Left(LoginError.UnknownRole(account.getRole))

  private def roleExists(role: String): Boolean =
    Role()
      .getRecords[Role]()
      .exists(_.getRole.trim.equalsIgnoreCase(role.trim))

  private def registerAccess(account: Account): Account =
    AccessLog().writeAccessLog(account.getUsername, account.getRole, DateTime.localDate, DateTime.localTime)
    account