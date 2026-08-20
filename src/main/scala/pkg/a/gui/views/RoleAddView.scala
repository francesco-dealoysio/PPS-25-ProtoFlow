package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Roles.Add as Text
import pkg.a.gui.text.UiText.Validation.Role as Validation
import pkg.a.gui.validation.RoleValidator

object RoleAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val validator = new RoleValidator()
    val role = stringField(prompt = Prompts.Role)
    val name = stringField(prompt = Prompts.RoleName)
    val description = areaField(prompt = Prompts.Description)
    val result = createResultMessage()
    val monitoredFields = Seq(role, description)

    def currentRole(id: String = ""): Role =
      Role(
        id = id,
        role = role.value.toLowerCase,
        name = name.value,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        validator.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case Validation.RoleRequired | Validation.DuplicateRole => role
        case Validation.DescriptionRequired => description

    def resetForm(): Unit =
      resetFields(monitoredFields*)
      clearErrors()
      role.requestFocus()

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val newRole = currentRole(IdGen(inIdsFilePathName("roleId")))
          val saved = roleLogic.recordInsert(newRole)

          result.show(
            message = if saved then Text.Success else Text.Error,
            success = saved
          )
          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Labels.required(Labels.Role), role),
          formRow(Labels.required(Labels.Description), description)
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