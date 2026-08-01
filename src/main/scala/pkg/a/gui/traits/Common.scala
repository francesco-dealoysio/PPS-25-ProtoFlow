package pkg.a.gui.traits

import scalafx.geometry.Pos
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.layout.{HBox, VBox}
import pkg.a.gui.text.UiStyles

trait Common:

  protected case class ResultMessage(label: Label, successStyle: String, errorStyle: String):

    def clear(): Unit =
      clearMessage(label, successStyle, errorStyle)

    def show(message: String, success: Boolean): Unit =
      showMessage(label, message, success, successStyle, errorStyle)

  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content
    dialog.showAndWait().contains(ButtonType.OK)

  protected def createResultMessage(baseStyle: String = UiStyles.Common.Message, successStyle: String = UiStyles.Common.MessageSuccess, errorStyle: String = UiStyles.Common.MessageError): ResultMessage =
    ResultMessage(messageLabel(baseStyle), successStyle, errorStyle)

  protected def fieldLabel(text: String, styleName: String = "form-label"): Label =
    new Label(text):
      styleClass += styleName

  protected def messageLabel(baseStyle: String = UiStyles.Common.Message): Label =
    new Label:
      visible = false
      managed = false
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += baseStyle

  protected def actionBar(buttons: Seq[Button], styleName: String = "form-actions", barAlignment: Pos = Pos.CenterRight): HBox =
    new HBox:
      alignment = barAlignment
      spacing = 12
      styleClass += styleName
      children = buttons

  protected def primaryButton(text: String, action: () => Unit): Button =
    styledButton(text, "primary-button", action)

  protected def secondaryButton(text: String, action: () => Unit): Button =
    styledButton(text, "secondary-button", action)

  protected def dangerButton(text: String, action: () => Unit): Button =
    styledButton(text, "danger-button", action)

  protected def closeButton(onExit: () => Unit, text: String = "Chiudi"): Button =
    secondaryButton(text, onExit)

  protected def titleBox(titleText: String, subtitleText: String, titleStyle: String = UiStyles.Common.Title, subtitleStyle: String = UiStyles.Common.Subtitle): VBox =
    new VBox:
      spacing = 5
      children = Seq(
        new Label(titleText):
          styleClass += titleStyle,
        new Label(subtitleText):
          wrapText = true
          styleClass += subtitleStyle
      )

  private def styledButton(text: String, styleName: String, action: () => Unit): Button =
    new Button(text):
      styleClass += styleName
      onAction = _ => action()

  protected def showMessage(label: Label, message: String, success: Boolean, successStyle: String = UiStyles.Common.MessageSuccess, errorStyle: String = UiStyles.Common.MessageError): Unit =
    label.text = message
    label.visible = true
    label.managed = true
    label.styleClass.removeAll(successStyle, errorStyle)
    label.styleClass +=
      (if success then successStyle else errorStyle)

  protected def clearMessage(label: Label, successStyle: String = UiStyles.Common.MessageSuccess, errorStyle: String = UiStyles.Common.MessageError): Unit =
    label.text = ""
    label.visible = false
    label.managed = false
    label.styleClass.removeAll(successStyle, errorStyle)