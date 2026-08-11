package pkg.a.gui.views

import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.ArchivedDocuments.{Errors as ArchiveErrors, Fields as ArchiveFields, Process as Text, Prompts as ArchivePrompts}
import pkg.a.gui.text.UiText.Fields.Labels
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields as DocumentFields, Prompts as DocumentPrompts}
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.traits.Form
import pkg.b.logic.{ArchivedDocumentService, RegisteredDocument}
import pkg.d.util.DateTime.{localDate, localTime}
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.geometry.Pos
import scalafx.scene.layout.{HBox, Priority}
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
    val result = createResultMessage()

    val protocolNumber = stringField(RegistrationFields.ProtocolNumber, selectedDocument.getProtocolNumber)
    val registeredDate = stringField(RegistrationFields.RegisteredDate, selectedDocument.getRegisteredDate)
    val registeredTime = stringField(RegistrationFields.RegisteredTime, selectedDocument.getRegisteredTime)
    val registeredBy = stringField(RegistrationFields.RegisteredBy, selectedDocument.getRegisteredBy)
    val documentType = stringField(DocumentPrompts.DocumentType, selectedDocument.getDocumentType)
    val sender = stringField(DocumentPrompts.Sender, selectedDocument.getSender)
    val recipient = stringField(DocumentPrompts.Recipient, selectedDocument.getRecipient)
    val subject = stringField(DocumentPrompts.Subject, selectedDocument.getSubject)
    val remarks = areaField(DocumentPrompts.Remarks, DescriptionAreaStyle, selectedDocument.getRemarks)

    /*
     * Dati propri dell'archiviazione.
     */
    val archivedDate = dateField(LocalDate.parse(localDate))
    val archivedTime = stringField(ArchivePrompts.ArchivedTime, localTime)
    val archivedBy = stringField(ArchivePrompts.ArchivedBy, operatorUsername)
    val archiveLocation = stringField(ArchivePrompts.ArchiveLocation)

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
      Seq(archivedDate, archivedTime, archiveLocation)

    val archiveFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archivedBy, archiveLocation)

    def clearErrors(): Unit =
      clearFormFieldErrors(archiveFields*)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      var valid = true
      if archivedDate.value.isBlank then
        archivedDate.showError(ArchiveErrors.ArchivedDateRequired)
        valid = false

      if archivedTime.value.isBlank then
        archivedTime.showError(ArchiveErrors.ArchivedTimeRequired)
        valid = false

      if archivedBy.value.isBlank then
        archivedBy.showError(ArchiveErrors.ArchivedByRequired)
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
              titleText = Text.SaveTitle,
              header = Text.SaveHeader,
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
              archiveLocation = archiveLocation.value
            ) match
              case Right(_) =>
                formSaved = true
                result.show(
                  message = Text.Success,
                  success = true
                )
                onArchived()

              case Left(error) =>
                result.show(
                  message = error,
                  success = false
                )

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val documentForm =
      formGrid(
        Seq(
          formRow(Labels.required(RegistrationFields.ProtocolNumber), protocolNumber),
          formRow(Labels.required(RegistrationFields.RegisteredDate), registeredDate),
          formRow(Labels.required(RegistrationFields.RegisteredTime), registeredTime),
          formRow(Labels.required(RegistrationFields.RegisteredBy), registeredBy),
          formRow(Labels.required(DocumentFields.DocumentType), documentType),
          formRow(Labels.required(DocumentFields.Sender), sender),
          formRow(Labels.required(DocumentFields.Recipient), recipient),
          formRow(Labels.required(DocumentFields.Subject), subject),
          formRow(DocumentFields.Remarks, remarks)
        )
      )
    val archiveForm =
      formGrid(
        Seq(
          formRow(Labels.required(ArchiveFields.ArchivedDate), archivedDate),
          formRow(Labels.required(ArchiveFields.ArchivedTime), archivedTime),
          formRow(Labels.required(ArchiveFields.ArchivedBy), archivedBy),
          formRow(ArchiveFields.ArchiveLocation, archiveLocation)
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
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, editableFields)
    )
