package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.xmlManagement.Xml.*
import pkg.d.util.Properties.*

case class Ruolo(
                private var id: String = "",
                private var ruolo: String = "",
                private var descrizione: String = ""
                ) extends Entity:
  def this() =
    this("","","")

  def setId(value: String): Unit = id = value
  def setRuolo(value: String): Unit = ruolo = value
  def setDescrizione(value: String): Unit = descrizione = value

  def getId: String = id
  def getRuolo: String = ruolo
  def getDescrizione: String = descrizione

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  override def xmlFile = "ruoli.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Ruolo] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Ruolo])
        .map(r => r.asInstanceOf[Ruolo])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Ruolo]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Ruolo =
    try
      getRecordFromXML(xmlFilePathName, classOf[Ruolo])
        .map(a => a.asInstanceOf[Ruolo]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Ruolo

  // DA FARE
  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try
      getRecordFromXML(xmlFilePathName, classOf[Ruolo])
        .map(a => a.asInstanceOf[Ruolo]).count(a => a.ruolo == "viewer")
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        NONE

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    try
      insertElemIntoXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        println(s"Errore in recordInsert: ${e.getMessage}")

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    try
      updateElemOfXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        println(s"Errore in recordDelete: ${e.getMessage}")

@main def tryRuolo: Unit =
  println("Tested in RuoloTest.scala")