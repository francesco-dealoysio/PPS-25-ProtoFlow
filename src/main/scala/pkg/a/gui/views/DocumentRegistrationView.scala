package pkg.a.gui.views

import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.LoadedDocuments.Fields
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Process as Text
import pkg.a.gui.traits.Form
import pkg.b.logic.{Classification, LoadedDocument}
import scalafx.scene.layout.BorderPane

object DocumentRegistrationView extends Form:

  def apply(
             selectedDocument: LoadedDocument,
             operatorUsername: String,
             onRegistered: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new LoadedDocumentService()

    val classificationLogic = new Classification()
    val id = readOnlyStringField(selectedDocument.getId)
    val documentDate = readOnlyStringField(selectedDocument.getDocumentDate)
    val documentProtocol = readOnlyStringField(selectedDocument.getDocumentProtocol)
    val documentType = readOnlyStringField(selectedDocument.getDocumentType)
    val sender = readOnlyStringField(selectedDocument.getSender)
    val recipient = readOnlyStringField(selectedDocument.getRecipient)
    val subject = readOnlyStringField(selectedDocument.getSubject)
    val remarks = readOnlyAreaField(selectedDocument.getRemarks)
    val classification = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), prompt = Prompts.Classification)

    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(classification)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      if classification.value.isEmpty then
        classification.showError("Seleziona una classifica.")
        false
      else
        true

    def resetForm(): Unit =
      resetFields(classification)
      clearErrors()
      classification.requestFocus()

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
          formRow(Fields.DocumentDate, documentDate),
          formRow(Fields.DocumentProtocol, documentProtocol),
          formRow(Fields.DocumentType, documentType),
          formRow(CommonDocumentFields.Sender, sender),
          formRow(CommonDocumentFields.Recipient, recipient),
          formRow(CommonDocumentFields.Subject, subject),
          formRow(Fields.Remarks, remarks),
          formRow(Labels.required(Labels.Classification), classification)
        )
      )

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      initialFocus = Some(classification),
      hasUnsavedChanges = () => hasFormChanges(formSaved, Seq(classification))
    )