package pkg.a.gui.views

import pkg.a.gui.services.RegistrationRequestService
import pkg.a.gui.traits.Management
import pkg.b.logic.Registration
import pkg.d.util.{DateTime, XmlToPdf}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.RegistrationRequests.Management as Text

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

    def printPendingList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = service.requestsFilePath,
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          recordIds = requests.map(_.getId).toSeq
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Buttons.Refresh, loadPendingRequests)
    val printButton = secondaryButton(Buttons.PrintList, printPendingList)
    val processButton = primaryButton(Buttons.Process, () => withSelectedItem(table, result, Text.SelectToProcess)(onProcess))

    disableWithoutSelection(table, processButton)
    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, printButton, processButton))

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
