package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.IdGen
import pkg.d.util.Util.*
import pkg.d.util.Logger.*

case class ErrorLog(
                   private var id: String = "",
                   private var date: String = "",
                   private var time: String = "",
                   private var `class`: String = "",
                   private var method: String = "",
                   private var line: String = "",
                   private var message: String = "",
                   private var stack: String = ""
                 ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setDate(value: String): Unit = date = value
  def setTime(value: String): Unit = time = value
  def setClass(value: String): Unit = `class` = value
  def setMethod(value: String): Unit = method = value
  def setLine(value: String): Unit = line = value
  def setMessage(value: String): Unit = message = value
  def setStack(value: String): Unit = stack = value

  def getId: String = id
  def getDate: String = date
  def getTime: String = time
  def getClass: String = `class`
  def getMethod: String = method
  def getLine: String = line
  def getMessage: String = message
  def getStrack: String = stack

  override def xmlFile = "errors.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[ErrorLog] =
    try
      getRecordFromXML(xmlFilePathName, classOf[ErrorLog])
        .map(r => r.asInstanceOf[ErrorLog])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[ErrorLog]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): ErrorLog =
    try
      getRecordFromXML(xmlFilePathName, classOf[ErrorLog])
        .map(a => a.asInstanceOf[ErrorLog]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new ErrorLog

  override def getRecordsByFilter[ErrorLog](predicate: ErrorLog => Boolean, xmlFilePathName: String = defaultXmlFilePathName, classType: Class[ErrorLog]): Seq[ErrorLog] =
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(o => o.asInstanceOf[ErrorLog]).filter(predicate)
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        Seq.empty[ErrorLog]

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[ErrorLog]
      val id = record.id
      //val id = IdGen(inIdsFilePathName("errorlogid"))
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        println(s"Errore in recordInsert: valori duplicati (id)")
    catch
      case e: Exception =>
        logger(e)
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

@main def tryErrorLog: Unit =
  println("Tested in ErrorTest.scala")
