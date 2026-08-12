package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Roles.Edit as Text
import pkg.a.gui.text.UiText.Validation.Role as Validation
import pkg.a.gui.validation.RoleValidator

object RoleEditView extends Form:

  def apply(selectedRole: Role, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val validator = new RoleValidator()

    val role = stringField("", selectedRole.getRole)

    val description = areaField("", DescriptionAreaStyle, selectedRole.getDescription)
    val monitoredFields = Seq(role, description)
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(role, description)
      result.clear()

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
        validator.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords(),
          currentRoleId = Some(selectedRole.getId)
        )

      showFormFieldErrors(errors):
        case Validation.RoleRequired | Validation.DuplicateRole => role
        case Validation.DescriptionRequired  => description

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val updated = roleLogic.recordUpdate(currentRole())

          result.show(
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
          formRow(Labels.Role, role),
          formRow(Labels.Description, description),
        )
      )

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )