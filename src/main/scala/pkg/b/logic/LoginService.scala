package pkg.b.logic

import pkg.c.data.Xml.*
import pkg.c.data.Properties.*
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
        case Some(account) =>
          Right(account)

        case None =>
          Left(LoginError.InvalidCredentials)

  private def checkCredentials(username: String, password: String): Option[Account] =
    accounts.find(account => account.getUsername == username && account.getPassword == cipher(password))

  private def accounts: Seq[Account] =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    getRecordsFromXML(databaseFolder + fs + "accounts.xml", classOf[Account]).map(_.asInstanceOf[Account])