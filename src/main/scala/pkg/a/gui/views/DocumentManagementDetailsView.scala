package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService.ManagedDocument
import pkg.a.gui.text.UiText.DocumentManagementControl as Text
import pkg.a.gui.text.UiText.DocumentManagementControl.Fields as ManagementFields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as LoadedFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegisteredFields
import pkg.a.gui.text.UiText.ArchivedDocuments.Fields as ArchivedFields
import pkg.a.gui.traits.Form
import scalafx.geometry.Pos
import scalafx.scene.layout.{BorderPane, HBox, Priority}

object DocumentManagementDetailsView extends Form:

  def apply(selectedDocument: ManagedDocument, onExit: () => Unit = () => ()): BorderPane =

    def orNotAvailable(value: String): String =
      if value.nonEmpty then value else Text.NotAvailable

    val id = readOnlyStringField(selectedDocument.id)
    val stage = readOnlyStringField(Text.Stages.labelOf(selectedDocument.stage))
    val operator = readOnlyStringField(orNotAvailable(selectedDocument.operator))

    val documentType = readOnlyStringField(orNotAvailable(selectedDocument.documentType))
    val sender = readOnlyStringField(orNotAvailable(selectedDocument.sender))
    val recipient = readOnlyStringField(orNotAvailable(selectedDocument.recipient))
    val subject = readOnlyStringField(orNotAvailable(selectedDocument.subject))
    val remarks = readOnlyAreaField(orNotAvailable(selectedDocument.remarks))

    val loadedDate = readOnlyStringField(orNotAvailable(selectedDocument.loadedDate))
    val loadedTime = readOnlyStringField(orNotAvailable(selectedDocument.loadedTime))
    val loadedBy = readOnlyStringField(orNotAvailable(selectedDocument.loadedBy))

    val protocolNumber = readOnlyStringField(orNotAvailable(selectedDocument.protocolNumber))
    val registeredDate = readOnlyStringField(orNotAvailable(selectedDocument.registeredDate))
    val registeredTime = readOnlyStringField(orNotAvailable(selectedDocument.registeredTime))
    val registeredBy = readOnlyStringField(orNotAvailable(selectedDocument.registeredBy))

    val archivedDate = readOnlyStringField(orNotAvailable(selectedDocument.archivedDate))
    val archivedTime = readOnlyStringField(orNotAvailable(selectedDocument.archivedTime))
    val archivedBy = readOnlyStringField(orNotAvailable(selectedDocument.archivedBy))
    val archiveLocation = readOnlyStringField(orNotAvailable(selectedDocument.archiveLocation))

    val documentForm =
      formGrid(
        Seq(
          formRow(CommonDocumentFields.Id, id),
          formRow(Text.StageColumn, stage),
          formRow(Text.OperatorColumn, operator),
          formRow(LoadedFields.DocumentType, documentType),
          formRow(CommonDocumentFields.Sender, sender),
          formRow(CommonDocumentFields.Recipient, recipient),
          formRow(CommonDocumentFields.Subject, subject),
          formRow(LoadedFields.Remarks, remarks),
          formRow(ManagementFields.LoadedDate, loadedDate),
          formRow(ManagementFields.LoadedTime, loadedTime),
          formRow(LoadedFields.ProcessedBy, loadedBy)
        )
      )

    val phasesForm =
      formGrid(
        Seq(
          formRow(CommonDocumentFields.ProtocolNumber, protocolNumber),
          formRow(RegisteredFields.RegisteredDate, registeredDate),
          formRow(RegisteredFields.RegisteredTime, registeredTime),
          formRow(RegisteredFields.RegisteredBy, registeredBy),
          formRow(ArchivedFields.ArchivedDate, archivedDate),
          formRow(ArchivedFields.ArchivedTime, archivedTime),
          formRow(ArchivedFields.ArchivedBy, archivedBy),
          formRow(ArchivedFields.ArchiveLocation, archiveLocation)
        )
      )

    documentForm.maxWidth = Double.MaxValue
    phasesForm.maxWidth = Double.MaxValue

    val form =
      new HBox:
        spacing = 30
        alignment = Pos.TopCenter
        children = Seq(documentForm, phasesForm)

        HBox.setHgrow(documentForm, Priority.Always)
        HBox.setHgrow(phasesForm, Priority.Always)

    val result = createResultMessage()

    val exitButton = closeButton(onExit)

    formPage(
      header = FormHeader(Text.DetailsTitle, Text.DetailsSubtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton))
    )
