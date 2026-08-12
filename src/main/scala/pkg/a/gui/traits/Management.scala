package pkg.a.gui.traits

import pkg.a.gui.text.UiText.Common.Buttons.Print
import pkg.a.gui.text.UiStyles.Common.*
import pkg.d.util.Logger.logger
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label, TableColumn, TableView}
import scalafx.scene.layout.{BorderPane, Priority, VBox}

trait Management extends Common:

  protected def managementTable[T](items: ObservableBuffer[T], emptyText: String): TableView[T] =
    new TableView[T](items):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(emptyText)
      styleClass += TableStyle
      
  protected def stringColumn[T](title: String, widthColumn: Option[Double] = None)(value: T => String): TableColumn[T, String] =
    new TableColumn[T, String]:
      text = title
      widthColumn.foreach(prefWidth = _)
      cellValueFactory = cell =>
        StringProperty(value(cell.value))

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

  protected def printButton(action: () => Unit, text: String = Print): Button =
    secondaryButton(text = text, action = action)

  protected def withSelectedItem[T](table: TableView[T], result: ResultMessage, noSelectionMessage: String)(action: T => Unit): Unit =
    selectedItem(table) match
      case Some(selected) =>
        result.clear()
        action(selected)

      case None =>
        result.show(noSelectionMessage, success = false)

  protected def loadTableItemsSafely[T](table: TableView[T], items: ObservableBuffer[T], result: ResultMessage, emptyMessage: String, loadErrorMessage: String)(load: => Seq[T]): Unit =
    try
      val loadedItems = load
      items.clear()
      items ++= loadedItems
      if items.isEmpty then
        result.show(emptyMessage, success = false)
      else
        result.clear()
    catch
      case exception: Exception =>
        items.clear()
        result.show(loadErrorMessage, success = false)
        logger(exception)

  protected def managementPage(pageChildren: Seq[Node], growNode: Option[Node] = None, spacingValue: Double = 18, paddingValue: Insets = Insets(20), rootStyle: String = RootStyle): BorderPane =
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

  private def selectedItem[T](table: TableView[T]): Option[T] =
    Option(table.selectionModel.value.selectedItem.value)