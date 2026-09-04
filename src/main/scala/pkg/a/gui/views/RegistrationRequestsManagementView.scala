package pkg.a.gui.views

import pkg.a.gui.services.RegistrationRequestService
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.RegistrationRequests.Management as Text
import pkg.a.gui.traits.Management
import pkg.b.logic.Registration
import pkg.b.logic.pdf.{PdfTableCreator, PdfViewer}
import pkg.d.util.DateTime
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*

object RegistrationRequestsManagementView extends Management:

  def apply(
             onProcess: Registration => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()
    val requests = ObservableBuffer.empty[Registration]
    val result = createResultMessage()
    val table = managementTable(requests, Text.Empty)

    table.columns ++= Seq(
      stringColumn[Registration](Labels.Name, Some(110))(_.getName),
      stringColumn[Registration](Labels.Surname, Some(120))(_.getSurname),
      stringColumn[Registration](Labels.Email, Some(210))(_.getEmail),
      stringColumn[Registration](Labels.Role, Some(150))(_.getRole),
      stringColumn[Registration](Labels.Area, Some(140))(_.getArea),
      stringColumn[Registration](Labels.Assignment, Some(140))(_.getAssignment),
      stringColumn[Registration](Labels.Date, Some(150))(request => DateTime.displayDateTime(request.getDate))
    )

    def loadPendingRequests(): Unit =
      loadTableItemsSafely(requests, result, Text.Empty, Text.LoadError):
        service
          .getPendingRequests
          .sortBy(_.getDate)
    
    clearResultOnSelection(table, result)

    def requestRow(request: Registration): Seq[String] =
      Seq(
        request.getName,
        request.getSurname,
        request.getEmail,
        request.getRole,
        request.getArea,
        request.getAssignment,
        DateTime.displayDateTime(request.getDate)
      )

    def printPendingList(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileName}.pdf")
      val printed =
        PdfTableCreator.createTablePdf(
          pdfPathName = pdfPath,
          title = Text.PrintTitle,
          headers = Seq(
            Labels.Name,
            Labels.Surname,
            Labels.Email,
            Labels.Role,
            Labels.Area,
            Labels.Assignment,
            Labels.Date
          ),
          rows = requests.toSeq.map(requestRow),
          columnWeights = Seq(1.1f, 1.2f, 2f, 1.2f, 1.2f, 1.4f, 1.5f)
        )

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadPendingRequests)
    val processButton = primaryButton(Buttons.Process, () => withSelectedItem(table, result, Text.SelectToProcess)(onProcess))
    disableWithoutSelection(table, processButton)
    val bottomActions = actionBar(Seq(closeButton(onExit), refreshButton, printButton(printPendingList), processButton))
    val header = titleBox(Text.Title, Text.Subtitle)

    loadPendingRequests()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
