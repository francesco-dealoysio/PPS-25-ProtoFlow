package pkg.a.gui.services

import pkg.b.logic.{ArchivedDocument, RegisteredDocument}
import pkg.d.util.Logger.logger
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.a.gui.validators.DocumentArchivingValidator

object ArchivedDocumentService:

  private val registeredDocumentLogic = new RegisteredDocument()
  private val archivedDocumentLogic = new ArchivedDocument()
  private val documentArchivingValidator = new DocumentArchivingValidator()

  def getRegisteredDocuments: List[RegisteredDocument] =
    registeredDocumentLogic.getRecords[RegisteredDocument]().toList

  def getArchivedDocuments: List[ArchivedDocument] =
    archivedDocumentLogic.getRecords[ArchivedDocument]().toList

  def getArchivedDocuments(predicate: Any => Boolean): List[ArchivedDocument] =
    archivedDocumentLogic.getRecordsByFilter[ArchivedDocument](predicate).toList

  def deleteArchivedDocument(id: String): Boolean =
    archivedDocumentLogic.recordDelete(id)

  def archiveDocument(
                       source: RegisteredDocument,
                       archivedDate: String,
                       archivedTime: String,
                       operatorUsername: String,
                       archiveLocation: String,
                       registeredFilePathName: String = inDocumentsFilePathName("registered.xml"),
                       archivedFilePathName: String = inDocumentsFilePathName("archived.xml")
                     ): Either[String, ArchivedDocument] =

    validateArchiving(
      source = source,
      archivedDate = archivedDate,
      archivedTime = archivedTime,
      operatorUsername = operatorUsername,
      archiveLocation = archiveLocation,
      archivedFilePathName = archivedFilePathName
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
              archiveLocation = archiveLocation
            )
          saveArchivedDocument(source, archived, registeredFilePathName, archivedFilePathName)

        catch
          case exception: Exception =>
            logger(exception)
            Left("Errore imprevisto durante l'archiviazione del documento")

  private def validateArchiving(
                                 source: RegisteredDocument,
                                 archivedDate: String,
                                 archivedTime: String,
                                 operatorUsername: String,
                                 archiveLocation: String,
                                 archivedFilePathName: String
                               ): Option[String] =
    if source == null then
      Some("Documento non valido")
    else if source.getId.trim.isEmpty then
      Some("Id documento non valido")
    else if isAlreadyArchived(source, archivedFilePathName) then
      Some("Il documento risulta già archiviato")
    else
      documentArchivingValidator
        .validate(archivedDate, archivedTime, operatorUsername, archiveLocation)
        .headOption

  private def isAlreadyArchived(source: RegisteredDocument, archivedFilePathName: String): Boolean =
    archivedDocumentLogic
      .getRecords[ArchivedDocument](archivedFilePathName)
      .exists: archived =>
        archived.getProtocolNumber.trim.nonEmpty &&
          archived.getProtocolNumber.equalsIgnoreCase(source.getProtocolNumber)

  private def buildArchivedDocument(
                                     source: RegisteredDocument,
                                     archivedDate: String,
                                     archivedTime: String,
                                     operatorUsername: String,
                                     archiveLocation: String
                                   ): ArchivedDocument =
    
    ArchivedDocument(
      id = source.getId,
      documentDate = source.getDocumentDate,
      documentProtocol = source.getDocumentProtocol,
      documentType = source.getDocumentType,
      sender = source.getSender,
      recipient = source.getRecipient,
      subject = source.getSubject,
      remarks = source.getRemarks,
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
      classification = source.getClassification
    )

  private def saveArchivedDocument(source: RegisteredDocument, archived: ArchivedDocument, registeredFilePathName: String, archivedFilePathName: String): Either[String, ArchivedDocument] =
    val inserted = archivedDocumentLogic.recordInsert(archived, archivedFilePathName)
    if !inserted then
      Left("Errore durante il salvataggio del documento archiviato")
    else
      val removed = registeredDocumentLogic.recordDelete(source.getId, registeredFilePathName)
      if removed then
        Right(archived)
      else
        rollbackArchivedDocument(archived, archivedFilePathName)
        Left("Errore durante la rimozione del documento dai protocollati")

  private def rollbackArchivedDocument(archived: ArchivedDocument, archivedFilePathName: String): Unit =
    archivedDocumentLogic.recordDelete(archived.getId, archivedFilePathName)