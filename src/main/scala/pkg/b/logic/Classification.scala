package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.Logger.*

case class Classification(
                          private var id: String = "",
                          private var classification: String = "",
                          private var description: String = ""
                         ) extends Entity:
  def this() =
    this("","","")

  def setId(value: String): Unit = id = value
  def setClassification(value: String): Unit = classification = value
  def setDescription(value: String): Unit = description = value // ??

  def getId: String = id
  def getClassification: String = classification
  def getDescription: String = description // ??

  override def xmlFile = "classifications.xml"

  override def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Classification]
      val id = record.id
      val classification = record.classification

      if !(fieldExists("id", id, xmlFilePathName) || fieldExists("classification", classification, xmlFilePathName)) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (id o classifica)!")
    catch
      case e: Exception =>
        logger(e)
    result
  
  override def recordUpdate[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Classification]
      val id = record.id
      val classification = record.classification

      val  found = countRecordsByFilter[Classification](a => a.id != id && a.classification == classification, xmlFilePathName, classOf[Classification])
      if (found == 0) then
        result = updateElemOfXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (classifica)!")
    catch
      case e: Exception =>
        logger(e)
        false
    result
