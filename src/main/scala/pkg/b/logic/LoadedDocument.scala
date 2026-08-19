package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.insertElemIntoXML
import pkg.d.util.Logger.*
import pkg.d.util.Util.inDocumentsFilePathName

case class LoadedDocument(
                         private var id: String = "",
                         private var documentDate: String = "",
                         private var documentTime: String = "",
                         private var documentProtocol: String = "",
                         private var documentType: String = "",
                         private var sender: String = "",
                         private var recipient: String = "",
                         private var subject: String = "",
                         private var remarks: String = "",
                         private var processedDate: String = "",
                         private var processedTime: String = "",
                         private var processedBy: String = ""
                       ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setDocumentDate(value: String): Unit = documentDate = value
  def setDocumentTime(value: String): Unit = documentTime = value
  def setDocumentProtocol(value: String): Unit = documentProtocol = value
  def setDocumentType(value: String): Unit = documentType = value
  def setSender(value: String): Unit = sender = value
  def setRecipient(value: String): Unit = recipient = value
  def setSubject(value: String): Unit = subject = value
  def setRemarks(value: String): Unit = remarks = value
  def setProcessedDate(value: String): Unit = processedDate = value
  def setProcessedTime(value: String): Unit = processedTime = value
  def setProcessedBy(value: String): Unit = processedBy = value

  def getId: String = id
  def getDocumentDate: String = documentDate
  def getDocumentTime: String = documentTime
  def getDocumentProtocol: String = documentProtocol
  def getDocumentType: String = documentType
  def getSender: String = sender
  def getRecipient: String = recipient
  def getSubject: String = subject
  def getRemarks: String = remarks
  def getProcessedDate: String = processedDate
  def getProcessedTime: String = processedTime
  def getProcessedBy: String = processedBy

  override def xmlFile = "loaded.xml"

  override protected def defaultXmlFilePathName: String =
    inDocumentsFilePathName(xmlFile)

  override def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[LoadedDocument]
      val id = record.id
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
        if result then
          DocumentLog().writeDocumentOperationLog(
            record.id,
            "loading",
            record.processedDate,
            record.processedTime,
            record.processedBy
          )
      else
        throw new RuntimeException("Valore duplicato (id)!")
    catch
      case e: Exception =>
        logger(e)
    result

@main def tryLoadedDocument: Unit =
  println("Tested in LoadedDocumentTest")
