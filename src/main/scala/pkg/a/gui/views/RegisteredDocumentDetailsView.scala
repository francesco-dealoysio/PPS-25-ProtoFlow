package pkg.a.gui.views

import pkg.a.gui.text.UiText.RegisteredDocuments.{Details as Text, Fields as RegistrationFields}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.traits.Form
import pkg.b.logic.RegisteredDocument
import scalafx.scene.layout.BorderPane
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf

object RegisteredDocumentDetailsView extends Form:

  def apply(selectedDocument: RegisteredDocument, onExit: () => Unit = () => ()): BorderPane =
    val id = readOnlyStringField(selectedDocument.getId)
    val protocolNumber = readOnlyStringField(selectedDocument.getProtocolNumber)
    val classification = readOnlyStringField(selectedDocument.getClassification)
    val registeredDate = readOnlyStringField(selectedDocument.getRegisteredDate)
    val registeredTime = readOnlyStringField(selectedDocument.getRegisteredTime)
    val registeredBy = readOnlyStringField(selectedDocument.getRegisteredBy)
    val documentType = readOnlyStringField(selectedDocument.getDocumentType)
    val sender = readOnlyStringField(selectedDocument.getSender)
    val recipient = readOnlyStringField(selectedDocument.getRecipient)
    val subject = readOnlyStringField(selectedDocument.getSubject)
    val remarks = readOnlyAreaField(selectedDocument.getRemarks)

    val documentForm =
      formGrid(
        Seq(
          formRow(CommonDocumentFields.Id, id),
          formRow(CommonDocumentFields.ProtocolNumber, protocolNumber),
          formRow(Labels.Classification, classification),
          formRow(RegistrationFields.RegisteredDate, registeredDate),
          formRow(RegistrationFields.RegisteredTime, registeredTime),
          formRow(RegistrationFields.RegisteredBy, registeredBy),
          formRow(DocumentFields.DocumentType, documentType),
          formRow(CommonDocumentFields.Sender, sender),
          formRow(CommonDocumentFields.Recipient, recipient),
          formRow(CommonDocumentFields.Subject, subject),
          formRow(DocumentFields.Remarks, remarks)
        )
      )

    documentForm.maxWidth = Double.MaxValue

    val result = createResultMessage()

    def printDocumentDetails(): Unit =
      val safeProtocolNumber =
        selectedDocument.getProtocolNumber.replaceAll("[^a-zA-Z0-9_-]", "_")

      val printed =
        XmlToPdf.printDetails(
          xmlPath = inDocumentsFilePathName("registered.xml"),
          recordId = selectedDocument.getId,
          pdfFileName = s"${Text.PrintFileNamePrefix}_$safeProtocolNumber",
          title = Text.PrintTitle
        )

      result.show(
        message = if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val exitButton = closeButton(onExit)
    val printButton = secondaryButton(Buttons.Print, () => printDocumentDetails())

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = documentForm,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton, printButton))
    )
