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

  override protected def defaultXmlFilePathName: String =
    inLogFilePathName(xmlFile)

@main def tryErrorLog: Unit =
  println("Tested in ErrorLogTest.scala")
