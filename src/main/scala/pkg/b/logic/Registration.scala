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
  def setSurname(value: String): Unit = surname = value
  def setName(value: String): Unit = name = value
  def setEmail(value: String): Unit = email = value
  def setPhone(value: String): Unit = phone = value
  def setRole(value: String): Unit = role = value
  def setArea(value: String): Unit = area = value
  def setAssignment(value: String): Unit = assignment = value
  def setDate(value: String): Unit = date = value
  def setState(value: String): Unit = state = value
  def setResult(value: String): Unit = result = value
  def setMotivation(value: String): Unit = motivation = value

  def getId: String = id
  def getSurname: String = surname
  def getName: String = name
  def getEmail: String = email
  def getPhone: String = phone
  def getRole: String = role
  def getArea: String = area
  def getAssignment: String = assignment
  def getDate: String = date
  def getState: String = state
  def getResult: String = result
  def getMotivation: String = motivation

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  def idExists(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    searchFieldValue(xmlFilePathName, "id", id)

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

  // TESTARE

  override def getRecordsByFilter[Registration](predicate: Registration => Boolean, xmlFilePathName: String = defaultXmlFilePathName, classType: Class[Registration]): Seq[Registration] =
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(o => o.asInstanceOf[Registration]).filter(predicate)
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        Seq.empty[Registration]

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Registration]
      val id = record.id
      if !idExists(id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        println(s"Errore in recordInsert: valori duplicati (id)")
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
