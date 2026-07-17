package pkg.a.gui

import scalafx.geometry.Pos
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.layout.{HBox, VBox}

trait AppView:

  protected case class ResultMessage(label: Label, successStyle: String, errorStyle: String):

    def clear(): Unit =
      clearMessage(
        label = label,
        successStyle = successStyle,
        errorStyle = errorStyle
      )

    def show(message: String, success: Boolean): Unit =
      showMessage(
        label = label,
        message = message,
        success = success,
        successStyle = successStyle,
        errorStyle = errorStyle
      )

  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content

    dialog
      .showAndWait()
      .contains(ButtonType.OK)

  protected def createResultMessage(baseStyle: String, successStyle: String, errorStyle: String): ResultMessage =
    ResultMessage(
      label = messageLabel(baseStyle),
      successStyle = successStyle,
      errorStyle = errorStyle
    )

  protected def closeButton(onExit: () => Unit, text: String = "Chiudi"): Button =
    new Button(text):
      styleClass += "secondary-button"
      onAction = _ => onExit()

  protected def fieldLabel(text: String, styleName: String = "form-label"): Label =
    new Label(text):
      styleClass += styleName

  protected def messageLabel(baseStyle: String): Label =
    new Label:
      visible = false
      managed = false
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += baseStyle

  protected def showMessage(label: Label, message: String, success: Boolean, successStyle: String, errorStyle: String): Unit =
    label.text = message
    label.visible = true
    label.managed = true
    label.styleClass.removeAll(successStyle, errorStyle)
    label.styleClass += (if success then successStyle else errorStyle)

  protected def clearMessage(label: Label, successStyle: String, errorStyle: String): Unit =
    label.text = ""
    label.visible = false
    label.managed = false

    label.styleClass.removeAll(successStyle, errorStyle)

  protected def actionBar(buttons: Button*): HBox =
    new HBox:
      spacing = 12
      alignment = Pos.CenterRight
      children = buttons

  protected def primaryButton(text: String, action: () => Unit): Button =
    new Button(text):
      styleClass += "primary-button"
      onAction = _ => action()

  protected def secondaryButton(text: String, action: () => Unit): Button =
    new Button(text):
      styleClass += "secondary-button"
      onAction = _ => action()

  protected def dangerButton(text: String, action: () => Unit): Button =
    new Button(text):
      styleClass += "danger-button"
      onAction = _ => action()

  protected def titleBox(titleText: String, subtitleText: String, titleStyle: String, subtitleStyle: String): VBox =
    new VBox:
      spacing = 5
      children = Seq(
        new Label(titleText):
          styleClass += titleStyle,
        new Label(subtitleText):
          wrapText = true
          styleClass += subtitleStyle
        )
