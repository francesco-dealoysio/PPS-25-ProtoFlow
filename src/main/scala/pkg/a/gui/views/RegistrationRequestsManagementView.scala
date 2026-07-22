package pkg.a.gui.views

import pkg.a.gui.structures.RegistrationRequest
import pkg.a.gui.traits.Management
import pkg.b.logic.RegistrationRequestService
import pkg.c.data.xmlManagement.RegistrationRequestRepository
import scalafx.Includes.jfxMultipleSelectionModel2sfx
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.layout.*
import java.time.format.DateTimeFormatter

object RegistrationRequestsManagementView extends Management:

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

    table.columns ++= Seq(
      stringColumn("Nome", 110)(_.name),
      stringColumn("Cognome", 120)(_.surname),
      stringColumn("Email", 210)(_.email),
      stringColumn("Ruolo richiesto", 150)(_.requestedRole),
      stringColumn("Area", 140)(_.requestedArea),
      stringColumn("Incarico", 140)(_.assignment),
      stringColumn("Data richiesta", 150):
        _.requestDate.format(dateFormatter),
      stringColumn("Stato", 100):
        _.status.toString
    )

    case class DetailField(title: String, valueLabel: Label, valueOf: RegistrationRequest => String)

    def valueOrDash(value: String): String =
      Option(value)
        .map(_.trim)
        .filter(_.nonEmpty)
        .getOrElse("-")

    val idField = DetailField("ID richiesta", detailValue(), request => valueOrDash(request.id))
    val emailField = DetailField("Email", detailValue(), request => valueOrDash(request.email))

    val detailFields = Seq(
      idField,
      DetailField("Nome", detailValue(), request => valueOrDash(request.name)),
      DetailField("Cognome", detailValue(), request => valueOrDash(request.surname)),
      emailField,
      DetailField("Telefono", detailValue(), request => valueOrDash(request.phone)),
      DetailField("Ruolo richiesto", detailValue(), request => valueOrDash(request.requestedRole)),
      DetailField("Area richiesta", detailValue(), request => valueOrDash(request.requestedArea)),
      DetailField("Incarico", detailValue(), request => valueOrDash(request.assignment)),
      DetailField("Data richiesta", detailValue(), request => request.requestDate.format(dateFormatter)),
      DetailField("Stato", detailValue(), request => request.status.toString)
    )

    def clearDetails(): Unit =
      detailFields.foreach(_.valueLabel.text = "-")

    def showDetails(request: RegistrationRequest): Unit =
      detailFields.foreach: field =>
        field.valueLabel.text = field.valueOf(request)

    val detailsGrid = new GridPane:
      hgap = 18
      vgap = 12
      padding = Insets(18)
      styleClass += "request-details-grid"
      add(detailLabel(idField.title), 0, 0)
      add(idField.valueLabel, 1, 0, 3, 1)

      detailFields
        .drop(1)
        .grouped(2)
        .zipWithIndex
        .foreach:
          case (fields, rowIndex) =>
            val row = rowIndex + 1

            fields.zipWithIndex.foreach:
              case (field, columnIndex) =>
                val labelColumn = columnIndex * 2
                val valueColumn = labelColumn + 1

                add(detailLabel(field.title), labelColumn, row)
                add(field.valueLabel, valueColumn, row)

    GridPane.setHgrow(idField.valueLabel, Priority.Always)
    GridPane.setHgrow(emailField.valueLabel, Priority.Always)

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