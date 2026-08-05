package pkg.b.logic

import pkg.b.logic.Entity
import pkg.d.util.Logger.*
import pkg.d.util.Util.inLogFilePathName

case class DocumentLog(
                     private var id: String = "",
                     private var operationType: String = "",
                     private var processedDate: String = "",
                     private var processedTime: String = "",
                     private var processedBy: String = ""                      
                   ) extends Entity:
  def this() =
    this("", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setOperationType(value: String): Unit = operationType = value
  def setProcessedDate(value: String): Unit = processedDate = value
  def setProcessedTime(value: String): Unit = processedTime = value
  def setProcessedBy(value: String): Unit = processedBy = value

  def getId: String = id
  def getOperationType: String = operationType
  def getProcessedDate: String = processedDate
  def getProcessedTime: String = processedTime
  def getProcessedBy: String = processedBy
  
  override def xmlFile = "documentOperations.xml"

  override protected def defaultXmlFilePathName: String =
    try
      inLogFilePathName(xmlFile)
    catch
      case e: Exception =>
        logger(e); ""

@main def tryDocumentLog: Unit =
  println("Tested in DocumentLogTest.scala")
