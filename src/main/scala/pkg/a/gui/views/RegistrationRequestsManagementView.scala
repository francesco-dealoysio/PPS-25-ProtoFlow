package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{Registration, RegistrationDates, RegistrationRequestService}
import pkg.d.util.XmlToPdf
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Common, Fields, RegistrationRequests}

import java.time.format.DateTimeFormatter

object RegistrationRequestsManagementView extends Management:

  private val dateFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

  def apply(
             onProcess: Registration => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()
    val requests = ObservableBuffer.empty[Registration]

    val result =
      createResultMessage(
        baseStyle = UiStyles.Requests.Message,
        successStyle = UiStyles.Requests.MessageSuccess,
        errorStyle = UiStyles.Requests.MessageError
      )

    val table = new TableView[Registration](requests):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(RegistrationRequests.Management.Empty)
      styleClass += UiStyles.Requests.Table

    def stringColumn(title: String, colWidth: Double)(value: Registration => String): TableColumn[Registration, String] =
      new TableColumn[Registration, String]:
        text = title
        prefWidth = colWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    table.columns ++= Seq(
      stringColumn(Fields.Labels.Name, 110)(_.getName),
      stringColumn(Fields.Labels.Surname, 120)(_.getSurname),
      stringColumn(Fields.Labels.Email, 210)(_.getEmail),
      stringColumn(Fields.Labels.Role, 150)(_.getRole),
      stringColumn(Fields.Labels.Area, 140)(_.getArea),
      stringColumn(Fields.Labels.Assignment, 140)(_.getAssignment),
      stringColumn(Fields.Labels.Date, 150)(request => RegistrationDates.parse(request.getDate).format(dateFormatter))
    )

    def loadPendingRequests(): Unit =
      result.clear()

      val pending = service.getPendingRequests
      requests.setAll(pending.sortBy(_.getDate)*)
      table.selectionModel.value.clearSelection()

      if pending.isEmpty then
        result.show(RegistrationRequests.Management.Empty, success = true)

    clearResultOnSelection(table, result)

    def printPendingList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = service.pendingRequestsFilePath,
          pdfFileName = RegistrationRequests.Management.PrintFileName,
          title = RegistrationRequests.Management.PrintTitle
        )

      result.show(
        if printed then
          RegistrationRequests.Management.PrintSuccess
        else
          RegistrationRequests.Management.PrintError,
        success = printed
      )

    val refreshButton = secondaryButton(Common.Buttons.Refresh, () => loadPendingRequests())
    val printButton = secondaryButton(Common.Buttons.PrintList, () => printPendingList())

    val processButton =
      primaryButton(Common.Buttons.Process, () =>
        selectedItem(table) match
          case Some(selected) =>
            result.clear()
            onProcess(selected)

          case None =>
            result.show(RegistrationRequests.Management.SelectToProcess, success = false)
      )

    disableWithoutSelection(table, processButton)
    val exitButton = closeButton(onExit)
    val bottomActions = actionBar(Seq(exitButton, refreshButton, printButton, processButton))

    val header =
      titleBox(
        titleText = RegistrationRequests.Process.Title,
        subtitleText = RegistrationRequests.Process.Subtitle,
        titleStyle = UiStyles.Requests.Title,
        subtitleStyle = UiStyles.Requests.Subtitle
      )

    loadPendingRequests()

    managementPage(
      rootStyle = UiStyles.Requests.Root,
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
