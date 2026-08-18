package pkg.a.gui.views

import pkg.a.gui.services.LoadedDocumentService
import pkg.a.gui.traits.Management
import pkg.b.logic.RegisteredDocument
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.{XmlToPdf, getRegisteredDocumentPredicate}
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.RegisteredDocuments.{Fields, Management as Text}
import pkg.a.gui.text.UiText.Common.Documents.Fields as CommonDocumentFields
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.{BorderPane, HBox}

object RegisteredDocumentManagementView extends Management:

  def apply(
             onArchive: RegisteredDocument => Unit = _ => (),
             onView: RegisteredDocument => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new LoadedDocumentService()
    val documents = ObservableBuffer.empty[RegisteredDocument]

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
      stringColumn[RegisteredDocument](CommonDocumentFields.Id, Some(140))(_.getId),
      stringColumn[RegisteredDocument](CommonDocumentFields.ProtocolNumber, Some(140))(_.getProtocolNumber),
      stringColumn[RegisteredDocument](CommonDocumentFields.Sender, Some(150))(_.getSender),
      stringColumn[RegisteredDocument](CommonDocumentFields.Subject, Some(220))(_.getSubject),
      stringColumn[RegisteredDocument](Fields.Type, Some(90))(_.getDocumentType),
      stringColumn[RegisteredDocument](Fields.ProtocolledBy, Some(130))(_.getRegisteredBy)
    )

    def updateOperatorFilter(loadedDocuments: Seq[RegisteredDocument]): Unit =
      val operators =
        loadedDocuments
          .map(_.getRegisteredBy.trim)
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
          .getRegisteredDocuments
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
          .map(date => ("getRegisteredDate", ">=", List(date.toString)))
          .toList

      val toDateCriteria =
        Option(toDateFilter.value.value)
          .map(date => ("getRegisteredDate", "<=", List(date.toString)))
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
          .map(value => ("getRegisteredBy", "=", List(value)))
          .toList

      fromDateCriteria ++ toDateCriteria ++ subjectCriteria ++ operatorCriteria

    def searchDocuments(): Unit =
      result.clear()

      val criteria = buildFilterCriteria()

      val filteredDocuments =
        if criteria.isEmpty then
          service.getRegisteredDocuments
        else
          service.getRegisteredDocuments(getRegisteredDocumentPredicate(criteria))

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
          if service.deleteRegisteredDocument(selected.getId) then
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

    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(resetFilterButton, exitButton, refreshButton, printListButton, deleteButton, viewButton, archiveButton))
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
