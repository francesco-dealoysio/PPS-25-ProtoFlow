package pkg.a.gui.views

import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.traits.Management
import pkg.b.logic.RegisteredDocument
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.Filters.getRegisteredDocumentPredicate
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.RegisteredDocuments.{Fields, Management as Text}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object RegisteredDocumentManagementView extends Management:

  def apply(onArchive: RegisteredDocument => Unit = _ => (), onView: RegisteredDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =
    
    val documents = ObservableBuffer.empty[RegisteredDocument]

    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    val fromDateFilter = dateFilter()
    val toDateFilter = dateFilter()
    val subjectFilter = textFilter(CommonDocumentFields.Subject)
    val operatorFilter = comboFilter(Text.AllOperators)

    table.columns ++= Seq(
      stringColumn[RegisteredDocument](CommonDocumentFields.Id, Some(140))(_.getId),
      stringColumn[RegisteredDocument](CommonDocumentFields.ProtocolNumber, Some(140))(_.getProtocolNumber),
      stringColumn[RegisteredDocument](CommonDocumentFields.Sender, Some(150))(_.getSender),
      stringColumn[RegisteredDocument](CommonDocumentFields.Subject, Some(220))(_.getSubject),
      stringColumn[RegisteredDocument](Fields.Type, Some(90))(_.getDocumentType),
      stringColumn[RegisteredDocument](Fields.RegisteredBy, Some(160))(_.getRegisteredBy)
    )

    def loadDocuments(): Unit =
      result.clear()

      val loadedDocuments =
        LoadedDocumentService
          .getRegisteredDocuments
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

      updateComboFilter(operatorFilter, Text.AllOperators, loadedDocuments)(_.getRegisteredBy)

      documents.setAll(loadedDocuments*)
      table.selectionModel.value.clearSelection()

      if loadedDocuments.isEmpty then
        result.show(Text.Empty, success = true)

    clearResultOnSelection(table, result)

    def buildFilterCriteria(): List[FilterCriterion] =
      List(
        dateCriterion(fromDateFilter, "getRegisteredDate", ">="),
        dateCriterion(toDateFilter, "getRegisteredDate", "<="),
        textCriterion(subjectFilter, "getSubject", "contains"),
        comboCriterion(operatorFilter, Text.AllOperators, "getRegisteredBy")
      ).flatten

    def searchDocuments(): Unit =
      result.clear()

      val criteria = buildFilterCriteria()

      val filteredDocuments =
        if criteria.isEmpty then
          LoadedDocumentService.getRegisteredDocuments
        else
          LoadedDocumentService.getRegisteredDocuments(getRegisteredDocumentPredicate(criteria))

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
              s"""Numero protocollo: ${selected.getProtocolNumber}
                 |Mittente: ${selected.getSender}
                 |Oggetto: ${selected.getSubject}""".stripMargin
          )
        if confirmed then
          if LoadedDocumentService.deleteRegisteredDocument(selected.getId) then
            loadDocuments()
            result.show(Text.Deleted, success = true)
          else
            result.show(Text.DeleteError, success = false)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("registered.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          fields = Seq("protocolNumber", "registeredDate", "registeredTime", "registeredBy", "documentType", "sender", "recipient", "subject"),
          recordIds = documents.map(_.getId).toSeq
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadDocuments)
    val printListButton = printButton(printDocumentsList)
    val resetFilterButton = secondaryButton(Buttons.ResetFilter, () => resetFilters())
    val archiveButton = primaryButton(Buttons.Archive, () => withSelectedItem(table, result, Text.SelectToArchive)(onArchive))
    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedDocument)

    val viewButton = primaryButton(Text.View, () => withSelectedItem(table, result, Text.SelectToView)(onView))

    disableWithoutSelection(table, archiveButton, viewButton, deleteButton)

    val bottomActions = actionBar(Seq(resetFilterButton, closeButton(onExit), refreshButton, printListButton, deleteButton, viewButton, archiveButton))
    val header = titleBox(Text.Title, Text.Subtitle)

    val filters = filterBar(fromDateFilter, toDateFilter, subjectFilter, operatorFilter)

    loadDocuments()

    bindSearch(
      dateFilters = Seq(fromDateFilter, toDateFilter),
      textFilters = Seq(subjectFilter),
      comboFilters = Seq(operatorFilter)
    )(searchDocuments)

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
