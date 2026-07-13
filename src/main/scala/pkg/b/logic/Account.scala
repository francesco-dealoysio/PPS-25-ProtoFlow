package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.xmlManagement.Xml.*
import pkg.d.util.Util.md5
import pkg.d.util.Properties.*


case class Account(
                 private var id: String = "",
                 private var cognome: String = "",
                 private var nome: String = "",
                 private var email: String = "",
                 private var telefono: String = "",
                 private var ruolo: String = "",
                 private var area: String = "",
                 private var incarico: String = "",
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

  override def xmlFile = "accounts.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Account] =
      getRecordFromXML(xmlFilePathName, classOf[Account])
      .map(r => r.asInstanceOf[Account])

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Account =
    getRecordFromXML(xmlFilePathName, classOf[Account])
      .map(a => a.asInstanceOf[Account]).filter(_.id == id).head

  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    getRecordFromXML(xmlFilePathName, classOf[Account])
      //.map(a => a.asInstanceOf[Account]).count(a => a.ruolo == "viewer" && a.nome == "francesco")
      .map(a => a.asInstanceOf[Account]).count(a => a.ruolo == "viewer")

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    insertElemIntoXML(xmlFilePathName, obj)

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    updateElemOfXML(xmlFilePathName, obj)

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    removeElemFromXML(xmlFilePathName, id)

@main def tryEntity: Unit =

  Account().recordInsert(Account().getRecordById("3"))
  //Account().recordDelete("5")
  //println(Account().getRecordsByFilter(true))
