package pkg.a.gui.views

import pkg.a.gui.services.ArchivedDocumentService
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.ArchivedDocuments.{Errors as ArchiveErrors, Fields as ArchiveFields, Process as Text}
import pkg.a.gui.text.UiText.LoadedDocuments.Fields as DocumentFields
import pkg.a.gui.text.UiText.RegisteredDocuments.Fields as RegistrationFields
import pkg.a.gui.traits.Form
import pkg.b.logic.RegisteredDocument
import pkg.d.util.DateTime.{localDate, localTime}
import pkg.a.gui.validation.DocumentArchivingValidator
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.geometry.Pos
import scalafx.scene.layout.{HBox, Priority}
import scalafx.scene.layout.BorderPane

import java.time.LocalDate

object DocumentArchivingView extends Form:

  def apply(
             selectedDocument: RegisteredDocument,
             operatorUsername: String,
             onArchived: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new ArchivedDocumentService()
    val result = createResultMessage()
    val validator = new DocumentArchivingValidator()

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

    /*
     * Dati propri dell'archiviazione.
     */
    val archivedDate = dateField(LocalDate.parse(localDate))
    val archivedTime = stringField("", localTime)
    val archivedBy = stringField("", operatorUsername)
    val archiveLocation = stringField("")

    /*
     * I dati già protocollati sono solamente visualizzati.
     */
    Seq(id, protocolNumber, registeredDate, registeredTime, registeredBy, documentType, sender, recipient, subject, remarks, archivedBy
    ).foreach: field =>
      field.control.setDisable(true)

    /*
     * Solo questi campi possono realmente cambiare.
     */
    val editableFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archiveLocation)

    val archiveFields: Seq[FormField[? <: Node]] =
      Seq(archivedDate, archivedTime, archivedBy, archiveLocation)

    def clearErrors(): Unit =
      clearFormFieldErrors(archiveFields*)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      val errors = validator.validate(archivedDate.value, archivedTime.value, archivedBy.value)
      errors.foreach:
        case error @ (ArchiveErrors.ArchivedDateRequired | ArchiveErrors.ArchivedDateInvalid) =>
          archivedDate.showError(error)
        case error @ (ArchiveErrors.ArchivedTimeRequired | ArchiveErrors.ArchivedTimeInvalid) =>
          archivedTime.showError(error)
        case error @ ArchiveErrors.ArchivedByRequired =>
          archivedBy.showError(error)
        case _ =>
      errors.isEmpty

    def resetForm(): Unit =
      resetFields(editableFields*)
      clearErrors()
      archiveLocation.requestFocus()

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val confirmed =
            askConfirmation(
              titleText = Text.SaveTitle,
              header = Text.SaveHeader,
              content =
                s"""Numero protocollo: ${protocolNumber.value}
                   |Mittente: ${sender.value}
                   |Oggetto: ${subject.value}""".stripMargin
            )

          if confirmed then
            service.archiveDocument(
              source = selectedDocument,
              archivedDate = archivedDate.value,
              archivedTime = archivedTime.value,
              operatorUsername = operatorUsername,
              archiveLocation = archiveLocation.value
            ) match
              case Right(_) =>
                formSaved = true
                result.show(
                  message = Text.Success,
                  success = true
                )
                onArchived()

              case Left(error) =>
                result.show(
                  message = error,
                  success = false
                )

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val documentForm =
      formGrid(
        Seq(
          formRow(RegistrationFields.Id, id),
          formRow(RegistrationFields.ProtocolNumber, protocolNumber),
          formRow(RegistrationFields.RegisteredDate, registeredDate),
          formRow(RegistrationFields.RegisteredTime, registeredTime),
          formRow(RegistrationFields.RegisteredBy, registeredBy),
          formRow(DocumentFields.DocumentType, documentType),
          formRow(DocumentFields.Sender, sender),
          formRow(DocumentFields.Recipient, recipient),
          formRow(DocumentFields.Subject, subject),
          formRow(DocumentFields.Remarks, remarks)
        )
      )
    val archiveForm =
      formGrid(
        Seq(
          formRow(ArchiveFields.ArchivedDate, archivedDate),
          formRow(ArchiveFields.ArchivedTime, archivedTime),
          formRow(ArchiveFields.ArchivedBy, archivedBy),
          formRow(ArchiveFields.ArchiveLocation, archiveLocation)
        )
      )

    archiveForm.maxWidth = Double.MaxValue
    documentForm.maxWidth = Double.MaxValue
    val form =
      new HBox:
        spacing = 30
        alignment = Pos.TopCenter
        children = Seq(documentForm, archiveForm)

        HBox.setHgrow(documentForm, Priority.Always)
        HBox.setHgrow(archiveForm, Priority.Always)

    Platform.runLater:
      archiveLocation.requestFocus()

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, editableFields)
    )
