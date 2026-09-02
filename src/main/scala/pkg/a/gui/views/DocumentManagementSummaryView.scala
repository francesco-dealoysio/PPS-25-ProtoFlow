package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService.{DocumentManagementSummary, ManagementPhase}
import pkg.a.gui.text.UiText.Common.ApplicationName
import pkg.a.gui.text.UiText.DocumentLogs.Operations
import pkg.a.gui.text.UiText.DocumentManagementControl as Text
import pkg.a.gui.traits.{Form, Management}
import pkg.d.util.{DateTime, XmlToPdf}
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.BorderPane

object DocumentManagementSummaryView extends Form with Management:

  def apply(summary: DocumentManagementSummary, generatedBy: String, onExit: () => Unit = () => ()): BorderPane =
    val result = createResultMessage()
    val documentCode = readOnlyStringField(summary.documentCode)
    val classification = readOnlyStringField(summary.classification)

    val documentData =
      formGrid(
        Seq(
          formRow(Text.DocumentCodeField, documentCode),
          formRow(Text.SummaryClassificationField, classification)
        )
      )
    val phases = ObservableBuffer(summary.phases*)
    val phasesTable = managementTable(phases, Text.SummaryEmpty)

    phasesTable.columns ++= Seq(
      stringColumn[ManagementPhase](Text.PhaseColumn, Some(180)): phase =>
        Operations.labelOf(phase.operationType),
      stringColumn[ManagementPhase](Text.DateTimeColumn, Some(180)): phase =>
        Seq(phase.date, phase.time).filter(_.nonEmpty).mkString(" "),
      stringColumn[ManagementPhase](Text.SummaryOperatorColumn, Some(160))(_.operator),
      stringColumn[ManagementPhase](Text.OutcomeColumn, Some(220))(_.outcome)
    )

    def printSummary(): Unit =
      val safeDocumentCode = summary.documentCode.replaceAll("[^A-Za-z0-9._-]", "_")
      val phaseRows =
        summary.phases.map: phase =>
          Seq(
            Operations.labelOf(phase.operationType),
            Seq(phase.date, phase.time)
              .filter(_.nonEmpty)
              .mkString(" "),
            phase.operator,
            phase.outcome
          )

      val printData =
        XmlToPdf.SummaryPrintData(
          applicationTitle = ApplicationName,
          reportTitle = Text.SummaryTitle,
          generatedAtLabel = Text.SummaryGeneratedAt,
          generatedAt = DateTime.currentDateTime,
          documentDataSectionTitle = Text.DocumentDataSection,
          documentCodeLabel = Text.DocumentCodeField,
          documentCode = summary.documentCode,
          classificationLabel = Text.SummaryClassificationField,
          classification = summary.classification,
          phasesSectionTitle = Text.PhasesSection,
          phaseHeaders = Seq(Text.PhaseColumn, Text.DateTimeColumn, Text.SummaryOperatorColumn, Text.OutcomeColumn),
          phaseRows = phaseRows,
          generatedByLabel = Text.SummaryGeneratedBy,
          generatedBy = generatedBy,
          pageLabel = Text.SummaryPage,
          logoResourcePath = "/img/message.jpg"
        )

      val printed =
        XmlToPdf.printDocumentManagementSummary(
          pdfFileName = s"${Text.SummaryPrintFileName}_$safeDocumentCode.pdf",
          data = printData
        )

      result.show(
        if printed then
          Text.SummaryPrintSuccess
        else
          Text.SummaryPrintError,
        success = printed
      )

    managementPage(
      growNode = Some(phasesTable),
      pageChildren = Seq(
        titleBox(Text.SummaryTitle, Text.SummarySubtitle),
        fieldLabel(Text.DocumentDataSection),
        documentData,
        fieldLabel(Text.PhasesSection),
        phasesTable,
        result.label,
        actionBar(Seq(closeButton(onExit), printButton(printSummary)))
      )
    )