package pkg.a.gui.views

import pkg.a.gui.structures.RoleViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform

import scalafx.scene.control.{TextArea, TextField}
import scalafx.scene.layout.BorderPane

object RoleAddView extends Form:

  def apply(
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val roleLogic = new Role()
    val viewModel = new RoleViewModel()

    val roleField = textField("Inserisci il ruolo")
    val descriptionArea = textArea(prompt = "Inserisci la descrizione", styleName = "role-description-area")
    val roleError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()
    val resultMessage = messageLabel("roles-message")
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
                "Ruolo inserito correttamente."
              else
                "Errore durante l'inserimento del ruolo.",
            success = saved,
            successStyle = "roles-message-success",
            errorStyle = "roles-message-error"
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
      titleText = "Aggiunta ruolo",
      subtitleText = "Inserisci i dati del nuovo ruolo.",
      titleStyle = "roles-title",
      subtitleStyle = "roles-subtitle",
      rootStyle = "roles-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )