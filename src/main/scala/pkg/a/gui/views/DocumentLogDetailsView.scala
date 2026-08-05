package pkg.a.gui.views

import pkg.a.gui.text.UiText.DocumentLogs
import pkg.a.gui.traits.Form
import pkg.b.logic.DocumentLog
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane

object DocumentLogDetailsView extends Form:

  def apply(selectedLog: DocumentLog, onExit: () => Unit = () => ()): BorderPane =

    val id = stringField("", selectedLog.getId)
    val documentId = stringField("", selectedLog.getDocumentId)
    val operationType = stringField("", DocumentLogs.operationLabel(selectedLog.getOperationType))
    val processedDate = stringField("", selectedLog.getProcessedDate)
    val processedTime = stringField("", selectedLog.getProcessedTime)
    val processedBy = stringField("", selectedLog.getProcessedBy)

    val fields: Seq[FormField[? <: Node]] =
      Seq(id, documentId, operationType, processedDate, processedTime, processedBy)

    fields.foreach(_.control.setDisable(true))

    val form =
      formGrid(
        Seq(
          formRow(DocumentLogs.Fields.Id, id),
          formRow(DocumentLogs.Fields.DocumentId, documentId),
          formRow(DocumentLogs.Fields.OperationType, operationType),
          formRow(DocumentLogs.Fields.ProcessedDate, processedDate),
          formRow(DocumentLogs.Fields.ProcessedTime, processedTime),
          formRow(DocumentLogs.Fields.ProcessedBy, processedBy)
        )
      )

    formPage(
      titleText = DocumentLogs.Details.Title,
      subtitleText = DocumentLogs.Details.Subtitle,
      form = form,
      resultMessage = createResultMessage().label,
      actions = actionBar(
        Seq(closeButton(onExit))
      )
    )
