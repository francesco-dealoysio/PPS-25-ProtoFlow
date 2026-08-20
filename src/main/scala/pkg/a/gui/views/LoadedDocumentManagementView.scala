package pkg.a.gui.views

import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.traits.Management
import pkg.b.logic.LoadedDocument
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.{XmlToPdf, getLoadedDocumentPredicate}
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.LoadedDocuments.{Fields, Management as Text}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object LoadedDocumentManagementView extends Management:

  def apply(onRegister: LoadedDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val service = new LoadedDocumentService()
    val documents = ObservableBuffer.empty[LoadedDocument]

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
      stringColumn[LoadedDocument](CommonDocumentFields.Id, Some(160))(_.getId),
      stringColumn[LoadedDocument](CommonDocumentFields.Sender, Some(160))(_.getSender),
      stringColumn[LoadedDocument](CommonDocumentFields.Subject, Some(220))(_.getSubject),
      stringColumn[LoadedDocument](Fields.DocumentType, Some(110))(_.getDocumentType),
      stringColumn[LoadedDocument](Fields.DocumentDate, Some(110))(_.getDocumentDate),
      stringColumn[LoadedDocument](Fields.ProcessedBy, Some(140))(_.getProcessedBy)
    )

    def loadDocuments(): Unit =
      result.clear()

      val loadedDocuments =
        service
          .getLoadedDocuments
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      updateComboFilter(operatorFilter, Text.AllOperators, loadedDocuments)(_.getProcessedBy)

      documents.setAll(loadedDocuments*)
      table.selectionModel.value.clearSelection()

      if loadedDocuments.isEmpty then
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
        if criteria.isEmpty then
          service.getLoadedDocuments
        else
          service.getLoadedDocuments(getLoadedDocumentPredicate(criteria))

      showFilteredItems(documents, table, filteredDocuments, result)(_.getId)

    def resetFilters(): Unit =
      fromDateFilter.value = null
      toDateFilter.value = null
      subjectFilter.clear()
      operatorFilter.value = Text.AllOperators
      loadDocuments()

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
          if service.deleteLoadedDocument(selected.getId) then
            loadDocuments()
            result.show(Text.Deleted, success = true)
          else
            result.show(Text.DeleteError, success = false)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("loaded.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          recordIds = documents.map(_.getId).toSeq
        )

      result.show(
        if printed then Text.PrintSuccess
        else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadDocuments)
    val printListButton = printButton(printDocumentsList)
    val resetFilterButton = secondaryButton(Buttons.ResetFilter, resetFilters)

    val registerButton = primaryButton(Buttons.Register, () => withSelectedItem(table, result, Text.SelectToRegister)(onRegister))
    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedDocument)

    disableWithoutSelection(table, registerButton, deleteButton)

    val exitButton = closeButton(onExit)

    val bottomActions = actionBar(Seq(resetFilterButton, exitButton, refreshButton, printListButton, deleteButton, registerButton))

    val header = titleBox(Text.Title, Text.Subtitle)

    val filters = filterBar(fromDateFilter, toDateFilter, subjectFilter, operatorFilter)

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
