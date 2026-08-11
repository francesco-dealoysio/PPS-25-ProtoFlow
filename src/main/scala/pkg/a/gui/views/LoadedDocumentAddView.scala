package pkg.a.gui.views

import pkg.a.gui.structures.LoadedDocumentViewModel
import pkg.a.gui.text.UiText.Fields.Labels
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields, Prompts}
import pkg.a.gui.text.UiText.LoadedDocuments.Add as Text
import pkg.a.gui.text.UiText.Validation.LoadedDocument as Validation
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
    val documentTime = stringField(Prompts.DocumentTime, defaultTime)
    val documentProtocol = stringField(Prompts.DocumentProtocol)
    val documentType = stringField(Prompts.DocumentType)
    val sender = stringField(Prompts.Sender)
    val recipient = stringField(Prompts.Recipient)
    val subject = stringField(Prompts.Subject)
    val remarks = areaField(Prompts.Remarks, DescriptionAreaStyle)
    val monitoredFields: Seq[FormField[? <: Node]] = Seq(documentDate, documentTime, documentProtocol, documentType, sender, recipient, subject, remarks)
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

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
        case Validation.DocumentDateRequired => documentDate
        case Validation.DocumentTimeRequired => documentTime
        case Validation.DocumentProtocolRequired => documentProtocol
        case Validation.DocumentTypeRequired => documentType
        case Validation.SenderRequired => sender
        case Validation.RecipientRequired => recipient
        case Validation.SubjectRequired => subject

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
              titleText = Text.SaveTitle,
              header = Text.SaveHeader,
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

            result.show(
              message = if saved then Text.Success else Text.Error,
              success = saved
            )

            if saved then
              formSaved = true
              val alert = new Alert(Alert.AlertType.Information):
                title = Text.Title
                headerText = None
                contentText = Text.Success
              alert.showAndWait()

              resetForm()
              formSaved = false
              onSaved()

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)
    val form =
      formGrid(
        Seq(
          formRow(Labels.required(Fields.DocumentDate), documentDate),
          formRow(Labels.required(Fields.DocumentTime), documentTime),
          formRow(Labels.required(Fields.DocumentProtocol), documentProtocol),
          formRow(Labels.required(Fields.DocumentType), documentType),
          formRow(Labels.required(Fields.Sender), sender),
          formRow(Labels.required(Fields.Recipient), recipient),
          formRow(Labels.required(Fields.Subject), subject),
          formRow(Fields.Remarks, remarks)
        )
      )

    Platform.runLater:
      documentProtocol.requestFocus()

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )