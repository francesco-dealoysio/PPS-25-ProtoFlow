package pkg.b.logic

import pkg.b.logic.Entity
import pkg.d.util.Logger.*
import pkg.d.util.Util.inLogFilePathName

case class DocumentLog(
                     private var id: String = "",
                     private var documentId: String = "",
                     private var operationType: String = "",
                     private var processedDate: String = "",
                     private var processedTime: String = "",
                     private var processedBy: String = ""                      
                   ) extends Entity:
  def this() =
    this("", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setDocumentId(value: String): Unit = documentId = value
  def setOperationType(value: String): Unit = operationType = value
  def setProcessedDate(value: String): Unit = processedDate = value
  def setProcessedTime(value: String): Unit = processedTime = value
  def setProcessedBy(value: String): Unit = processedBy = value

  def getId: String = id
  def getDocumentId: String = documentId
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

  def writeDocumentOperationLog(
                         documentId: String,
                         operationType: String,
                         operationDate: String,
                         operationTime: String,
                         operator: String
                       ): Boolean = {
    import pkg.d.util.IdGen
    import pkg.d.util.Util.inIdsFilePathName

    val logDocumentOperation = DocumentLog(
      IdGen(inIdsFilePathName("documentOperationlogId")),
      documentId,
      operationType,
      operationDate,
      operationTime,
      operator
    )

    logDocumentOperation.recordInsert(logDocumentOperation,DocumentLog().defaultXmlFilePathName)
  }