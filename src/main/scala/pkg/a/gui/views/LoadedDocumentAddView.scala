package pkg.a.gui.views

import pkg.a.gui.structures.LoadedDocumentViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, LoadedDocuments}
import pkg.a.gui.traits.Form
import pkg.b.logic.LoadedDocument
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, localDate, localTime}

import scalafx.application.Platform
import scalafx.scene.control.{Alert, DatePicker}
import scalafx.scene.layout.BorderPane

import java.time.LocalDate

object LoadedDocumentAddView extends Form:

  def apply(
             operatorUsername: String,
             onSaved: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val documentLogic = new LoadedDocument()
    val viewModel = new LoadedDocumentViewModel()

    val defaultTime = localTime

    val documentDatePicker =
      new DatePicker(LocalDate.now()):
        maxWidth = Double.MaxValue
        styleClass += UiStyles.Common.FormField

    val documentTimeField = textField(LoadedDocuments.Prompts.DocumentTime, defaultTime)
    val documentProtocolField = textField(LoadedDocuments.Prompts.DocumentProtocol)
    val documentTypeField = textField(LoadedDocuments.Prompts.DocumentType)
    val senderField = textField(LoadedDocuments.Prompts.Sender)
    val recipientField = textField(LoadedDocuments.Prompts.Recipient)
    val subjectField = textField(LoadedDocuments.Prompts.Subject)
    val remarksArea = textArea(LoadedDocuments.Prompts.Remarks, UiStyles.Roles.DescriptionArea)

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

    val initialValues = Seq(defaultTime, "", "", "", "", "", "")

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

    def currentDocument(id: String = ""): LoadedDocument =
      LoadedDocument(
        id = id,
        documentDate = Option(documentDatePicker.value.value).map(_.toString).getOrElse(""),
        documentTime = documentTimeField.text.value.trim,
        documentProtocol = documentProtocolField.text.value.trim,
        documentType = documentTypeField.text.value.trim,
        sender = senderField.text.value.trim,
        recipient = recipientField.text.value.trim,
        subject = subjectField.text.value.trim,
        remarks = remarksArea.text.value.trim,
        state = "loaded"
      )

    def validateForm(): Boolean =
      clearErrors()

      val errors = viewModel.validate(currentDocument())

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
      documentDatePicker.value = LocalDate.now()
      documentTimeField.text = defaultTime
      documentProtocolField.clear()
      documentTypeField.clear()
      senderField.clear()
      recipientField.clear()
      subjectField.clear()
      remarksArea.clear()
      clearErrors()

      documentProtocolField.requestFocus()

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val confirmed =
            askConfirmation(
              titleText = LoadedDocuments.Add.SaveTitle,
              header = LoadedDocuments.Add.SaveHeader,
              content =
                s"""Mittente: ${senderField.text.value.trim}
                   |Oggetto: ${subjectField.text.value.trim}""".stripMargin
            )

          if confirmed then
            val newDocument =
              currentDocument(IdGen(inIdsFilePathName("loadedDocumentId")))
                .copy(
                  processedDate = localDate,
                  processedTime = localTime,
                  processedBy = operatorUsername
                )

            val saved = documentLogic.recordInsert(newDocument)

            showMessage(
              label = resultMessage,
              message =
                if saved then LoadedDocuments.Add.Success
                else LoadedDocuments.Add.Error,
              success = saved,
              successStyle = UiStyles.LoadedDocuments.MessageSuccess,
              errorStyle = UiStyles.LoadedDocuments.MessageError
            )

            if saved then
              formSaved = true

              new Alert(Alert.AlertType.Information):
                title = LoadedDocuments.Add.Title
                headerText = None
                contentText = LoadedDocuments.Add.Success
              .showAndWait()

              resetForm()
              formSaved = false

              onSaved()

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
      titleText = LoadedDocuments.Add.Title,
      subtitleText = LoadedDocuments.Add.Subtitle,
      titleStyle = UiStyles.LoadedDocuments.Title,
      subtitleStyle = UiStyles.LoadedDocuments.Subtitle,
      rootStyle = UiStyles.LoadedDocuments.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredTextFields, initialValues = initialValues)
    )
