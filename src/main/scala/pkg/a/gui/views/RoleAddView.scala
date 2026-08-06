package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Roles.Add as Text
import pkg.a.gui.text.UiText.Validation.Role as Validation

object RoleAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val viewModel = new RoleViewModel()
    val role = stringField(Prompts.Role)
    val description = areaField(Prompts.Description, DescriptionAreaStyle)
    val resultMessage = messageLabel(MessageStyle)
    val monitoredFields = Seq(role, description)
    def currentRole(id: String = ""): Role =
      Role(
        id = id,
        role = role.value.toLowerCase,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(role, description)
      clearMessage(resultMessage)

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        viewModel.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case Validation.RoleRequired | Validation.DuplicateRole => role
        case Validation.DescriptionRequired => description

    def resetForm(): Unit =
      resetFields(role, description)
      clearErrors()
      role.requestFocus()

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val newRole = currentRole(IdGen(inIdsFilePathName("roleId")))
          val saved = roleLogic.recordInsert(newRole)

          showMessage(
            label = resultMessage,
            message = if saved then Text.Success else Text.Error,
            success = saved,
            successStyle = MessageSuccessStyle,
            errorStyle = MessageErrorStyle
          )
          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Labels.required(Labels.Role), role),
          formRow(Labels.required(Labels.Description), description)
        )
      )

    Platform.runLater:
      role.requestFocus()

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )