package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentLogs
import pkg.a.gui.text.UiText.DocumentLogs.{Fields, Management as Text}
import pkg.a.gui.traits.Management
import pkg.b.logic.DocumentLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.{XmlToPdf, getDocumentLogPredicate}

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ComboBox, DatePicker, TextField}
import scalafx.scene.layout.{BorderPane, HBox}

object DocumentLogManagementView extends Management:

  def apply(onView: DocumentLog => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val logs = ObservableBuffer.empty[DocumentLog]
    val result = createResultMessage()
    val table = managementTable(logs, Text.Empty)

    val operationFilter = new ComboBox[String]:
      items = ObservableBuffer("Tutte", "Presa in carico", "Protocollazione", "Archiviazione")
      value = "Tutte"

    val fromDateFilter = new DatePicker()
    val toDateFilter = new DatePicker()

    val documentIdFilter = new TextField:
      promptText = "ID documento"

    val operatorFilter = new ComboBox[String]:
      items = ObservableBuffer("Tutti gli operatori")
      value = "Tutti gli operatori"

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
      operatorFilter.items = ObservableBuffer("Tutti gli operatori" +: operators *)

      if currentSelection != null && operatorFilter.items.value.contains(currentSelection) then
        operatorFilter.value = currentSelection
      else
        operatorFilter.value = "Tutti gli operatori"

    def selectedOperationType: Option[String] =
      operationFilter.value.value match
        case "Presa in carico" =>
          Some("loading")

        case "Protocollazione" =>
          Some("registering")

        case "Archiviazione" =>
          Some("archiving")

        case _ =>
          None

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
          .filter(_ != "Tutti gli operatori")
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
          val predicate = getDocumentLogPredicate(criteria)

          DocumentLog().getRecordsByFilter[DocumentLog](predicate)

      val sortedLogs = filteredLogs.sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      logs.setAll(sortedLogs*)
      table.selectionModel.value.clearSelection()

      if sortedLogs.isEmpty then
        result.show("Nessun log corrisponde ai filtri selezionati.", success = true)

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
      operationFilter.value = "Tutte"
      fromDateFilter.value = null
      toDateFilter.value = null
      documentIdFilter.clear()
      operatorFilter.value = "Tutti gli operatori"
      loadLogs()

    val exitButton = closeButton(onExit)
    val refreshButton = secondaryButton(Buttons.Refresh, () => loadLogs())
    val print = printButton(() => printLogs())
    val resetFilterButton = secondaryButton(Buttons.ResetFilter, () => resetFilters())

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