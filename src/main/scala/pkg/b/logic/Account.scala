package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.Util.md5
import pkg.c.data.Properties.*

case class Account(
                 private var id: String = "",
                 private var surname: String = "",
                 private var name: String = "",
                 private var email: String = "",
                 private var phone: String = "",
                 private var role: String = "",
                 private var area: String = "",
                 private var assignment: String = "",
                 private var username: String = "",
                 private var password: String = ""
               ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setSurname(value: String): Unit = surname = value
  def setName(value: String): Unit = name = value
  def setEmail(value: String): Unit = email = value
  def setPhone(value: String): Unit = phone = value
  def setRole(value: String): Unit = role = value
  def setArea(value: String): Unit = area = value
  def setAssignment(value: String): Unit = assignment = value
  def setUsername(value: String): Unit = username = value
  def setPassword(value: String): Unit = password = md5(value)

  def getId: String = id
  def getSurname: String = surname
  def getName: String = name
  def getEmail: String = email
  def getPhone: String = phone
  def getRole: String = role
  def getArea: String = area
  def getAssignment: String = assignment
  def getUsername: String = username
  def getPassword: String = password

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  def idExists(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    searchFieldValue(xmlFilePathName, "id", id)

  def usernameExists(username: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    searchFieldValue(xmlFilePathName, "username", username)

  override def xmlFile = "accounts.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Account] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Account])
      .map(r => r.asInstanceOf[Account])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Account]

  override def getRecordsByFilter[Account](predicate: Account => Boolean, xmlFilePathName: String = defaultXmlFilePathName, classType: Class[Account]): Seq[Account] =
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(_.asInstanceOf[Account]).filter(predicate)
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        Seq.empty[Account]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Account =
    try
      getRecordFromXML(xmlFilePathName, classOf[Account])
        .map(o => o.asInstanceOf[Account]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Account

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Account]
      val id = record.id
      val username = record.username
      if !(idExists(id, xmlFilePathName) || usernameExists(username, xmlFilePathName)) then
        insertElemIntoXML(xmlFilePathName, obj)
        result = true
      else
        println(s"Errore in recordInsert: valori duplicati (id o username)")
    catch
      case e: Exception =>
        println(s"Errore in recordInsert: ${e.getMessage}")
    result

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Account]
      val id = record.id
      val username = record.username
      val  found = countRecordsByFilter[Account](a => a.id != id && a.username == username, xmlFilePathName, classOf[Account])
      if (found == 0) then
        updateElemOfXML(xmlFilePathName, obj)
        result = true
      else
        println(s"Errore in recordInsert: valori duplicati (username)")
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")
    result

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        println(s"Errore in recordDelete: ${e.getMessage}")
        false

@main def tryAccount: Unit =
  println("Tested in AccountTest.scala")
