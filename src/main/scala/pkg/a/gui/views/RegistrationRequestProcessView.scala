package pkg.a.gui.views

import pkg.a.gui.services.RegistrationRequestService
import pkg.a.gui.text.UiStyles.Common.FormFieldStyle
import pkg.a.gui.text.UiStyles.Requests.*
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.RegistrationRequests.Process as Text
import pkg.a.gui.traits.Management
import pkg.b.logic.Registration
import pkg.b.logic.pdf.{PdfDetailsCreator, PdfViewer}
import pkg.d.util.DateTime
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.layout.*

object RegistrationRequestProcessView extends Management:

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
      Labels.Id -> request.getId,
      Labels.Name -> request.getName,
      Labels.Surname-> request.getSurname,
      Labels.Email -> request.getEmail,
      Labels.Phone -> request.getPhone,
      Labels.Role -> request.getRole,
      Labels.Area -> request.getArea,
      Labels.Assignment -> request.getAssignment,
      Labels.Date -> DateTime.displayDateTime(request.getDate)
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
        new Label(Text.DetailsTitle):
          styleClass += DetailsTitleStyle,
          detailsGrid
      )

    val motivationField =
      new TextArea:
        promptText = Text.MotivationPrompt
        wrapText = true
        prefRowCount = 3
        maxWidth = Double.MaxValue
        styleClass += FormFieldStyle

    val motivationBox = new VBox:
      spacing = 8
      styleClass += DetailsCardStyle
      children = Seq(
        fieldLabel(Text.MotivationLabel),
        motivationField
      )

    def requestFields(currentRequest: Registration): Seq[(String, String)] =
      Seq(
        Labels.Id -> currentRequest.getId,
        Labels.Surname -> currentRequest.getSurname,
        Labels.Name -> currentRequest.getName,
        Labels.Email -> currentRequest.getEmail,
        Labels.Phone -> currentRequest.getPhone,
        Labels.Role -> currentRequest.getRole,
        Labels.Area -> currentRequest.getArea,
        Labels.Assignment -> currentRequest.getAssignment,
        Labels.Date -> DateTime.displayDateTime(currentRequest.getDate)
      )

    def printPendingRequest(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileNamePrefix}_${request.getId}.pdf")
      val printed = PdfDetailsCreator.createDetailsPdf(pdfPath, Text.PrintPendingTitle, requestFields(request))

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    def approve(): Unit =
      val confirmed =
        askConfirmation(
          titleText = Text.ApproveTitle,
          header = Text.ApproveHeader,
          content =
            s"""${request.getName} ${request.getSurname}
               |${request.getEmail}
               |Ruolo: ${request.getRole}""".stripMargin
        )

      if confirmed then
        service.approveRequest(request.getId, operatorUsername) match
          case Right(approval) =>
            val pdfPath = inPrintsFilePathName(s"${Text.PrintFileNamePrefix}_${approval.request.getId}_approvata.pdf")
            val fields =
              requestFields(approval.request) ++
                Seq(
                  Labels.Username -> approval.request.getAssignedUsername,
                  Text.TemporaryPasswordLabel -> approval.generatedPassword
                )

            val printed = PdfDetailsCreator.createDetailsPdf(pdfPath, Text.PrintApprovedTitle, fields)

            if printed then
              PdfViewer.viewPdf(pdfPath)

            showSuccess(
              Text.ApproveTitle,
              Text.approved(approval.account.getUsername, approval.generatedPassword)
            )

            onProcessed()

          case Left(error) =>
            result.show(error, success = false)

    def reject(): Unit =
      val motivation = motivationField.text.value.trim

      if motivation.isEmpty then
        result.show(Text.EmptyMotivationError, success = false)
      else
        val confirmed =
          askConfirmation(
            titleText = Text.RejectTitle,
            header = Text.RejectHeader,
            content =
              s"""${request.getName} ${request.getSurname}
                 |${request.getEmail}
                 |Motivazione: $motivation""".stripMargin
          )

        if confirmed then
          service.rejectRequest(request.getId, operatorUsername, motivation) match
            case Right(rejected) =>
              val pdfPath = inPrintsFilePathName(s"${Text.PrintFileNamePrefix}_${rejected.getId}_rifiutata.pdf")
              val fields = requestFields(rejected) ++ Seq(Text.MotivationLabel -> rejected.getMotivation)
              val printed = PdfDetailsCreator.createDetailsPdf(pdfPath, Text.PrintRejectedTitle, fields)

              if printed then
                PdfViewer.viewPdf(pdfPath)

              result.show(Text.RejectSuccess, success = true)
              onProcessed()

            case Left(error) =>
              result.show(error, success = false)
    
    val rejectButton = dangerButton(Buttons.Reject, reject)
    val approveButton = primaryButton(Buttons.Approve, approve)

    val actionsBox = actionBar(Seq(closeButton(onExit), printButton(printPendingRequest), rejectButton, approveButton))

    val header = titleBox(Text.Title, Text.Subtitle)

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