package pkg.b.logic

import Account.*
import pkg.c.data.xmlManagement.Xml.*
import pkg.d.util.Properties.*
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
        case Some(account) if validRoles.contains(account.ruolo) =>
          Right(toLoggedUser(account))

        case Some(account) =>
          Left(LoginError.UnknownRole(account.ruolo))

        case None =>
          Left(LoginError.InvalidCredentials)

  private val validRoles: Set[String] =
    Set("admin", "oper", "viewer")

  private def toLoggedUser(account: Account): LoggedUser =
    val fullName =
      s"${account.nome} ${account.cognome}".trim match
        case "" => account.username
        case name => name

    LoggedUser(
      username = account.username,
      fullName = fullName,
      role = account.ruolo
    )

  private def checkCredentials(username: String, password: String): Option[Account] =
    accounts.find(account => account.username == username && account.password == md5(password))

  private def accounts: Seq[Account] =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    loadXML(databaseFolder + fs + "accounts.xml", classOf[Account]).map(_.asInstanceOf[Account])