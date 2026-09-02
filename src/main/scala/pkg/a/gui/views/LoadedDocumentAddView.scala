package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields, Prompts, DocumentTypes}
import pkg.a.gui.text.UiText.LoadedDocuments.Add as Text
import pkg.a.gui.text.UiText.Validation.LoadedDocument as Validation
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.traits.Form
import pkg.a.gui.validation.LoadedDocumentValidator
import pkg.b.logic.LoadedDocument
import pkg.d.util.DateTime.{localDate, localTime}
import scalafx.scene.Node
import scalafx.scene.control.Alert
import scalafx.scene.layout.BorderPane

object LoadedDocumentAddView extends Form:

  def apply(operatorUsername: String, onSaved: () => Unit = () => (), onExit: () => Unit = () => ()): BorderPane =

    val service = new LoadedDocumentService()
    val validator = new LoadedDocumentValidator()
    val documentDate = dateField(localDate)
    val documentProtocol = stringField(prompt = Prompts.DocumentProtocol)
    val documentType = stringComboField(DocumentTypes.All, prompt = Prompts.DocumentType)
    val sender = stringField(prompt = Prompts.Sender)
    val recipient = stringField(prompt = Prompts.Recipient)
    val subject = stringField(prompt = Prompts.Subject)
    val remarks = areaField(prompt = Prompts.Remarks)
    val monitoredFields: Seq[FormField[? <: Node]] = Seq(documentDate, documentProtocol, documentType, sender, recipient, subject, remarks)
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def currentDocument(id: String = ""): LoadedDocument =
      LoadedDocument(
        id = id,
        documentDate = documentDate.value,
        documentProtocol = documentProtocol.value,
        documentType = documentType.value,
        sender = sender.value,
        recipient = recipient.value,
        subject = subject.value,
        remarks = remarks.value
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors = validator.validate(currentDocument())
      showFormFieldErrors(errors):
        case Validation.DocumentDateRequired => documentDate
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
            service.addLoadedDocument(
              documentDate = documentDate.value,
              documentProtocol = documentProtocol.value,
              documentType = documentType.value,
              sender = sender.value,
              recipient = recipient.value,
              subject = subject.value,
              remarks = remarks.value,
              operatorUsername = operatorUsername
            ) match
              case Right(_) =>
                formSaved = true
                val alert = new Alert(Alert.AlertType.Information):
                  title = Text.Title
                  headerText = None
                  contentText = Text.Success
                alert.showAndWait()

                resetForm()
                formSaved = false
                onSaved()

              case Left(error) =>
                result.show(message = error, success = false)

    val form =
      formGrid(
        Seq(
          formRow(Labels.required(Fields.DocumentDate), documentDate),
          formRow(Labels.required(Fields.DocumentProtocol), documentProtocol),
          formRow(Labels.required(Fields.DocumentType), documentType),
          formRow(Labels.required(CommonDocumentFields.Sender), sender),
          formRow(Labels.required(CommonDocumentFields.Recipient), recipient),
          formRow(Labels.required(CommonDocumentFields.Subject), subject),
          formRow(Fields.Remarks, remarks)
        )
      )

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(closeButton(onExit), resetButton(resetForm), save)),
      initialFocus = Some(documentProtocol),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )