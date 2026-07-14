package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.c.data.Properties.*

case class Classifica(
                  private var id: String = "",
                  private var classifica: String = ""
                ) extends Entity:
  def this() =
    this("","")

  def setId(value: String): Unit = id = value
  def setClassifica(value: String): Unit = classifica = value

  def getId: String = id
  def getClassifica: String = classifica

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  override def xmlFile = "classifiche.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Classifica] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Classifica])
        .map(r => r.asInstanceOf[Classifica])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Classifica]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Classifica =
    try
      getRecordFromXML(xmlFilePathName, classOf[Classifica])
        .map(a => a.asInstanceOf[Classifica]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Classifica

  // DA FARE
  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try
      getRecordFromXML(xmlFilePathName, classOf[Classifica])
        .map(a => a.asInstanceOf[Classifica]).count(a => a.classifica == "amministrazione")
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

@main def tryClassifica: Unit =
  println("Tested in ClassificaTest.scala")
