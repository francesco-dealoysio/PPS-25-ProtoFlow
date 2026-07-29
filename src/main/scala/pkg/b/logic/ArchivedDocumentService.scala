package pkg.b.logic

import pkg.d.util.IdGen
import pkg.d.util.Logger.logger
import pkg.d.util.Util.inIdsFilePathName

import java.time.{LocalDate, LocalTime}
import scala.util.Try

class ArchivedDocumentService:

  private val registeredDocumentLogic = new RegisteredDocument()
  private val archivedDocumentLogic = new ArchivedDocument()

  def getRegisteredDocuments: List[RegisteredDocument] =
    registeredDocumentLogic
      .getRecords[RegisteredDocument]()
      .filter(_.getState.equalsIgnoreCase("registered"))
      .toList

  def getArchivedDocuments: List[ArchivedDocument] =
    archivedDocumentLogic
      .getRecords[ArchivedDocument]()
      .toList

  def deleteArchivedDocument(id: String): Boolean =
    archivedDocumentLogic.recordDelete(id)

  def archiveDocument(
                       source: RegisteredDocument,
                       archivedDate: String,
                       archivedTime: String,
                       operatorUsername: String,
                       archiveLocation: String,
                       archiveRemarks: String
                     ): Either[String, ArchivedDocument] =

    validateArchiving(
      source = source,
      archivedDate = archivedDate,
      archivedTime = archivedTime,
      operatorUsername = operatorUsername
    ) match
      case Some(error) =>
        Left(error)

      case None =>
        try
          val archived =
            buildArchivedDocument(
              source = source,
              archivedDate = archivedDate,
              archivedTime = archivedTime,
              operatorUsername = operatorUsername,
              archiveLocation = archiveLocation,
              archiveRemarks = archiveRemarks
            )
          saveArchivedDocument(source, archived)

        catch
          case exception: Exception =>
            logger(exception)
            Left("Errore imprevisto durante l'archiviazione del documento")

  private def validateArchiving(
                                 source: RegisteredDocument,
                                 archivedDate: String,
                                 archivedTime: String,
                                 operatorUsername: String
                               ): Option[String] =

    if source == null then
      Some("Documento protocollato non valido")
    else if source.getId.trim.isEmpty then
      Some("Documento protocollato non valido")
    else if !source.getState.equalsIgnoreCase("registered") then
      Some("Il documento non risulta protocollato")
    else if isAlreadyArchived(source) then
      Some("Il documento risulta già archiviato")
    else if operatorUsername.trim.isEmpty then
      Some("L'operatore archiviatore è obbligatorio")
    else if !isValidDate(archivedDate) then
      Some("La data di archiviazione non è valida")
    else if !isValidTime(archivedTime) then
      Some("L'ora di archiviazione non è valida")
    else
      None

  private def isAlreadyArchived(source: RegisteredDocument): Boolean =
    archivedDocumentLogic
      .getRecords[ArchivedDocument]()
      .exists: archived =>
        archived.getProtocolNumber.trim.nonEmpty &&
          archived.getProtocolNumber.equalsIgnoreCase(source.getProtocolNumber)

  private def buildArchivedDocument(
                                     source: RegisteredDocument,
                                     archivedDate: String,
                                     archivedTime: String,
                                     operatorUsername: String,
                                     archiveLocation: String,
                                     archiveRemarks: String
                                   ): ArchivedDocument =
    
    ArchivedDocument(
      id = IdGen(inIdsFilePathName("archivedDocumentId")),
      documentDate = source.getDocumentDate,
      documentTime = source.getDocumentTime,
      documentProtocol = source.getDocumentProtocol,
      documentType = source.getDocumentType,
      sender = source.getSender,
      recipient = source.getRecipient,
      subject = source.getSubject,
      remarks = source.getRemarks,
      state = "archived",
      loadedDate = source.getLoadedDate,
      loadedTime = source.getLoadedTime,
      loadedBy = source.getLoadedBy,
      protocolNumber = source.getProtocolNumber,
      registeredDate = source.getRegisteredDate,
      registeredTime = source.getRegisteredTime,
      registeredBy = source.getRegisteredBy,
      archivedDate = archivedDate.trim,
      archivedTime = archivedTime.trim,
      archivedBy = operatorUsername.trim,
      archiveLocation = archiveLocation.trim,
      archiveRemarks = archiveRemarks.trim
    )

  private def saveArchivedDocument(source: RegisteredDocument, archived: ArchivedDocument): Either[String, ArchivedDocument] =
    val inserted = archivedDocumentLogic.recordInsert(archived)
    if !inserted then
      Left("Errore durante il salvataggio del documento archiviato")
    else
      val removed = registeredDocumentLogic.recordDelete(source.getId)
      if removed then
        Right(archived)
      else
        rollbackArchivedDocument(archived)
        Left("Errore durante la rimozione del documento dai protocollati")

  private def rollbackArchivedDocument(archived: ArchivedDocument): Unit =
    archivedDocumentLogic.recordDelete(archived.getId)

  private def isValidDate(value: String): Boolean =
    value.trim.nonEmpty && Try(LocalDate.parse(value.trim)).isSuccess

  private def isValidTime(value: String): Boolean =
    value.trim.nonEmpty && Try(LocalTime.parse(value.trim)).isSuccess