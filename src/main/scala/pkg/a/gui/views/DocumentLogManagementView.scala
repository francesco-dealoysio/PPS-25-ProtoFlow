package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentLogs
import pkg.a.gui.text.UiText.DocumentLogs.{Fields, Management as Text}
import pkg.a.gui.traits.Management
import pkg.b.logic.DocumentLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.XmlToPdf
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.BorderPane

object DocumentLogManagementView extends Management:

  def apply(onView: DocumentLog => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val logs = ObservableBuffer.empty[DocumentLog]
    val result = createResultMessage()
    val table = managementTable(logs, DocumentLogs.Management.Empty)


    table.columns ++= Seq(
      stringColumn[DocumentLog](Fields.Id, Some(90))(_.getId),
      stringColumn[DocumentLog](Fields.DocumentId, Some(120))(_.getDocumentId),
      stringColumn[DocumentLog](Fields.OperationType, Some(170)): log =>
        DocumentLogs.operationLabel(log.getOperationType),
      stringColumn[DocumentLog](Fields.ProcessedDate, Some(130))(_.getProcessedDate),
      stringColumn[DocumentLog](Fields.ProcessedTime, Some(120))(_.getProcessedTime),
      stringColumn[DocumentLog](Fields.ProcessedBy, Some(190))(_.getProcessedBy)
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
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val exitButton = closeButton(onExit)
    val refreshButton = secondaryButton(Buttons.Refresh, () => loadLogs())

    val print = printButton(() => printLogs())

    val viewButton =
      primaryButton(
        DocumentLogs.Management.View,
        () =>
          selectedItem(table) match
            case Some(selected) =>
              result.clear()
              onView(selected)

            case None =>
              result.show(Text.SelectToView, success = false)
      )

    disableWithoutSelection(table, viewButton)

    val header = titleBox(Text.Title, Text.Subtitle)

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