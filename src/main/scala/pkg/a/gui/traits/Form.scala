package pkg.a.gui.traits

import pkg.a.gui.navigation.HomeNavigator.ViewNavigationState
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, ComboBox, DatePicker, Label, PasswordField, ScrollPane, TextArea, TextField, TextInputControl}
import scalafx.scene.layout.*
import pkg.d.util.DateTime
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Common.Buttons.*
import scalafx.application.Platform

/**
 * Provides reusable components and utilities for building and managing forms,
 * including fields, validation feedback, layout, and form state.
 */
trait Form extends Common:

  /**
   * Represents a row of a form.
   * @param label      the text describing the field
   * @param field      the form control
   * @param errorLabel the label used to display validation errors
   */
  protected case class FormRow(label: String, field: Node, errorLabel: Label)

  /**
   * Wraps a form control together with its validation and state management.
   * @param control      the underlying UI control
   * @param errorLabel   the label used to display validation errors
   * @param initialValue the initial value of the field
   * @param readValue    the function used to read the control value
   * @param writeValue   the function used to update the control value
   * @tparam C the type of the wrapped control
   */
  protected case class FormField[C <: Node](control: C, errorLabel: Label, initialValue: String, readValue: C => String, writeValue: (C, String) => Unit):
    /**
     * Returns the current trimmed value of the field.
     */
    def value: String =
      readValue(control).trim

    /**
     * Restores the field to its initial value.
     */
    def reset(): Unit =
      writeValue(control, initialValue)

    /**
     * Checks whether the current value differs from the initial one.
     */
    def hasChanged: Boolean =
      value != initialValue.trim

    /**
     * Requests focus for the underlying form control.
     */
    def requestFocus(): Unit =
      control.requestFocus()

    /**
     * Displays a validation error and applies the error style to the control.
     * @param message the validation error to display
     */
    def showError(message: String): Unit =
      errorLabel.text = message
      errorLabel.visible = true
      errorLabel.managed = true
      if !control.styleClass.contains(FormFieldErrorStyle) then
        control.styleClass += FormFieldErrorStyle

    /**
     * Clears the validation error and removes the error style from the control.
     */
    def clearError(): Unit =
      errorLabel.text = ""
      errorLabel.visible = false
      errorLabel.managed = false
      control.styleClass.remove(FormFieldErrorStyle)

  /**
   * Restores the given fields to their initial values.
   * @param fields the fields to reset
   */
  protected def resetFields(fields: FormField[? <: Node]*): Unit =
    fields.foreach(_.reset())

  /**
   * Checks whether an unsaved form contains modified fields.
   * @param formSaved whether the form has already been saved
   * @param fields    the fields to check
   * @return true if the form has unsaved changes
   */
  protected def hasFormChanges(formSaved: Boolean, fields: Seq[FormField[? <: Node]]): Boolean =
    !formSaved && fields.exists(_.hasChanged)

  /**
   * Creates a form row from a label and a form field.
   * @param label the text describing the field
   * @param field the field displayed in the row
   * @return the configured form row
   */
  protected def formRow(label: String, field: FormField[? <: Node]): FormRow =
    FormRow(
      label = label,
      field = field.control,
      errorLabel = field.errorLabel
    )

  /**
   * Clears validation errors from the given fields.
   * @param fields the fields whose errors are cleared
   */
  protected def clearFormFieldErrors(fields: FormField[? <: Node]*): Unit =
    fields.foreach(_.clearError())

  /**
   * Creates a text form field.
   * @param initialValue the initial field value
   * @param prompt       the placeholder text
   * @return the configured text field
   */
  protected def stringField(initialValue: String = "", prompt: String = ""): FormField[TextField] =
    formField(textField(prompt, initialValue), initialValue)(
      readValue = _.text.value,
      writeValue = (field, value) => field.text = value
    )

  /**
   * Creates a multiline text form field.
   * @param initialValue the initial field value
   * @param styleName    the style applied to the text area
   * @param prompt       the placeholder text
   * @param rows         the preferred number of text rows
   * @return the configured text area field
   */
  protected def areaField(initialValue: String = "", styleName: String = DescriptionAreaStyle, prompt: String = "", rows: Int = 5): FormField[TextArea] =
    formField(textArea(prompt, styleName, initialValue, rows), initialValue)(
      readValue = _.text.value,
      writeValue = (field, value) => field.text = value
    )

  /**
   * Creates a password form field.
   * @param prompt       the placeholder text
   * @param initialValue the initial field value
   * @return the configured password field
   */
  protected def passwordFormField(prompt: String, initialValue: String = ""): FormField[PasswordField] =
    val control =
      new PasswordField:
        text = initialValue
        promptText = prompt
        maxWidth = Double.MaxValue
        styleClass += FormFieldStyle
    formField(control, initialValue)(
      readValue = _.text.value,
      writeValue = (field, value) => field.text = value
    )

  /**
   * Creates a password form field.
   * @param prompt       the placeholder text
   * @param initialValue the initial field value
   * @return the configured password field
   */
  protected def stringComboField(items: Seq[String], initialValue: String = "", prompt: String = ""): FormField[ComboBox[String]] =
    val control =
      new ComboBox[String](items):
        if initialValue.nonEmpty then
          value = initialValue

        promptText = prompt
        maxWidth = Double.MaxValue
        styleClass += FormFieldStyle

    formField(control, initialValue)(
      readValue = combo =>
        Option(combo.value.value).getOrElse(""),
      writeValue = (combo, value) =>
        combo.value = if value.isBlank then null else value
    )

  /**
   * Creates a grid containing the given form rows.
   * @param rows the rows displayed in the form
   * @return the configured form grid
   */
  protected def formGrid(rows: Seq[FormRow]): GridPane =
    new GridPane:
      hgap = 16
      vgap = 8
      maxWidth = 700

      columnConstraints = Seq(
        new ColumnConstraints:
          minWidth = 130,
        new ColumnConstraints:
          hgrow = Priority.Always
        )

      rows.zipWithIndex.foreach:
        case (row, index) =>
          val fieldRow = index * 2
          val label = fieldLabel(row.label)
          label.wrapText = true

          add(label, 0, fieldRow)
          add(row.field, 1, fieldRow)
          add(row.errorLabel, 1, fieldRow + 1)

  /**
   * Creates a two-column layout containing the given form grids.
   * @param left  the form grid displayed in the left column
   * @param right the form grid displayed in the right column
   * @return the configured two-column form layout
   */
  protected def twoColumnForm(left: GridPane, right: GridPane): HBox =
    left.maxWidth = Double.MaxValue
    right.maxWidth = Double.MaxValue

    val box =
      new HBox:
        spacing = 30
        alignment = Pos.TopCenter
        children = Seq(left, right)

    HBox.setHgrow(left, Priority.Always)
    HBox.setHgrow(right, Priority.Always)
    box

  /**
   * Creates a button used to save a form.
   * @param onSave the action executed when the button is pressed
   * @param text   the button text
   */
  protected def saveButton(onSave: () => Unit, text: String = Save): Button =
    primaryButton(text, onSave)

  /**
   * Creates a button used to reset a form.
   * @param onReset the action executed when the button is pressed
   * @param text    the button text
   */
  protected def resetButton(onReset: () => Unit, text: String = Reset): Button =
    secondaryButton(text, onReset)

  /**
   * Displays validation errors on their associated form fields.
   * @param errors  the validation errors to display
   * @param mapping the mapping between errors and form fields
   * @return true if no validation errors are present
   */
  protected def showFormFieldErrors(errors: Seq[String])(mapping: PartialFunction[String, FormField[? <: Node]]): Boolean =
    errors.foreach: error =>
      mapping
        .lift(error)
        .foreach(_.showError(error))
    errors.isEmpty

  /**
   * Creates a date form field.
   * @param initialValue the initial date value
   * @return the configured date field
   */
  protected def dateField(initialValue: String): FormField[DatePicker] =
    val control =
      new DatePicker(DateTime.parseDate(initialValue)):
        maxWidth = Double.MaxValue
        styleClass += FormFieldStyle
  
    formField(control, initialValue)(
      readValue = picker =>
        Option(picker.value.value)
          .map(_.toString)
          .getOrElse(""),
      writeValue = (picker, value) =>
        picker.value =
          if value.isBlank then null
          else DateTime.parseDate(value)
    )

  /**
   * Creates a date form field.
   * @param initialValue the initial date value
   * @return the configured date field
   */
  protected def readOnlyStringField(initialValue: String = ""): FormField[TextField] =
    makeReadOnly(stringField(initialValue))

  /**
   * Creates a read-only multiline text form field.
   * @param initialValue the field value
   * @param styleName    the style applied to the text area
   * @param rows         the preferred number of text rows
   * @return the configured read-only text area field
   */
  protected def readOnlyAreaField(initialValue: String = "", styleName: String = DescriptionAreaStyle, rows: Int = 5): FormField[TextArea] =
    makeReadOnly(areaField(initialValue, styleName, rows = rows))

  /**
   * Defines the textual content and styles of a form page header.
   * @param title the page title
   * @param subtitle the page subtitle
   * @param titleStyle the style applied to the title
   * @param subtitleStyle the style applied to the subtitle
   */
  protected case class FormHeader(title: String, subtitle: String, titleStyle: String = TitleStyle, subtitleStyle: String = SubtitleStyle)

  /**
   * Defines the layout and style configuration of a form page.
   * @param rootStyle       the style applied to the page root
   * @param contentStyle    the optional style applied to the page content
   * @param actionsAtBottom whether actions are placed at the bottom of the page
   */
  protected case class PageConfig(rootStyle: String = RootStyle, contentStyle: Option[String] = None, actionsAtBottom: Boolean = true)

  /**
   * Builds a complete form page with header, content, feedback, and actions.
   * @param header the page header configuration
   * @param form the form content
   * @param resultMessage the label used to display operation feedback
   * @param actions the available form actions
   * @param initialFocus the field that receives focus when the page is opened
   * @param hasUnsavedChanges the function used to detect unsaved changes
   * @param config the page layout and style configuration
   * @return the configured form page
   */
  protected def formPage(
                          header: FormHeader,
                          form: Node,
                          resultMessage: Label,
                          actions: HBox,
                          initialFocus: Option[FormField[? <: Node]] = None,
                          hasUnsavedChanges: () => Boolean = () => false,
                          config: PageConfig = PageConfig()
                        ): BorderPane =


    val titleBoxNode = titleBox(
      titleText = header.title,
      subtitleText = header.subtitle,
      titleStyle = header.titleStyle,
      subtitleStyle = header.subtitleStyle
    )

    val spacer = new Region
    VBox.setVgrow(spacer, Priority.Always)

    val pageChildren =
      if config.actionsAtBottom then
        Seq(titleBoxNode, form, resultMessage, spacer, actions)
      else
        Seq(titleBoxNode, form, resultMessage, actions)

    val formContent = new VBox:
      spacing = 20
      padding = Insets(25)
      maxWidth = 800
      maxHeight = Double.MaxValue
      config.contentStyle.foreach(styleClass += _)
      children = pageChildren

    val stack = new StackPane:
      alignment = Pos.TopCenter
      children = Seq(formContent)

    // Content taller than the window would otherwise be clipped (no scrollbar), hiding
    // fields and, worse, the action buttons: wrap in a ScrollPane instead of using the
    // stack directly. fitToWidth/fitToHeight keep the short-content behavior unchanged
    // (centered, actions pushed to the bottom via the spacer) while letting tall content
    // scroll instead of overflow.
    val scrollPane = new ScrollPane:
      content = stack
      fitToWidth = true
      fitToHeight = true
      styleClass += FormScrollStyle

    val page = new BorderPane:
      styleClass += config.rootStyle
      center = scrollPane

    page.delegate.setUserData(ViewNavigationState(hasUnsavedChanges))
    initialFocus.foreach(focusOnOpen)
    page

  private def makeReadOnly[C <: TextInputControl](field: FormField[C]): FormField[C] =
    field.control.editable = false
    field.control.focusTraversable = false
    field.control.styleClass += ReadOnlyFormFieldStyle
    field

  private def focusOnOpen(field: FormField[? <: Node]): Unit =
    Platform.runLater:
      field.requestFocus()

  private def formField[C <: Node](control: C, initialValue: String)(readValue: C => String, writeValue: (C, String) => Unit): FormField[C] =
    FormField(
      control = control,
      errorLabel = fieldErrorLabel(),
      initialValue = initialValue,
      readValue = readValue,
      writeValue = writeValue
    )

  private def textArea(prompt: String, styleName: String, initialText: String = "", rows: Int = 5): TextArea =
    new TextArea:
      text = initialText
      promptText = prompt
      wrapText = true
      prefRowCount = rows
      maxWidth = Double.MaxValue
      styleClass += styleName

  private def textField(prompt: String, initialText: String = ""): TextField =
    new TextField:
      text = initialText
      promptText = prompt
      maxWidth = Double.MaxValue
      styleClass += FormFieldStyle

  private def fieldErrorLabel(styleName: String = FieldErrorStyle): Label =
    new Label:
      visible = false
      managed = false
      wrapText = true
      styleClass += styleName