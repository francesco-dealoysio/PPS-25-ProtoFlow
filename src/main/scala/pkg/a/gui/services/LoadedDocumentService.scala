package pkg.a.gui.services

import pkg.b.logic.{LoadedDocument, RegisteredDocument}
import pkg.d.util.DateTime.{localDate, localTime}

import java.time.LocalDate

class LoadedDocumentService:

  private val loadedDocumentLogic = new LoadedDocument()
  private val registeredDocumentLogic = new RegisteredDocument()

  def getLoadedDocuments: List[LoadedDocument] =
    loadedDocumentLogic.getRecords[LoadedDocument]().toList

  def getLoadedDocuments(predicate: Any => Boolean): List[LoadedDocument] =
    loadedDocumentLogic.getRecordsByFilter[LoadedDocument](predicate).toList

  def deleteLoadedDocument(id: String): Boolean =
    loadedDocumentLogic.recordDelete(id)

  def getRegisteredDocuments: List[RegisteredDocument] =
    registeredDocumentLogic.getRecords[RegisteredDocument]().toList

  def getRegisteredDocuments(predicate: Any => Boolean): List[RegisteredDocument] =
    registeredDocumentLogic.getRecordsByFilter[RegisteredDocument](predicate).toList

  def deleteRegisteredDocument(id: String): Boolean =
    registeredDocumentLogic.recordDelete(id)

  /**
   * Genera un numero di protocollo, crea il RegisteredDocument dai dati del form (eventualmente
   * corretti dall'operatore) e sposta il documento da "presi in carico" a "protocollati".
   */
  def registerDocument(source: LoadedDocument, edited: LoadedDocument, operatorUsername: String): Either[String, RegisteredDocument] =

    val protocolNumber = s"${LocalDate.now().getYear}/${source.getId}"

    val registered =
      RegisteredDocument(
        id = source.getId,
        documentDate = edited.getDocumentDate,
        documentTime = edited.getDocumentTime,
        documentProtocol = edited.getDocumentProtocol,
        documentType = edited.getDocumentType,
        sender = edited.getSender,
        recipient = edited.getRecipient,
        subject = edited.getSubject,
        remarks = edited.getRemarks,
        loadedDate = source.getProcessedDate,
        loadedTime = source.getProcessedTime,
        loadedBy = source.getProcessedBy,
        protocolNumber = protocolNumber,
        registeredDate = localDate,
        registeredTime = localTime,
        registeredBy = operatorUsername
      )

    if !registeredDocumentLogic.recordInsert(registered) then
      Left("Errore durante la protocollazione del documento")
    else if !loadedDocumentLogic.recordDelete(source.getId) then
      Left("Errore durante la rimozione del documento dai presi in carico")
    else
      Right(registered)
