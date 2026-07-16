package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.c.data.Properties.*

case class Classification(
                          private var id: String = "",
                          private var classification: String = "",
                          private var description: String = ""
                         ) extends Entity:
  def this() =
    this("","","")

  def setId(value: String): Unit = id = value
  def setClassification(value: String): Unit = classification = value
  def setDescription(value: String): Unit = description = value
  def getId: String = id
  def getClassification: String = classification
  def getDescription: String = description

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  override def xmlFile = "classifications.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Classification] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Classification])
        .map(r => r.asInstanceOf[Classification])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Classification]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Classification =
    try
      getRecordFromXML(xmlFilePathName, classOf[Classification])
        .map(a => a.asInstanceOf[Classification]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Classification

  // DA FARE
  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try
      getRecordFromXML(xmlFilePathName, classOf[Classification])
        .map(a => a.asInstanceOf[Classification]).count(a => a.classification == "amministrazione")
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
