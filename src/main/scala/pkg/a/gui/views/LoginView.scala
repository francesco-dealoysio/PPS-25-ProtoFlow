package pkg.a.gui.views

import pkg.a.gui.services.LoginService
import pkg.a.gui.text.UiStyles.Common.FormFieldStyle
import pkg.a.gui.text.UiStyles.Login.*
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.ApplicationName
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Login
import pkg.a.gui.traits.Form
import pkg.a.gui.services.LoginService.LoginError
import pkg.b.logic.Account
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Label, PasswordField, TextField}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, Region, StackPane, VBox}

object LoginView extends Form:

  def apply(onLoginSuccess: Account => Unit, onRegistrationRequest: () => Unit): BorderPane =

    val usernameField = new TextField:
      maxWidth = 220
      promptText = Prompts.Username
      styleClass += FormFieldStyle

    val passwordField = new PasswordField:
      maxWidth = 220
      promptText = Prompts.Password
      styleClass += FormFieldStyle

    val result =
      createResultMessage(
        baseStyle = MessageStyle,
        errorStyle = MessageErrorStyle
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
            result.show(Login.unknownRole(role), false)

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
      styleClass += LogoStyle
      children = new Label("PF"):
        styleClass += LogoTextStyle

    val titleSection =
      titleBox(
        titleText = ApplicationName,
        subtitleText = Login.ApplicationSubtitle,
        titleStyle = TitleStyle,
        subtitleStyle = SubtitleStyle
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
        fieldLabel(Labels.required(Labels.Username)),
        usernameField
      )

    val passwordBox = new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(
        fieldLabel(Labels.required(Labels.Password)),
        passwordField
      )

    val formBox = new VBox:
      spacing = 16
      alignment = Pos.Center
      children = Seq(usernameBox, passwordBox)

    val clearButton = resetButton(clearFields, Buttons.Clear)
    val accessButton = primaryButton(Buttons.Login, access)
    val buttonsBox = actionBar(Seq(clearButton, accessButton), barAlignment = Pos.Center)
    val registrationButton = secondaryButton(Buttons.RequestRegistration, onRegistrationRequest)
    registrationButton.maxWidth = 220

    val card = new VBox:
      alignment = Pos.Center
      spacing = 18
      padding = Insets(36, 46, 36, 46)
      styleClass ++= Seq(CardStyle, LoginCardStyle)
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
      styleClass += RootStyle
      center = card