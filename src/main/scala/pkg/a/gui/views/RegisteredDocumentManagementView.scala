package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{LoadedDocumentService, RegisteredDocument}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Common, RegisteredDocuments}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object RegisteredDocumentManagementView extends Management:

  def apply(onArchive: RegisteredDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val service = new LoadedDocumentService()
    val documents = ObservableBuffer.empty[RegisteredDocument]

    val result = createResultMessage()

    val table = managementTable(documents, RegisteredDocuments.Management.Empty)

    def stringColumn(title: String, colWidth: Double)(value: RegisteredDocument => String): TableColumn[RegisteredDocument, String] =
      new TableColumn[RegisteredDocument, String]:
        text = title
        prefWidth = colWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    table.columns ++= Seq(
      stringColumn("Numero protocollo", 140)(_.getProtocolNumber),
      stringColumn("Mittente", 150)(_.getSender),
      stringColumn("Oggetto", 220)(_.getSubject),
      stringColumn("Tipo", 90)(_.getDocumentType),
      stringColumn("Protocollato da", 130)(_.getRegisteredBy)
    )

    def loadDocuments(): Unit =
      result.clear()

      val registered =
        service.getRegisteredDocuments()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      documents.setAll(registered*)
      table.selectionModel.value.clearSelection()

      if registered.isEmpty then
        result.show(RegisteredDocuments.Management.Empty, success = true)

    clearResultOnSelection(table, result)

    def deleteSelectedDocument(): Unit =
      selectedItem(table) match
        case None =>
          result.show(RegisteredDocuments.Management.SelectToDelete, success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = RegisteredDocuments.Management.DeleteTitle,
              header = RegisteredDocuments.Management.DeleteConfirmation,
              content =
                s"""Numero protocollo: ${selected.getProtocolNumber}
                   |Mittente: ${selected.getSender}
                   |Oggetto: ${selected.getSubject}""".stripMargin
            )

          if confirmed then
            if service.deleteRegisteredDocument(selected.getId) then
              loadDocuments()
              result.show(RegisteredDocuments.Management.Deleted, success = true)
            else
              result.show(RegisteredDocuments.Management.DeleteError, success = false)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("registered.xml"),
          pdfFileName = RegisteredDocuments.Management.PrintFileName,
          title = RegisteredDocuments.Management.PrintTitle,
          fields = Seq("protocolNumber", "registeredDate", "registeredTime", "registeredBy", "documentType", "sender", "recipient", "subject")
        )

      result.show(
        if printed then RegisteredDocuments.Management.PrintSuccess
        else RegisteredDocuments.Management.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Common.Buttons.Refresh, () => loadDocuments())
    val printListButton = printButton(action = () => printDocumentsList())

    val archiveButton =
      primaryButton(Common.Buttons.Archive, () =>
        selectedItem(table) match
          case Some(selected) =>
            result.clear()
            onArchive(selected)

          case None =>
            result.show(RegisteredDocuments.Management.SelectToArchive, success = false)
      )

    val deleteButton = dangerButton(Common.Buttons.Delete, () => deleteSelectedDocument())

    disableWithoutSelection(table, archiveButton, deleteButton)

    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, printListButton, deleteButton, archiveButton))
    val header = titleBox(RegisteredDocuments.Management.Title, RegisteredDocuments.Management.Subtitle)

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
