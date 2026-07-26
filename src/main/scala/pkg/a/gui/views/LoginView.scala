package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.b.logic.LoginService.LoginError
import pkg.b.logic.{Account, LoginService}
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, Region, StackPane, VBox}
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Common, Fields, Login}

object LoginView extends Form:

  def apply(onLoginSuccess: Account => Unit,
             onRegistrationRequest: () => Unit
           ): BorderPane =

    val usernameField = new TextField:
      maxWidth = 220
      promptText = Fields.Prompts.Username
      styleClass += UiStyles.Common.FormField

    val passwordField = new PasswordField:
      maxWidth = 220
      promptText = Fields.Prompts.Password
      styleClass += UiStyles.Common.FormField

    val result =
      createResultMessage(
        baseStyle = UiStyles.Login.Message,
        successStyle = UiStyles.Login.MessageSuccess,
        errorStyle = UiStyles.Login.MessageError
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
        result.show(Login.EmptyCredentials, false)
        usernameField.requestFocus()
      else
        LoginService.login(username, password) match
          case Right(user) =>
            onLoginSuccess(user)

          case Left(LoginError.EmptyCredentials) =>
            result.show(Login.EmptyCredentials, false)
            usernameField.requestFocus()

          case Left(LoginError.InvalidCredentials) =>
            result.show(Login.InvalidCredentials, false)
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
      styleClass += UiStyles.Login.Logo
      children = new Label("PF"):
        styleClass += UiStyles.Login.LogoText

    val titleSection =
      titleBox(
        titleText = Login.ApplicationTitle,
        subtitleText = Login.ApplicationSubtitle,
        titleStyle = UiStyles.Login.Title,
        subtitleStyle = UiStyles.Login.Subtitle
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
        fieldLabel(Fields.Labels.required(Fields.Labels.Username)),
        usernameField
      )

    val passwordBox = new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(
        fieldLabel(Fields.Labels.required(Fields.Labels.Password)),
        passwordField
      )

    val formBox = new VBox:
      spacing = 16
      alignment = Pos.Center
      children = Seq(usernameBox, passwordBox)

    val clearButton = resetButton(() => clearFields(), Common.Buttons.Clear)
    val accessButton = primaryButton(Common.Buttons.Login, () => access())
    val buttonsBox = actionBar(Seq(clearButton, accessButton), UiStyles.Login.Actions, Pos.Center)
    val registrationButton = secondaryButton(Common.Buttons.RequestRegistration, () => onRegistrationRequest())
    registrationButton.maxWidth = 220

    val card = new VBox:
      alignment = Pos.Center
      spacing = 18
      padding = Insets(36, 46, 36, 46)
      styleClass ++= Seq(UiStyles.Login.Card, UiStyles.Login.LoginCard)
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
      styleClass += UiStyles.Login.Root
      center = card