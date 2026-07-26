package pkg.a.gui.traits

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, ComboBox, Label, TextArea, TextField, TextInputControl}
import scalafx.scene.layout.*

trait Form extends Root:

  protected case class FormRow(label: String, field: Node, errorLabel: Label)

  protected def fieldErrorLabel(styleName: String = "field-error"): Label =
    new Label:
      visible = false
      managed = false
      wrapText = true
      styleClass += styleName

  protected def showFieldError(field: Node, errorLabel: Label, message: String): Unit =
    errorLabel.text = message
    errorLabel.visible = true
    errorLabel.managed = true

    field match
      case control: scalafx.scene.control.Control =>
        if !control.styleClass.contains("form-field-error") then
          control.styleClass += "form-field-error"
      case _ => ()

  protected def clearFieldErrors(fields: (Node, Label)*): Unit =
    fields.foreach:
      case (field, errorLabel) =>
        clearFieldError(field, errorLabel)

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

  protected def saveButton(onSave: () => Unit, text: String = "Salva"): Button =
    primaryButton(text, onSave)

  protected def resetButton(onReset: () => Unit, text: String = "Reset"): Button =
    secondaryButton(text, onReset)

  protected def showMappedErrors(errors: Seq[String])(mapping: PartialFunction[String, (TextInputControl, Label)]): Boolean =
    errors.foreach: error =>
      mapping.lift(error).foreach:
        case (field, errorLabel) =>
          showFieldError(field, errorLabel, error)
    errors.isEmpty

  protected def textField(prompt: String, initialText: String = ""): TextField =
    new TextField:
      text = initialText
      promptText = prompt
      maxWidth = Double.MaxValue
      styleClass += "form-field"

  protected def textArea(prompt: String, styleName: String, initialText: String = "", rows: Int = 5): TextArea =
    new TextArea:
      text = initialText
      promptText = prompt
      wrapText = true
      prefRowCount = rows
      maxWidth = Double.MaxValue
      styleClass += styleName

  protected def formPage(
                          titleText: String,
                          subtitleText: String,
                          titleStyle: String,
                          subtitleStyle: String,
                          rootStyle: String,
                          form: GridPane,
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

  protected def hasFormChanges(formSaved: Boolean, textFields: Seq[TextInputControl], comboBoxes: Seq[ComboBox[String]] = Seq.empty, initialValues: Seq[String] = Seq.empty): Boolean =
    if formSaved then
      false
    else
      val currentValues = formValues(textFields, comboBoxes)

      val baseline =
        if initialValues.nonEmpty then
          initialValues.map(_.trim)
        else
          Seq.fill(currentValues.size)("")

      currentValues != baseline

  private def formValues(textFields: Seq[TextInputControl], comboBoxes: Seq[ComboBox[String]] = Seq.empty): Seq[String] =
    textFields.map(_.text.value.trim) ++
      comboBoxes.map: combo =>
        Option(combo.value.value)
          .map(_.trim)
          .getOrElse("")

  private def clearFieldError(field: Node, errorLabel: Label): Unit =
    errorLabel.text = ""
    errorLabel.visible = false
    errorLabel.managed = false

    field match
      case control: scalafx.scene.control.Control =>
        control.styleClass.remove("form-field-error")
      case _ => ()

