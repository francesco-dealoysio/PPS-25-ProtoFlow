package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.text.UiStyles.Common.*
import UiText.{Fields, Roles}

object RoleEditView extends Form:

  def apply(selectedRole: Role, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val viewModel = new RoleViewModel()

    val role = stringField(Fields.Prompts.Role, selectedRole.getRole)
    val descriptionArea = areaField(Fields.Prompts.Description, DescriptionAreaStyle, selectedRole.getDescription)
    val description = areaField(Fields.Prompts.Description, DescriptionAreaStyle, selectedRole.getDescription)
    val monitoredFields = Seq(role, description)
    val resultMessage = messageLabel()

    def clearErrors(): Unit =
      clearFormFieldErrors(role, description)
      clearMessage(resultMessage)

    def currentRole(): Role =
      Role(
        id = selectedRole.getId,
        role = role.value.toLowerCase,
        description = description.value
      )

    def resetForm(): Unit =
      resetFields(role, description)
      clearErrors()
      role.requestFocus()

    def validateForm(): Boolean = 
      clearErrors()
      val errors =
        viewModel.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords(),
          currentRoleId = Some(selectedRole.getId)
        )

      showFormFieldErrors(errors):
        case RoleViewModel.RoleRequiredError | RoleViewModel.DuplicateRoleError =>
          role
        case RoleViewModel.DescriptionRequiredError =>
          description

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val updated = roleLogic.recordUpdate(currentRole())

          showMessage(
            label = resultMessage,
            message =
              if updated then
                Roles.Edit.Success
              else
                Roles.Edit.Error,
            success = updated
          )

          if updated then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Fields.Labels.required(Fields.Labels.Role), role),
          formRow(Fields.Labels.required(Fields.Labels.Description), description),
        )
      )

    formPage(
      titleText = Roles.Edit.Title,
      subtitleText = Roles.Edit.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )