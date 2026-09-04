package pkg.a.gui.views

import pkg.a.gui.text.UiText.ArchivedDocuments.{Details as Text, Fields as ArchiveFields}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.DocumentManagementControl.Fields as ManagementFields
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.traits.Form
import pkg.b.logic.ArchivedDocument
import pkg.b.logic.pdf.{PdfDetailsCreator, PdfViewer}
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.scene.layout.BorderPane

object ArchivedDocumentDetailsView extends Form:

  def apply(selectedDocument: ArchivedDocument, onExit: () => Unit = () => ()): BorderPane =
    val protocolNumber = readOnlyStringField(selectedDocument.getProtocolNumber)
    val classification = readOnlyStringField(selectedDocument.getClassification)
    val loadedDate = readOnlyStringField(selectedDocument.getLoadedDate)
    val loadedTime = readOnlyStringField(selectedDocument.getLoadedTime)
    val loadedBy = readOnlyStringField(selectedDocument.getLoadedBy)
    val registeredDate = readOnlyStringField(selectedDocument.getRegisteredDate)
    val registeredTime = readOnlyStringField(selectedDocument.getRegisteredTime)
    val registeredBy = readOnlyStringField(selectedDocument.getRegisteredBy)
    val documentType = readOnlyStringField(selectedDocument.getDocumentType)
    val sender = readOnlyStringField(selectedDocument.getSender)
    val recipient = readOnlyStringField(selectedDocument.getRecipient)
    val subject = readOnlyStringField(selectedDocument.getSubject)
    val remarks = readOnlyAreaField(selectedDocument.getRemarks)
    val archivedDate = readOnlyStringField(selectedDocument.getArchivedDate)
    val archivedTime = readOnlyStringField(selectedDocument.getArchivedTime)
    val archivedBy = readOnlyStringField(selectedDocument.getArchivedBy)
    val archiveLocation = readOnlyStringField(selectedDocument.getArchiveLocation)

    val documentForm =
      formGrid(
        Seq(
          formRow(CommonDocumentFields.ProtocolNumber, protocolNumber),
          formRow(Labels.Classification, classification),
          formRow(ManagementFields.LoadedDate, loadedDate),
          formRow(ManagementFields.LoadedTime, loadedTime),
          formRow(DocumentFields.ProcessedBy, loadedBy),
          formRow(RegistrationFields.RegisteredDate, registeredDate),
          formRow(RegistrationFields.RegisteredTime, registeredTime),
          formRow(RegistrationFields.RegisteredBy, registeredBy),
          formRow(DocumentFields.DocumentType, documentType),
          formRow(CommonDocumentFields.Sender, sender),
          formRow(CommonDocumentFields.Recipient, recipient),
          formRow(CommonDocumentFields.Subject, subject),
          formRow(DocumentFields.Remarks, remarks)
        )
      )

    val archiveForm =
      formGrid(
        Seq(
          formRow(ArchiveFields.ArchivedDate, archivedDate),
          formRow(ArchiveFields.ArchivedTime, archivedTime),
          formRow(ArchiveFields.ArchivedBy, archivedBy),
          formRow(ArchiveFields.ArchiveLocation, archiveLocation)
        )
      )

    val form = twoColumnForm(documentForm, archiveForm)

    val result = createResultMessage()

    def printDocumentDetails(): Unit =
      val safeProtocolNumber = selectedDocument.getProtocolNumber.replaceAll("[^a-zA-Z0-9_-]", "_")
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileNamePrefix}_$safeProtocolNumber.pdf")
      val fields =
        Seq(
          CommonDocumentFields.Id -> selectedDocument.getId,
          DocumentFields.DocumentDate -> selectedDocument.getDocumentDate,
          DocumentFields.DocumentProtocol -> selectedDocument.getDocumentProtocol,
          DocumentFields.DocumentType -> selectedDocument.getDocumentType,
          CommonDocumentFields.Sender -> selectedDocument.getSender,
          CommonDocumentFields.Recipient -> selectedDocument.getRecipient,
          CommonDocumentFields.Subject -> selectedDocument.getSubject,
          DocumentFields.Remarks -> selectedDocument.getRemarks,
          ManagementFields.LoadedDate -> selectedDocument.getLoadedDate,
          ManagementFields.LoadedTime -> selectedDocument.getLoadedTime,
          DocumentFields.ProcessedBy -> selectedDocument.getLoadedBy,
          CommonDocumentFields.ProtocolNumber -> selectedDocument.getProtocolNumber,
          RegistrationFields.RegisteredDate -> selectedDocument.getRegisteredDate,
          RegistrationFields.RegisteredTime -> selectedDocument.getRegisteredTime,
          RegistrationFields.RegisteredBy -> selectedDocument.getRegisteredBy,
          ArchiveFields.ArchivedDate -> selectedDocument.getArchivedDate,
          ArchiveFields.ArchivedTime -> selectedDocument.getArchivedTime,
          ArchiveFields.ArchivedBy -> selectedDocument.getArchivedBy,
          ArchiveFields.ArchiveLocation -> selectedDocument.getArchiveLocation,
          Labels.Classification -> selectedDocument.getClassification
        )

      val printed = PdfDetailsCreator.createDetailsPdf(pdfPath, Text.PrintTitle, fields)

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        message = if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(closeButton(onExit), printButton(printDocumentDetails)))
    )