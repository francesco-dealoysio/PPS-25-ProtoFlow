package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Account
import pkg.d.util.Logger.*

import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.*
import scalafx.scene.layout.*

object AccountManagement$ extends Management:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Account => Unit = _ => (),
             onDelete: Account => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val accountLogic = new Account()
    val accounts = ObservableBuffer.empty[Account]

    val result =
      createResultMessage(
        baseStyle = "accounts-message",
        successStyle = "accounts-message-success",
        errorStyle = "accounts-message-error"
      )

    val table = new TableView[Account](accounts):

      columnResizePolicy = TableView.ConstrainedResizePolicy

      placeholder =
        new Label(
          "Non sono presenti account nel sistema."
        )

      styleClass += "accounts-table"

    val surnameColumn = new TableColumn[Account, String]:
      text = "Cognome"
      cellValueFactory = cell =>
        StringProperty(cell.value.getSurname)

    val nameColumn = new TableColumn[Account, String]:
      text = "Nome"
      cellValueFactory = cell =>
        StringProperty(cell.value.getName)

    val usernameColumn = new TableColumn[Account, String]:
      text = "Username"
      cellValueFactory = cell =>
        StringProperty(cell.value.getUsername)

    val emailColumn = new TableColumn[Account, String]:
      text = "Email"
      cellValueFactory = cell =>
        StringProperty(cell.value.getEmail)

    val phoneColumn = new TableColumn[Account, String]:
      text = "Telefono"
      cellValueFactory = cell =>
        StringProperty(cell.value.getPhone)

    val roleColumn = new TableColumn[Account, String]:
      text = "Ruolo"
      cellValueFactory = cell =>
        StringProperty(cell.value.getRole)

    val areaColumn = new TableColumn[Account, String]:
      text = "Area"
      cellValueFactory = cell =>
        StringProperty(cell.value.getArea)

    val assignmentColumn = new TableColumn[Account, String]:
      text = "Mansione"
      cellValueFactory = cell =>
        StringProperty(cell.value.getAssignment)

    table.columns ++= Seq(
      surnameColumn,
      nameColumn,
      usernameColumn,
      emailColumn,
      phoneColumn,
      roleColumn,
      areaColumn,
      assignmentColumn
    )

    def selectedAccount(): Option[Account] =
      Option(
        table.selectionModel.value
          .selectedItem
          .value
      )

    def loadAccounts(): Unit =
      result.clear()
      try
        val loaded =
          accountLogic
            .getRecords[Account]()
            .sortBy: account =>
              account
                .getId
                .toIntOption
                .getOrElse(Int.MaxValue)

        accounts.setAll(loaded*)

        table.selectionModel.value
          .clearSelection()

        if loaded.isEmpty then
          result.show("Non sono presenti account nel sistema.", success = true)

      catch
        case exception: Exception =>
          accounts.clear()
          result.show("Errore durante il caricamento degli account.", success = false)
          logger(exception)

    // Pulisce il messaggio quando viene selezionata una nuova riga.
    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>

          if selected != null then
            result.clear()

    val addButton = primaryButton(text = "Aggiunta", action = () =>
      result.clear()
      onAdd())

    val editButton =
      secondaryButton(
        text = "Modifica",
        action = () =>
          selectedAccount() match
            case Some(selected) =>
              result.clear()
              onEdit(selected)

            case None =>
              result.show("Seleziona un account da modificare.", success = false)
      )

    editButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val deleteButton =
      dangerButton(
        text = "Eliminazione",
        action = () =>
          selectedAccount() match
            case Some(selected) =>
              result.clear()
              onDelete(selected)

            case None =>
              result.show("Seleziona un account da eliminare.", success = false)
      )

    deleteButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val exitButton = closeButton(onExit)

    val navigationMenu =
      new HBox:
        spacing = 12
        alignment = Pos.CenterLeft
        styleClass += "accounts-toolbar"
        children = Seq(
          addButton,
          editButton,
          deleteButton
        )

    // Pulsante in fondo alla pagina.
    val bottomActions = actionBar(exitButton)

    val header =
      titleBox(
        titleText = "Gestione Account Utente",
        subtitleText = "Visualizza, aggiungi, modifica ed elimina gli account degli utenti del sistema.",
        titleStyle = "accounts-title",
        subtitleStyle = "accounts-subtitle"
      )

    loadAccounts() // Prima lettura dal file XML.

    managementPage(
      rootStyle = "accounts-management-root",
      growNode = Some(table),
      pageChildren = Seq(header, navigationMenu, table, result.label, bottomActions)
    )
