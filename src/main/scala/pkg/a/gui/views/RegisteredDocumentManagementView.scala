package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{LoadedDocumentService, RegisteredDocument}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.RegisteredDocuments.{Fields, Management as Text}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object RegisteredDocumentManagementView extends Management:

  def apply(onArchive: RegisteredDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val service = new LoadedDocumentService()
    val documents = ObservableBuffer.empty[RegisteredDocument]

    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    table.columns ++= Seq(
      stringColumn[RegisteredDocument](Fields.ProtocolNumber, Some(140))(_.getProtocolNumber),
      stringColumn[RegisteredDocument](Fields.Sender, Some(150))(_.getSender),
      stringColumn[RegisteredDocument](Fields.Subject, Some(220))(_.getSubject),
      stringColumn[RegisteredDocument](Fields.Type, Some(90))(_.getDocumentType),
      stringColumn[RegisteredDocument](Fields.ProtocolledBy, Some(130))(_.getRegisteredBy)
    )

    def loadDocuments(): Unit =
      result.clear()

      val registered =
        service.getRegisteredDocuments()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      documents.setAll(registered*)
      table.selectionModel.value.clearSelection()

      if registered.isEmpty then
        result.show(Text.Empty, success = true)

    clearResultOnSelection(table, result)

    def deleteSelectedDocument(): Unit =
      selectedItem(table) match
        case None =>
          result.show(Text.SelectToDelete, success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = Text.DeleteTitle,
              header = Text.DeleteConfirmation,
              content =
                s"""Numero protocollo: ${selected.getProtocolNumber}
                   |Mittente: ${selected.getSender}
                   |Oggetto: ${selected.getSubject}""".stripMargin
            )

          if confirmed then
            if service.deleteRegisteredDocument(selected.getId) then
              loadDocuments()
              result.show(Text.Deleted, success = true)
            else
              result.show(Text.DeleteError, success = false)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("registered.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          fields = Seq("protocolNumber", "registeredDate", "registeredTime", "registeredBy", "documentType", "sender", "recipient", "subject")
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, () => loadDocuments())
    val printListButton = printButton(action = () => printDocumentsList())

    val archiveButton =
      primaryButton(Buttons.Archive, () =>
        selectedItem(table) match
          case Some(selected) =>
            result.clear()
            onArchive(selected)

          case None =>
            result.show(Text.SelectToArchive, success = false)
      )

    val deleteButton = dangerButton(Buttons.Delete, () => deleteSelectedDocument())

    disableWithoutSelection(table, archiveButton, deleteButton)

    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, printListButton, deleteButton, archiveButton))
    val header = titleBox(Text.Title, Text.Subtitle)

    loadDocuments()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
