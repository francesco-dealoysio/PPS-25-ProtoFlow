package pkg.a.gui.views

import pkg.a.gui.services.ArchivedDocumentService
import pkg.a.gui.text.UiText.ArchivedDocuments.{Errors as ArchiveErrors, Fields as ArchiveFields, Prompts as ArchivePrompts, Process as Text}
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.traits.Form
import pkg.b.logic.{Classification, RegisteredDocument}
import pkg.d.util.DateTime.{localDate, localTime}
import pkg.a.gui.validators.DocumentArchivingValidator
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane

object DocumentArchivingView extends Form:

  def apply(
             selectedDocument: RegisteredDocument,
             operatorUsername: String,
             onArchived: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val result = createResultMessage()
    val validator = new DocumentArchivingValidator()
    val classificationLogic = new Classification()

    val id = readOnlyStringField(selectedDocument.getId)
    val documentDate = readOnlyStringField(selectedDocument.getDocumentDate)
    val documentProtocol = readOnlyStringField(selectedDocument.getDocumentProtocol)
    val protocolNumber = readOnlyStringField(selectedDocument.getProtocolNumber)
    val registeredDate = readOnlyStringField(selectedDocument.getRegisteredDate)
    val registeredTime = readOnlyStringField(selectedDocument.getRegisteredTime)
    val documentType = readOnlyStringField(selectedDocument.getDocumentType)
    val sender = readOnlyStringField(selectedDocument.getSender)
    val recipient = readOnlyStringField(selectedDocument.getRecipient)
    val subject = readOnlyStringField(selectedDocument.getSubject)
    val remarks = readOnlyAreaField(selectedDocument.getRemarks)

    val archivedDate = dateField(localDate)
    val archivedTime = stringField(localTime)
    val archivedBy = readOnlyStringField(operatorUsername)
    val archiveLocation = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), prompt = ArchivePrompts.ArchiveLocation)

    val editableFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archiveLocation)

    val archiveFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archivedBy, archiveLocation)

    def clearErrors(): Unit =
      clearFormFieldErrors(archiveFields*)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      val errors = validator.validate(archivedDate.value, archivedTime.value, archivedBy.value, archiveLocation.value)
      errors.foreach:
        case error @ (ArchiveErrors.ArchivedDateRequired | ArchiveErrors.ArchivedDateInvalid) =>
          archivedDate.showError(error)
        case error @ (ArchiveErrors.ArchivedTimeRequired | ArchiveErrors.ArchivedTimeInvalid) =>
          archivedTime.showError(error)
        case error @ ArchiveErrors.ArchivedByRequired =>
          archivedBy.showError(error)
        case error @ ArchiveErrors.ArchiveLocationRequired =>
          archiveLocation.showError(error)
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
            ArchivedDocumentService.archiveDocument(
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

    val documentForm =
      formGrid(
        Seq(
          formRow(CommonDocumentFields.Id, id),
          formRow(DocumentFields.DocumentDate, documentDate),
          formRow(DocumentFields.DocumentProtocol, documentProtocol),
          formRow(CommonDocumentFields.ProtocolNumber, protocolNumber),
          formRow(RegistrationFields.RegisteredDate, registeredDate),
          formRow(RegistrationFields.RegisteredTime, registeredTime),
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
          formRow(Labels.required(ArchiveFields.ArchiveLocation), archiveLocation)
        )
      )

    val form = twoColumnForm(documentForm, archiveForm)

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(closeButton(onExit), resetButton(resetForm), save)),
      initialFocus = Some(archiveLocation),
      hasUnsavedChanges = () => hasFormChanges(formSaved, editableFields)
    )
