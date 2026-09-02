package pkg.a.gui.traits

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

/**
 * Provides reusable components and utilities for management views,
 * including tables, filters, selection handling, and data loading.
 */
trait Management extends Common:

  /**
   * Creates a table used in management views.
   * @param items     the items displayed in the table
   * @param emptyText the text displayed when the table is empty
   * @tparam T the type of the table items
   * @return the configured table
   */
  protected def managementTable[T](items: ObservableBuffer[T], emptyText: String): TableView[T] =
    new TableView[T](items):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      placeholder = new Label(emptyText)
      styleClass += TableStyle

  /**
   * Creates a table column displaying string values.
   * @param title       the column title
   * @param widthColumn the optional preferred column width
   * @param value       the function used to extract the displayed value
   * @tparam T the type of the table items
   * @return the configured table column
   */
  protected def stringColumn[T](title: String, widthColumn: Option[Double] = None)(value: T => String): TableColumn[T, String] =
    new TableColumn[T, String]:
      text = title
      widthColumn.foreach(prefWidth = _)
      cellValueFactory = cell =>
        StringProperty(value(cell.value))

  /**
   * Disables the given buttons when no table item is selected.
   * @param table   the table whose selection is observed
   * @param buttons the buttons whose state is updated
   * @tparam T the type of the table items
   */
  protected def disableWithoutSelection[T](table: TableView[T], buttons: Button*): Unit =
    buttons.foreach: button =>
      button.disable <==
        table.selectionModel.value
          .selectedItem
          .isNull

  /**
   * Clears the result message when a table item is selected.
   * @param table  the table whose selection is observed
   * @param result the result message to clear
   * @tparam T the type of the table items
   */
  protected def clearResultOnSelection[T](table: TableView[T], result: ResultMessage): Unit =
    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>
          if selected != null then
            result.clear()

  /**
   * Executes an action on the currently selected table item.
   * Displays an error message when no item is selected.
   * @param table              the table containing the selectable items
   * @param result             the result message used for feedback
   * @param noSelectionMessage the message displayed when no item is selected
   * @param action             the action executed on the selected item
   * @tparam T the type of the table items
   */
  protected def withSelectedItem[T](table: TableView[T], result: ResultMessage, noSelectionMessage: String)(action: T => Unit): Unit =
    selectedItem(table) match
      case Some(selected) =>
        result.clear()
        action(selected)

      case None =>
        result.show(noSelectionMessage, success = false)

  /**
   * Loads table items while handling empty results and loading failures.
   * @param items            the buffer receiving the loaded items
   * @param result           the result message used for feedback
   * @param emptyMessage     the message displayed when no items are loaded
   * @param loadErrorMessage the message displayed when loading fails
   * @param load             the operation used to load the items
   * @tparam T the type of the loaded items
   */
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

  /**
   * Builds the common layout of a management page.
   * @param pageChildren the nodes displayed in the page
   * @param growNode     the optional node allowed to grow vertically
   * @param spacingValue the spacing between page elements
   * @param paddingValue the page padding
   * @param rootStyle    the style applied to the page root
   * @return the configured management page
   */
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

  /**
   * Represents a filtering criterion composed of a field,
   * an operator, and the values used by the filter.
   */
  protected type FilterCriterion = (String, String, List[String])

  /**
   * Creates a filter criterion from a selected date.
   * @param filter   the date filter
   * @param field    the field to filter
   * @param operator the comparison operator
   * @return the criterion if a date is selected
   */
  protected def dateCriterion(filter: DatePicker, field: String, operator: String): Option[FilterCriterion] =
    Option(filter.value.value)
      .map(date => (field, operator, List(date.toString)))

  /**
   * Creates a filter criterion from a non-empty text value.
   * @param filter   the text filter
   * @param field    the field to filter
   * @param operator the comparison operator
   * @return the criterion if the filter contains a value
   */
  protected def textCriterion(filter: TextField, field: String, operator: String): Option[FilterCriterion] =
    Option(filter.text.value)
      .map(_.trim)
      .filter(_.nonEmpty)
      .map(value => (field, operator, List(value)))

  /**
   * Creates a filter criterion from the selected combo box value.
   * @param filter       the combo box filter
   * @param defaultValue the value representing no active filter
   * @param field        the field to filter
   * @param operator     the comparison operator
   * @return the criterion if a non-default value is selected
   */
  protected def comboCriterion(filter: ComboBox[String], defaultValue: String, field: String, operator: String = "="): Option[FilterCriterion] =
    Option(filter.value.value)
      .filter(_ != defaultValue)
      .map(value => (field, operator, List(value)))

  /**
   * Updates the available values of a combo box filter while preserving
   * the current selection when possible.
   * @param filter       the combo box to update
   * @param defaultValue the default filter value
   * @param elements     the elements used to derive the available values
   * @param valueOf      the function used to extract a value from each element
   * @tparam T the type of the source elements
   */
  protected def updateComboFilter[T](filter: ComboBox[String], defaultValue: String, elements: Seq[T])(valueOf: T => String): Unit =
    val values =
      elements
        .map(valueOf)
        .map(_.trim)
        .filter(_.nonEmpty)
        .distinct
        .sorted

    val currentSelection = filter.value.value

    filter.items = ObservableBuffer(defaultValue +: values *)

    if currentSelection != null &&
      filter.items.value.contains(currentSelection)
    then
      filter.value = currentSelection
    else
      filter.value = defaultValue

  /**
   * Creates a horizontal container for the given filters.
   * @param filters the filter controls displayed in the bar
   * @return the configured filter bar
   */
  protected def filterBar(filters: Node*): HBox =
    new HBox:
      spacing = 10
      children = filters

  /**
   * Displays filtered items in the table and clears the current selection.
   * @param items         the buffer containing the displayed items
   * @param table         the table displaying the items
   * @param filteredItems the items resulting from the filtering operation
   * @param result        the result message used for feedback
   * @param idOf          the function used to extract the item identifier for sorting
   * @tparam T the type of the table items
   */
  protected def showFilteredItems[T](items: ObservableBuffer[T], table: TableView[T], filteredItems: Seq[T], result: ResultMessage)(idOf: T => String): Unit =
    val sortedItems = filteredItems.sortBy(item => idOf(item).toIntOption.getOrElse(Int.MaxValue))

    items.setAll(sortedItems*)
    table.selectionModel.value.clearSelection()

    if sortedItems.isEmpty then
      result.show(NoFilterResults, success = true)

  /**
   * Executes the search action whenever one of the given filters changes.
   * @param dateFilters  the date filters to observe
   * @param textFilters  the text filters to observe
   * @param comboFilters the combo box filters to observe
   * @param search       the search action to execute
   */
  protected def bindSearch(dateFilters: Seq[DatePicker] = Seq.empty, textFilters: Seq[TextField] = Seq.empty, comboFilters: Seq[ComboBox[String]] = Seq.empty)(search: () => Unit): Unit =
    dateFilters.foreach(_.value.onChange(search()))
    textFilters.foreach(_.text.onChange(search()))
    comboFilters.foreach(_.value.onChange(search()))

  /**
   * Creates a date filter.
   * @param prompt the placeholder text
   * @return the configured date filter
   */
  protected def dateFilter(prompt: String = ""): DatePicker =
    new DatePicker:
      promptText = prompt

  /**
   * Creates a text filter.
   * @param prompt the placeholder text
   * @return the configured text filter
   */
  protected def textFilter(prompt: String = ""): TextField =
    new TextField:
      promptText = prompt

  /**
   * Creates a combo box filter with a default value.
   * @param defaultValue the value representing no active filter
   * @param values       the available filter values
   * @return the configured combo box filter
   */
  protected def comboFilter(defaultValue: String, values: Seq[String] = Seq.empty): ComboBox[String] =
    new ComboBox[String]:
      items = ObservableBuffer(defaultValue +: values *)
      value = defaultValue

  private def selectedItem[T](table: TableView[T]): Option[T] =
    Option(table.selectionModel.value.selectedItem.value)