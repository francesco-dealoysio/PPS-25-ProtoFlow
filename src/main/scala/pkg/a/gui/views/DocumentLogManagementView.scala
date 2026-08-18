package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentLogs
import pkg.a.gui.text.UiText.DocumentLogs.{Fields, Management as Text, Operations}
import pkg.a.gui.traits.Management
import pkg.b.logic.DocumentLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.{XmlToPdf, getDocumentOperationsLogPredicate}

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ComboBox, DatePicker, TextField}
import scalafx.scene.layout.{BorderPane, HBox}

object DocumentLogManagementView extends Management:

  def apply(onView: DocumentLog => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val logs = ObservableBuffer.empty[DocumentLog]
    val result = createResultMessage()
    val table = managementTable(logs, Text.Empty)

    val operationFilter = new ComboBox[String]:
      items = ObservableBuffer(Text.AllOperations +: Operations.values.map(_._2) *)
      value = Text.AllOperations

    val fromDateFilter = new DatePicker()
    val toDateFilter = new DatePicker()

    val documentIdFilter = new TextField:
      promptText = Fields.DocumentId

    val operatorFilter = new ComboBox[String]:
      items = ObservableBuffer(Text.AllOperators)
      value = Text.AllOperators

    table.columns ++= Seq(
      stringColumn[DocumentLog](Fields.Id, Some(90))(_.getId),
      stringColumn[DocumentLog](Fields.DocumentId, Some(120))(_.getDocumentId),
      stringColumn[DocumentLog](Fields.OperationType, Some(170)): log =>
        Operations.labelOf(log.getOperationType),
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

      updateOperatorFilter(loadedLogs)

      logs.setAll(loadedLogs*)
      table.selectionModel.value.clearSelection()

      if loadedLogs.isEmpty then
        result.show(Text.Empty, success = true)

    def updateOperatorFilter(loadedLogs: Seq[DocumentLog]): Unit =
      val operators =
        loadedLogs
          .map(_.getProcessedBy.trim)
          .filter(_.nonEmpty)
          .distinct
          .sorted

      val currentSelection = operatorFilter.value.value
      operatorFilter.items = ObservableBuffer(Text.AllOperators +: operators *)

      if currentSelection != null && operatorFilter.items.value.contains(currentSelection) then
        operatorFilter.value = currentSelection
      else
        operatorFilter.value = Text.AllOperators

    def selectedOperationType: Option[String] =
      Operations.valueOf(operationFilter.value.value)

    def buildFilterCriteria(): List[(String, String, List[String])] =

      val operationCriteria =
        selectedOperationType
          .map(value => ("getOperationType", "=", List(value)))
          .toList

      val fromDateCriteria =
        Option(fromDateFilter.value.value)
          .map(date => ("getProcessedDate", ">=", List(date.toString)))
          .toList

      val toDateCriteria =
        Option(toDateFilter.value.value)
          .map(date => ("getProcessedDate", "<=", List(date.toString)))
          .toList

      val documentIdCriteria =
        Option(documentIdFilter.text.value)
          .map(_.trim)
          .filter(_.nonEmpty)
          .map(value => ("getDocumentId", "=", List(value)))
          .toList

      val operatorCriteria =
        Option(operatorFilter.value.value)
          .filter(_ != Text.AllOperators)
          .map(value => ("getProcessedBy", "=", List(value)))
          .toList

      operationCriteria ++
        fromDateCriteria ++
        toDateCriteria ++
        documentIdCriteria ++
        operatorCriteria

    def searchLogs(): Unit =
      result.clear()

      val criteria = buildFilterCriteria()

      val filteredLogs =
        if criteria.isEmpty then
          DocumentLog().getRecords[DocumentLog]()
        else
          val predicate = getDocumentOperationsLogPredicate(criteria)

          DocumentLog().getRecordsByFilter[DocumentLog](predicate)

      val sortedLogs = filteredLogs.sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      logs.setAll(sortedLogs*)
      table.selectionModel.value.clearSelection()

      if sortedLogs.isEmpty then
        result.show(Text.NoFilterResults, success = true)

    clearResultOnSelection(table, result)

    def printLogs(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inLogFilePathName("documentOperations.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          fields = Seq(
            "id",
            "documentId",
            "operationType",
            "processedDate",
            "processedTime",
            "processedBy"
          ),
          recordIds = logs.map(_.getId).toSeq
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    def resetFilters(): Unit =
      operationFilter.value = Text.AllOperations
      fromDateFilter.value = null
      toDateFilter.value = null
      documentIdFilter.clear()
      operatorFilter.value = Text.AllOperations
      loadLogs()

    val exitButton = closeButton(onExit)
    val refreshButton = secondaryButton(Buttons.Refresh, loadLogs)
    val print = printButton(printLogs)
    val resetFilterButton = secondaryButton(Buttons.ResetFilter, resetFilters)

    val viewButton = primaryButton(DocumentLogs.Management.View, () => withSelectedItem(table, result, Text.SelectToView)(onView))

    val filters =
      new HBox:
        spacing = 10
        children = Seq(
          operationFilter,
          fromDateFilter,
          toDateFilter,
          documentIdFilter,
          operatorFilter
        )

    disableWithoutSelection(table, viewButton)

    val header = titleBox(Text.Title, Text.Subtitle)

    val actions = actionBar(Seq(resetFilterButton, exitButton, refreshButton, print, viewButton))

    loadLogs()

    operationFilter.value.onChange:
      searchLogs()

    fromDateFilter.value.onChange:
      searchLogs()

    toDateFilter.value.onChange:
      searchLogs()

    documentIdFilter.text.onChange:
      searchLogs()

    operatorFilter.value.onChange:
      searchLogs()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        filters,
        table,
        result.label,
        actions
      )
    )