package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{ArchivedDocument, ArchivedDocumentService}
import pkg.d.util.Util.inDocumentsFilePathName
import pkg.d.util.XmlToPdf
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.ArchivedDocuments.{Fields, Management as Text}
import pkg.a.gui.text.UiText.Common.Buttons

object ArchivedDocumentManagementView extends Management:

  def apply(onView: ArchivedDocument => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val service = new ArchivedDocumentService()
    val documents = ObservableBuffer.empty[ArchivedDocument]

    val result = createResultMessage()

    val table = managementTable(documents, Text.Empty)

    table.columns ++= Seq(
      stringColumn[ArchivedDocument](Fields.ProtocolNumber, Some(140))(_.getProtocolNumber),
      stringColumn[ArchivedDocument](Fields.ArchivedDate, Some(120))(_.getArchivedDate),
      stringColumn[ArchivedDocument](Fields.ArchivedTime, Some(100))(_.getArchivedTime),
      stringColumn[ArchivedDocument](Fields.ArchivedBy, Some(150))(_.getArchivedBy),
      stringColumn[ArchivedDocument](Fields.Subject, Some(220))(_.getSubject),
      stringColumn[ArchivedDocument](Fields.ArchiveLocation, Some(180))(_.getArchiveLocation)
    )

    def loadDocuments(): Unit =
      loadTableItemsSafely(table, documents, result, Text.Empty, Text.LoadError):
        service
          .getArchivedDocuments
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

    clearResultOnSelection(table, result)

    def printDocumentsList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDocumentsFilePathName("archived.xml"),
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          fields = Seq("protocolNumber", "archivedDate", "archivedTime", "archivedBy", "archiveLocation", "archiveRemarks")
        )

      result.show(
        message = if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, () => loadDocuments())

    val print = printButton(action = () => printDocumentsList())

    val viewButton = primaryButton(Text.View, () => withSelectedItem(table, result, Text.SelectToView)(onView))

    disableWithoutSelection(table, viewButton)
    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, print, viewButton))

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