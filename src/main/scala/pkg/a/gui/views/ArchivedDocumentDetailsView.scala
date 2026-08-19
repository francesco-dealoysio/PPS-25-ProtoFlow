package pkg.a.gui.views

import pkg.a.gui.text.UiText.ArchivedDocuments.{Details as Text, Fields as ArchiveFields}
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.traits.Form
import pkg.b.logic.ArchivedDocument
import scalafx.geometry.Pos
import scalafx.scene.layout.{BorderPane, HBox, Priority}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf

object ArchivedDocumentDetailsView extends Form:

  def apply(selectedDocument: ArchivedDocument, onExit: () => Unit = () => ()): BorderPane =
    val protocolNumber = readOnlyStringField(selectedDocument.getProtocolNumber)
    val classification = readOnlyStringField(selectedDocument.getClassification)
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

    documentForm.maxWidth = Double.MaxValue
    archiveForm.maxWidth = Double.MaxValue

    val form =
      new HBox:
        spacing = 30
        alignment = Pos.TopCenter
        maxWidth = Double.MaxValue
        children = Seq(
          documentForm,
          archiveForm
        )

    HBox.setHgrow(documentForm, Priority.Always)
    HBox.setHgrow(archiveForm, Priority.Always)

    val result = createResultMessage()

    def printDocumentDetails(): Unit =
      val safeProtocolNumber =
        selectedDocument.getProtocolNumber.replaceAll("[^a-zA-Z0-9_-]", "_")

      val printed =
        XmlToPdf.printDetails(
          xmlPath = inDocumentsFilePathName("archived.xml"),
          recordId = selectedDocument.getId,
          pdfFileName = s"${Text.PrintFileNamePrefix}_$safeProtocolNumber",
          title = Text.PrintTitle
        )

      result.show(
        message = if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val exitButton = closeButton(onExit)
    val printButton = secondaryButton(Buttons.Print, printDocumentDetails)

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton, printButton))
    )