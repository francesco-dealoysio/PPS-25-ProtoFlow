package pkg.a.gui.views

import pkg.a.gui.services.ArchivedDocumentService
import pkg.a.gui.text.UiText.ArchivedDocuments.{Errors as ArchiveErrors, Fields as ArchiveFields, Process as Text}
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.traits.Form
import pkg.b.logic.RegisteredDocument
import pkg.d.util.DateTime.{localDate, localTime}
import pkg.a.gui.validation.DocumentArchivingValidator
import scalafx.scene.Node
import scalafx.geometry.Pos
import scalafx.scene.layout.{HBox, Priority}
import scalafx.scene.layout.BorderPane

object DocumentArchivingView extends Form:

  def apply(
             selectedDocument: RegisteredDocument,
             operatorUsername: String,
             onArchived: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new ArchivedDocumentService()
    val result = createResultMessage()
    val validator = new DocumentArchivingValidator()

    val id = readOnlyStringField(selectedDocument.getId)
    val protocolNumber = readOnlyStringField(selectedDocument.getProtocolNumber)
    val registeredDate = readOnlyStringField(selectedDocument.getRegisteredDate)
    val registeredTime = readOnlyStringField(selectedDocument.getRegisteredTime)
    val registeredBy = readOnlyStringField(selectedDocument.getRegisteredBy)
    val documentType = readOnlyStringField(selectedDocument.getDocumentType)
    val sender = readOnlyStringField(selectedDocument.getSender)
    val recipient = readOnlyStringField(selectedDocument.getRecipient)
    val subject = readOnlyStringField(selectedDocument.getSubject)
    val remarks = readOnlyAreaField(selectedDocument.getRemarks)

    val archivedDate = dateField(localDate)
    val archivedTime = stringField(localTime)
    val archivedBy = readOnlyStringField(operatorUsername)
    val archiveLocation = stringField()

    val editableFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archiveLocation)

    val archiveFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archivedBy, archiveLocation)

    def clearErrors(): Unit =
      clearFormFieldErrors(archiveFields*)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      val errors = validator.validate(archivedDate.value, archivedTime.value, archivedBy.value)
      errors.foreach:
        case error @ (ArchiveErrors.ArchivedDateRequired | ArchiveErrors.ArchivedDateInvalid) =>
          archivedDate.showError(error)
        case error @ (ArchiveErrors.ArchivedTimeRequired | ArchiveErrors.ArchivedTimeInvalid) =>
          archivedTime.showError(error)
        case error @ ArchiveErrors.ArchivedByRequired =>
          archivedBy.showError(error)
        case _ =>
      errors.isEmpty

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
          formRow(CommonDocumentFields.Id, id),
          formRow(CommonDocumentFields.ProtocolNumber, protocolNumber),
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

    archiveForm.maxWidth = Double.MaxValue
    documentForm.maxWidth = Double.MaxValue
    val form =
      new HBox:
        spacing = 30
        alignment = Pos.TopCenter
        children = Seq(documentForm, archiveForm)

        HBox.setHgrow(documentForm, Priority.Always)
        HBox.setHgrow(archiveForm, Priority.Always)

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      initialFocus = Some(archiveLocation),
      hasUnsavedChanges = () => hasFormChanges(formSaved, editableFields)
    )
