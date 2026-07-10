package pkg.a.gui

import pkg.b.logic.RegistrationRequestService
import pkg.c.data.guiStructures.RegistrationRequest
import pkg.c.data.xmlManagement.RegistrationRequestRepository
import scalafx.Includes.jfxMultipleSelectionModel2sfx
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*

import java.time.format.DateTimeFormatter

object RegistrationRequestsManagementView:

  private val dateFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

  def apply(
             onExit: () => Unit = () => ()
           ): BorderPane =

    val repository = new RegistrationRequestRepository()
    val service = new RegistrationRequestService(repository)

    val requests =
      ObservableBuffer.empty[RegistrationRequest]

    val messageLabel = new Label:
      visible = false
      managed = false
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += "requests-message"

    def showMessage(message: String, success: Boolean): Unit =
      messageLabel.text = message
      messageLabel.visible = true
      messageLabel.managed = true

      messageLabel.styleClass.removeAll(
        "requests-message-success",
        "requests-message-error"
      )

      if success then
        messageLabel.styleClass += "requests-message-success"
      else
        messageLabel.styleClass += "requests-message-error"

    def clearMessage(): Unit =
      messageLabel.text = ""
      messageLabel.visible = false
      messageLabel.managed = false

      messageLabel.styleClass.removeAll(
        "requests-message-success",
        "requests-message-error"
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
            clearMessage()

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
      clearMessage()
      clearDetails()

      val pending = service.getPendingRequests

      requests.clear()
      requests ++= pending.sortBy(_.requestDate)

      table.selectionModel.value.clearSelection()

      if pending.isEmpty then
        showMessage(
          "Non sono presenti richieste di registrazione da elaborare.",
          success = true
        )

    val refreshButton = new Button("Aggiorna"):
      styleClass += "secondary-button"

      onAction = _ =>
        loadPendingRequests()

    val approveButton = new Button("Approva"):
      styleClass += "primary-button"

      onAction = _ =>
        selectedRequest() match
          case None =>
            showMessage(
              "Seleziona una richiesta da approvare.",
              success = false
            )

          case Some(request) =>
            val confirmation = new Alert(Alert.AlertType.Confirmation):
              title = "Conferma approvazione"
              headerText = "Approvare la richiesta selezionata?"
              contentText =
                s"${request.name} ${request.surname}\n" +
                  s"${request.email}\n" +
                  s"Ruolo: ${request.requestedRole}"

            confirmation.showAndWait() match
              case Some(ButtonType.OK) =>
                service.approveRequest(request.id) match
                  case Right(_) =>
                    loadPendingRequests()
                    showMessage(
                      "Richiesta approvata correttamente.",
                      success = true
                    )

                  case Left(error) =>
                    showMessage(error, success = false)

              case _ =>
                ()

    val rejectButton = new Button("Rifiuta"):
      styleClass += "danger-button"

      onAction = _ =>
        selectedRequest() match
          case None =>
            showMessage(
              "Seleziona una richiesta da rifiutare.",
              success = false
            )

          case Some(request) =>
            val confirmation = new Alert(Alert.AlertType.Confirmation):
              title = "Conferma rifiuto"
              headerText = "Rifiutare la richiesta selezionata?"
              contentText =
                s"${request.name} ${request.surname}\n" +
                  s"${request.email}"

            confirmation.showAndWait() match
              case Some(ButtonType.OK) =>
                service.rejectRequest(request.id) match
                  case Right(_) =>
                    loadPendingRequests()
                    showMessage(
                      "Richiesta rifiutata correttamente.",
                      success = true
                    )

                  case Left(error) =>
                    showMessage(error, success = false)

              case _ =>
                ()

    val closeButton = new Button("Chiudi"):
      styleClass += "secondary-button"
      onAction = _ => onExit()

    val actionsBox = new HBox:
      spacing = 12
      alignment = Pos.CenterRight
      children = Seq(
        closeButton,
        refreshButton,
        rejectButton,
        approveButton
      )

    val titleBox = new VBox:
      spacing = 5

      children = Seq(
        {
          val l = new Label("Gestione richieste registrazione")
          l.styleClass += "requests-title"
          l
        },
        {
          val l = new Label("Visualizza e gestisci...")
          l.styleClass += "requests-subtitle"
          l
        }
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
      padding = Insets(24)
      VBox.setVgrow(table, Priority.Always)

      children = Seq(
        titleBox,
        table,
        detailsCard,
        messageLabel,
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