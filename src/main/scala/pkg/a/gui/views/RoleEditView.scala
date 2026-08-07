package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Roles.Edit as Text
import pkg.a.gui.text.UiText.Validation.Role as Validation

object RoleEditView extends Form:

  def apply(selectedRole: Role, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val viewModel = new RoleViewModel()

    val role = stringField(Prompts.Role, selectedRole.getRole)
    val descriptionArea = areaField(Prompts.Description, DescriptionAreaStyle, selectedRole.getDescription)
    val description = areaField(Prompts.Description, DescriptionAreaStyle, selectedRole.getDescription)
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
        case Validation.RoleRequired | Validation.DescriptionRequired => role
        case Validation.DuplicateRole => description

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val updated = roleLogic.recordUpdate(currentRole())

          showMessage(
            label = resultMessage,
            message = if updated then Text.Success else Text.Error,
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
          formRow(Labels.required(Labels.Role), role),
          formRow(Labels.required(Labels.Description), description),
        )
      )

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )