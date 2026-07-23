package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.control.{TextArea, TextField}
import scalafx.scene.layout.BorderPane

object RoleEditView extends Form:

  def apply(
             selectedRole: Role,
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val roleLogic = new Role()
    val viewModel = new RoleViewModel()

    val initialRole = selectedRole.getRole
    val initialDescription = selectedRole.getDescription

    val roleField =
      new TextField:
        text = initialRole
        promptText = "Inserisci il ruolo"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val descriptionArea =
      new TextArea:
        text = initialDescription
        promptText = "Inserisci la descrizione"
        wrapText = true
        prefRowCount = 5
        maxWidth = Double.MaxValue
        styleClass += "role-description-area"

    val roleError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()

    val resultMessage =
      messageLabel("roles-message")

    def clearErrors(): Unit =
      clearFieldErrors(
        roleField -> roleError,
        descriptionArea -> descriptionError
      )

      clearMessage(
        resultMessage,
        successStyle = "roles-message-success",
        errorStyle = "roles-message-error"
      )

    def currentRole(): Role =
      Role(
        id = selectedRole.getId,
        role = roleField.text.value.trim.toLowerCase,
        description = descriptionArea.text.value.trim
      )

    def resetForm(): Unit =
      roleField.text = initialRole
      descriptionArea.text = initialDescription

      clearErrors()
      roleField.requestFocus()

    def validateForm(): Boolean = 
      clearErrors()

      val errors =
        viewModel.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords(),
          currentRoleId = Some(selectedRole.getId)
        )

      showMappedErrors(errors):
        case RoleViewModel.RoleRequiredError |
             RoleViewModel.DuplicateRoleError =>
          roleField -> roleError

        case RoleViewModel.DescriptionRequiredError =>
          descriptionArea -> descriptionError

    val save =
      saveButton: () =>
        if validateForm() then
          val updated =
            roleLogic.recordUpdate(currentRole())

          showMessage(
            label = resultMessage,
            message =
              if updated then
                "Ruolo modificato correttamente."
              else
                "Errore durante la modifica del ruolo.",
            success = updated,
            successStyle = "roles-message-success",
            errorStyle = "roles-message-error"
          )

          if updated then
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          FormRow("Ruolo *", roleField, roleError),
          FormRow("Descrizione *", descriptionArea, descriptionError
          )
        )
      )

    formPage(
      titleText = "Modifica ruolo",
      subtitleText = "Modifica i dati del ruolo selezionato.",
      titleStyle = "roles-title",
      subtitleStyle = "roles-subtitle",
      rootStyle = "roles-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(exit, reset, save)
    )