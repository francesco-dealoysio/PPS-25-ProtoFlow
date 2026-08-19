package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Roles.Edit as Text
import pkg.a.gui.text.UiText.Validation.Role as Validation
import pkg.a.gui.validation.RoleValidator

object RoleEditView extends Form:

  def apply(selectedRole: Role, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val validator = new RoleValidator()

    val role = stringField("", selectedRole.getRole)
    role.control.setDisable(true)
    val name = stringField("", selectedRole.getName)
    val description = areaField("", DescriptionAreaStyle, selectedRole.getDescription)
    val monitoredFields = Seq(name, description)
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def currentRole(): Role =
      Role(
        id = selectedRole.getId,
        role = selectedRole.getRole,
        name = name.value,
        description = description.value
      )

    def resetForm(): Unit =
      resetFields(monitoredFields*)
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

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Labels.Role, role),
          formRow(Labels.RoleName, name),
          formRow(Labels.Description, description),
        )
      )

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      initialFocus = Some(role),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )