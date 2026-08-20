package pkg.a.gui.traits

import scalafx.geometry.Pos
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.layout.{HBox, VBox}
import pkg.a.gui.text.UiText.Common.Buttons.Close
import pkg.a.gui.text.UiStyles.Common.*

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

  protected def showError(titleText: String, header: String, content: String): Unit =
    new Alert(Alert.AlertType.Error):
      title = titleText
      headerText = header
      contentText = content
    .showAndWait()

  protected def createResultMessage(baseStyle: String = MessageStyle, successStyle: String = MessageSuccessStyle, errorStyle: String = MessageErrorStyle): ResultMessage =
    ResultMessage(messageLabel(baseStyle), successStyle, errorStyle)

  protected def fieldLabel(text: String, styleName: String = FormLabelStyle): Label =
    new Label(text):
      styleClass += styleName

  protected def actionBar(buttons: Seq[Button], styleName: String =  FormActionsStyle, barAlignment: Pos = Pos.CenterRight): HBox =
    new HBox:
      alignment = barAlignment
      spacing = 12
      styleClass += styleName
      children = buttons

  protected def primaryButton(text: String, action: () => Unit): Button =
    styledButton(text, PrimaryButtonStyle, action)

  protected def secondaryButton(text: String, action: () => Unit): Button =
    styledButton(text, SecondaryButtonStyle, action)

  protected def dangerButton(text: String, action: () => Unit): Button =
    styledButton(text, DangerButtonStyle, action)

  protected def closeButton(onExit: () => Unit, text: String = Close): Button =
    secondaryButton(text, onExit)

  protected def titleBox(titleText: String, subtitleText: String, titleStyle: String = TitleStyle, subtitleStyle: String = SubtitleStyle): VBox =
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

  private def showMessage(label: Label, message: String, success: Boolean, successStyle: String = MessageSuccessStyle, errorStyle: String = MessageErrorStyle): Unit =
    label.text = message
    label.visible = true
    label.managed = true
    label.styleClass.removeAll(successStyle, errorStyle)
    label.styleClass +=
      (if success then successStyle else errorStyle)

  private def clearMessage(label: Label, successStyle: String = MessageSuccessStyle, errorStyle: String = MessageErrorStyle): Unit =
    label.text = ""
    label.visible = false
    label.managed = false
    label.styleClass.removeAll(successStyle, errorStyle)

  private def messageLabel(baseStyle: String = MessageStyle): Label =
    new Label:
      visible = false
      managed = false
      wrapText = true
      maxWidth = Double.MaxValue
      styleClass += baseStyle