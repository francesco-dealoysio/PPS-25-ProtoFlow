package pkg.a.gui.views

import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.text.UiText.{ArchivedDocuments, Fields, LoadedDocuments, RegisteredDocuments}
import pkg.a.gui.traits.Form
import pkg.b.logic.{ArchivedDocumentService, RegisteredDocument}
import pkg.d.util.Util.{localDate, localTime}
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.geometry.Pos
import scalafx.scene.layout.{BorderPane, HBox, Priority}
import scalafx.scene.layout.BorderPane

import java.time.LocalDate

object ArchivedDocumentView extends Form:

  def apply(
             selectedDocument: RegisteredDocument,
             operatorUsername: String,
             onArchived: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new ArchivedDocumentService()
    val protocolNumber = stringField(RegisteredDocuments.Fields.ProtocolNumber, selectedDocument.getProtocolNumber)
    val registeredDate = stringField(RegisteredDocuments.Fields.RegisteredDate, selectedDocument.getRegisteredDate)
    val registeredTime = stringField(RegisteredDocuments.Fields.RegisteredTime, selectedDocument.getRegisteredTime)
    val registeredBy = stringField(RegisteredDocuments.Fields.RegisteredBy, selectedDocument.getRegisteredBy)
    val documentType = stringField(LoadedDocuments.Prompts.DocumentType, selectedDocument.getDocumentType)
    val sender = stringField(LoadedDocuments.Prompts.Sender, selectedDocument.getSender)
    val recipient = stringField(LoadedDocuments.Prompts.Recipient, selectedDocument.getRecipient)
    val subject = stringField(LoadedDocuments.Prompts.Subject, selectedDocument.getSubject)
    val remarks = areaField(LoadedDocuments.Prompts.Remarks, UiStyles.Roles.DescriptionArea, selectedDocument.getRemarks)

    /*
     * Dati propri dell'archiviazione.
     */
    val archivedDate = dateField(LocalDate.parse(localDate))
    val archivedTime = stringField(ArchivedDocuments.Prompts.ArchivedTime, localTime)
    val archivedBy = stringField(ArchivedDocuments.Prompts.ArchivedBy, operatorUsername)
    val archiveLocation = stringField(ArchivedDocuments.Prompts.ArchiveLocation)
    val archiveRemarks = areaField(ArchivedDocuments.Prompts.ArchiveRemarks, UiStyles.Roles.DescriptionArea)

    /*
     * I dati già protocollati sono solamente visualizzati.
     */
    Seq(protocolNumber, registeredDate, registeredTime, registeredBy, documentType, sender, recipient, subject, remarks, archivedBy
    ).foreach: field =>
      field.control.setDisable(true)

    /*
     * Solo questi campi possono realmente cambiare.
     */
    val editableFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archiveLocation, archiveRemarks)

    val archiveFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archivedBy, archiveLocation, archiveRemarks)

    val resultMessage = messageLabel(UiStyles.LoadedDocuments.Message)

    def clearErrors(): Unit =
      clearFormFieldErrors(archiveFields*)
      clearMessage(resultMessage, UiStyles.LoadedDocuments.MessageSuccess, UiStyles.LoadedDocuments.MessageError)

    def validateForm(): Boolean =
      clearErrors()
      var valid = true
      if archivedDate.value.isBlank then
        archivedDate.showError(ArchivedDocuments.Errors.ArchivedDateRequired)
        valid = false

      if archivedTime.value.isBlank then
        archivedTime.showError(ArchivedDocuments.Errors.ArchivedTimeRequired)
        valid = false

      if archivedBy.value.isBlank then
        archivedBy.showError(ArchivedDocuments.Errors.ArchivedByRequired)
        valid = false

      valid

    def resetForm(): Unit =
      resetFields(editableFields*)
      clearErrors()
      archiveLocation.requestFocus()

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val confirmed =
            askConfirmation(
              titleText = ArchivedDocuments.Process.SaveTitle,
              header = ArchivedDocuments.Process.SaveHeader,
              content =
                s"""Numero protocollo: ${protocolNumber.value}
                   |Mittente: ${sender.value}
                   |Oggetto: ${subject.value}""".stripMargin
            )

          if confirmed then
            service.archiveDocument(
              source = selectedDocument,
              archivedDate = archivedDate.value,
              archivedTime = archivedTime.value,
              operatorUsername = operatorUsername,
              archiveLocation = archiveLocation.value,
              archiveRemarks = archiveRemarks.value
            ) match
              case Right(_) =>
                formSaved = true

                showMessage(
                  label = resultMessage,
                  message = ArchivedDocuments.Process.Success,
                  success = true,
                  successStyle = UiStyles.LoadedDocuments.MessageSuccess,
                  errorStyle = UiStyles.LoadedDocuments.MessageError
                )

                onArchived()

              case Left(error) =>
                showMessage(
                  label = resultMessage,
                  message = error,
                  success = false,
                  successStyle = UiStyles.LoadedDocuments.MessageSuccess,
                  errorStyle = UiStyles.LoadedDocuments.MessageError
                )

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val documentForm =
      formGrid(
        Seq(
          formRow(Fields.Labels.required(RegisteredDocuments.Fields.ProtocolNumber), protocolNumber),
          formRow(Fields.Labels.required(RegisteredDocuments.Fields.RegisteredDate), registeredDate),
          formRow(Fields.Labels.required(RegisteredDocuments.Fields.RegisteredTime), registeredTime),
          formRow(Fields.Labels.required(RegisteredDocuments.Fields.RegisteredBy), registeredBy),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentType), documentType),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.Sender), sender),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.Recipient), recipient),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.Subject), subject),
          formRow(LoadedDocuments.Fields.Remarks, remarks)
        )
      )
    val archiveForm =
      formGrid(
        Seq(
          formRow(Fields.Labels.required(ArchivedDocuments.Fields.ArchivedDate), archivedDate),
          formRow(Fields.Labels.required(ArchivedDocuments.Fields.ArchivedTime), archivedTime),
          formRow(Fields.Labels.required(ArchivedDocuments.Fields.ArchivedBy), archivedBy),
          formRow(ArchivedDocuments.Fields.ArchiveLocation, archiveLocation),
          formRow(ArchivedDocuments.Fields.ArchiveRemarks, archiveRemarks)
        )
      )

    archiveForm.maxWidth = Double.MaxValue
    documentForm.maxWidth = Double.MaxValue
    val form =
      new HBox:
        spacing = 30
        alignment = Pos.TopCenter
        children = Seq(documentForm, archiveForm)

        HBox.setHgrow(documentForm, Priority.Always)
        HBox.setHgrow(archiveForm, Priority.Always)

    Platform.runLater:
      archiveLocation.requestFocus()

    formPage(
      titleText = ArchivedDocuments.Process.Title,
      subtitleText = ArchivedDocuments.Process.Subtitle,
      titleStyle = UiStyles.LoadedDocuments.Title,
      subtitleStyle = UiStyles.LoadedDocuments.Subtitle,
      rootStyle = UiStyles.LoadedDocuments.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, editableFields)
    )
