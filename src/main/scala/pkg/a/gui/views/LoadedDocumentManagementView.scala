package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{LoadedDocument, LoadedDocumentService}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Common, LoadedDocuments}

import scalafx.Includes.*
import scalafx.beans.property.StringProperty
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

    val result =
      createResultMessage(
        baseStyle = UiStyles.LoadedDocuments.Message,
        successStyle = UiStyles.LoadedDocuments.MessageSuccess,
        errorStyle = UiStyles.LoadedDocuments.MessageError
      )

    val table = new TableView[LoadedDocument](documents):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(LoadedDocuments.Management.Empty)
      styleClass += UiStyles.LoadedDocuments.Table

    def stringColumn(title: String, colWidth: Double)(value: LoadedDocument => String): TableColumn[LoadedDocument, String] =
      new TableColumn[LoadedDocument, String]:
        text = title
        prefWidth = colWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    table.columns ++= Seq(
      stringColumn(LoadedDocuments.Fields.Sender, 160)(_.getSender),
      stringColumn(LoadedDocuments.Fields.Subject, 220)(_.getSubject),
      stringColumn(LoadedDocuments.Fields.DocumentType, 110)(_.getDocumentType),
      stringColumn(LoadedDocuments.Fields.DocumentDate, 110)(_.getDocumentDate),
      stringColumn("Preso in carico da", 140)(_.getProcessedBy)
    )

    def loadDocuments(): Unit =
      result.clear()

      val loaded =
        service.getLoadedDocuments()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      documents.setAll(loaded*)
      table.selectionModel.value.clearSelection()

      if loaded.isEmpty then
        result.show(LoadedDocuments.Management.Empty, success = true)

    clearResultOnSelection(table, result)

    def deleteSelectedDocument(): Unit =
      selectedItem(table) match
        case None =>
          result.show(LoadedDocuments.Management.SelectToDelete, success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = LoadedDocuments.Management.DeleteTitle,
              header = LoadedDocuments.Management.DeleteConfirmation,
              content =
                s"""Mittente: ${selected.getSender}
                   |Oggetto: ${selected.getSubject}""".stripMargin
            )

          if confirmed then
            if service.deleteLoadedDocument(selected.getId) then
              loadDocuments()
              result.show(LoadedDocuments.Management.Deleted, success = true)
            else
              result.show(LoadedDocuments.Management.DeleteError, success = false)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("loaded.xml"),
          pdfFileName = LoadedDocuments.Management.PrintFileName,
          title = LoadedDocuments.Management.PrintTitle
        )

      result.show(
        if printed then LoadedDocuments.Management.PrintSuccess
        else LoadedDocuments.Management.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Common.Buttons.Refresh, () => loadDocuments())
    val printListButton = printButton(action = () => printDocumentsList())

    val registerButton =
      primaryButton(Common.Buttons.Register, () =>
        selectedItem(table) match
          case Some(selected) =>
            result.clear()
            onRegister(selected)

          case None =>
            result.show(LoadedDocuments.Management.SelectToRegister, success = false)
      )

    val deleteButton = dangerButton(Common.Buttons.Delete, () => deleteSelectedDocument())

    disableWithoutSelection(table, registerButton, deleteButton)

    val exitButton = closeButton(onExit)

    val bottomActions =
      actionBar(Seq(exitButton, refreshButton, printListButton, deleteButton, registerButton))

    val header =
      titleBox(
        titleText = LoadedDocuments.Management.Title,
        subtitleText = LoadedDocuments.Management.Subtitle,
        titleStyle = UiStyles.LoadedDocuments.Title,
        subtitleStyle = UiStyles.LoadedDocuments.Subtitle
      )

    loadDocuments()

    managementPage(
      rootStyle = UiStyles.LoadedDocuments.Root,
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
