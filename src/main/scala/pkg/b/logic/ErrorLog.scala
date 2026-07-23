package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.IdGen
import pkg.d.util.Logger.*
import pkg.d.util.Util.inLogFilePathName

import java.nio.file.{Files, Paths}

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

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[ErrorLog]
      val id = record.id
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (id)!")
    catch
      case e: Exception =>
        logger(e)
    result

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      updateElemOfXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        logger(e)
        false

@main def tryErrorLog: Unit =
  println("Tested in ErrorLogTest.scala")
