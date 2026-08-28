package pkg.a.gui.traits

import pkg.a.gui.text.UiText.Common.Buttons.Print
import pkg.a.gui.text.UiStyles.Common.*
import pkg.d.util.Logger.logger
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Node
import pkg.a.gui.text.UiText.ArchivedDocuments.Management.NoFilterResults
import scalafx.scene.control.{Button, ComboBox, DatePicker, Label, TableColumn, TableView, TextField}
import scalafx.scene.layout.{BorderPane, HBox, Priority, VBox}

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

  protected def loadTableItemsSafely[T](items: ObservableBuffer[T], result: ResultMessage, emptyMessage: String, loadErrorMessage: String)(load: => Seq[T]): Unit =
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

  protected type FilterCriterion = (String, String, List[String])

  protected def dateCriterion(filter: DatePicker, field: String, operator: String): Option[FilterCriterion] =
    Option(filter.value.value)
      .map(date => (field, operator, List(date.toString)))

  protected def textCriterion(filter: TextField, field: String, operator: String): Option[FilterCriterion] =
    Option(filter.text.value)
      .map(_.trim)
      .filter(_.nonEmpty)
      .map(value => (field, operator, List(value)))

  protected def comboCriterion(filter: ComboBox[String], defaultValue: String, field: String, operator: String = "="): Option[FilterCriterion] =
    Option(filter.value.value)
      .filter(_ != defaultValue)
      .map(value => (field, operator, List(value)))

  protected def updateComboFilter[T](filter: ComboBox[String], defaultValue: String, elements: Seq[T])(valueOf: T => String): Unit =
    val values =
      elements
        .map(valueOf)
        .map(_.trim)
        .filter(_.nonEmpty)
        .distinct
        .sorted

    val currentSelection = filter.value.value

    filter.items = ObservableBuffer((defaultValue +: values) *)

    if currentSelection != null &&
      filter.items.value.contains(currentSelection)
    then
      filter.value = currentSelection
    else
      filter.value = defaultValue

  protected def filterBar(filters: Node*): HBox =
    new HBox:
      spacing = 10
      children = filters

  protected def showFilteredItems[T](items: ObservableBuffer[T], table: TableView[T], filteredItems: Seq[T], result: ResultMessage)(idOf: T => String): Unit =
    val sortedItems = filteredItems.sortBy(item => idOf(item).toIntOption.getOrElse(Int.MaxValue))

    items.setAll(sortedItems*)
    table.selectionModel.value.clearSelection()

    if sortedItems.isEmpty then
      result.show(NoFilterResults, success = true)


  protected def bindSearch(dateFilters: Seq[DatePicker] = Seq.empty, textFilters: Seq[TextField] = Seq.empty, comboFilters: Seq[ComboBox[String]] = Seq.empty)(search: () => Unit): Unit =
    dateFilters.foreach(_.value.onChange(search()))
    textFilters.foreach(_.text.onChange(search()))
    comboFilters.foreach(_.value.onChange(search()))

  protected def dateFilter(prompt: String = ""): DatePicker =
    new DatePicker:
      promptText = prompt

  protected def textFilter(prompt: String = ""): TextField =
    new TextField:
      promptText = prompt

  protected def comboFilter(defaultValue: String, values: Seq[String] = Seq.empty): ComboBox[String] =
    new ComboBox[String]:
      items = ObservableBuffer((defaultValue +: values) *)
      value = defaultValue

  private def selectedItem[T](table: TableView[T]): Option[T] =
    Option(table.selectionModel.value.selectedItem.value)