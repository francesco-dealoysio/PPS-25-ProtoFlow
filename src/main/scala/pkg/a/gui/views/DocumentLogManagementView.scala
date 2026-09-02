package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentLogs
import pkg.a.gui.text.UiText.DocumentLogs.{Fields, Management as Text, Operations, Prompts}
import pkg.a.gui.traits.Management
import pkg.b.logic.DocumentLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.Filters.getDocumentOperationsLogPredicate
import pkg.d.util.XmlToPdf
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ComboBox, DatePicker, TextField}
import scalafx.scene.layout.BorderPane

object DocumentLogManagementView extends Management:

  def apply(onView: DocumentLog => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val logs = ObservableBuffer.empty[DocumentLog]
    val result = createResultMessage()
    val table = managementTable(logs, Text.Empty)

    val operationFilter = comboFilter(Text.AllOperations, Operations.values.map(_._2))
    val fromDateFilter = dateFilter(Prompts.FromDate)
    val toDateFilter = dateFilter(Prompts.ToDate)
    val documentIdFilter = textFilter(Fields.DocumentId)
    val operatorFilter = comboFilter(Text.AllOperators)

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

      updateComboFilter(operatorFilter, Text.AllOperators, loadedLogs)(_.getProcessedBy)

      logs.setAll(loadedLogs*)
      table.selectionModel.value.clearSelection()

      if loadedLogs.isEmpty then
        result.show(Text.Empty, success = true)

    def selectedOperationType: Option[String] =
      Operations.valueOf(operationFilter.value.value)

    def buildFilterCriteria(): List[FilterCriterion] =
      val operationCriterion =
        selectedOperationType
          .map(value => ("getOperationType", "=", List(value)))

      List(
        operationCriterion,
        dateCriterion(fromDateFilter, "getProcessedDate", ">="),
        dateCriterion(toDateFilter, "getProcessedDate", "<="),
        textCriterion(documentIdFilter, "getDocumentId", "="),
        comboCriterion(operatorFilter, Text.AllOperators, "getProcessedBy")
      ).flatten

    def searchLogs(): Unit =
      result.clear()
      val criteria = buildFilterCriteria()
      val filteredLogs =
        if criteria.isEmpty then
          DocumentLog().getRecords[DocumentLog]()
        else
          val predicate = getDocumentOperationsLogPredicate(criteria)

          DocumentLog().getRecordsByFilter[DocumentLog](predicate)

      showFilteredItems(logs, table, filteredLogs, result)(_.getId)

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
      operatorFilter.value = Text.AllOperators
      loadLogs()

    val refreshButton = secondaryButton(Buttons.Refresh, loadLogs)
    val resetFilterButton = secondaryButton(Buttons.ResetFilter, resetFilters)
    val viewButton = primaryButton(DocumentLogs.Management.View, () => withSelectedItem(table, result, Text.SelectToView)(onView))
    val filters = filterBar(operationFilter, fromDateFilter, toDateFilter, documentIdFilter, operatorFilter)

    disableWithoutSelection(table, viewButton)

    val header = titleBox(Text.Title, Text.Subtitle)

    val actions = actionBar(Seq(resetFilterButton, closeButton(onExit), refreshButton, printButton(printLogs), viewButton))

    loadLogs()

    bindSearch(
      dateFilters = Seq(fromDateFilter, toDateFilter),
      textFilters = Seq(documentIdFilter),
      comboFilters = Seq(operationFilter, operatorFilter)
    )(searchLogs)

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