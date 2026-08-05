package pkg.a.gui.views

import pkg.a.gui.text.UiText.{Common, DocumentLogs}
import pkg.a.gui.text.UiText.DocumentLogs.Management.*
import pkg.a.gui.traits.Management
import pkg.b.logic.DocumentLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.XmlToPdf
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object DocumentLogManagementView extends Management:

  def apply(onView: DocumentLog => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val logs = ObservableBuffer.empty[DocumentLog]
    val result = createResultMessage()
    val table = managementTable(logs, DocumentLogs.Management.Empty)

    def stringColumn(title: String, columnWidth: Double)(value: DocumentLog => String): TableColumn[DocumentLog, String] =
      new TableColumn[DocumentLog, String]:
        text = title
        prefWidth = columnWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    table.columns ++= Seq(
      stringColumn(DocumentLogs.Fields.Id, 90)(_.getId),
      stringColumn(DocumentLogs.Fields.DocumentId, 120)(_.getDocumentId),
      stringColumn(DocumentLogs.Fields.OperationType, 170): log =>
        DocumentLogs.operationLabel(log.getOperationType),
      stringColumn(DocumentLogs.Fields.ProcessedDate, 130)(_.getProcessedDate),
      stringColumn(DocumentLogs.Fields.ProcessedTime, 120)(_.getProcessedTime),
      stringColumn(DocumentLogs.Fields.ProcessedBy, 190)(_.getProcessedBy)
    )

    def loadLogs(): Unit =
      result.clear()

      val loadedLogs =
        DocumentLog()
          .getRecords[DocumentLog]()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      logs.setAll(loadedLogs*)
      table.selectionModel.value.clearSelection()

      if loadedLogs.isEmpty then
        result.show(
          DocumentLogs.Management.Empty,
          success = true
        )

    clearResultOnSelection(table, result)

    def printLogs(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inLogFilePathName("documentOperations.xml"),
          pdfFileName = DocumentLogs.Management.PrintFileName,
          title = DocumentLogs.Management.PrintTitle,
          fields = Seq(
            "id",
            "documentId",
            "operationType",
            "processedDate",
            "processedTime",
            "processedBy"
          )
        )

      result.show(
        if printed then PrintSuccess else PrintError,
        success = printed
      )

    val exitButton =
      closeButton(onExit)

    val refreshButton =
      secondaryButton(
        Common.Buttons.Refresh,
        () => loadLogs()
      )

    val print =
      printButton(() => printLogs())

    val viewButton =
      primaryButton(
        DocumentLogs.Management.View,
        () =>
          selectedItem(table) match
            case Some(selected) =>
              result.clear()
              onView(selected)

            case None =>
              result.show(SelectToView, success = false)
      )

    disableWithoutSelection(table, viewButton)

    val header = titleBox(Title, Subtitle)

    val actions = actionBar(Seq(exitButton, refreshButton, print, viewButton))

    loadLogs()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        actions
      )
    )