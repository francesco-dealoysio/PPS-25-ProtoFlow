package pkg.a.gui

import pkg.b.logic.LoginService
import pkg.b.logic.LoginService.{LoggedUser, LoginError}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, Region, StackPane, VBox}

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

    val result =
      createResultMessage(
        baseStyle = "login-message",
        successStyle = "login-message-success",
        errorStyle = "login-message-error"
      )

    def clearFields(): Unit =
      usernameField.clear()
      passwordField.clear()
      result.clear()
      usernameField.requestFocus()

    def access(): Unit =
      val username = usernameField.text.value.trim
      val password = passwordField.text.value.trim

      if username.isEmpty || password.isEmpty then
        result.show("Inserisci username e password.", false)
        usernameField.requestFocus()
      else
        LoginService.login(username, password) match
          case Right(user) =>
            onLoginSuccess(user)

          case Left(LoginError.EmptyCredentials) =>
            result.show("Inserisci username e password.", false)
            usernameField.requestFocus()

          case Left(LoginError.InvalidCredentials) =>
            result.show("Accesso negato. Username o password non corretti.", false)
            passwordField.clear()
            passwordField.requestFocus()

          case Left(LoginError.UnknownRole(role)) =>
            result.show(s"Application Error: ruolo '$role' non riconosciuto.", false)

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

    val titleSection =
      titleBox(
        titleText = "ProtoFlow",
        subtitleText = "Enterprise Document Protocol System",
        titleStyle = "registration-title",
        subtitleStyle = "registration-subtitle"
      )
    titleSection.alignment = Pos.Center

    val header = new VBox:
      alignment = Pos.Center
      spacing = 10
      children = Seq(logo, titleSection)

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

    val clearButton = resetButton(() => clearFields(), "Pulisci")
    val accessButton = primaryButton("Accedi", () => access())

    val buttonsBox = actionBar(clearButton, accessButton)
    buttonsBox.alignment = Pos.Center

    val registrationButton = secondaryButton("Richiedi registrazione", () => onRegistrationRequest())
    registrationButton.maxWidth = 220

    val card = new VBox:
      alignment = Pos.Center
      spacing = 18
      padding = Insets(36, 46, 36, 46)
      styleClass ++= Seq("registration-card", "login-card")
      children = Seq(
        header,
        formBox,
        result.label,
        buttonsBox,
        new Region:
          minHeight = 6
        ,
        registrationButton
      )

    new BorderPane:
      styleClass += "registration-root"
      center = card