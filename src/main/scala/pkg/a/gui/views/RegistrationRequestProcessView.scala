package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{Registration, RegistrationDates, RegistrationRequestService}
import pkg.d.util.XmlToPdf
import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.layout.*
import java.time.format.DateTimeFormatter
import pkg.a.gui.text.UiText
import pkg.a.gui.text.UiStyles.Requests.*
import pkg.a.gui.text.UiStyles.Common.FormFieldStyle
import UiText.{Fields, RegistrationRequests, Common}

object RegistrationRequestProcessView extends Management:

  private val dateFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

  def apply(
             request: Registration,
             operatorUsername: String,
             onProcessed: () => Unit = () => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()

    val result = createResultMessage()

    val detailsGrid = new GridPane:
      hgap = 18
      vgap = 12
      padding = Insets(18)
      styleClass += DetailsGridStyle

    val detailRows = Seq(
      Fields.Labels.Name -> request.getName,
      Fields.Labels.Surname-> request.getSurname,
      Fields.Labels.Email -> request.getEmail,
      Fields.Labels.Phone -> request.getPhone,
      Fields.Labels.Role -> request.getRole,
      Fields.Labels.Area -> request.getArea,
      Fields.Labels.Assignment -> request.getAssignment,
      Fields.Labels.Date -> RegistrationDates.parse(request.getDate).format(dateFormatter)
    )

    detailRows.zipWithIndex.foreach:
      case ((label, value), index) =>
        val row = index / 2
        val labelColumn = (index % 2) * 2

        detailsGrid.add(detailLabel(label), labelColumn, row)
        detailsGrid.add(detailValue(value), labelColumn + 1, row)

    val detailsCard = new VBox:
      spacing = 10
      styleClass += DetailsCardStyle
      children = Seq(
        new Label(RegistrationRequests.Process.DetailsTitle):
          styleClass += DetailsTitleStyle,
          detailsGrid
      )

    val motivationField =
      new TextArea:
        promptText = RegistrationRequests.Process.MotivationPrompt
        wrapText = true
        prefRowCount = 3
        maxWidth = Double.MaxValue
        styleClass += FormFieldStyle

    val motivationBox = new VBox:
      spacing = 8
      styleClass += DetailsCardStyle
      children = Seq(
        fieldLabel(RegistrationRequests.Process.MotivationLabel),
        motivationField
      )

    def printPendingRequest(): Unit =
      val printed =
        XmlToPdf.printDetails(
          xmlPath = service.pendingRequestsFilePath,
          recordId = request.getId,
          pdfFileName = s"richiesta_${request.getId}",
          title = RegistrationRequests.Process.PrintPendingTitle
        )

      result.show(
        if printed then RegistrationRequests.Process.PrintSuccess
        else RegistrationRequests.Process.PrintError,
        success = printed
      )

    def approve(): Unit =
      val confirmed =
        askConfirmation(
          titleText = RegistrationRequests.Process.ApproveTitle,
          header = RegistrationRequests.Process.ApproveHeader,
          content =
            s"""${request.getName} ${request.getSurname}
               |${request.getEmail}
               |Ruolo: ${request.getRole}""".stripMargin
        )

      if confirmed then
        service.approveRequest(request.getId, operatorUsername) match
          case Right(approval) =>
            XmlToPdf.printDetails(
              xmlPath = service.acceptedRequestsFilePath,
              recordId = approval.request.getId,
              pdfFileName = s"richiesta_${approval.request.getId}_approvata",
              title = RegistrationRequests.Process.PrintApprovedTitle
            )

            result.show(
              RegistrationRequests.Process.approved(approval.account.getUsername, approval.generatedPassword),
              success = true
            )

            onProcessed()

          case Left(error) =>
            result.show(error, success = false)

    def reject(): Unit =
      val motivation = motivationField.text.value.trim

      if motivation.isEmpty then
        result.show(RegistrationRequests.Process.EmptyMotivationError, success = false)
      else
        val confirmed =
          askConfirmation(
            titleText = RegistrationRequests.Process.RejectTitle,
            header = RegistrationRequests.Process.RejectHeader,
            content =
              s"""${request.getName} ${request.getSurname}
                 |${request.getEmail}
                 |Motivazione: $motivation""".stripMargin
          )

        if confirmed then
          service.rejectRequest(request.getId, operatorUsername, motivation) match
            case Right(rejected) =>
              XmlToPdf.printDetails(
                xmlPath = service.rejectedRequestsFilePath,
                recordId = rejected.getId,
                pdfFileName = s"richiesta_${rejected.getId}_rifiutata",
                title = RegistrationRequests.Process.PrintRejectedTitle
              )

              result.show(RegistrationRequests.Process.RejectSuccess, success = true)
              onProcessed()

            case Left(error) =>
              result.show(error, success = false)

    val printButton = secondaryButton(Common.Buttons.Print, () => printPendingRequest())
    val rejectButton = dangerButton(Common.Buttons.Reject, () => reject())
    val approveButton = primaryButton(Common.Buttons.Approve, () => approve())
    val exitButton = closeButton(onExit)

    val actionsBox = actionBar(Seq(exitButton, printButton, rejectButton, approveButton))

    val header = titleBox(RegistrationRequests.Management.Title, RegistrationRequests.Management.Subtitle)

    managementPage(
      pageChildren = Seq(
        header,
        detailsCard,
        motivationBox,
        result.label,
        actionsBox
      )
    )

  private def detailLabel(text: String): Label =
    new Label(text):
      styleClass += DetailLabelStyle

  private def detailValue(text: String): Label =
    new Label(if text.trim.isEmpty then "-" else text):
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += DetailValueStyle
