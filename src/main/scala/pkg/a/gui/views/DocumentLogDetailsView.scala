package pkg.a.gui.views

import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.DocumentLogs.{Details as Text, Fields, Operations}
import pkg.a.gui.traits.Form
import pkg.b.logic.DocumentLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.XmlToPdf
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
      val printed =
        XmlToPdf.printDetails(
          xmlPath = inLogFilePathName("documentOperations.xml"),
          recordId = selectedLog.getId,
          pdfFileName = s"log_documento_${selectedLog.getId}",
          title = Text.PrintTitle
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val exitButton = closeButton(onExit)
    val printButton = secondaryButton(Buttons.Print, printLogDetails)

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exitButton, printButton))
    )