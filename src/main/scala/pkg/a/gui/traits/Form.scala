package pkg.a.gui.traits

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, ComboBox, DatePicker, Label, PasswordField, TextArea, TextField}
import scalafx.scene.layout.*
import pkg.d.util.DateTime
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Common.Buttons.*

trait Form extends Common:

  protected case class FormRow(label: String, field: Node, errorLabel: Label)

  protected case class FormField[C <: Node](control: C, errorLabel: Label, initialValue: String, readValue: C => String, writeValue: (C, String) => Unit):
    def value: String =
      readValue(control).trim

    def reset(): Unit =
      writeValue(control, initialValue)

    def hasChanged: Boolean =
      value != initialValue.trim

    def requestFocus(): Unit =
      control.requestFocus()

    def showError(message: String): Unit =
      errorLabel.text = message
      errorLabel.visible = true
      errorLabel.managed = true

      control match
        case styledControl: scalafx.scene.control.Control =>
          if !styledControl.styleClass.contains(FormFieldErrorStyle) then
            styledControl.styleClass += FormFieldErrorStyle

        case _ =>
          ()

    def clearError(): Unit =
      errorLabel.text = ""
      errorLabel.visible = false
      errorLabel.managed = false

      control match
        case styledControl: scalafx.scene.control.Control =>
          styledControl.styleClass.remove(FormFieldErrorStyle)

        case _ =>
          ()

  protected def resetFields(fields: FormField[? <: Node]*): Unit =
    fields.foreach(_.reset())

  protected def hasFormChanges(formSaved: Boolean, fields: Seq[FormField[? <: Node]]): Boolean =
    !formSaved && fields.exists(_.hasChanged)

  protected def formRow(label: String, field: FormField[? <: Node]): FormRow =
    FormRow(
      label = label,
      field = field.control,
      errorLabel = field.errorLabel
    )

  protected def clearFormFieldErrors(fields: FormField[? <: Node]*): Unit =
    fields.foreach(_.clearError())

  protected def stringField(prompt: String = "", initialValue: String = ""): FormField[TextField] =
    formField(textField(prompt, initialValue), initialValue)(
      readValue = _.text.value,
      writeValue = (field, value) => field.text = value
    )

  protected def areaField(prompt: String, styleName: String, initialValue: String = "", rows: Int = 5): FormField[TextArea] =
    formField(textArea(prompt, styleName, initialValue, rows), initialValue)(
      readValue = _.text.value,
      writeValue = (field, value) => field.text = value
    )

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

  protected def stringComboField(items: Seq[String], prompt: String, initialValue: String = ""): FormField[ComboBox[String]] =
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

          add(fieldLabel(row.label), 0, fieldRow)
          add(row.field, 1, fieldRow)
          add(row.errorLabel, 1, fieldRow + 1)

  protected def saveButton(onSave: () => Unit, text: String = Save): Button =
    primaryButton(text, onSave)

  protected def resetButton(onReset: () => Unit, text: String = Reset): Button =
    secondaryButton(text, onReset)

  protected def showFormFieldErrors(errors: Seq[String])(mapping: PartialFunction[String, FormField[? <: Node]]): Boolean =
    errors.foreach: error =>
      mapping
        .lift(error)
        .foreach(_.showError(error))
    errors.isEmpty

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

  protected def formPage(
                          titleText: String,
                          subtitleText: String,
                          titleStyle: String = TitleStyle,
                          subtitleStyle: String = SubtitleStyle,
                          rootStyle: String = RootStyle,
                          form: Node,
                          resultMessage: Label,
                          actions: HBox,
                          contentStyle: Option[String] = None,
                          hasUnsavedChanges: () => Boolean = () => false,
                          actionsAtBottom: Boolean = true
                        ): BorderPane =

    val header =
      titleBox(
        titleText = titleText,
        subtitleText = subtitleText,
        titleStyle = titleStyle,
        subtitleStyle = subtitleStyle
      )
    val spacer = new Region
    VBox.setVgrow(spacer, Priority.Always)
    val pageChildren =
      if actionsAtBottom then
        Seq(header, form, resultMessage, spacer, actions)
      else
        Seq(header, form, resultMessage, actions)
    val content =
      new VBox:
        spacing = 20
        padding = Insets(25)
        maxWidth = 800
        maxHeight = Double.MaxValue
        contentStyle.foreach(styleClass += _)
        children = pageChildren

    val page = new BorderPane:
      styleClass += rootStyle
      center =
        new StackPane:
          alignment = Pos.TopCenter
          children = Seq(content)

    page.delegate
      .getProperties
      .put("has-unsaved-changes", hasUnsavedChanges)
    page

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