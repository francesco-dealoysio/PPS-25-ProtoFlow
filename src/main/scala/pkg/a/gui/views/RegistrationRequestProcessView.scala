package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{Registration, RegistrationApproval, RegistrationDates, RegistrationRequestService}
import pkg.d.util.XmlToPdf

import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.layout.*

import java.time.format.DateTimeFormatter
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Common, Fields, Roles}

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

    val result =
      createResultMessage(
        baseStyle = UiStyles.Requests.Message,
        successStyle = UiStyles.Requests.MessageSuccess,
        errorStyle = UiStyles.Requests.MessageError
      )

    val detailsGrid = new GridPane:
      hgap = 18
      vgap = 12
      padding = Insets(18)
      styleClass += "request-details-grid"

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
      styleClass += "request-details-card"
      children = Seq(
        new Label("Dettaglio richiesta"):
          styleClass += "request-details-title",
        detailsGrid
      )

    val motivationField =
      new TextArea:
        promptText = "Obbligatoria per rifiutare la richiesta"
        wrapText = true
        prefRowCount = 3
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val motivationBox = new VBox:
      spacing = 8
      styleClass += "request-details-card"
      children = Seq(
        fieldLabel("Motivazione rifiuto"),
        motivationField
      )

    def printPendingRequest(): Unit =
      val printed =
        XmlToPdf.printDetails(
          xmlPath = service.pendingRequestsFilePath,
          recordId = request.getId,
          pdfFileName = s"richiesta_${request.getId}",
          title = "Scheda Richiesta di Registrazione"
        )

      result.show(
        if printed then "Scheda stampata correttamente in PDF."
        else "Errore durante la stampa della scheda.",
        success = printed
      )

    def approve(): Unit =
      val confirmed =
        askConfirmation(
          titleText = "Conferma approvazione",
          header = "Approvare la richiesta selezionata?",
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
              title = "Esito Richiesta di Registrazione - Approvata"
            )

            result.show(
              s"Richiesta approvata. Account creato con username '${approval.account.getUsername}' " +
                s"e password temporanea '${approval.generatedPassword}': comunicali al richiedente.",
              success = true
            )

            onProcessed()

          case Left(error) =>
            result.show(error, success = false)

    def reject(): Unit =
      val motivation = motivationField.text.value.trim

      if motivation.isEmpty then
        result.show("Inserisci la motivazione del rifiuto.", success = false)
      else
        val confirmed =
          askConfirmation(
            titleText = "Conferma rifiuto",
            header = "Rifiutare la richiesta selezionata?",
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
                title = "Esito Richiesta di Registrazione - Rifiutata"
              )

              result.show("Richiesta rifiutata correttamente.", success = true)
              onProcessed()

            case Left(error) =>
              result.show(error, success = false)

    val printButton = secondaryButton("Stampa", () => printPendingRequest())
    val rejectButton = dangerButton("Rifiuta", () => reject())
    val approveButton = primaryButton("Approva", () => approve())
    val exitButton = closeButton(onExit)

    val actionsBox = actionBar(Seq(exitButton, printButton, rejectButton, approveButton))

    val header =
      titleBox(
        titleText = "Elaborazione richiesta registrazione",
        subtitleText = "Visualizza i dati della richiesta e approvala o rifiutala.",
        titleStyle = "requests-title",
        subtitleStyle = "requests-subtitle"
      )

    managementPage(
      rootStyle = "requests-management-root",
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
      styleClass += "request-detail-label"

  private def detailValue(text: String): Label =
    new Label(if text.trim.isEmpty then "-" else text):
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += "request-detail-value"
