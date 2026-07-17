package pkg.a.gui

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.*

trait FormView extends AppView:

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

  protected def formPage(
                          titleText: String,
                          subtitleText: String,
                          titleStyle: String,
                          subtitleStyle: String,
                          rootStyle: String,
                          form: GridPane,
                          resultMessage: Label,
                          actions: HBox
                        ): BorderPane =

    val header =
      titleBox(
        titleText = titleText,
        subtitleText = subtitleText,
        titleStyle = titleStyle,
        subtitleStyle = subtitleStyle
      )

    val content =
      new VBox:
        spacing = 20
        padding = Insets(25)
        maxWidth = 800
        children = Seq(
          header,
          form,
          resultMessage,
          actions
        )

    new BorderPane:
      styleClass += rootStyle
      center =
        new StackPane:
          alignment = Pos.TopCenter
          children = Seq(content)

  private def clearFieldError(field: Node, errorLabel: Label): Unit =
    errorLabel.text = ""
    errorLabel.visible = false
    errorLabel.managed = false

    field match
      case control: scalafx.scene.control.Control =>
        control.styleClass.remove("form-field-error")
      case _ => ()