package pkg.b.logic

import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, localDate, localTime}

import java.time.LocalDate

class LoadedDocumentService:

  private val loadedDocumentLogic = new LoadedDocument()
  private val registeredDocumentLogic = new RegisteredDocument()

  def getLoadedDocuments(): List[LoadedDocument] =
    loadedDocumentLogic.getRecords[LoadedDocument]().toList

  def deleteLoadedDocument(id: String): Boolean =
    loadedDocumentLogic.recordDelete(id)

  def getRegisteredDocuments(): List[RegisteredDocument] =
    registeredDocumentLogic.getRecords[RegisteredDocument]().toList

  def deleteRegisteredDocument(id: String): Boolean =
    registeredDocumentLogic.recordDelete(id)

  /**
   * Genera un numero di protocollo, crea il RegisteredDocument dai dati del form (eventualmente
   * corretti dall'operatore) e sposta il documento da "presi in carico" a "protocollati".
   */
  def registerDocument(source: LoadedDocument, edited: LoadedDocument, operatorUsername: String): Either[String, RegisteredDocument] =
    val newId = IdGen(inIdsFilePathName("registeredDocumentId"))

    val protocolNumber =
      f"${LocalDate.now().getYear}%d/${newId.toIntOption.getOrElse(0)}%06d"

    val registered =
      RegisteredDocument(
        id = newId,
        documentDate = edited.getDocumentDate,
        documentTime = edited.getDocumentTime,
        documentProtocol = edited.getDocumentProtocol,
        documentType = edited.getDocumentType,
        sender = edited.getSender,
        recipient = edited.getRecipient,
        subject = edited.getSubject,
        remarks = edited.getRemarks,
        state = "registered",
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
