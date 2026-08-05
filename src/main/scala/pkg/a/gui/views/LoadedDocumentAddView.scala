package pkg.a.gui.views

import pkg.a.gui.structures.LoadedDocumentViewModel
import pkg.a.gui.text.UiText.{Fields, LoadedDocuments}
import pkg.a.gui.text.UiText.Validation.LoadedDocument.*
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.traits.Form
import pkg.b.logic.LoadedDocument
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import pkg.d.util.DateTime.{localDate, localTime}
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.scene.control.Alert
import scalafx.scene.layout.BorderPane
import java.time.LocalDate

object LoadedDocumentAddView extends Form:

  def apply(operatorUsername: String, onSaved: () => Unit = () => (), onExit: () => Unit = () => ()): BorderPane =

    val documentLogic = new LoadedDocument()
    val viewModel = new LoadedDocumentViewModel()
    val initialDate = LocalDate.now()
    val defaultTime = localTime
    val documentDate = dateField(initialDate)
    val documentTime = stringField(LoadedDocuments.Prompts.DocumentTime, defaultTime)
    val documentProtocol = stringField(LoadedDocuments.Prompts.DocumentProtocol)
    val documentType = stringField(LoadedDocuments.Prompts.DocumentType)
    val sender = stringField(LoadedDocuments.Prompts.Sender)
    val recipient = stringField(LoadedDocuments.Prompts.Recipient)
    val subject = stringField(LoadedDocuments.Prompts.Subject)
    val remarks = areaField(LoadedDocuments.Prompts.Remarks, DescriptionAreaStyle)
    val monitoredFields: Seq[FormField[? <: Node]] = Seq(documentDate, documentTime, documentProtocol, documentType, sender, recipient, subject, remarks)
    val resultMessage = messageLabel(MessageStyle)

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      clearMessage(resultMessage)

    def currentDocument(id: String = ""): LoadedDocument =
      LoadedDocument(
        id = id,
        documentDate = documentDate.value,
        documentTime = documentTime.value,
        documentProtocol = documentProtocol.value,
        documentType = documentType.value,
        sender = sender.value,
        recipient = recipient.value,
        subject = subject.value,
        remarks = remarks.value,
        state = "loaded"
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors = viewModel.validate(currentDocument())
      showFormFieldErrors(errors):
        case DocumentDateRequired => documentDate
        case DocumentTimeRequired => documentTime
        case DocumentProtocolRequired => documentProtocol
        case DocumentTypeRequired => documentType
        case SenderRequired => sender
        case RecipientRequired => recipient
        case SubjectRequired => subject

    def resetForm(): Unit =
      resetFields(monitoredFields*)
      clearErrors()
      documentProtocol.requestFocus()

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val confirmed =
            askConfirmation(
              titleText = LoadedDocuments.Add.SaveTitle,
              header = LoadedDocuments.Add.SaveHeader,
              content =
                s"""Mittente: ${sender.value}
                   |Oggetto: ${subject.value}""".stripMargin
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
                if saved then
                  LoadedDocuments.Add.Success
                else
                  LoadedDocuments.Add.Error,
              success = saved
            )

            if saved then
              formSaved = true
              val alert = new Alert(Alert.AlertType.Information):
                title = LoadedDocuments.Add.Title
                headerText = None
                contentText = LoadedDocuments.Add.Success
              alert.showAndWait()

              resetForm()
              formSaved = false
              onSaved()

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)
    val form =
      formGrid(
        Seq(
          formRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentDate), documentDate),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentTime), documentTime),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentProtocol), documentProtocol),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.DocumentType), documentType),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.Sender), sender),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.Recipient), recipient),
          formRow(Fields.Labels.required(LoadedDocuments.Fields.Subject), subject),
          formRow(LoadedDocuments.Fields.Remarks, remarks
          )
        )
      )

    Platform.runLater:
      documentProtocol.requestFocus()

    formPage(
      titleText = LoadedDocuments.Add.Title,
      subtitleText = LoadedDocuments.Add.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )