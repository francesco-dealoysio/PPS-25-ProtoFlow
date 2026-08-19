package pkg.a.gui.services

import pkg.b.logic.{ArchivedDocument, LoadedDocument, RegisteredDocument}

object DocumentManagementControlService:

  case class ManagedDocument(
                               id: String,
                               stage: String,
                               documentType: String,
                               sender: String,
                               recipient: String,
                               subject: String,
                               remarks: String,
                               loadedDate: String,
                               loadedTime: String,
                               loadedBy: String,
                               protocolNumber: String,
                               registeredDate: String,
                               registeredTime: String,
                               registeredBy: String,
                               archivedDate: String,
                               archivedTime: String,
                               archivedBy: String,
                               archiveLocation: String,
                               operator: String
                             )

  object Stages:
    val Loading = "loading"
    val Registering = "registering"
    val Archiving = "archiving"

  def getManagedDocuments(
                            loadedFilePathName: String = "",
                            registeredFilePathName: String = "",
                            archivedFilePathName: String = ""
                          ): List[ManagedDocument] =
    val fromLoaded = loadedDocuments(loadedFilePathName).map(toManagedDocument)
    val fromRegistered = registeredDocuments(registeredFilePathName).map(toManagedDocument)
    val fromArchived = archivedDocuments(archivedFilePathName).map(toManagedDocument)

    (fromLoaded ++ fromRegistered ++ fromArchived)
      .sortBy(_.id.toIntOption.getOrElse(Int.MaxValue))
      .toList

  private def loadedDocuments(xmlFilePathName: String): Seq[LoadedDocument] =
    if xmlFilePathName.isEmpty then LoadedDocument().getRecords[LoadedDocument]()
    else LoadedDocument().getRecords[LoadedDocument](xmlFilePathName)

  private def registeredDocuments(xmlFilePathName: String): Seq[RegisteredDocument] =
    if xmlFilePathName.isEmpty then RegisteredDocument().getRecords[RegisteredDocument]()
    else RegisteredDocument().getRecords[RegisteredDocument](xmlFilePathName)

  private def archivedDocuments(xmlFilePathName: String): Seq[ArchivedDocument] =
    if xmlFilePathName.isEmpty then ArchivedDocument().getRecords[ArchivedDocument]()
    else ArchivedDocument().getRecords[ArchivedDocument](xmlFilePathName)

  private def toManagedDocument(document: LoadedDocument): ManagedDocument =
    ManagedDocument(
      id = document.getId,
      stage = Stages.Loading,
      documentType = document.getDocumentType,
      sender = document.getSender,
      recipient = document.getRecipient,
      subject = document.getSubject,
      remarks = document.getRemarks,
      loadedDate = document.getProcessedDate,
      loadedTime = document.getProcessedTime,
      loadedBy = document.getProcessedBy,
      protocolNumber = "",
      registeredDate = "",
      registeredTime = "",
      registeredBy = "",
      archivedDate = "",
      archivedTime = "",
      archivedBy = "",
      archiveLocation = "",
      operator = document.getProcessedBy
    )

  private def toManagedDocument(document: RegisteredDocument): ManagedDocument =
    ManagedDocument(
      id = document.getId,
      stage = Stages.Registering,
      documentType = document.getDocumentType,
      sender = document.getSender,
      recipient = document.getRecipient,
      subject = document.getSubject,
      remarks = document.getRemarks,
      loadedDate = document.getLoadedDate,
      loadedTime = document.getLoadedTime,
      loadedBy = document.getLoadedBy,
      protocolNumber = document.getProtocolNumber,
      registeredDate = document.getRegisteredDate,
      registeredTime = document.getRegisteredTime,
      registeredBy = document.getRegisteredBy,
      archivedDate = "",
      archivedTime = "",
      archivedBy = "",
      archiveLocation = "",
      operator = document.getRegisteredBy
    )

  private def toManagedDocument(document: ArchivedDocument): ManagedDocument =
    ManagedDocument(
      id = document.getId,
      stage = Stages.Archiving,
      documentType = document.getDocumentType,
      sender = document.getSender,
      recipient = document.getRecipient,
      subject = document.getSubject,
      remarks = document.getRemarks,
      loadedDate = document.getLoadedDate,
      loadedTime = document.getLoadedTime,
      loadedBy = document.getLoadedBy,
      protocolNumber = document.getProtocolNumber,
      registeredDate = document.getRegisteredDate,
      registeredTime = document.getRegisteredTime,
      registeredBy = document.getRegisteredBy,
      archivedDate = document.getArchivedDate,
      archivedTime = document.getArchivedTime,
      archivedBy = document.getArchivedBy,
      archiveLocation = document.getArchiveLocation,
      operator = document.getArchivedBy
    )
