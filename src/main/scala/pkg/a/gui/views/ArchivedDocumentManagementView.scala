package pkg.a.gui.views

import pkg.a.gui.services.ArchivedDocumentService
import pkg.a.gui.traits.Management
import pkg.b.logic.ArchivedDocument
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.{XmlToPdf, getDocumentPredicate}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.{BorderPane, HBox}
import pkg.a.gui.text.UiText.ArchivedDocuments.{Fields, Management as Text}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.Common.Buttons

object ArchivedDocumentManagementView extends Management:

  def apply(onView: ArchivedDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val service = new ArchivedDocumentService()
    val documents = ObservableBuffer.empty[ArchivedDocument]

    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    val fromDateFilter = new DatePicker()
    val toDateFilter = new DatePicker()

    val subjectFilter = new TextField:
      promptText = CommonDocumentFields.Subject

    val operatorFilter = new ComboBox[String]:
      items = ObservableBuffer(Text.AllOperators)
      value = Text.AllOperators

    table.columns ++= Seq(
      stringColumn[ArchivedDocument](CommonDocumentFields.Id, Some(140))(_.getId),
      stringColumn[ArchivedDocument](Fields.ProtocolNumber, Some(140))(_.getProtocolNumber),
      stringColumn[ArchivedDocument](Fields.ArchivedDate, Some(120))(_.getArchivedDate),
      stringColumn[ArchivedDocument](Fields.ArchivedTime, Some(100))(_.getArchivedTime),
      stringColumn[ArchivedDocument](Fields.ArchivedBy, Some(150))(_.getArchivedBy),
      stringColumn[ArchivedDocument](CommonDocumentFields.Subject, Some(220))(_.getSubject),
      stringColumn[ArchivedDocument](Fields.ArchiveLocation, Some(180))(_.getArchiveLocation)
    )

    def updateOperatorFilter(loadedDocuments: Seq[ArchivedDocument]): Unit =
      val operators =
        loadedDocuments
          .map(_.getArchivedBy.trim)
          .filter(_.nonEmpty)
          .distinct
          .sorted

      val currentSelection = operatorFilter.value.value
      operatorFilter.items = ObservableBuffer(Text.AllOperators +: operators *)

      if currentSelection != null && operatorFilter.items.value.contains(currentSelection) then
        operatorFilter.value = currentSelection
      else
        operatorFilter.value = Text.AllOperators

    def loadDocuments(): Unit =
      result.clear()

      val loadedDocuments =
        service
          .getArchivedDocuments
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      updateOperatorFilter(loadedDocuments)

      documents.setAll(loadedDocuments*)
      table.selectionModel.value.clearSelection()

      if loadedDocuments.isEmpty then
        result.show(Text.Empty, success = true)

    clearResultOnSelection(table, result)

    def buildFilterCriteria(): List[(String, String, List[String])] =

      val fromDateCriteria =
        Option(fromDateFilter.value.value)
          .map(date => ("getArchivedDate", ">=", List(date.toString)))
          .toList

      val toDateCriteria =
        Option(toDateFilter.value.value)
          .map(date => ("getArchivedDate", "<=", List(date.toString)))
          .toList

      val subjectCriteria =
        Option(subjectFilter.text.value)
          .map(_.trim)
          .filter(_.nonEmpty)
          .map(value => ("getSubject", "contains", List(value)))
          .toList

      val operatorCriteria =
        Option(operatorFilter.value.value)
          .filter(_ != Text.AllOperators)
          .map(value => ("getArchivedBy", "=", List(value)))
          .toList

      fromDateCriteria ++ toDateCriteria ++ subjectCriteria ++ operatorCriteria

    def searchDocuments(): Unit =
      result.clear()

      val criteria = buildFilterCriteria()

      val filteredDocuments =
        if criteria.isEmpty then
          service.getArchivedDocuments
        else
          service.getArchivedDocuments(getDocumentPredicate(criteria))

      val sortedDocuments = filteredDocuments.sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      documents.setAll(sortedDocuments*)
      table.selectionModel.value.clearSelection()

      if sortedDocuments.isEmpty then
        result.show(Text.NoFilterResults, success = true)

    def resetFilters(): Unit =
      fromDateFilter.value = null
      toDateFilter.value = null
      subjectFilter.clear()
      operatorFilter.value = Text.AllOperators
      loadDocuments()

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("archived.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          fields = Seq("protocolNumber", "archivedDate", "archivedTime", "archivedBy", "archiveLocation"),
          recordIds = documents.map(_.getId).toSeq
        )

      result.show(
        message = if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadDocuments)

    val print = printButton(printDocumentsList)

    val resetFilterButton = secondaryButton(Buttons.ResetFilter, () => resetFilters())

    val viewButton = primaryButton(Text.View, () => withSelectedItem(table, result, Text.SelectToView)(onView))

    disableWithoutSelection(table, viewButton)
    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(resetFilterButton, exitButton, refreshButton, print, viewButton))

    val header = titleBox(Text.Title, Text.Subtitle)

    val filters =
      new HBox:
        spacing = 10
        children = Seq(
          fromDateFilter,
          toDateFilter,
          subjectFilter,
          operatorFilter
        )

    loadDocuments()

    fromDateFilter.value.onChange:
      searchDocuments()

    toDateFilter.value.onChange:
      searchDocuments()

    subjectFilter.text.onChange:
      searchDocuments()

    operatorFilter.value.onChange:
      searchDocuments()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        filters,
        table,
        result.label,
        bottomActions
      )
    )
