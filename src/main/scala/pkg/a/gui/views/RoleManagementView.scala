package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Role
import pkg.d.util.Logger.*
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane

object RoleManagementView extends Management:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Role => Unit = _ => (),
             onDelete: Role => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val roleLogic = new Role()
    val roles = ObservableBuffer.empty[Role]

    val result =
      createResultMessage(
        baseStyle = "roles-message",
        successStyle = "roles-message-success",
        errorStyle = "roles-message-error"
      )

    val table =
      new TableView[Role](roles):
        columnResizePolicy =
          TableView.ConstrainedResizePolicy

        placeholder = new Label("Non sono presenti ruoli nel sistema.")
        styleClass += "roles-table"

    val roleColumn =
      new TableColumn[Role, String]:
        text = "Ruolo"
        cellValueFactory = cell =>
          StringProperty(cell.value.getRole)

    val descriptionColumn =
      new TableColumn[Role, String]:
        text = "Descrizione"

        cellValueFactory = cell =>
          StringProperty(cell.value.getDescription)

    table.columns ++= Seq(
      roleColumn,
      descriptionColumn
    )

    def selectedRole(): Option[Role] =
      Option(
        table.selectionModel.value
          .selectedItem
          .value
      )

    def loadRoles(): Unit =
      result.clear()

      try
        val loaded =
          roleLogic
            .getRecords[Role]()
            .sortBy: role =>
              role
                .getId
                .toIntOption
                .getOrElse(Int.MaxValue)

        roles.setAll(loaded*)

        table.selectionModel.value
          .clearSelection()

        if loaded.isEmpty then
          result.show("Non sono presenti ruoli nel sistema.", success = true)

      catch
        case exception: Exception =>
          roles.clear()
          result.show("Errore durante il caricamento dei ruoli.", success = false)
          logger(exception)

    def deleteSelectedRole(): Unit =
      selectedRole() match
        case None =>
          result.show("Seleziona un ruolo da eliminare.", success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = "Eliminazione ruolo",
              header = "Confermi l'eliminazione del ruolo selezionato?",
              content =
                s"""Ruolo: ${selected.getRole}
                   |Codice: ${selected.getId}
                   |Descrizione: ${selected.getDescription}
                   |
                   |L'operazione non può essere annullata.""".stripMargin
            )

          if confirmed then
            val deleted = roleLogic.recordDelete(selected.getId)
            if deleted then
              loadRoles()
              result.show(s"Il ruolo '${selected.getRole}' è stato eliminato correttamente.", success = true)
            else
              result.show("Non è stato possibile eliminare il ruolo.", success = false)

    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>
          if selected != null then
            result.clear()

    val addButton = primaryButton("Aggiungi", () =>
          result.clear()
          onAdd()
      )

    val editButton =
      secondaryButton("Modifica", () =>
          selectedRole() match
            case Some(selected) =>
              result.clear()
              onEdit(selected)

            case None =>
              result.show("Seleziona un ruolo da modificare.", success = false)
      )

    editButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val deleteButton = dangerButton("Elimina", () => deleteSelectedRole())

    deleteButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val exitButton = closeButton(onExit)

    val bottomActions =
      actionBar(
        exitButton,
        editButton,
        deleteButton,
        addButton
      )

    val header =
      titleBox(
        titleText = "Gestione Ruoli",
        subtitleText = "Visualizza, aggiungi, modifica ed elimina i ruoli del sistema.",
        titleStyle = "roles-title",
        subtitleStyle = "roles-subtitle"
      )

    loadRoles()

    managementPage(
      rootStyle = "roles-management-root",
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )