package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.Roles.Edit as Text
import pkg.a.gui.text.UiText.Validation.Role as Validation
import pkg.a.gui.validation.RoleValidator

object RoleEditView extends Form:

  def apply(selectedRole: Role, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val validator = new RoleValidator()

    val role = readOnlyStringField(selectedRole.getRole)
    val name = stringField(selectedRole.getName)
    val description = areaField(selectedRole.getDescription, DescriptionAreaStyle)
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
      name.requestFocus()

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
        case Validation.NameRequired | Validation.DuplicateRoleName => name
        case Validation.DescriptionRequired  => description

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val updated = roleLogic.recordUpdate(currentRole())
          if updated then
            formSaved = true
            showSuccess(Text.Title, Text.Success)
            onSaved()
          else
            result.show(Text.Error, success = false)

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
      initialFocus = Some(name),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )