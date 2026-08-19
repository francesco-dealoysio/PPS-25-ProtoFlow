package pkg.b.logic

import pkg.b.logic.Entity
import pkg.d.util.Logger.logger
import pkg.c.data.Xml.{createEmptyXmlFile, insertElemIntoXML}
import pkg.d.util.Util.inDocumentsFilePathName

case class RegisteredDocument(
                         private var id: String = "",
                         private var documentDate: String = "",
                         private var documentTime: String = "",
                         private var documentProtocol: String = "",
                         private var documentType: String = "",
                         private var sender: String = "",
                         private var recipient: String = "",
                         private var subject: String = "",
                         private var remarks: String = "",
                         private var loadedDate: String = "",
                         private var loadedTime: String = "",
                         private var loadedBy: String = "",
                         private var protocolNumber: String = "",
                         private var registeredDate: String = "",
                         private var registeredTime: String = "",
                         private var registeredBy: String = ""
                       ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setDocumentDate(value: String): Unit = documentDate = value
  def setDocumentTime(value: String): Unit = documentTime = value
  def setDocumentProtocol(value: String): Unit = documentProtocol = value
  def setDocumentType(value: String): Unit = documentType = value
  def setSender(value: String): Unit = sender = value
  def setRecipient(value: String): Unit = recipient = value
  def setSubject(value: String): Unit = subject = value
  def setRemarks(value: String): Unit = remarks = value
  def setLoadedDate(value: String): Unit = loadedDate = value
  def setLoadedTime(value: String): Unit = loadedTime = value
  def setLoadedBy(value: String): Unit = loadedBy = value
  def setProtocolNumber(value: String): Unit = protocolNumber = value
  def setRegisteredDate(value: String): Unit = registeredDate = value
  def setRegisteredTime(value: String): Unit = registeredTime = value
  def setRegisteredBy(value: String): Unit = registeredBy = value

  def getId: String = id
  def getDocumentDate: String = documentDate
  def getDocumentTime: String = documentTime
  def getDocumentProtocol: String = documentProtocol
  def getDocumentType: String = documentType
  def getSender: String = sender
  def getRecipient: String = recipient
  def getSubject: String = subject
  def getRemarks: String = remarks
  def getLoadedDate: String = loadedDate
  def getLoadedTime: String = loadedTime
  def getLoadedBy: String = loadedBy
  def getProtocolNumber: String = protocolNumber
  def getRegisteredDate: String = registeredDate
  def getRegisteredTime: String = registeredTime
  def getRegisteredBy: String = registeredBy

  override def xmlFile = "registered.xml"

  override protected def defaultXmlFilePathName: String =
    val path = inDocumentsFilePathName(xmlFile)
    if java.nio.file.Files.notExists(java.nio.file.Paths.get(path)) then
      createEmptyXmlFile(path, "registeredDocuments")
    path

  override def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[RegisteredDocument]
      val id = record.id
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
        if result then
          DocumentLog().writeDocumentOperationLog(
            record.id,
            "registering",
            record.registeredDate,
            record.registeredTime,
            record.registeredBy
          )
      else
        throw new RuntimeException("Valore duplicato (id)!")
    catch
      case e: Exception =>
        logger(e)
    result

@main def tryRegisteredDocument: Unit =
  println("Tested in RegisteredDocumentTest")
