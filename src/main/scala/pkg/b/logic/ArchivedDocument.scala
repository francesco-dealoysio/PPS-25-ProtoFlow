package pkg.b.logic

import pkg.d.util.Util.inDocumentsFilePathName
import pkg.c.data.Xml.{createEmptyXmlFile, insertElemIntoXML}
import pkg.d.util.Logger.*
import java.nio.file.{Files, Paths}

case class ArchivedDocument(
                             private var id: String = "",

                             // Dati originali del documento
                             private var documentDate: String = "",
                             private var documentTime: String = "",
                             private var documentProtocol: String = "",
                             private var documentType: String = "",
                             private var sender: String = "",
                             private var recipient: String = "",
                             private var subject: String = "",
                             private var remarks: String = "",

                             // Dati della presa in carico
                             private var loadedDate: String = "",
                             private var loadedTime: String = "",
                             private var loadedBy: String = "",

                             // Dati della protocollazione
                             private var protocolNumber: String = "",
                             private var registeredDate: String = "",
                             private var registeredTime: String = "",
                             private var registeredBy: String = "",

                             // Dati dell'archiviazione
                             private var archivedDate: String = "",
                             private var archivedTime: String = "",
                             private var archivedBy: String = "",
                             private var archiveLocation: String = "",

                             private var classification: String = ""
                           ) extends Entity:

  def this() =
    this( "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    
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
  def setArchivedDate(value: String): Unit = archivedDate = value
  def setArchivedTime(value: String): Unit = archivedTime = value
  def setArchivedBy(value: String): Unit = archivedBy = value
  def setArchiveLocation(value: String): Unit = archiveLocation = value
  def setClassification(value: String): Unit = classification = value
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
  def getArchivedDate: String = archivedDate
  def getArchivedTime: String = archivedTime
  def getArchivedBy: String = archivedBy
  def getArchiveLocation: String = archiveLocation
  def getClassification: String = classification

  override def xmlFile: String = "archived.xml"

  override protected def defaultXmlFilePathName: String =
    val path = inDocumentsFilePathName(xmlFile)
    if Files.notExists(Paths.get(path)) then
      createEmptyXmlFile(path, "archivedDocuments")
    path

  override def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[ArchivedDocument]
      val id = record.id
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
        if result then
          DocumentLog().writeDocumentOperationLog(
            record.id,
            "archiving",
            record.archivedDate,
            record.archivedTime,
            record.archivedBy
          )
      else
        throw new RuntimeException("Valore duplicato (id)!")
    catch
      case e: Exception =>
        logger(e)
    result