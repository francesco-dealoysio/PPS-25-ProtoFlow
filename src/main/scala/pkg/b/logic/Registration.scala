package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.c.data.Properties.*

case class Registration(
                    private var id: String = "",
                    private var surname: String = "",
                    private var name: String = "",
                    private var email: String = "",
                    private var phone: String = "",
                    private var role: String = "",
                    private var area: String = "",
                    private var assignment: String = "",
                    private var date: String = "",
                    private var state: String = "",
                    private var result: String = "",
                    private var motivation: String = ""
                  ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "")
  
  def setId(value: String): Unit = id = value
  def setCognome(value: String): Unit = surname = value
  def setNome(value: String): Unit = name = value
  def setEmail(value: String): Unit = email = value
  def setTelefono(value: String): Unit = phone = value
  def setRuolo(value: String): Unit = role = value
  def setArea(value: String): Unit = area = value
  def setAssignment(value: String): Unit = assignment = value
  def setData(value: String): Unit = date = value
  def setStato(value: String): Unit = state = value
  def setEsito(value: String): Unit = result = value
  def setMotivazione(value: String): Unit = motivation = value

  def getId: String = id
  def getCognome: String = surname
  def getNome: String = name
  def getEmail: String = email
  def getTelefono: String = phone
  def getRuolo: String = role
  def getArea: String = area
  def getAssignment: String = assignment
  def getData: String = date
  def getStato: String = state
  def getEsito: String = result
  def getMotivazione: String = motivation

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  override def xmlFile = "registrations.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Registration] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Registration])
        .map(r => r.asInstanceOf[Registration])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Registration]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Registration =
    try
      getRecordFromXML(xmlFilePathName, classOf[Registration])
        .map(a => a.asInstanceOf[Registration]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Registration

  //
  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try
      getRecordFromXML(xmlFilePathName, classOf[Registration])
        .map(a => a.asInstanceOf[Registration]).count(a => a.role == "viewer")
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        NONE

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      insertElemIntoXML(xmlFilePathName, obj)
      result = true
    catch
      case e: Exception =>
        println(s"Errore in recordInsert: ${e.getMessage}")
    result

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      updateElemOfXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")
        false

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        println(s"Errore in recordDelete: ${e.getMessage}")
        false

@main def tryRegistration: Unit =
  println("Tested in RegistrationTest.scala")
