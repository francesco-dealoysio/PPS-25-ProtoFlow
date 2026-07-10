package pkg.b.logic

import pkg.d.util.Properties.*
import pkg.c.data.xmlManagement.Entities.Account
import pkg.c.data.xmlManagement.Xml.*
import pkg.d.util.Util.md5

object CheckCredentials:
  def checkCredentials(username: String, password: String): Option[Account] =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"

    val databaseFolder =
      getPropsFileProperty(
        baseFolder + fs + "protoflow.properties",
        "database.folder"
      )

    val accounts =
      loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
        .map(_.asInstanceOf[Account])

    accounts.find(account =>
      account.username == username && account.password == md5(password)
    )
