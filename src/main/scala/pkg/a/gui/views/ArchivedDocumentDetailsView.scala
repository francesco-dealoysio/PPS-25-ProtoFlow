package pkg.a.gui.views

import pkg.a.gui.text.UiText.ArchivedDocuments.{Details as Text, Fields as ArchiveFields, Prompts as ArchivePrompts}
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields as DocumentFields, Prompts as DocumentPrompts}
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.text.UiStyles.Common.DescriptionAreaStyle
import pkg.a.gui.traits.Form
import pkg.b.logic.ArchivedDocument
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.layout.{BorderPane, HBox, Priority}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf

object ArchivedDocumentDetailsView extends Form:

  def apply(selectedDocument: ArchivedDocument, onExit: () => Unit = () => ()): BorderPane =
    val protocolNumber = stringField(RegistrationFields.ProtocolNumber, selectedDocument.getProtocolNumber)
    val registeredDate = stringField(RegistrationFields.RegisteredDate, selectedDocument.getRegisteredDate)
    val registeredTime = stringField(RegistrationFields.RegisteredTime, selectedDocument.getRegisteredTime)
    val registeredBy = stringField(RegistrationFields.RegisteredBy, selectedDocument.getRegisteredBy)
    val documentType = stringField(DocumentPrompts.DocumentType, selectedDocument.getDocumentType)
    val sender = stringField(DocumentPrompts.Sender, selectedDocument.getSender)
    val recipient = stringField(DocumentPrompts.Recipient, selectedDocument.getRecipient)
    val subject = stringField(DocumentPrompts.Subject, selectedDocument.getSubject)
    val remarks = areaField(DocumentPrompts.Remarks, DescriptionAreaStyle, selectedDocument.getRemarks)
    val archivedDate = stringField(ArchivePrompts.ArchivedDate, selectedDocument.getArchivedDate)
    val archivedTime = stringField(ArchivePrompts.ArchivedTime, selectedDocument.getArchivedTime)
    val archivedBy = stringField(ArchivePrompts.ArchivedBy, selectedDocument.getArchivedBy)
    val archiveLocation = stringField(ArchivePrompts.ArchiveLocation, selectedDocument.getArchiveLocation)
    val archiveRemarks = areaField(ArchivePrompts.ArchiveRemarks, DescriptionAreaStyle, selectedDocument.getArchiveRemarks)

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
          formRow(RegistrationFields.ProtocolNumber, protocolNumber),
          formRow(RegistrationFields.RegisteredDate, registeredDate),
          formRow(RegistrationFields.RegisteredTime, registeredTime),
          formRow(RegistrationFields.RegisteredBy, registeredBy),
          formRow(DocumentFields.DocumentType, documentType),
          formRow(DocumentFields.Sender, sender),
          formRow(DocumentFields.Recipient, recipient),
          formRow(DocumentFields.Subject, subject),
          formRow(DocumentFields.Remarks, remarks)
        )
      )

    val archiveForm =
      formGrid(
        Seq(
          formRow(ArchiveFields.ArchivedDate, archivedDate),
          formRow(ArchiveFields.ArchivedTime, archivedTime),
          formRow(ArchiveFields.ArchivedBy, archivedBy),
          formRow(ArchiveFields.ArchiveLocation, archiveLocation),
          formRow(ArchiveFields.ArchiveRemarks, archiveRemarks)
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

    val result =
      createResultMessage()

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
    val printButton = secondaryButton(Buttons.Print, () => printDocumentDetails())

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton, printButton))
    )