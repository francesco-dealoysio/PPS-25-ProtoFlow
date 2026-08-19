package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService.ManagedDocument
import pkg.a.gui.text.UiText.DocumentManagementControl as Text
import pkg.a.gui.text.UiText.DocumentManagementControl.Fields as ManagementFields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as LoadedFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegisteredFields
import pkg.a.gui.text.UiText.ArchivedDocuments.Fields as ArchivedFields
import pkg.a.gui.text.UiStyles.Common.DescriptionAreaStyle
import pkg.a.gui.traits.Form
import scalafx.scene.layout.BorderPane

object DocumentManagementDetailsView extends Form:

  def apply(selectedDocument: ManagedDocument, onExit: () => Unit = () => ()): BorderPane =

    def orNotAvailable(value: String): String =
      if value.nonEmpty then value else Text.NotAvailable

    val id = stringField("", selectedDocument.id)
    val stage = stringField("", Text.Stages.labelOf(selectedDocument.stage))
    val operator = stringField("", orNotAvailable(selectedDocument.operator))

    val documentType = stringField("", orNotAvailable(selectedDocument.documentType))
    val sender = stringField("", orNotAvailable(selectedDocument.sender))
    val recipient = stringField("", orNotAvailable(selectedDocument.recipient))
    val subject = stringField("", orNotAvailable(selectedDocument.subject))
    val remarks = areaField("", DescriptionAreaStyle, selectedDocument.remarks)

    val loadedDate = stringField("", orNotAvailable(selectedDocument.loadedDate))
    val loadedTime = stringField("", orNotAvailable(selectedDocument.loadedTime))
    val loadedBy = stringField("", orNotAvailable(selectedDocument.loadedBy))

    val protocolNumber = stringField("", orNotAvailable(selectedDocument.protocolNumber))
    val registeredDate = stringField("", orNotAvailable(selectedDocument.registeredDate))
    val registeredTime = stringField("", orNotAvailable(selectedDocument.registeredTime))
    val registeredBy = stringField("", orNotAvailable(selectedDocument.registeredBy))

    val archivedDate = stringField("", orNotAvailable(selectedDocument.archivedDate))
    val archivedTime = stringField("", orNotAvailable(selectedDocument.archivedTime))
    val archivedBy = stringField("", orNotAvailable(selectedDocument.archivedBy))
    val archiveLocation = stringField("", orNotAvailable(selectedDocument.archiveLocation))

    val readOnlyFields =
      Seq(
        id, stage, operator,
        documentType, sender, recipient, subject, remarks,
        loadedDate, loadedTime, loadedBy,
        protocolNumber, registeredDate, registeredTime, registeredBy,
        archivedDate, archivedTime, archivedBy, archiveLocation
      )

    readOnlyFields.foreach: field =>
      field.control.setDisable(true)

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
          formRow(LoadedFields.ProcessedBy, loadedBy),
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

    val result = createResultMessage()

    val exitButton = closeButton(onExit)

    formPage(
      header = FormHeader(Text.DetailsTitle, Text.DetailsSubtitle),
      form = documentForm,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton))
    )
