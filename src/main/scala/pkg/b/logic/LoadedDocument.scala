package pkg.b.logic

import pkg.b.logic.Entity

case class LoadedDocument(
                         private var id: String = "",
                         private var documentDate: String = "",
                         private var documentProtocol: String = "",
                         private var documentType: String = "",
                         private var sender: String = "",
                         private var recipient: String = "",
                         private var subject: String = "",
                         private var remarks: String = "",
                         private var state: String = "",
                         private var processedDate: String = "",
                         private var processedTime: String = "",
                         private var processedBy: String = ""
                       ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setDocumentDate(value: String): Unit = documentDate = value
  def setDocumentProtocol(value: String): Unit = documentProtocol = value
  def setDocumentType(value: String): Unit = documentType = value
  def setSender(value: String): Unit = sender = value
  def setRecipient(value: String): Unit = recipient = value
  def setSubject(value: String): Unit = subject = value
  def setRemarks(value: String): Unit = remarks = value
  def setState(value: String): Unit = state = value
  def setProcessedDate(value: String): Unit = processedDate = value
  def setProcessedTime(value: String): Unit = processedTime = value
  def setProcessedBy(value: String): Unit = processedBy = value

  def getId: String = id
  def getDocumentDate: String = documentDate
  def getDocumentProtocol: String = documentProtocol
  def getDocumentType: String = documentType
  def getSender: String = sender
  def getRecipient: String = recipient
  def getSubject: String = subject
  def getRemarks: String = remarks
  def getState: String = state
  def getProcessedDate: String = processedDate
  def getProcessedTime: String = processedTime
  def getProcessedBy: String = processedBy

  override def xmlFile = "loaded.xml"

@main def tryLoadedDocument: Unit =
  println("Tested in LoadedDocumentTest")
