package pkg.a.gui

import pkg.b.logic.RegistrationRequestService
import pkg.c.data.guiStructures.RegistrationRequest
import pkg.c.data.xmlManagement.RegistrationRequestRepository
import scalafx.Includes.jfxMultipleSelectionModel2sfx
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.layout.*

import java.time.format.DateTimeFormatter

object RegistrationRequestsManagementView extends ManagementView:

  private val dateFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

  def apply(
             onExit: () => Unit = () => ()
           ): BorderPane =

    val repository = new RegistrationRequestRepository()
    val service = new RegistrationRequestService(repository)

    val requests = ObservableBuffer.empty[RegistrationRequest]

    val resultMessage = messageLabel("requests-message")
    val successMessageStyle = "requests-message-success"
    val errorMessageStyle = "requests-message-error"

    def showResult(message: String, success: Boolean): Unit =
      showMessage(
        label = resultMessage,
        message = message,
        success = success,
        successStyle = successMessageStyle,
        errorStyle = errorMessageStyle
      )

    def clearResult(): Unit =
      clearMessage(
        resultMessage,
        successMessageStyle,
        errorMessageStyle
      )

    val table = new TableView[RegistrationRequest](requests):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(
        "Non sono presenti richieste di registrazione da elaborare."
      )
      styleClass += "requests-table"

    val nameColumn = new TableColumn[RegistrationRequest, String]:
      text = "Nome"
      prefWidth = 110
      cellValueFactory = cell =>
        StringProperty(cell.value.name)

    val surnameColumn = new TableColumn[RegistrationRequest, String]:
      text = "Cognome"
      prefWidth = 120
      cellValueFactory = cell =>
        StringProperty(cell.value.surname)

    val emailColumn = new TableColumn[RegistrationRequest, String]:
      text = "Email"
      prefWidth = 210
      cellValueFactory = cell =>
        StringProperty(cell.value.email)

    val roleColumn = new TableColumn[RegistrationRequest, String]:
      text = "Ruolo richiesto"
      prefWidth = 150
      cellValueFactory = cell =>
        StringProperty(cell.value.requestedRole)

    val areaColumn = new TableColumn[RegistrationRequest, String]:
      text = "Area"
      prefWidth = 140
      cellValueFactory = cell =>
        StringProperty(cell.value.requestedArea)

    val assignmentColumn = new TableColumn[RegistrationRequest, String]:
      text = "Incarico"
      prefWidth = 140
      cellValueFactory = cell =>
        StringProperty(cell.value.assignment)

    val dateColumn = new TableColumn[RegistrationRequest, String]:
      text = "Data richiesta"
      prefWidth = 150
      cellValueFactory = cell =>
        StringProperty(
          cell.value.requestDate.format(dateFormatter)
        )

    val statusColumn = new TableColumn[RegistrationRequest, String]:
      text = "Stato"
      prefWidth = 100
      cellValueFactory = cell =>
        StringProperty(cell.value.status.toString)

    table.columns ++= Seq(
      nameColumn,
      surnameColumn,
      emailColumn,
      roleColumn,
      areaColumn,
      assignmentColumn,
      dateColumn,
      statusColumn
    )

    val idValue = detailValue("-")
    val nameValue = detailValue("-")
    val surnameValue = detailValue("-")
    val emailValue = detailValue("-")
    val phoneValue = detailValue("-")
    val roleValue = detailValue("-")
    val areaValue = detailValue("-")
    val assignmentValue = detailValue("-")
    val dateValue = detailValue("-")
    val statusValue = detailValue("-")

    def clearDetails(): Unit =
      idValue.text = "-"
      nameValue.text = "-"
      surnameValue.text = "-"
      emailValue.text = "-"
      phoneValue.text = "-"
      roleValue.text = "-"
      areaValue.text = "-"
      assignmentValue.text = "-"
      dateValue.text = "-"
      statusValue.text = "-"

    def showDetails(request: RegistrationRequest): Unit =
      idValue.text = request.id
      nameValue.text = request.name
      surnameValue.text = request.surname
      emailValue.text = request.email

      phoneValue.text =
        if request.phone.trim.nonEmpty then request.phone
        else "-"

      roleValue.text = request.requestedRole
      areaValue.text = request.requestedArea
      assignmentValue.text = request.assignment
      dateValue.text = request.requestDate.format(dateFormatter)
      statusValue.text = request.status.toString

    table.selectionModel.value.selectedItem.onChange {
      (_, _, selectedRequest) =>
        Option(selectedRequest) match
          case Some(request) =>
            showDetails(request)
            clearResult()

          case None =>
            clearDetails()
    }

    val detailsGrid = new GridPane:
      hgap = 18
      vgap = 12
      padding = Insets(18)
      styleClass += "request-details-grid"

      add(detailLabel("ID richiesta"), 0, 0)
      add(idValue, 1, 0, 3, 1)

      add(detailLabel("Nome"), 0, 1)
      add(nameValue, 1, 1)

      add(detailLabel("Cognome"), 2, 1)
      add(surnameValue, 3, 1)

      add(detailLabel("Email"), 0, 2)
      add(emailValue, 1, 2)

      add(detailLabel("Telefono"), 2, 2)
      add(phoneValue, 3, 2)

      add(detailLabel("Ruolo richiesto"), 0, 3)
      add(roleValue, 1, 3)

      add(detailLabel("Area richiesta"), 2, 3)
      add(areaValue, 3, 3)

      add(detailLabel("Incarico"), 0, 4)
      add(assignmentValue, 1, 4)

      add(detailLabel("Data richiesta"), 2, 4)
      add(dateValue, 3, 4)

      add(detailLabel("Stato"), 0, 5)
      add(statusValue, 1, 5)

    GridPane.setHgrow(idValue, Priority.Always)
    GridPane.setHgrow(emailValue, Priority.Always)

    def selectedRequest(): Option[RegistrationRequest] =
      Option(table.selectionModel.value.selectedItem.value)

    def loadPendingRequests(): Unit =
      clearResult()
      clearDetails()

      val pending = service.getPendingRequests
      requests.clear()
      requests ++= pending.sortBy(_.requestDate)
      table.selectionModel.value.clearSelection()
      if pending.isEmpty then
        showResult(
          "Non sono presenti richieste di registrazione da elaborare.",
          success = true
        )

    def approveSelectedRequest(): Unit =
      selectedRequest() match
        case None =>
          showResult(
            "Seleziona una richiesta da approvare.",
            success = false
          )

        case Some(request) =>
          val confirmed =
            askConfirmation(
              titleText = "Conferma approvazione",
              header = "Approvare la richiesta selezionata?",
              content =
                s"""${request.name} ${request.surname}
                   |${request.email}
                   |Ruolo: ${request.requestedRole}""".stripMargin
            )

          if confirmed then
            service.approveRequest(request.id) match
              case Right(_) =>
                loadPendingRequests()
                showResult(
                  "Richiesta approvata correttamente.",
                  success = true
                )

              case Left(error) =>
                showResult(error, success = false)

    def rejectSelectedRequest(): Unit =
      selectedRequest() match
        case None =>
          showResult(
            "Seleziona una richiesta da rifiutare.",
            success = false
          )

        case Some(request) =>
          val confirmed =
            askConfirmation(
              titleText = "Conferma rifiuto",
              header = "Rifiutare la richiesta selezionata?",
              content =
                s"""${request.name} ${request.surname}
                   |${request.email}""".stripMargin
            )

          if confirmed then
            service.rejectRequest(request.id) match
              case Right(_) =>
                loadPendingRequests()
                showResult(
                  "Richiesta rifiutata correttamente.",
                  success = true
                )

              case Left(error) =>
                showResult(error, success = false)

    val refreshButton = secondaryButton(text = "Aggiorna", action = () => loadPendingRequests())
    val approveButton = primaryButton(text = "Approva", action = () => approveSelectedRequest())

    val rejectButton = new Button("Rifiuta"):
      styleClass += "danger-button"
      onAction = _ => rejectSelectedRequest()

    val exitButton = closeButton(onExit)

    val actionsBox = actionBar(exitButton, refreshButton, rejectButton, approveButton)

    val header =
      titleBox(
        titleText = "Gestione richieste registrazione",
        subtitleText = "Visualizza e gestisci le richieste di registrazione.",
        titleStyle = "requests-title",
        subtitleStyle = "requests-subtitle"
      )

    val detailsCard = new VBox:
      spacing = 10
      styleClass += "request-details-card"

      children = Seq(
        new Label("Dettaglio richiesta"):
          styleClass += "request-details-title",
          detailsGrid
      )

    val content = new VBox:
      spacing = 18
      padding = Insets(20)
      VBox.setVgrow(table, Priority.Always)

      children = Seq(
        header,
        table,
        detailsCard,
        resultMessage,
        actionsBox
      )

    loadPendingRequests()

    new BorderPane:
      styleClass += "requests-management-root"
      center = content

  private def detailLabel(text: String): Label =
    new Label(text):
      styleClass += "request-detail-label"

  private def detailValue(text: String): Label =
    new Label(text):
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += "request-detail-value"