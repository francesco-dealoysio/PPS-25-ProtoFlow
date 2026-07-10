package pkg.a.gui

import pkg.b.logic.LoginService
import pkg.b.logic.LoginService.{LoggedUser, LoginError}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, HBox, Region, StackPane, VBox}

object LoginView:

  def apply(
             onLoginSuccess: LoggedUser => Unit,
             onRegistrationRequest: () => Unit
           ): BorderPane =

    val usernameField = new TextField:
      maxWidth = 220
      promptText = "Inserisci username"
      styleClass += "form-field"

    val passwordField = new PasswordField:
      maxWidth = 220
      promptText = "Inserisci password"
      styleClass += "form-field"

    val messageLabel = new Label:
      visible = false
      managed = false
      wrapText = true
      styleClass ++= Seq("login-message", "login-message-error")

    def showMessage(message: String): Unit =
      messageLabel.text = message
      messageLabel.visible = true
      messageLabel.managed = true

    def clearMessage(): Unit =
      messageLabel.text = ""
      messageLabel.visible = false
      messageLabel.managed = false

    def clearFields(): Unit =
      usernameField.clear()
      passwordField.clear()
      clearMessage()
      usernameField.requestFocus()

    def access(): Unit =
      val username = usernameField.text.value.trim
      val password = passwordField.text.value.trim

      if username.isEmpty || password.isEmpty then
        showMessage("Inserisci username e password.")
        usernameField.requestFocus()
      else
        LoginService.login(username, password) match
          case Right(user) =>
            onLoginSuccess(user)

          case Left(LoginError.EmptyCredentials) =>
            showMessage("Inserisci username e password.")
            usernameField.requestFocus()

          case Left(LoginError.InvalidCredentials) =>
            showMessage("Accesso negato. Username o password non corretti.")
            passwordField.clear()
            passwordField.requestFocus()

          case Left(LoginError.UnknownRole(role)) =>
            showMessage(s"Application Error: ruolo '$role' non riconosciuto.")

    usernameField.onKeyPressed = event =>
      if event.code == KeyCode.Enter then
        passwordField.requestFocus()

    passwordField.onKeyPressed = event =>
      if event.code == KeyCode.Enter then
        access()

    val logo = new StackPane:
      minWidth = 58
      minHeight = 58
      maxWidth = 58
      maxHeight = 58
      styleClass += "login-logo"
      children = new Label("PF"):
        styleClass += "login-logo-text"

    val title = new Label("ProtoFlow"):
      styleClass += "registration-title"

    val subtitle = new Label("Enterprise Document Protocol System"):
      styleClass += "registration-subtitle"

    val header = new VBox:
      alignment = Pos.Center
      spacing = 10
      children = Seq(logo, title, subtitle)

    val usernameBox = new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(
        formLabel("Username *"),
        usernameField
      )

    val passwordBox = new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(
        formLabel("Password *"),
        passwordField
      )

    val formBox = new VBox:
      spacing = 16
      alignment = Pos.Center
      children = Seq(
        usernameBox,
        passwordBox
      )

    val clearButton = new Button("Pulisci"):
      styleClass += "secondary-button"
      onAction = _ => clearFields()

    val accessButton = new Button("Accedi"):
      styleClass += "primary-button"
      defaultButton = true
      onAction = _ => access()

    val buttonsBox = new HBox:
      alignment = Pos.Center
      spacing = 12
      children = Seq(clearButton, accessButton)

    val registrationButton = new Button("Richiedi registrazione"):
      maxWidth = 220
      styleClass += "secondary-button"
      onAction = _ => onRegistrationRequest()

    val card = new VBox:
      alignment = Pos.Center
      spacing = 18
      padding = Insets(36, 46, 36, 46)
      styleClass ++= Seq("registration-card", "login-card")
      children = Seq(
        header,
        formBox,
        messageLabel,
        buttonsBox,
        new Region:
          minHeight = 6
        ,
        registrationButton
      )

    new BorderPane:
      styleClass += "registration-root"
      center = card

  private def formLabel(text: String): Label =
    new Label(text):
      styleClass += "form-label"