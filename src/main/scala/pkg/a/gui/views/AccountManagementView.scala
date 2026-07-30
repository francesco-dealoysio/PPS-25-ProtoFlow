package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Account
import pkg.d.util.Logger.*
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Accounts, Common, Fields}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*

object AccountManagementView extends Management:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Account => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val accountLogic = new Account()
    val accounts = ObservableBuffer.empty[Account]

    val result =
      createResultMessage(
        baseStyle = UiStyles.Accounts.Message,
        successStyle = UiStyles.Accounts.MessageSuccess,
        errorStyle = UiStyles.Accounts.MessageError
      )

    val table = new TableView[Account](accounts):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(Accounts.Management.Empty)
      styleClass += UiStyles.Accounts.Table

    val surnameColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Surname
      cellValueFactory = cell =>
        StringProperty(cell.value.getSurname)

    val nameColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Name
      cellValueFactory = cell =>
        StringProperty(cell.value.getName)

    val usernameColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Username
      cellValueFactory = cell =>
        StringProperty(cell.value.getUsername)

    val emailColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Email
      cellValueFactory = cell =>
        StringProperty(cell.value.getEmail)

    val phoneColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Phone
      cellValueFactory = cell =>
        StringProperty(cell.value.getPhone)

    val roleColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Role
      cellValueFactory = cell =>
        StringProperty(cell.value.getRole)

    val areaColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Area
      cellValueFactory = cell =>
        StringProperty(cell.value.getArea)

    val assignmentColumn = new TableColumn[Account, String]:
      text = Fields.Labels.Assignment
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
        table.selectionModel.value.clearSelection()
        if loaded.isEmpty then
          result.show(Accounts.Management.Empty, success = true)

      catch
        case exception: Exception =>
          accounts.clear()
          result.show(Accounts.Management.LoadError, success = false)
          logger(exception)

    def deleteSelectedAccount(): Unit =
      selectedItem(table) match
        case None =>
          result.show(Accounts.Management.SelectToDelete, success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = Accounts.Management.DeleteTitle,
              header = Accounts.Management.DeleteConfirmation,
              content =
                s"""Account: ${selected.getUsername}
                   |Nominativo: ${selected.getName} ${selected.getSurname}
                   |Codice: ${selected.getId}
                   |
                   |L'operazione non può essere annullata.""".stripMargin
            )

          if confirmed then
            val deleted = accountLogic.recordDelete(selected.getId)

            if deleted then
              loadAccounts()
              result.show(Accounts.Management.deleted(selected.getUsername), success = true)
            else
              result.show(Accounts.Management.DeleteError, success = false)

    clearResultOnSelection(table, result)

    val addButton = primaryButton(Common.Buttons.Add, () =>
      result.clear()
      onAdd())

    val editButton =
      secondaryButton(Common.Buttons.Edit, () =>
        selectedItem(table) match
          case Some(selected) =>
            result.clear()
            onEdit(selected)

          case None =>
            result.show(Accounts.Management.SelectToEdit, success = false)
      )

    val deleteButton = dangerButton(Common.Buttons.Delete, () => deleteSelectedAccount())
    disableWithoutSelection(table, editButton, deleteButton)

    val exitButton = closeButton(onExit)

    val bottomActions = actionBar(Seq(exitButton, editButton, deleteButton, addButton))

    val header =
      titleBox(
        titleText = Accounts.Management.Title,
        subtitleText = "Visualizza, aggiungi, modifica ed elimina gli account degli utenti del sistema.",
        titleStyle = UiStyles.Accounts.Title,
        subtitleStyle = UiStyles.Accounts.Subtitle
      )

    loadAccounts() // Prima lettura dal file XML.

    managementPage(
      rootStyle = UiStyles.Accounts.Root,
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
