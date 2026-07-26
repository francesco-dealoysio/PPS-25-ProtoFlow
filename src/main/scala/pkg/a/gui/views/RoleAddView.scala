package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform
import scalafx.scene.control.{TextArea, TextField}
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
    val styles = UiStyles.Roles
    val roleField = textField(Fields.Prompts.Role)
    val descriptionArea = textArea(Fields.Prompts.Description, styleName = "role-description-area")
    val roleError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()
    val resultMessage = messageLabel(styles.message)
    val monitoredFields = Seq(roleField, descriptionArea)
    def currentRole(id: String = ""): Role =
      Role(
        id = id,
        role = roleField.text.value.trim.toLowerCase,
        description = descriptionArea.text.value.trim
      )

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

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        viewModel.validate(
          role = currentRole(),
          existingRoles = roleLogic.getRecords()
        )

      showMappedErrors(errors):
        case RoleViewModel.RoleRequiredError |
             RoleViewModel.DuplicateRoleError =>
          roleField -> roleError

        case RoleViewModel.DescriptionRequiredError =>
          descriptionArea -> descriptionError

    def resetForm(): Unit =
      roleField.clear()
      descriptionArea.clear()
      clearErrors()

      roleField.requestFocus()


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
            successStyle = styles.messageSuccess,
            errorStyle = styles.messageError
          )
          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          FormRow("Ruolo *", roleField, roleError),
          FormRow("Descrizione *", descriptionArea, descriptionError)
        )
      )

    Platform.runLater {
      roleField.requestFocus()
    }

    formPage(
      titleText = Roles.Add.Title,
      subtitleText = Roles.Add.Subtitle,
      titleStyle = styles.title,
      subtitleStyle = styles.subtitle,
      rootStyle = styles.root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )