package pkg.a.gui.views

import pkg.a.gui.structures.MenuAction
import pkg.a.gui.text.UiText.AuthorizationRules.Management as Text
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Menu
import pkg.a.gui.traits.Management
import pkg.b.logic.AuthorizationEngine
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.BorderPane

object AuthorizationRulesManagementView extends Management:

  private case class CustomRule(role: String, action: MenuAction)

  def apply(onAdd: () => Unit = () => (), onExit: () => Unit = () => ()): BorderPane =

    val rules = ObservableBuffer.empty[CustomRule]

    val result = createResultMessage()

    val table = managementTable(rules, Text.Empty)

    table.columns ++= Seq(
      stringColumn[CustomRule](Text.RoleColumn)(_.role),
      stringColumn[CustomRule](Text.ActionColumn)(rule => Menu.labels(rule.action))
    )

    def loadRules(): Unit =
      loadTableItemsSafely(rules, result, Text.Empty, Text.Empty):
        AuthorizationEngine.listCustomRules().map(CustomRule.apply)

    def deleteSelectedRule(): Unit =
      withSelectedItem(table, result, Text.SelectToDelete): selected =>
        val confirmed =
          askConfirmation(
            titleText = Text.DeleteTitle,
            header = Text.DeleteConfirmation,
            content = s"${selected.role} → ${Menu.labels(selected.action)}"
          )

        if confirmed then
          val removed = AuthorizationEngine.removeCustomRule(selected.role, selected.action)
          if removed then
            loadRules()
            result.show(Text.deleted(selected.role, Menu.labels(selected.action)), success = true)
          else
            result.show(Text.DeleteError, success = false)

    clearResultOnSelection(table, result)

    val addButton = primaryButton(Buttons.Add, () =>
      result.clear()
      onAdd())

    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedRule)
    disableWithoutSelection(table, deleteButton)
    val bottomActions = actionBar(Seq(closeButton(onExit), deleteButton, addButton))

    val header = titleBox(Text.Title, Text.Subtitle)

    loadRules()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(header, table, result.label, bottomActions)
    )
