package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.c.data.Properties.*

case class Classification(
                          private var id: String = "",
                          private var classification: String = ""
                         ) extends Entity:
  def this() =
    this("","")

  def setId(value: String): Unit = id = value
  def setClassification(value: String): Unit = classification = value

  def getId: String = id
  def getClassification: String = classification

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  def idExists(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    searchFieldValue(xmlFilePathName, "id", id)

  def classificationExists(classification: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    searchFieldValue(xmlFilePathName, "classification", classification)

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
  override def getRecordsByFilter[Classification](predicate: Classification => Boolean, xmlFilePathName: String = defaultXmlFilePathName, classType: Class[Classification]): Seq[Classification] =
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(o => o.asInstanceOf[Classification]).filter(predicate)
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        Seq.empty[Classification]


  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Classification]
      val id = record.id
      val classification = record.classification
      if !(idExists(id, xmlFilePathName) || classificationExists(classification, xmlFilePathName)) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        println(s"Errore in recordInsert: valori duplicati (id o classification)")
    catch
      case e: Exception =>
        println(s"Errore in recordInsert: ${e.getMessage}")
    result
  
  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Classification]
      val id = record.id
      val classification = record.classification
      val  found = countRecordsByFilter[Classification](a => a.id != id && a.classification == classification, xmlFilePathName, classOf[Classification])
      if (found == 0) then
        result = updateElemOfXML(xmlFilePathName, obj)
      else
        println(s"Errore in recordInsert: valori duplicati (classifica)")
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")
        false
    result

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        println(s"Errore in recordDelete: ${e.getMessage}")
        false

@main def tryClassifica: Unit =
  println("Tested in ClassificaTest.scala")
