package pkg.a.gui.traits

import scalafx.Includes.*
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{Button, TableView}
import scalafx.scene.layout.{BorderPane, Priority, VBox}

trait Management extends Root:

  protected def selectedItem[T](table: TableView[T]): Option[T] =
    Option(
      table.selectionModel.value
        .selectedItem
        .value
    )

  protected def disableWithoutSelection[T](table: TableView[T], buttons: Button*): Unit =
    buttons.foreach: button =>
      button.disable <==
        table.selectionModel.value
          .selectedItem
          .isNull

  protected def clearResultOnSelection[T](table: TableView[T], result: ResultMessage): Unit =
    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>
          if selected != null then
            result.clear()

  protected def printButton(action: () => Unit, text: String = "Stampa"): Button =
    secondaryButton(text = text, action = action)

  protected def managementPage(
                                rootStyle: String,
                                pageChildren: Seq[Node],
                                growNode: Option[Node] = None,
                                spacingValue: Double = 18,
                                paddingValue: Insets = Insets(20)
                              ): BorderPane =

    val content =
      new VBox:
        spacing = spacingValue
        padding = paddingValue

        growNode.foreach: node =>
          VBox.setVgrow(node, Priority.Always)

        children = pageChildren

    new BorderPane:
      styleClass += rootStyle
      center = content