package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.{Account, Role, Registration}
import pkg.d.util.Util.inDatabaseFilePathName
import pkg.d.util.XmlToPdf
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.Roles.Management as Text

object RoleManagementView extends Management:

  def apply(onAdd: () => Unit = () => (), onEdit: Role => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val roleLogic = new Role()
    val accountLogic = new Account()
    val registrationLogic = new Registration()
    val roles = ObservableBuffer.empty[Role]
    val result = createResultMessage()
    val table = managementTable(roles, Text.Empty)

    table.columns ++= Seq(
      stringColumn[Role](Labels.Role)(_.getRole),
      stringColumn[Role](Labels.RoleName)(_.getName),
      stringColumn[Role](Labels.Description)(_.getDescription)
    )

    def loadRoles(): Unit =
      loadTableItemsSafely(roles, result, Text.Empty, Text.LoadError):
        roleLogic
          .getRecords[Role]()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

    def deleteSelectedRole(): Unit =
      withSelectedItem(table, result, Text.SelectToDelete): selected =>
        val confirmed =
          askConfirmation(
            titleText = Text.DeleteTitle,
            header = Text.DeleteConfirmation,
            content =
              s"""Ruolo: ${selected.getRole}
                 |Codice: ${selected.getId}
                 |Descrizione: ${selected.getDescription}
                 |
                 |L'operazione non può essere annullata.""".stripMargin
          )

        if confirmed then
          val roleAssignedToAccount =
            accountLogic
            .getRecords[Account]()
            .exists: account =>
                account.getRole.trim.equalsIgnoreCase(selected.getRole.trim)

          val roleRequestedByPendingRegistration =
            registrationLogic
              .getRecords[Registration]()
              .exists: request =>
                request.getState == "Pending" &&
                  request.getRole.trim.equalsIgnoreCase(selected.getRole.trim)

          val roleInUse = roleAssignedToAccount || roleRequestedByPendingRegistration

          if selected.getRole.equalsIgnoreCase("admin") then
            result.show(Text.AdminRoleDeleteError, success = false)
          else if roleInUse then
            result.show(Text.RoleInUseDeleteError, success = false)
          else
            val deleted = roleLogic.recordDelete(selected.getId)
            if deleted then
              loadRoles()
              result.show(Text.deleted(selected.getRole), success = true)
            else
              result.show(Text.DeleteError, success = false)

    def printRoles(): Unit =
      val printed =
        XmlToPdf.printList(
          xmlPath = inDatabaseFilePathName("roles.xml"),
          pdfFileName = "elenco-ruoli.pdf",
          title = Text.PrintTitle
        )
      if printed then
        result.show(Text.PrintSuccess, success = true)
      else
        result.show(Text.PrintError, success = false)

    clearResultOnSelection(table, result)

    val addButton = primaryButton(Buttons.Add, () =>
          result.clear()
          onAdd()
    )

    val editButton = secondaryButton(Buttons.Edit, () => withSelectedItem(table, result, Text.SelectToEdit)(onEdit))

    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedRole)
    disableWithoutSelection(table, editButton, deleteButton)
    val bottomActions = actionBar(Seq(closeButton(onExit), printButton(printRoles), editButton, deleteButton, addButton))

    val header = titleBox(Text.Title, Text.Subtitle)

    loadRoles()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )