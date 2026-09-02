package pkg.a.gui.services

import pkg.b.logic.{Account, AccessLog, Role}
import pkg.d.util.DateTime
import pkg.d.util.Util.*
import pkg.d.util.Util.cipher

object LoginService:

  enum LoginError:
    case EmptyCredentials
    case InvalidCredentials
    case UnknownRole(role: String)

  def login(
             username: String,
             password: String,
             accountsFilePathName: String = inDatabaseFilePathName("accounts.xml"),
             rolesFilePathName: String = inDatabaseFilePathName("roles.xml"),
             accessLogFilePathName: String = inLogFilePathName("accessLog.xml"),
             accessLogIdFilePathName: String = inIdsFilePathName("accessLogId")
           ): Either[LoginError, Account] =
    val cleanUsername = username.trim
    val cleanPassword = password.trim

    if credentialsAreEmpty(cleanUsername, cleanPassword) then
      Left(LoginError.EmptyCredentials)
    else
      authenticate(cleanUsername, cleanPassword, accountsFilePathName)
        .toRight(LoginError.InvalidCredentials)
        .flatMap(account => validateRole(account, rolesFilePathName))
        .map(account =>
          registerAccess(account, accessLogFilePathName, accessLogIdFilePathName)
        )

  private def credentialsAreEmpty(username: String, password: String): Boolean =
    username.isEmpty || password.isEmpty

  private def authenticate(username: String, password: String, accountsFilePathName: String): Option[Account] =
    Account()
      .getRecords[Account](accountsFilePathName)
      .find(account =>
        account.getUsername == username &&
          account.getPassword == cipher(password)
      )

  private def validateRole(account: Account, rolesFilePathName: String): Either[LoginError, Account] =
    if roleExists(account.getRole, rolesFilePathName) then
      Right(account)
    else
      Left(LoginError.UnknownRole(account.getRole))

  private def roleExists(role: String, rolesFilePathName: String): Boolean =
    Role()
      .getRecords[Role](rolesFilePathName)
      .exists(_.getRole.trim.equalsIgnoreCase(role.trim))

  private def registerAccess(account: Account, accessLogFilePathName: String, accessLogIdFilePathName: String): Account =
    AccessLog().writeAccessLog(
      account.getUsername,
      account.getRole,
      DateTime.localDate,
      DateTime.localTime,
      accessLogFilePathName,
      accessLogIdFilePathName
    )
    account