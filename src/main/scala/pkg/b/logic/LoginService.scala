package pkg.b.logic

import Account.*
import pkg.c.data.Xml.*
import pkg.c.data.Properties.*
import pkg.d.util.Util.md5

object LoginService:

  enum LoginError:
    case EmptyCredentials
    case InvalidCredentials
    case UnknownRole(role: String)

  case class LoggedUser(
                         username: String,
                         fullName: String,
                         role: String
                       )

  def login(username: String, password: String): Either[LoginError, LoggedUser] =
    val cleanUsername = username.trim
    val cleanPassword = password.trim

    if cleanUsername.isEmpty || cleanPassword.isEmpty then
      Left(LoginError.EmptyCredentials)
    else
      checkCredentials(cleanUsername, cleanPassword) match
        case Some(account) if validRoles.contains(account.getRole) =>
          Right(toLoggedUser(account))

        case Some(account) =>
          Left(LoginError.UnknownRole(account.getRole))

        case None =>
          Left(LoginError.InvalidCredentials)

  private val validRoles: Set[String] =
    Set("admin", "oper", "viewer")

  private def toLoggedUser(account: Account): LoggedUser =
    val fullName =
      s"${account.getName} ${account.getSurname}".trim match
        case "" => account.getUsername
        case name => name

    LoggedUser(
      username = account.getUsername,
      fullName = fullName,
      role = account.getRole
    )

  private def checkCredentials(username: String, password: String): Option[Account] =
    accounts.find(account => account.getUsername == username && account.getPassword == md5(password))

  private def accounts: Seq[Account] =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    getRecordFromXML(databaseFolder + fs + "accounts.xml", classOf[Account]).map(_.asInstanceOf[Account])