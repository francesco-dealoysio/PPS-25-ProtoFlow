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

    val result =
      createResultMessage(
        baseStyle = "requests-message",
        successStyle = "requests-message-success",
        errorStyle = "requests-message-error"
      )


    val table = new TableView[RegistrationRequest](requests):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(
        "Non sono presenti richieste di registrazione da elaborare."
      )
      styleClass += "requests-table"

    def stringColumn(title: String, colWidth: Double)(value: RegistrationRequest => String): TableColumn[RegistrationRequest, String] =
      new TableColumn[RegistrationRequest, String]:
        text = title
        prefWidth = colWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    val nameColumn = stringColumn("Nome", 110)(_.name)
    val surnameColumn = stringColumn("Cognome", 120)(_.surname)
    val emailColumn = stringColumn("Email", 210)(_.email)
    val roleColumn = stringColumn("Ruolo richiesto", 150)(_.requestedRole)
    val areaColumn = stringColumn("Area", 140)(_.requestedArea)
    val assignmentColumn = stringColumn("Incarico", 140)(_.assignment)
    val dateColumn = stringColumn("Data richiesta", 150):
        _.requestDate.format(dateFormatter)
    val statusColumn = stringColumn("Stato", 100):
        _.status.toString

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

    val idValue = detailValue()
    val nameValue = detailValue()
    val surnameValue = detailValue()
    val emailValue = detailValue()
    val phoneValue = detailValue()
    val roleValue = detailValue()
    val areaValue = detailValue()
    val assignmentValue = detailValue()
    val dateValue = detailValue()
    val statusValue = detailValue()

    val detailValues = Seq(
      idValue,
      nameValue,
      surnameValue,
      emailValue,
      phoneValue,
      roleValue,
      areaValue,
      assignmentValue,
      dateValue,
      statusValue
    )

    def clearDetails(): Unit =
      detailValues.foreach(_.text = "-")

    def valueOrDash(value: String): String =
      Option(value)
        .map(_.trim)
        .filter(_.nonEmpty)
        .getOrElse("-")

    def showDetails(request: RegistrationRequest): Unit =
      idValue.text = valueOrDash(request.id)
      nameValue.text = valueOrDash(request.name)
      surnameValue.text = valueOrDash(request.surname)
      emailValue.text = valueOrDash(request.email)
      phoneValue.text = valueOrDash(request.phone)
      roleValue.text = valueOrDash(request.requestedRole)
      areaValue.text = valueOrDash(request.requestedArea)
      assignmentValue.text = valueOrDash(request.assignment)
      dateValue.text = request.requestDate.format(dateFormatter)
      statusValue.text = request.status.toString

    table.selectionModel.value.selectedItem.onChange {
      (_, _, selectedRequest) =>
        Option(selectedRequest) match
          case Some(request) =>
            showDetails(request)
            result.clear()

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
      result.clear()
      clearDetails()

      val pending = service.getPendingRequests
      requests.clear()
      requests ++= pending.sortBy(_.requestDate)
      table.selectionModel.value.clearSelection()
      if pending.isEmpty then
        result.show("Non sono presenti richieste di registrazione da elaborare.", success = true)

    def approveSelectedRequest(): Unit =
      selectedRequest() match
        case None =>
          result.show("Seleziona una richiesta da approvare.", success = false)

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
                result.show("Richiesta approvata correttamente.", success = true)

              case Left(error) =>
                result.show(error, success = false)

    def rejectSelectedRequest(): Unit =
      selectedRequest() match
        case None =>
          result.show("Seleziona una richiesta da rifiutare.", success = false)

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
                result.show("Richiesta rifiutata correttamente.", success = true)

              case Left(error) =>
                result.show(error, success = false)

    val refreshButton = secondaryButton("Aggiorna",() => loadPendingRequests())
    val approveButton = primaryButton("Approva",() => approveSelectedRequest())
    val rejectButton = dangerButton("Rifiuta", () => rejectSelectedRequest())
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

    loadPendingRequests()

    managementPage(
      rootStyle = "requests-management-root",
      growNode = Some(table),
      pageChildren = Seq(header, table, detailsCard, result.label, actionsBox)
    )

  private def detailLabel(text: String): Label =
    new Label(text):
      styleClass += "request-detail-label"

  private def detailValue(initialText: String = "-"): Label =
    new Label(initialText):
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += "request-detail-value"