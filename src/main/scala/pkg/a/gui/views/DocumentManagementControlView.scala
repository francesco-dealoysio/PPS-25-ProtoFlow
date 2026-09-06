package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.ManagedDocument
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentManagementControl as Text
import pkg.a.gui.traits.Management
import pkg.b.logic.pdf.{PdfTableCreator, PdfViewer}
import pkg.d.util.Util.inPrintsFilePathName
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
      stringColumn[ManagedDocument](Text.ClassificationColumn, Some(120)): document =>
        if document.classification.nonEmpty then document.classification else Text.NotAvailable,
      stringColumn[ManagedDocument](Text.RegisteredDateColumn, Some(150)): document =>
        if document.registeredDate.nonEmpty then document.registeredDate else Text.NotAvailable,
      stringColumn[ManagedDocument](Text.OperatorColumn, Some(180))(_.operator),
      stringColumn[ManagedDocument](Text.StageColumn, Some(150)): document =>
        Text.Stages.labelOf(document.stage)
    )

    def loadDocuments(): Unit =
      loadTableItemsSafely(documents, result, Text.Empty, Text.LoadError):
        DocumentManagementControlService.getManagedDocuments()

    clearResultOnSelection(table, result)

    def documentRow(document: ManagedDocument): Seq[String] =
      Seq(
        if document.protocolNumber.nonEmpty then document.protocolNumber else document.id,
        if document.classification.nonEmpty then document.classification else Text.NotAvailable,
        if document.registeredDate.nonEmpty then document.registeredDate else Text.NotAvailable,
        document.operator,
        Text.Stages.labelOf(document.stage)
      )

    def printDocumentsList(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileName}.pdf")
      val printed =
        PdfTableCreator.createTablePdf(
          pdfPathName = pdfPath,
          title = Text.PrintTitle,
          headers = Seq(
            Text.IdColumn,
            Text.ClassificationColumn,
            Text.RegisteredDateColumn,
            Text.OperatorColumn,
            Text.StageColumn
          ),
          rows = documents.toSeq.map(documentRow),
          columnWeights = Seq(1.3f, 1.2f, 1.4f, 1.7f, 1.4f)
        )

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadDocuments)
    val viewButton = primaryButton(Text.ViewDetails, () => withSelectedItem(table, result, Text.SelectToView)(onViewDetails))
    val summaryButton = primaryButton(Text.Summary, () => withSelectedItem(table, result, Text.SelectToSummary)(onSummary))
    disableWithoutSelection(table, viewButton, summaryButton)
    val bottomActions = actionBar(Seq(closeButton(onExit), refreshButton, printButton(printDocumentsList), viewButton, summaryButton))
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
