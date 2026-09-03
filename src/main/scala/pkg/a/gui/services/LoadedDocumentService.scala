package pkg.a.gui.services

import pkg.b.logic.{LoadedDocument, RegisteredDocument}
import pkg.d.util.DateTime.{localDate, localTime}
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, inDocumentsFilePathName}

object  LoadedDocumentService:

  private val loadedDocumentLogic = new LoadedDocument()
  private val registeredDocumentLogic = new RegisteredDocument()

  def addLoadedDocument(
                          documentDate: String,
                          documentProtocol: String,
                          documentType: String,
                          sender: String,
                          recipient: String,
                          subject: String,
                          remarks: String,
                          operatorUsername: String,
                          loadedFilePathName: String = inDocumentsFilePathName("loaded.xml"),
                          loadedDocumentIdFilePathName: String = inIdsFilePathName("loadedDocumentId")
                        ): Either[String, LoadedDocument] =

    val newDocument =
      LoadedDocument(
        id = IdGen(loadedDocumentIdFilePathName),
        documentDate = documentDate,
        documentProtocol = documentProtocol,
        documentType = documentType,
        sender = sender,
        recipient = recipient,
        subject = subject,
        remarks = remarks,
        processedDate = localDate,
        processedTime = localTime,
        processedBy = operatorUsername
      )

    if loadedDocumentLogic.recordInsert(newDocument, loadedFilePathName) then Right(newDocument)
    else Left("Errore durante la presa in carico del documento")

  def getLoadedDocuments: List[LoadedDocument] =
    loadedDocumentLogic.getRecords[LoadedDocument]().toList

  def getLoadedDocuments(predicate: LoadedDocument => Boolean): List[LoadedDocument] =
    loadedDocumentLogic.getRecordsByFilter[LoadedDocument](predicate).toList

  def deleteLoadedDocument(id: String): Boolean =
    loadedDocumentLogic.recordDelete(id)

  def getRegisteredDocuments: List[RegisteredDocument] =
    registeredDocumentLogic.getRecords[RegisteredDocument]().toList

  def getRegisteredDocuments(predicate: RegisteredDocument => Boolean): List[RegisteredDocument] =
    registeredDocumentLogic.getRecordsByFilter[RegisteredDocument](predicate).toList

  def deleteRegisteredDocument(id: String): Boolean =
    registeredDocumentLogic.recordDelete(id)

  def registerDocument(source: LoadedDocument,
                       operatorUsername: String,
                       classification: String,
                       loadedFilePathName: String = inDocumentsFilePathName("loaded.xml"),
                       registeredFilePathName: String = inDocumentsFilePathName("registered.xml")
                      ): Either[String, RegisteredDocument] =

    val protocolNumber = s"${localDate.take(4)}/${source.getId}/${classification.trim}"

    val registered =
      RegisteredDocument(
        id = source.getId,
        documentDate = source.getDocumentDate,
        documentProtocol = source.getDocumentProtocol,
        documentType = source.getDocumentType,
        sender = source.getSender,
        recipient = source.getRecipient,
        subject = source.getSubject,
        remarks = source.getRemarks,
        loadedDate = source.getProcessedDate,
        loadedTime = source.getProcessedTime,
        loadedBy = source.getProcessedBy,
        protocolNumber = protocolNumber,
        registeredDate = localDate,
        registeredTime = localTime,
        registeredBy = operatorUsername,
        classification = classification.trim
      )

    if classification.trim.isEmpty then
      Left("Seleziona una classifica")
    else
      saveRegisteredDocument(source, registered, loadedFilePathName, registeredFilePathName)


  private def saveRegisteredDocument(source: LoadedDocument,
                                     registered: RegisteredDocument,
                                     loadedFilePathName: String,
                                     registeredFilePathName: String
                                    ): Either[String, RegisteredDocument] =

    val inserted = registeredDocumentLogic.recordInsert(registered, registeredFilePathName)

    if !inserted then
      Left("Errore durante la protocollazione del documento")
    else
      val removed = loadedDocumentLogic.recordDelete(source.getId, loadedFilePathName)

      if removed then
        Right(registered)
      else
        rollbackRegisteredDocument(registered, registeredFilePathName)
        Left("Errore durante la rimozione del documento dai presi in carico")

  private def rollbackRegisteredDocument(registered: RegisteredDocument, registeredFilePathName: String): Unit =
    registeredDocumentLogic.recordDelete(registered.getId, registeredFilePathName)