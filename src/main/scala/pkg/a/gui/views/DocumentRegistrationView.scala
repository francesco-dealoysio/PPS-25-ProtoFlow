package pkg.a.gui.views

import pkg.a.gui.structures.LoadedDocumentViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, LoadedDocuments, RegisteredDocuments}
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
    val documentTime = stringField(LoadedDocuments.Prompts.DocumentTime, selectedDocument.getDocumentTime)
    val documentProtocol = stringField(LoadedDocuments.Prompts.DocumentProtocol, selectedDocument.getDocumentProtocol)
    val documentType = stringField(LoadedDocuments.Prompts.DocumentType, selectedDocument.getDocumentType)
    val sender = stringField(LoadedDocuments.Prompts.Sender, selectedDocument.getSender)
    val recipient = stringField(LoadedDocuments.Prompts.Recipient, selectedDocument.getRecipient)
    val subject = stringField(LoadedDocuments.Prompts.Subject, selectedDocument.getSubject)
    val remarks = areaField(LoadedDocuments.Prompts.Remarks, UiStyles.Common.DescriptionArea, selectedDocument.getRemarks)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(documentDate, documentTime, documentProtocol, documentType, sender, recipient, subject, remarks)

    val resultMessage = messageLabel()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      clearMessage(resultMessage)

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
        state = selectedDocument.getState,
        processedDate = selectedDocument.getProcessedDate,
        processedTime = selectedDocument.getProcessedTime,
        processedBy = selectedDocument.getProcessedBy
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors = viewModel.validate(editedDocument())
      showFormFieldErrors(errors):
        case LoadedDocumentViewModel.DocumentDateRequiredError => documentDate
        case LoadedDocumentViewModel.DocumentTimeRequiredError => documentTime
        case LoadedDocumentViewModel.DocumentProtocolRequiredError => documentProtocol
        case LoadedDocumentViewModel.DocumentTypeRequiredError => documentType
        case LoadedDocumentViewModel.SenderRequiredError => sender
        case LoadedDocumentViewModel.RecipientRequiredError => recipient
        case LoadedDocumentViewModel.SubjectRequiredError => subject

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
              titleText = RegisteredDocuments.Process.SaveTitle,
              header = RegisteredDocuments.Process.SaveHeader,
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
                showMessage(
                  label = resultMessage,
                  message = s"${RegisteredDocuments.Process.Success} Numero di protocollo: ${registered.getProtocolNumber}.",
                  success = true
                )

                formSaved = true
                onRegistered()

              case Left(error) =>
                showMessage(
                  label = resultMessage,
                  message = error,
                  success = false
                )

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
          formRow(LoadedDocuments.Fields.Remarks, remarks)
        )
      )

    Platform.runLater:
      documentProtocol.requestFocus()

    formPage(
      titleText = RegisteredDocuments.Process.Title,
      subtitleText = RegisteredDocuments.Process.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )