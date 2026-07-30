package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Role
import pkg.d.util.Logger.*
import pkg.d.util.Util.inDatabaseFilePathName
import pkg.d.util.XmlToPdf
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Common, Fields, Roles}

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
        baseStyle = UiStyles.Roles.Message,
        successStyle = UiStyles.Roles.MessageSuccess,
        errorStyle = UiStyles.Roles.MessageError
      )

    val table =
      new TableView[Role](roles):
        columnResizePolicy =
          TableView.ConstrainedResizePolicy

        placeholder = new Label(Roles.Management.Empty)
        styleClass += UiStyles.Roles.Table

    val roleColumn =
      new TableColumn[Role, String]:
        text = Fields.Labels.Role
        cellValueFactory = cell =>
          StringProperty(cell.value.getRole)

    val descriptionColumn =
      new TableColumn[Role, String]:
        text = Fields.Labels.Description

        cellValueFactory = cell =>
          StringProperty(cell.value.getDescription)

    table.columns ++= Seq(
      roleColumn,
      descriptionColumn
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
          result.show(Roles.Management.Empty, success = true)

      catch
        case exception: Exception =>
          roles.clear()
          result.show(Roles.Management.LoadError, success = false)
          logger(exception)

    def deleteSelectedRole(): Unit =
      selectedItem(table) match
        case None =>
          result.show(Roles.Management.SelectToDelete, success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = Roles.Management.DeleteTitle,
              header = Roles.Management.DeleteConfirmation,
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
              result.show(Roles.Management.deleted(selected.getRole), success = true)
            else
              result.show(Roles.Management.DeleteError, success = false)

    def printRoles(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDatabaseFilePathName("roles.xml"),
          pdfFileName = "elenco-ruoli.pdf",
          title = Roles.Management.PrintTitle
        )
      if printed then
        result.show(Roles.Management.PrintSuccess, success = true)
      else
        result.show(Roles.Management.PrintError, success = false)

    clearResultOnSelection(table, result)

    val addButton = primaryButton(Common.Buttons.Add, () =>
          result.clear()
          onAdd()
      )

    val editButton =
      secondaryButton(Common.Buttons.Edit, () =>
        selectedItem(table) match
            case Some(selected) =>
              result.clear()
              onEdit(selected)

            case None =>
              result.show(Roles.Management.SelectToEdit, success = false)
      )

    val deleteButton = dangerButton(Common.Buttons.Delete, () => deleteSelectedRole())
    val exitButton = closeButton(onExit)
    disableWithoutSelection(table, editButton, deleteButton)

    val print = printButton(action = () => printRoles())

    val bottomActions = actionBar(Seq(exitButton, print, editButton, deleteButton, addButton))

    val header =
      titleBox(
        titleText = Roles.Management.Title,
        subtitleText = Roles.Management.Subtitle,
        titleStyle = UiStyles.Roles.Title,
        subtitleStyle = UiStyles.Roles.Subtitle
      )

    loadRoles()

    managementPage(
      rootStyle = UiStyles.Roles.Root,
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )