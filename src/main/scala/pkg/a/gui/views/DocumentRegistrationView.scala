package pkg.a.gui.views

import pkg.a.gui.structures.LoadedDocumentViewModel
import pkg.a.gui.text.UiStyles.Common.DescriptionAreaStyle
import pkg.a.gui.text.UiText.Fields.Labels
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields, Prompts}
import pkg.a.gui.text.UiText.RegisteredDocuments.Process as Text
import pkg.a.gui.text.UiText.Validation.LoadedDocument as Validation
import pkg.a.gui.traits.Form
import pkg.b.logic.{LoadedDocument, LoadedDocumentService}
import scalafx.application.Platform
import scalafx.scene.Node
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
    val documentDate = dateField(LocalDate.parse(selectedDocument.getDocumentDate))
    val documentTime = stringField(Prompts.DocumentTime, selectedDocument.getDocumentTime)
    val documentProtocol = stringField(Prompts.DocumentProtocol, selectedDocument.getDocumentProtocol)
    val documentType = stringField(Prompts.DocumentType, selectedDocument.getDocumentType)
    val sender = stringField(Prompts.Sender, selectedDocument.getSender)
    val recipient = stringField(Prompts.Recipient, selectedDocument.getRecipient)
    val subject = stringField(Prompts.Subject, selectedDocument.getSubject)
    val remarks = areaField(Prompts.Remarks, DescriptionAreaStyle, selectedDocument.getRemarks)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(documentDate, documentTime, documentProtocol, documentType, sender, recipient, subject, remarks)

    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def editedDocument(): LoadedDocument =
      LoadedDocument(
        id = selectedDocument.getId,
        documentDate = documentDate.value,
        documentTime = documentTime.value,
        documentProtocol = documentProtocol.value,
        documentType = documentType.value,
        sender = sender.value,
        recipient = recipient.value,
        subject = subject.value,
        remarks = remarks.value,
        processedDate = selectedDocument.getProcessedDate,
        processedTime = selectedDocument.getProcessedTime,
        processedBy = selectedDocument.getProcessedBy
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors = viewModel.validate(editedDocument())
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
            service.registerDocument(
              selectedDocument,
              editedDocument(),
              operatorUsername
            ) match
              case Right(registered) =>
                result.show(
                  message = s"${Text.Success} Numero di protocollo: ${registered.getProtocolNumber}.",
                  success = true
                )

                formSaved = true
                onRegistered()

              case Left(error) =>
                result.show(
                  message = error,
                  success = false
                )

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