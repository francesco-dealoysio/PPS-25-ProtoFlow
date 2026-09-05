package pkg.a.gui.views

import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields, Management as Text}
import pkg.a.gui.traits.Management
import pkg.b.logic.LoadedDocument
import pkg.b.logic.pdf.{PdfTableCreator, PdfViewer}
import pkg.d.util.Filters.getLoadedDocumentPredicate
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object LoadedDocumentManagementView extends Management:

  def apply(
             onRegister: LoadedDocument => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =
    
    val documents = ObservableBuffer.empty[LoadedDocument]
    var allDocuments = List.empty[LoadedDocument]
    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    val fromDateFilter = dateFilter()
    val toDateFilter = dateFilter()
    val subjectFilter = textFilter(CommonDocumentFields.Subject)
    val operatorFilter = comboFilter(Text.AllOperators)

    table.columns ++= Seq(
      stringColumn[LoadedDocument](CommonDocumentFields.Id, Some(160))(_.getId),
      stringColumn[LoadedDocument](CommonDocumentFields.Sender, Some(160))(_.getSender),
      stringColumn[LoadedDocument](CommonDocumentFields.Subject, Some(220))(_.getSubject),
      stringColumn[LoadedDocument](Fields.DocumentType, Some(110))(_.getDocumentType),
      stringColumn[LoadedDocument](Fields.DocumentDate, Some(110))(_.getDocumentDate),
      stringColumn[LoadedDocument](Fields.ProcessedBy, Some(140))(_.getProcessedBy)
    )

    def loadDocuments(): Unit =
      result.clear()

      allDocuments =
        LoadedDocumentService
          .getLoadedDocuments
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      updateComboFilter(operatorFilter, Text.AllOperators, allDocuments)(_.getProcessedBy)

      documents.setAll(allDocuments*)
      table.selectionModel.value.clearSelection()

      if allDocuments.isEmpty then
        result.show(Text.Empty, success = true)

    clearResultOnSelection(table, result)

    def buildFilterCriteria(): List[FilterCriterion] =
      List(
        dateCriterion(fromDateFilter, "getProcessedDate", ">="),
        dateCriterion(toDateFilter, "getProcessedDate", "<="),
        textCriterion(subjectFilter, "getSubject", "contains"),
        comboCriterion(operatorFilter, Text.AllOperators, "getProcessedBy")
      ).flatten

    def searchDocuments(): Unit =
      result.clear()
      val criteria = buildFilterCriteria()
      val filteredDocuments =
        if criteria.isEmpty then allDocuments
        else allDocuments.filter(getLoadedDocumentPredicate(criteria))
      showFilteredItems(documents, table, filteredDocuments, result, Text.NoFilterResults)(_.getId)

    def resetFilters(): Unit =
      fromDateFilter.value = null
      toDateFilter.value = null
      subjectFilter.clear()
      operatorFilter.value = Text.AllOperators
      searchDocuments()

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
          if LoadedDocumentService.deleteLoadedDocument(selected.getId) then
            loadDocuments()
            result.show(Text.Deleted, success = true)
          else
            result.show(Text.DeleteError, success = false)

    def documentRow(document: LoadedDocument): Seq[String] =
      Seq(
        document.getId,
        document.getSender,
        document.getSubject,
        document.getDocumentType,
        document.getDocumentDate,
        document.getProcessedBy
      )

    def printDocumentsList(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileName}.pdf")
      val printed =
        PdfTableCreator.createTablePdf(
          pdfPathName = pdfPath,
          title = Text.PrintTitle,
          headers = Seq(
            CommonDocumentFields.Id,
            CommonDocumentFields.Sender,
            CommonDocumentFields.Subject,
            Fields.DocumentType,
            Fields.DocumentDate,
            Fields.ProcessedBy
          ),
          rows = documents.toSeq.map(documentRow),
          columnWeights = Seq(1.2f, 1.5f, 2.5f, 1.1f, 1.2f, 1.5f)
        )

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadDocuments)
    val resetFilterButton = secondaryButton(Buttons.ResetFilter, resetFilters)
    val registerButton = primaryButton(Buttons.Register, () => withSelectedItem(table, result, Text.SelectToRegister)(onRegister))
    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedDocument)
    disableWithoutSelection(table, registerButton, deleteButton)
    val bottomActions = actionBar(Seq(resetFilterButton, closeButton(onExit), refreshButton, printButton(printDocumentsList), deleteButton, registerButton))
    val header = titleBox(Text.Title, Text.Subtitle)
    val filters = filterBar(fromDateFilter, toDateFilter, subjectFilter, operatorFilter)

    loadDocuments()

    bindSearch(fromDateFilter, toDateFilter, subjectFilter, operatorFilter)(searchDocuments())

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