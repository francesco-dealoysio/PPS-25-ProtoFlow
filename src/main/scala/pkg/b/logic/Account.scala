package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.Util.md5
import pkg.c.data.Properties.*

case class Account(
                 private var id: String = "",
                 private var cognome: String = "", // surmname
                 private var nome: String = "", // name
                 private var email: String = "",
                 private var telefono: String = "", //  phone
                 private var ruolo: String = "",   // role
                 private var area: String = "",  // area
                 private var incarico: String = "", // assignment
                 private var username: String = "",
                 private var password: String = ""
               ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setCognome(value: String): Unit = cognome = value
  def setNome(value: String): Unit = nome = value
  def setEmail(value: String): Unit = email = value
  def setTelefono(value: String): Unit = telefono = value
  def setRuolo(value: String): Unit = ruolo = value
  def setArea(value: String): Unit = area = value
  def setIncarico(value: String): Unit = incarico = value
  def setUsername(value: String): Unit = username = value
  def setPassword(value: String): Unit = password = md5(value)

  def getId: String = id
  def getCognome: String = cognome
  def getNome: String = nome
  def getEmail: String = email
  def getTelefono: String = telefono
  def getRuolo: String = ruolo
  def getArea: String = area
  def getIncarico: String = incarico
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

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Account =
    try
      getRecordFromXML(xmlFilePathName, classOf[Account])
        .map(a => a.asInstanceOf[Account]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Account

  // FARE
  override def getRecordsByFilter(predicate: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try

      val words = List("Scala", "Java", "Kotlin", "JavaScript")
      val startsWithJ: String => Boolean = _.startsWith("J")
      println(s"Words starting with J: ${filterItems(words, startsWithJ)}")
      10

      //getRecordFromXML(xmlFilePathName, classOf[Account])
        //.map(a => a.asInstanceOf[Account]).count(a => a.ruolo == "viewer")
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        NONE

  // Generic higher-order function
  def filterItems[T](items: List[T], predicate: T => Boolean): List[T] = {
    items.filter(predicate)
  }

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val account = obj.asInstanceOf[Account]
      val id = account.id
      val username = account.username
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
      val account = obj.asInstanceOf[Account]
      val id = account.id
      val username = account.username
      // todo mettere prima a punto getRecordsByFilter
      if true then
        updateElemOfXML(xmlFilePathName, obj)
        result = true
      else
        println(s"Errore in recordInsert: valori duplicati (id o username)")
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
  Account().getRecordsByFilter(true, "pippo")

