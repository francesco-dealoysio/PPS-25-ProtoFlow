package pkg.a.gui.views

import pkg.a.gui.structures.MenuAction
import pkg.a.gui.text.UiText.AuthorizationRules.Add as Text
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Menu
import pkg.a.gui.traits.Form
import pkg.b.logic.{AuthorizationEngine, Role}
import scalafx.scene.layout.BorderPane

object AuthorizationRuleAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val roleLogic = new Role()
    val roles = roleLogic.getRecords[Role]()
    val customizableActions = MenuAction.values.filter(Menu.labels.contains).toSeq

    val role = stringComboField(roles.map(_.getName.trim), prompt = Prompts.SelectRole)
    val action = stringComboField(customizableActions.map(Menu.labels), prompt = Prompts.SelectAction)
    val monitoredFields = Seq(role, action)
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def resetForm(): Unit =
      resetFields(monitoredFields*)
      clearErrors()
      role.requestFocus()

    var formSaved = false
    val save =
      saveButton: () =>
        clearErrors()

        val selectedRole = roles.find(_.getName.trim == role.value).map(_.getRole)
        val selectedAction = customizableActions.find(a => Menu.labels(a) == action.value)

        (selectedRole, selectedAction) match
          case (None, _) =>
            role.showError(Text.RoleRequired)
          case (_, None) =>
            action.showError(Text.ActionRequired)
          case (Some(roleCode), Some(menuAction)) =>
            val added = AuthorizationEngine.addCustomRule(roleCode, menuAction)
            result.show(
              message = if added then Text.Success else Text.AlreadyExists,
              success = added
            )
            if added then
              formSaved = true
              onSaved()

    val form =
      formGrid(
        Seq(
          formRow(Labels.required(Labels.Role), role),
          formRow(Labels.required(Labels.Action), action)
        )
      )

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(closeButton(onExit), resetButton(resetForm), save)),
      initialFocus = Some(role),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )