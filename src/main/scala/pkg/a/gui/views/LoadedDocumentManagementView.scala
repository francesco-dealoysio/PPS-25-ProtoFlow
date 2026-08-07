package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{LoadedDocument, LoadedDocumentService}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields, Management as Text}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object LoadedDocumentManagementView extends Management:

  def apply(
             onRegister: LoadedDocument => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new LoadedDocumentService()
    val documents = ObservableBuffer.empty[LoadedDocument]

    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    table.columns ++= Seq(
      stringColumn[LoadedDocument](Fields.Sender, Some(160))(_.getSender),
      stringColumn[LoadedDocument](Fields.Subject, Some(220))(_.getSubject),
      stringColumn[LoadedDocument](Fields.DocumentType, Some(110))(_.getDocumentType),
      stringColumn[LoadedDocument](Fields.DocumentDate, Some(110))(_.getDocumentDate),
      stringColumn[LoadedDocument](Fields.ProcessedBy, Some(140))(_.getProcessedBy)
    )

    def loadDocuments(): Unit =
      loadTableItems(table, documents, result, Text.Empty):
        service
          .getLoadedDocuments()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

    clearResultOnSelection(table, result)

    def deleteSelectedDocument(): Unit =
      withSelectedItem(table, result, Text.SelectToDelete): selected =>
        val confirmed =
          askConfirmation(
            titleText = Text.DeleteTitle,
            header = Text.DeleteConfirmation,
            content =
              s"""Mittente: ${selected.getSender}
                 |Oggetto: ${selected.getSubject}""".stripMargin
          )
        if confirmed then
          if service.deleteLoadedDocument(selected.getId) then
            loadDocuments()
            result.show(Text.Deleted, success = true)
          else
            result.show(Text.DeleteError, success = false)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("loaded.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle
        )

      result.show(
        if printed then Text.PrintSuccess
        else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, () => loadDocuments())
    val printListButton = printButton(action = () => printDocumentsList())

    val registerButton = primaryButton(Buttons.Register, () => withSelectedItem(table, result, Text.SelectToRegister)(onRegister))

    val deleteButton = dangerButton(Buttons.Delete, () => deleteSelectedDocument())

    disableWithoutSelection(table, registerButton, deleteButton)

    val exitButton = closeButton(onExit)

    val bottomActions = actionBar(Seq(exitButton, refreshButton, printListButton, deleteButton, registerButton))

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
