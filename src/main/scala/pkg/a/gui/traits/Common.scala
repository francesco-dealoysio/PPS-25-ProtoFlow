package pkg.a.gui.traits

import pkg.a.gui.text.CommonText.Common.Buttons.Print
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.text.UiText.Common.Buttons.Close
import pkg.b.logic.pdf.{PdfDetailsCreator, PdfViewer}
import pkg.d.util.Logger.logger
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.geometry.Pos
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.layout.{HBox, VBox}

/**
 * Provides reusable UI components and common interaction utilities
 * shared by the application views.
 */
trait Common:

  /**
   * Represents a UI message that can display success or error feedback.
   * @param label        the label used to display the message
   * @param successStyle the style applied to success messages
   * @param errorStyle   the style applied to error messages
   */
  protected case class ResultMessage(label: Label, successStyle: String, errorStyle: String):

    /**
     * Clears and hides the current message.
     */
    def clear(): Unit =
      clearMessage(label, successStyle, errorStyle)

    /**
     * Displays the given message using the appropriate result style.
     * @param message the message to display
     * @param success whether the message represents a successful result
     */
    def show(message: String, success: Boolean): Unit =
      showMessage(label, message, success, successStyle, errorStyle)

  /**
   * Shows a confirmation dialog and waits for the user's choice.
   * @param titleText the dialog title
   * @param header    the dialog header
   * @param content   the dialog message
   * @return true if the user confirms, false otherwise
   */
  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content
    dialog.showAndWait().contains(ButtonType.OK)

  /**
   * Shows an error dialog.
   * @param titleText the dialog title
   * @param header    the dialog header
   * @param content   the error message
   */
  protected def showError(titleText: String, header: String, content: String): Unit =
    new Alert(Alert.AlertType.Error):
      title = titleText
      headerText = header
      contentText = content
    .showAndWait()

  /**
   * Shows an informational dialog for a successful operation.
   * @param titleText the dialog title
   * @param content the success message
   */
  protected def showSuccess(titleText: String, content: String): Unit =
    new Alert(Alert.AlertType.Information):
      title = titleText
      headerText = None
      contentText = content
    .showAndWait()

  /**
   * Creates a result message with configurable styles.
   * @param baseStyle    the base style of the message label
   * @param successStyle the style used for success messages
   * @param errorStyle   the style used for error messages
   * @return the configured result message
   */
  protected def createResultMessage(baseStyle: String = MessageStyle, successStyle: String = MessageSuccessStyle, errorStyle: String = MessageErrorStyle): ResultMessage =
    ResultMessage(messageLabel(baseStyle), successStyle, errorStyle)

  /**
   * Creates a styled label for a form field.
   * @param text      the label text
   * @param styleName the style applied to the label
   * @return the configured label
   */
  protected def fieldLabel(text: String, styleName: String = FormLabelStyle): Label =
    new Label(text):
      styleClass += styleName

  /**
   * Creates an action bar containing the given buttons.
   * @param buttons      the buttons displayed in the bar
   * @param styleName    the style applied to the bar
   * @param barAlignment the alignment of the buttons
   * @return the configured action bar
   */
  protected def actionBar(buttons: Seq[Button], styleName: String =  FormActionsStyle, barAlignment: Pos = Pos.CenterRight): HBox =
    new HBox:
      alignment = barAlignment
      spacing = 12
      styleClass += styleName
      children = buttons

  /**
   * Creates a primary-styled button executing the given action.
   * @param text   the button text
   * @param action the action executed when the button is pressed
   */
  protected def primaryButton(text: String, action: () => Unit): Button =
    styledButton(text, PrimaryButtonStyle, action)

  /**
   * Creates a secondary-styled button executing the given action.
   * @param text   the button text
   * @param action the action executed when the button is pressed
   */
  protected def secondaryButton(text: String, action: () => Unit): Button =
    styledButton(text, SecondaryButtonStyle, action)

  /**
   * Creates a danger-styled button executing the given action.
   * @param text   the button text
   * @param action the action executed when the button is pressed
   */
  protected def dangerButton(text: String, action: () => Unit): Button =
    styledButton(text, DangerButtonStyle, action)

  /**
   * Creates a close button executing the given exit action.
   * @param onExit the action executed when the button is pressed
   * @param text   the button text
   */
  protected def closeButton(onExit: () => Unit, text: String = Close): Button =
    secondaryButton(text, onExit)


  /**
   * Creates a button used to trigger a print action.
   * @param action the action executed when the button is pressed
   * @param text   the button text
   * @return the configured print button
   */
  protected def printButton(action: () => Unit, text: String = Print): Button =
    secondaryButton(text = text, action = action)

  /**
   * Creates a title section containing a title and a subtitle.
   * @param titleText     the title text
   * @param subtitleText  the subtitle text
   * @param titleStyle    the style applied to the title
   * @param subtitleStyle the style applied to the subtitle
   * @return the configured title section
   */
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