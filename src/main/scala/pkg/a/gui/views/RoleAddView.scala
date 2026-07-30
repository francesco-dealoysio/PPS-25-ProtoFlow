package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, Roles}

object RoleAddView extends Form:

  def apply(
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val roleLogic = new Role()
    val viewModel = new RoleViewModel()
    val role = stringField(Fields.Prompts.Role)
    val description = areaField(Fields.Prompts.Description, UiStyles.Roles.DescriptionArea)
    val resultMessage = messageLabel(UiStyles.Roles.Message)
    val monitoredFields = Seq(role, description)
    def currentRole(id: String = ""): Role =
      Role(
        id = id,
        role = role.value.toLowerCase,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(role, description)

      clearMessage(
        resultMessage,
        successStyle = UiStyles.Roles.MessageSuccess,
        errorStyle = UiStyles.Roles.MessageError
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        viewModel.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case RoleViewModel.RoleRequiredError | RoleViewModel.DuplicateRoleError =>
          role
        case RoleViewModel.DescriptionRequiredError =>
          description

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
            message =
              if saved then
                Roles.Add.Success
              else
                Roles.Add.Error,
            success = saved,
            successStyle = UiStyles.Roles.MessageSuccess,
            errorStyle = UiStyles.Roles.MessageError
          )
          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Fields.Labels.required(Fields.Labels.Role), role),
          formRow(Fields.Labels.required(Fields.Labels.Description), description)
        )
      )

    Platform.runLater:
      role.requestFocus()

    formPage(
      titleText = Roles.Add.Title,
      subtitleText = Roles.Add.Subtitle,
      titleStyle = UiStyles.Roles.Title,
      subtitleStyle = UiStyles.Roles.Subtitle,
      rootStyle = UiStyles.Roles.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )