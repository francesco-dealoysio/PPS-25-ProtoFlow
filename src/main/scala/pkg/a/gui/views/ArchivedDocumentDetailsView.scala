package pkg.a.gui.views

import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.text.UiText.{ArchivedDocuments, Fields, LoadedDocuments, RegisteredDocuments}
import pkg.a.gui.traits.Form
import pkg.b.logic.ArchivedDocument
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.layout.{BorderPane, HBox, Priority}

object ArchivedDocumentDetailsView extends Form:

  def apply(selectedDocument: ArchivedDocument, onExit: () => Unit = () => ()): BorderPane =
    val protocolNumber = stringField(RegisteredDocuments.Fields.ProtocolNumber, selectedDocument.getProtocolNumber)
    val registeredDate = stringField(RegisteredDocuments.Fields.RegisteredDate, selectedDocument.getRegisteredDate)
    val registeredTime = stringField(RegisteredDocuments.Fields.RegisteredTime, selectedDocument.getRegisteredTime)
    val registeredBy = stringField(RegisteredDocuments.Fields.RegisteredBy, selectedDocument.getRegisteredBy)
    val documentType = stringField(LoadedDocuments.Prompts.DocumentType, selectedDocument.getDocumentType)
    val sender = stringField(LoadedDocuments.Prompts.Sender, selectedDocument.getSender)
    val recipient = stringField(LoadedDocuments.Prompts.Recipient, selectedDocument.getRecipient)
    val subject = stringField(LoadedDocuments.Prompts.Subject, selectedDocument.getSubject)
    val remarks = areaField(LoadedDocuments.Prompts.Remarks, UiStyles.Roles.DescriptionArea, selectedDocument.getRemarks)
    val archivedDate = stringField(ArchivedDocuments.Fields.ArchivedDate, selectedDocument.getArchivedDate)
    val archivedTime = stringField(ArchivedDocuments.Prompts.ArchivedTime, selectedDocument.getArchivedTime)
    val archivedBy = stringField(ArchivedDocuments.Prompts.ArchivedBy, selectedDocument.getArchivedBy)
    val archiveLocation = stringField(ArchivedDocuments.Prompts.ArchiveLocation, selectedDocument.getArchiveLocation)
    val archiveRemarks = areaField(ArchivedDocuments.Prompts.ArchiveRemarks, UiStyles.Roles.DescriptionArea, selectedDocument.getArchiveRemarks)

    val readOnlyFields: Seq[FormField[? <: Node]] =
      Seq(
        protocolNumber,
        registeredDate,
        registeredTime,
        registeredBy,
        documentType,
        sender,
        recipient,
        subject,
        remarks,
        archivedDate,
        archivedTime,
        archivedBy,
        archiveLocation,
        archiveRemarks
      )

    readOnlyFields.foreach: field =>
      field.control.setDisable(true)

    val documentForm =
      formGrid(
        Seq(
          formRow(RegisteredDocuments.Fields.ProtocolNumber, protocolNumber),
          formRow(RegisteredDocuments.Fields.RegisteredDate, registeredDate),
          formRow(RegisteredDocuments.Fields.RegisteredTime, registeredTime),
          formRow(RegisteredDocuments.Fields.RegisteredBy, registeredBy),
          formRow(LoadedDocuments.Fields.DocumentType, documentType),
          formRow(LoadedDocuments.Fields.Sender, sender),
          formRow(LoadedDocuments.Fields.Recipient, recipient),
          formRow(LoadedDocuments.Fields.Subject, subject),
          formRow(LoadedDocuments.Fields.Remarks, remarks)
        )
      )

    val archiveForm =
      formGrid(
        Seq(
          formRow(ArchivedDocuments.Fields.ArchivedDate, archivedDate),
          formRow(ArchivedDocuments.Fields.ArchivedTime, archivedTime),
          formRow(ArchivedDocuments.Fields.ArchivedBy, archivedBy),
          formRow(ArchivedDocuments.Fields.ArchiveLocation, archiveLocation),
          formRow(ArchivedDocuments.Fields.ArchiveRemarks, archiveRemarks)
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

    val exitButton = closeButton(onExit)

    formPage(
      titleText = ArchivedDocuments.Details.Title,
      subtitleText = ArchivedDocuments.Details.Subtitle,
      titleStyle = UiStyles.ArchivedDocuments.Title,
      subtitleStyle = UiStyles.ArchivedDocuments.Subtitle,
      rootStyle = UiStyles.ArchivedDocuments.Root,
      form = form,
      resultMessage = messageLabel(UiStyles.ArchivedDocuments.Message),
      actions = actionBar(Seq(exitButton))
    )