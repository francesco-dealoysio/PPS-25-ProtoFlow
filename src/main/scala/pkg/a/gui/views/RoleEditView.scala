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
    val descriptionArea = textArea(Fields.Prompts.Description, UiStyles.Roles.DescriptionArea, selectedRole.getDescription)

    val roleError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()
    val monitoredFields = Seq(roleField, descriptionArea)
    val initialFormValues = Seq(initialRole, initialDescription)
    val resultMessage = messageLabel(UiStyles.Roles.Message)

    def clearErrors(): Unit =
      clearFieldErrors(
        roleField -> roleError,
        descriptionArea -> descriptionError
      )

      clearMessage(
        resultMessage,
        successStyle = UiStyles.Roles.MessageSuccess,
        errorStyle = UiStyles.Roles.MessageError
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
                Roles.Edit.Success
              else
                Roles.Edit.Error,
            success = updated,
            successStyle = UiStyles.Roles.MessageSuccess,
            errorStyle = UiStyles.Roles.MessageError
          )

          if updated then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          FormRow(Fields.Labels.required(Fields.Labels.Role), roleField, roleError),
          FormRow(Fields.Labels.required(Fields.Labels.Description), descriptionArea, descriptionError
          )
        )
      )

    formPage(
      titleText = Roles.Edit.Title,
      subtitleText = Roles.Edit.Subtitle,
      titleStyle = UiStyles.Roles.Title,
      subtitleStyle = UiStyles.Roles.Subtitle,
      rootStyle = UiStyles.Roles.Root,
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