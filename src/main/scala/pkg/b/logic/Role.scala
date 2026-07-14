package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.c.data.Properties.*

case class Role(
                private var id: String = "",
                private var role: String = "",
                private var description: String = ""
                ) extends Entity:
  def this() =
    this("","","")

  def setId(value: String): Unit = id = value
  def setRole(value: String): Unit = role = value
  def setDescription(value: String): Unit = description = value

  def getId: String = id
  def getRole: String = role
  def getDescription: String = description

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  override def xmlFile = "roles.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Role] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Role])
        .map(r => r.asInstanceOf[Role])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Role]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Role =
    try
      getRecordFromXML(xmlFilePathName, classOf[Role])
        .map(a => a.asInstanceOf[Role]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Role

  // DA FARE
  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try
      getRecordFromXML(xmlFilePathName, classOf[Role])
        .map(a => a.asInstanceOf[Role]).count(a => a.role == "viewer")
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

@main def tryRole: Unit =
    println("Tested in RoleTest.scala")