package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentLogs
import pkg.a.gui.text.UiText.DocumentLogs.{Fields, Operations, Prompts, Management as Text}
import pkg.a.gui.traits.Management
import pkg.b.logic.DocumentLog
import pkg.b.logic.pdf.{PdfTableCreator, PdfViewer}
import pkg.d.util.Filters.getDocumentOperationsLogPredicate
import pkg.d.util.Util.inPrintsFilePathName
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

      showFilteredItems(logs, table, filteredLogs, result, Text.NoFilterResults)(_.getId)

    clearResultOnSelection(table, result)

    def logRow(log: DocumentLog): Seq[String] =
      Seq(
        log.getId,
        log.getDocumentId,
        Operations.labelOf(log.getOperationType),
        log.getProcessedDate,
        log.getProcessedTime,
        log.getProcessedBy
      )

    def printLogs(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileName}.pdf")
      val printed =
        PdfTableCreator.createTablePdf(
          pdfPathName = pdfPath,
          title = Text.PrintTitle,
          headers = Seq(
            Fields.Id,
            Fields.DocumentId,
            Fields.OperationType,
            Fields.ProcessedDate,
            Fields.ProcessedTime,
            Fields.ProcessedBy
          ),
          rows = logs.toSeq.map(logRow),
          columnWeights = Seq(0.8f, 1.1f, 1.5f, 1.2f, 1f, 1.6f)
        )

      if printed then
        PdfViewer.viewPdf(pdfPath)

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