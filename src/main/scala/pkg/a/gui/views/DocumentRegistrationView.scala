package pkg.a.gui.views

import pkg.a.gui.structures.LoadedDocumentViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, LoadedDocuments, RegisteredDocuments}
import pkg.a.gui.traits.Form
import pkg.b.logic.{LoadedDocument, LoadedDocumentService}

import scalafx.application.Platform
import scalafx.scene.control.DatePicker
import scalafx.scene.layout.BorderPane

import java.time.LocalDate

object DocumentRegistrationView extends Form:

  def apply(
             selectedDocument: LoadedDocument,
             operatorUsername: String,
             onRegistered: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new LoadedDocumentService()
    val viewModel = new LoadedDocumentViewModel()

    val initialDocumentDate =
      LocalDate.parse(selectedDocument.getDocumentDate)

    val documentDatePicker =
      new DatePicker(initialDocumentDate):
        maxWidth = Double.MaxValue
        styleClass += UiStyles.Common.FormField

    val documentTimeField = textField(LoadedDocuments.Prompts.DocumentTime, selectedDocument.getDocumentTime)
    val documentProtocolField = textField(LoadedDocuments.Prompts.DocumentProtocol, selectedDocument.getDocumentProtocol)
    val documentTypeField = textField(LoadedDocuments.Prompts.DocumentType, selectedDocument.getDocumentType)
    val senderField = textField(LoadedDocuments.Prompts.Sender, selectedDocument.getSender)
    val recipientField = textField(LoadedDocuments.Prompts.Recipient, selectedDocument.getRecipient)
    val subjectField = textField(LoadedDocuments.Prompts.Subject, selectedDocument.getSubject)
    val remarksArea = textArea(LoadedDocuments.Prompts.Remarks, UiStyles.Roles.DescriptionArea, selectedDocument.getRemarks)

    val documentDateError = fieldErrorLabel()
    val documentTimeError = fieldErrorLabel()
    val documentProtocolError = fieldErrorLabel()
    val documentTypeError = fieldErrorLabel()
    val senderError = fieldErrorLabel()
    val recipientError = fieldErrorLabel()
    val subjectError = fieldErrorLabel()

    val resultMessage = messageLabel(UiStyles.LoadedDocuments.Message)

    val monitoredTextFields =
      Seq(documentTimeField, documentProtocolField, documentTypeField, senderField, recipientField, subjectField, remarksArea)

    val initialValues =
      Seq(
        selectedDocument.getDocumentTime,
        selectedDocument.getDocumentProtocol,
        selectedDocument.getDocumentType,
        selectedDocument.getSender,
        selectedDocument.getRecipient,
        selectedDocument.getSubject,
        selectedDocument.getRemarks
      )

    def clearErrors(): Unit =
      clearFieldErrors(
        documentDatePicker -> documentDateError,
        documentTimeField -> documentTimeError,
        documentProtocolField -> documentProtocolError,
        documentTypeField -> documentTypeError,
        senderField -> senderError,
        recipientField -> recipientError,
        subjectField -> subjectError
      )

      clearMessage(resultMessage, UiStyles.LoadedDocuments.MessageSuccess, UiStyles.LoadedDocuments.MessageError)

    def editedDocument(): LoadedDocument =
      LoadedDocument(
        id = selectedDocument.getId,
        documentDate = Option(documentDatePicker.value.value).map(_.toString).getOrElse(""),
        documentTime = documentTimeField.text.value.trim,
        documentProtocol = documentProtocolField.text.value.trim,
        documentType = documentTypeField.text.value.trim,
        sender = senderField.text.value.trim,
        recipient = recipientField.text.value.trim,
        subject = subjectField.text.value.trim,
        remarks = remarksArea.text.value.trim,
        state = selectedDocument.getState,
        processedDate = selectedDocument.getProcessedDate,
        processedTime = selectedDocument.getProcessedTime,
        processedBy = selectedDocument.getProcessedBy
      )

    def validateForm(): Boolean =
      clearErrors()

      val errors = viewModel.validate(editedDocument())

      if errors.contains(LoadedDocumentViewModel.DocumentDateRequiredError) then
        showFieldError(documentDatePicker, documentDateError, LoadedDocumentViewModel.DocumentDateRequiredError)

      showMappedErrors(errors):
        case LoadedDocumentViewModel.DocumentTimeRequiredError =>
          documentTimeField -> documentTimeError

        case LoadedDocumentViewModel.DocumentProtocolRequiredError =>
          documentProtocolField -> documentProtocolError

        case LoadedDocumentViewModel.DocumentTypeRequiredError =>
          documentTypeField -> documentTypeError

        case LoadedDocumentViewModel.SenderRequiredError =>
          senderField -> senderError

        case LoadedDocumentViewModel.RecipientRequiredError =>
          recipientField -> recipientError

        case LoadedDocumentViewModel.SubjectRequiredError =>
          subjectField -> subjectError

    def resetForm(): Unit =
      documentDatePicker.value = initialDocumentDate
      documentTimeField.text = selectedDocument.getDocumentTime
      documentProtocolField.text = selectedDocument.getDocumentProtocol
      documentTypeField.text = selectedDocument.getDocumentType
      senderField.text = selectedDocument.getSender
      recipientField.text = selectedDocument.getRecipient
      subjectField.text = selectedDocument.getSubject
      remarksArea.text = selectedDocument.getRemarks
      clearErrors()

      documentProtocolField.requestFocus()

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val confirmed =
            askConfirmation(
              titleText = RegisteredDocuments.Process.SaveTitle,
              header = RegisteredDocuments.Process.SaveHeader,
              content =
                s"""Mittente: ${senderField.text.value.trim}
                   |Oggetto: ${subjectField.text.value.trim}""".stripMargin
            )

          if confirmed then
            service.registerDocument(selectedDocument, editedDocument(), operatorUsername) match
              case Right(registered) =>
                showMessage(
                  label = resultMessage,
                  message = s"${RegisteredDocuments.Process.Success} Numero di protocollo: ${registered.getProtocolNumber}.",
                  success = true,
                  successStyle = UiStyles.LoadedDocuments.MessageSuccess,
                  errorStyle = UiStyles.LoadedDocuments.MessageError
                )

                formSaved = true
                onRegistered()

              case Left(error) =>
                showMessage(
                  label = resultMessage,
                  message = error,
                  success = false,
                  successStyle = UiStyles.LoadedDocuments.MessageSuccess,
                  errorStyle = UiStyles.LoadedDocuments.MessageError
                )

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentDate), documentDatePicker, documentDateError),
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentTime), documentTimeField, documentTimeError),
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentProtocol), documentProtocolField, documentProtocolError),
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentType), documentTypeField, documentTypeError),
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.Sender), senderField, senderError),
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.Recipient), recipientField, recipientError),
          FormRow(Fields.Labels.required(LoadedDocuments.Fields.Subject), subjectField, subjectError),
          FormRow(LoadedDocuments.Fields.Remarks, remarksArea, fieldErrorLabel())
        )
      )

    Platform.runLater:
      documentProtocolField.requestFocus()

    formPage(
      titleText = RegisteredDocuments.Process.Title,
      subtitleText = RegisteredDocuments.Process.Subtitle,
      titleStyle = UiStyles.LoadedDocuments.Title,
      subtitleStyle = UiStyles.LoadedDocuments.Subtitle,
      rootStyle = UiStyles.LoadedDocuments.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredTextFields, initialValues = initialValues)
    )
