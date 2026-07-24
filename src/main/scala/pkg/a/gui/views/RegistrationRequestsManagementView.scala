package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{Registration, RegistrationDates, RegistrationRequestService}
import pkg.d.util.XmlToPdf

import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*

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
        baseStyle = "requests-message",
        successStyle = "requests-message-success",
        errorStyle = "requests-message-error"
      )

    val table = new TableView[Registration](requests):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(
        "Non sono presenti richieste di registrazione da elaborare."
      )
      styleClass += "requests-table"

    def stringColumn(title: String, colWidth: Double)(value: Registration => String): TableColumn[Registration, String] =
      new TableColumn[Registration, String]:
        text = title
        prefWidth = colWidth
        cellValueFactory = cell =>
          StringProperty(value(cell.value))

    table.columns ++= Seq(
      stringColumn("Nome", 110)(_.getName),
      stringColumn("Cognome", 120)(_.getSurname),
      stringColumn("Email", 210)(_.getEmail),
      stringColumn("Ruolo richiesto", 150)(_.getRole),
      stringColumn("Area", 140)(_.getArea),
      stringColumn("Incarico", 140)(_.getAssignment),
      stringColumn("Data richiesta", 150)(request => RegistrationDates.parse(request.getDate).format(dateFormatter))
    )

    def selectedRequest(): Option[Registration] =
      Option(table.selectionModel.value.selectedItem.value)

    def loadPendingRequests(): Unit =
      result.clear()

      val pending = service.getPendingRequests
      requests.setAll(pending.sortBy(_.getDate)*)
      table.selectionModel.value.clearSelection()

      if pending.isEmpty then
        result.show("Non sono presenti richieste di registrazione da elaborare.", success = true)

    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>
          if selected != null then
            result.clear()

    def printPendingList(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = service.pendingRequestsFilePath,
          pdfFileName = "richieste_registrazione_elenco",
          title = "Elenco Richieste di Registrazione da Elaborare"
        )

      result.show(
        if printed then
          "Elenco stampato correttamente in PDF."
        else
          "Errore durante la stampa dell'elenco (nessuna richiesta presente?).",
        success = printed
      )

    val refreshButton = secondaryButton("Aggiorna", () => loadPendingRequests())
    val printButton = secondaryButton("Stampa elenco", () => printPendingList())

    val processButton =
      primaryButton("Elabora", () =>
        selectedRequest() match
          case Some(selected) =>
            result.clear()
            onProcess(selected)

          case None =>
            result.show("Seleziona una richiesta da elaborare.", success = false)
      )

    processButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val exitButton = closeButton(onExit)

    val bottomActions = actionBar(Seq(exitButton, refreshButton, printButton, processButton))

    val header =
      titleBox(
        titleText = "Gestione richieste registrazione",
        subtitleText = "Visualizza le richieste di registrazione in attesa ed elaborale.",
        titleStyle = "requests-title",
        subtitleStyle = "requests-subtitle"
      )

    loadPendingRequests()

    managementPage(
      rootStyle = "requests-management-root",
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
