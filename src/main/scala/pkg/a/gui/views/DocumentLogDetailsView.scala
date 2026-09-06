package pkg.a.gui.views

import pkg.a.gui.text.UiText.DocumentLogs.{Fields, Operations, Details as Text}
import pkg.a.gui.traits.Form
import pkg.b.logic.DocumentLog
import pkg.b.logic.pdf.{PdfDetailsCreator, PdfViewer}
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.scene.layout.BorderPane

object DocumentLogDetailsView extends Form:

  def apply(selectedLog: DocumentLog, onExit: () => Unit = () => ()): BorderPane =

    val id = readOnlyStringField(selectedLog.getId)
    val documentId = readOnlyStringField(selectedLog.getDocumentId)
    val operationType = readOnlyStringField(Operations.labelOf(selectedLog.getOperationType))
    val processedDate = readOnlyStringField(selectedLog.getProcessedDate)
    val processedTime = readOnlyStringField(selectedLog.getProcessedTime)
    val processedBy = readOnlyStringField(selectedLog.getProcessedBy)

    val form =
      formGrid(
        Seq(
          formRow(Fields.Id, id),
          formRow(Fields.DocumentId, documentId),
          formRow(Fields.OperationType, operationType),
          formRow(Fields.ProcessedDate, processedDate),
          formRow(Fields.ProcessedTime, processedTime),
          formRow(Fields.ProcessedBy, processedBy)
        )
      )

    val result = createResultMessage()

    def printLogDetails(): Unit =
      val pdfPath = inPrintsFilePathName(s"log_documento_${selectedLog.getId}.pdf")
      val fields =
        Seq(
          Fields.Id -> selectedLog.getId,
          Fields.DocumentId -> selectedLog.getDocumentId,
          Fields.OperationType -> Operations.labelOf(selectedLog.getOperationType),
          Fields.ProcessedDate -> selectedLog.getProcessedDate,
          Fields.ProcessedTime -> selectedLog.getProcessedTime,
          Fields.ProcessedBy -> selectedLog.getProcessedBy
        )
      val printed = PdfDetailsCreator.createDetailsPdf(pdfPath, Text.PrintTitle, fields)
      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess
        else Text.PrintError,
        success = printed
      )

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(closeButton(onExit), printButton(printLogDetails)))
    )