package pkg.a.gui.views

import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.LoadedDocuments.Fields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Process as Text
import pkg.a.gui.text.UiText.Validation.LoadedDocument as Validation
import pkg.a.gui.traits.Form
import pkg.a.gui.validation.LoadedDocumentValidator
import pkg.b.logic.{Classification, LoadedDocument}
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane

object DocumentRegistrationView extends Form:

  def apply(
             selectedDocument: LoadedDocument,
             operatorUsername: String,
             onRegistered: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new LoadedDocumentService()
    val validator = new LoadedDocumentValidator()
    val classificationLogic = new Classification()
    val id = readOnlyStringField(selectedDocument.getId)
    val documentDate = dateField(selectedDocument.getDocumentDate)
    val documentProtocol = stringField(selectedDocument.getDocumentProtocol)
    val documentType = stringField(selectedDocument.getDocumentType)
    val sender = stringField(selectedDocument.getSender)
    val recipient = stringField(selectedDocument.getRecipient)
    val subject = stringField(selectedDocument.getSubject)
    val remarks = areaField(selectedDocument.getRemarks)
    val classification = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), prompt = Prompts.Classification)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(documentDate, documentProtocol, documentType, sender, recipient, subject, remarks, classification)

    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def editedDocument(): LoadedDocument =
      LoadedDocument(
        id = selectedDocument.getId,
        documentDate = documentDate.value,
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
      val errors = validator.validate(editedDocument())
      val documentValid =
        showFormFieldErrors(errors):
          case Validation.DocumentDateRequired => documentDate
          case Validation.DocumentProtocolRequired => documentProtocol
          case Validation.DocumentTypeRequired => documentType
          case Validation.SenderRequired => sender
          case Validation.RecipientRequired => recipient
          case Validation.SubjectRequired => subject

      val classificationValid =
        if classification.value.isEmpty then
          classification.showError("Seleziona una classifica.")
          false
        else
          true

      documentValid && classificationValid

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
              operatorUsername,
              classification.value
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
          formRow(CommonDocumentFields.Id, id),
          formRow(Labels.required(Fields.DocumentDate), documentDate),
          formRow(Labels.required(Fields.DocumentProtocol), documentProtocol),
          formRow(Labels.required(Labels.Classification), classification),
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
      actions = actionBar(Seq(exit, reset, save)),
      initialFocus = Some(documentProtocol),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )