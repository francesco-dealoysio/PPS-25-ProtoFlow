package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, Roles}

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
    val styles = UiStyles.Roles
    val roleField = textField(Fields.Prompts.Role, initialRole)
    val descriptionArea = textArea(Fields.Prompts.Description, "role-description-area", selectedRole.getDescription)

    val roleError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()
    val monitoredFields = Seq(roleField, descriptionArea)
    val initialFormValues = Seq(initialRole, initialDescription)
    val resultMessage = messageLabel("roles-message")

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

    var formSaved = false
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
            formSaved = true
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
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () =>
        hasFormChanges(
          formSaved = formSaved,
          textFields = monitoredFields,
          initialValues = initialFormValues
        )
    )