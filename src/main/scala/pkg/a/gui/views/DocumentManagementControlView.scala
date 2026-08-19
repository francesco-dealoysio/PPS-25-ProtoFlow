package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.ManagedDocument
import pkg.a.gui.traits.Management
import pkg.a.gui.text.UiText.DocumentManagementControl as Text
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.d.util.XmlToPdf
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.BorderPane

object DocumentManagementControlView extends Management:

  def apply(onViewDetails: ManagedDocument => Unit = _ => (), onSummary: ManagedDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val documents = ObservableBuffer.empty[ManagedDocument]
    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    table.columns ++= Seq(
      stringColumn[ManagedDocument](Text.IdColumn, Some(160)): document =>
        if document.protocolNumber.nonEmpty then document.protocolNumber else document.id,
      stringColumn[ManagedDocument](Text.ClassificationColumn, Some(120)): _ =>
        Text.NotAvailable,
      stringColumn[ManagedDocument](Text.RegisteredDateColumn, Some(150)): document =>
        if document.registeredDate.nonEmpty then document.registeredDate else Text.NotAvailable,
      stringColumn[ManagedDocument](Text.OperatorColumn, Some(180))(_.operator),
      stringColumn[ManagedDocument](Text.StageColumn, Some(150)): document =>
        Text.Stages.labelOf(document.stage)
    )

    def loadDocuments(): Unit =
      loadTableItemsSafely(table, documents, result, Text.Empty, Text.LoadError):
        DocumentManagementControlService.getManagedDocuments()

    clearResultOnSelection(table, result)

    def printDocumentsList(): Unit =
      val rows =
        documents.map: document =>
          Seq(
            if document.protocolNumber.nonEmpty then document.protocolNumber else document.id,
            Text.NotAvailable,
            if document.registeredDate.nonEmpty then document.registeredDate else Text.NotAvailable,
            document.operator,
            Text.Stages.labelOf(document.stage)
          )
        .toSeq

      val printed =
        XmlToPdf.printSections(
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          sections = Seq(
            (
              Text.PrintTitle,
              Seq(Text.IdColumn, Text.ClassificationColumn, Text.RegisteredDateColumn, Text.OperatorColumn, Text.StageColumn),
              rows
            )
          )
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadDocuments)
    val printListButton = printButton(printDocumentsList)
    val viewButton = primaryButton(Text.ViewDetails, () => withSelectedItem(table, result, Text.SelectToView)(onViewDetails))
    val summaryButton = primaryButton(Text.Summary, () => withSelectedItem(table, result, Text.SelectToSummary)(onSummary))
    disableWithoutSelection(table, viewButton, summaryButton)

    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, printListButton, viewButton, summaryButton))

    val header = titleBox(Text.Title, Text.Subtitle)

    loadDocuments()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
