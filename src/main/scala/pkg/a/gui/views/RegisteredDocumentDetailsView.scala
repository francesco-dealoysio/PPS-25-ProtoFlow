package pkg.a.gui.views

import pkg.a.gui.text.UiText.RegisteredDocuments.{Details as Text, Fields as RegistrationFields}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.text.UiStyles.Common.DescriptionAreaStyle
import pkg.a.gui.traits.Form
import pkg.b.logic.RegisteredDocument
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf

object RegisteredDocumentDetailsView extends Form:

  def apply(selectedDocument: RegisteredDocument, onExit: () => Unit = () => ()): BorderPane =
    val id = stringField("", selectedDocument.getId)
    val protocolNumber = stringField("", selectedDocument.getProtocolNumber)
    val registeredDate = stringField("", selectedDocument.getRegisteredDate)
    val registeredTime = stringField("", selectedDocument.getRegisteredTime)
    val registeredBy = stringField("", selectedDocument.getRegisteredBy)
    val documentType = stringField("", selectedDocument.getDocumentType)
    val sender = stringField("", selectedDocument.getSender)
    val recipient = stringField("", selectedDocument.getRecipient)
    val subject = stringField("", selectedDocument.getSubject)
    val remarks = areaField("", DescriptionAreaStyle, selectedDocument.getRemarks)

    val readOnlyFields: Seq[FormField[? <: Node]] =
      Seq(id, protocolNumber, registeredDate, registeredTime, registeredBy, documentType, sender, recipient, subject, remarks)

    readOnlyFields.foreach: field =>
      field.control.setDisable(true)

    val documentForm =
      formGrid(
        Seq(
          formRow(CommonDocumentFields.ProtocolNumber, protocolNumber),
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

    val result =
      createResultMessage()

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
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = documentForm,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton, printButton))
    )
