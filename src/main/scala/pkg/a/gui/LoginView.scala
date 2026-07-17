package pkg.a.gui

import pkg.b.logic.LoginService
import pkg.b.logic.LoginService.{LoggedUser, LoginError}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, HBox, Region, StackPane, VBox}

object LoginView extends FormView:

  def apply(onLoginSuccess: LoggedUser => Unit,
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

    val resultMessage = messageLabel("login-message")

    def showLoginError(message: String): Unit =
      showMessage(
        label = resultMessage,
        message = message,
        success = false,
        successStyle = "login-message-success",
        errorStyle = "login-message-error"
      )

    def clearFields(): Unit =
      usernameField.clear()
      passwordField.clear()
      clearMessage(resultMessage, successStyle = "login-message-success", errorStyle = "login-message-error")
      usernameField.requestFocus()

    def access(): Unit =
      val username = usernameField.text.value.trim
      val password = passwordField.text.value.trim

      if username.isEmpty || password.isEmpty then
        showLoginError("Inserisci username e password.")
        usernameField.requestFocus()
      else
        LoginService.login(username, password) match
          case Right(user) =>
            onLoginSuccess(user)

          case Left(LoginError.EmptyCredentials) =>
            showLoginError("Inserisci username e password.")
            usernameField.requestFocus()

          case Left(LoginError.InvalidCredentials) =>
            showLoginError("Accesso negato. Username o password non corretti.")
            passwordField.clear()
            passwordField.requestFocus()

          case Left(LoginError.UnknownRole(role)) =>
            showLoginError(s"Application Error: ruolo '$role' non riconosciuto.")

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
        fieldLabel("Username *"),
        usernameField
      )

    val passwordBox = new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(
        fieldLabel("Password *"),
        passwordField
      )

    val formBox = new VBox:
      spacing = 16
      alignment = Pos.Center
      children = Seq(
        usernameBox,
        passwordBox
      )

    val clearButton = resetButton(onReset = () => clearFields(), text = "Pulisci")
    val accessButton = primaryButton("Accedi", () => access())

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
        resultMessage,
        buttonsBox,
        new Region:
          minHeight = 6
        ,
        registrationButton
      )

    new BorderPane:
      styleClass += "registration-root"
      center = card