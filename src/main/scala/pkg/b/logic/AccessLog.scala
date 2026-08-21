package pkg.b.logic

import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Logger.*
import pkg.d.util.Util.inLogFilePathName

import java.nio.file.{Files, Paths}

case class AccessLog(
                       private var id: String = "",
                       private var username: String = "",
                       private var role: String = "",
                       private var processedDate: String = "",
                       private var processedTime: String = ""
                     ) extends Entity:
  def this() =
    this("", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setUsername(value: String): Unit = username = value
  def setRole(value: String): Unit = role = value
  def setProcessedDate(value: String): Unit = processedDate = value
  def setProcessedTime(value: String): Unit = processedTime = value

  def getId: String = id
  def getUsername: String = username
  def getRole: String = role
  def getProcessedDate: String = processedDate
  def getProcessedTime: String = processedTime

  override def xmlFile: String = "accessLog.xml"

  override protected def defaultXmlFilePathName: String =
    val path = inLogFilePathName(xmlFile)
    if Files.notExists(Paths.get(path)) then
      createEmptyXmlFile(path, "accessLog")
    path

  def writeAccessLog(username: String, role: String, date: String, time: String): Boolean = {
    import pkg.d.util.IdGen
    import pkg.d.util.Util.inIdsFilePathName

    val log = AccessLog(
      IdGen(inIdsFilePathName("accessLogId")),
      username,
      role,
      date,
      time
    )

    log.recordInsert(log, log.defaultXmlFilePathName)
  }
