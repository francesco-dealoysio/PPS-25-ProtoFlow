package pkg.a.gui.views

import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.traits.Management
import pkg.b.logic.{ArchivedDocument, ArchivedDocumentService}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

import UiText.{ArchivedDocuments, Common}

object ArchivedDocumentManagementView extends Management:

  def apply(
             onView: ArchivedDocument => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new ArchivedDocumentService()
    val documents = ObservableBuffer.empty[ArchivedDocument]

    val result =
      createResultMessage(
        baseStyle = UiStyles.ArchivedDocuments.Message,
        successStyle = UiStyles.ArchivedDocuments.MessageSuccess,
        errorStyle = UiStyles.ArchivedDocuments.MessageError
      )

    val table =
      new TableView[ArchivedDocument](documents):
        columnResizePolicy = TableView.ConstrainedResizePolicy
        placeholder = new Label(ArchivedDocuments.Management.Empty)
        styleClass += UiStyles.LoadedDocuments.Table

    def stringColumn(title: String, columnWidth: Double)(value: ArchivedDocument => String): TableColumn[ArchivedDocument, String] =
      new TableColumn[ArchivedDocument, String]:
        text = title
        prefWidth = columnWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    table.columns ++= Seq(
      stringColumn(ArchivedDocuments.Fields.ProtocolNumber, 140)(_.getProtocolNumber),
      stringColumn(ArchivedDocuments.Fields.ArchivedDate, 120)(_.getArchivedDate),
      stringColumn(ArchivedDocuments.Fields.ArchivedTime, 100)(_.getArchivedTime),
      stringColumn(ArchivedDocuments.Fields.ArchivedBy, 150)(_.getArchivedBy),
      stringColumn(ArchivedDocuments.Fields.Subject, 220)(_.getSubject),
      stringColumn(ArchivedDocuments.Fields.ArchiveLocation, 180)(_.getArchiveLocation)
    )

    def loadDocuments(): Unit =
      result.clear()
      val archived =
        service
          .getArchivedDocuments
          .sortBy: document =>
            document
              .getId
              .toIntOption
              .getOrElse(Int.MaxValue)

      documents.setAll(archived*)
      table.selectionModel.value.clearSelection()

      if archived.isEmpty then
        result.show(ArchivedDocuments.Management.Empty, success = true)

    clearResultOnSelection(table, result)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("archived.xml"),
          pdfFileName = ArchivedDocuments.Management.PrintFileName,
          title = ArchivedDocuments.Management.PrintTitle,
          fields = Seq("protocolNumber", "archivedDate", "archivedTime", "archivedBy", "archiveLocation", "archiveRemarks")
        )

      result.show(
        message =
          if printed then
            ArchivedDocuments.Management.PrintSuccess
          else
            ArchivedDocuments.Management.PrintError,
        success = printed
      )

    val refreshButton =
      secondaryButton(
        Common.Buttons.Refresh,
        () => loadDocuments()
      )

    val print = printButton(action = () => printDocumentsList())

    val viewButton =
      primaryButton(
        ArchivedDocuments.Management.View,
        () =>
          selectedItem(table) match
            case Some(selected) =>
              result.clear()
              onView(selected)

            case None =>
              result.show(
                ArchivedDocuments.Management.SelectToView,
                success = false
              )
      )

    disableWithoutSelection(table, viewButton)
    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, print, viewButton))

    val header =
      titleBox(
        titleText = ArchivedDocuments.Management.Title,
        subtitleText = ArchivedDocuments.Management.Subtitle,
        titleStyle = UiStyles.ArchivedDocuments.Title,
        subtitleStyle = UiStyles.ArchivedDocuments.Subtitle
      )

    loadDocuments()

    managementPage(
      rootStyle = UiStyles.ArchivedDocuments.Root,
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )