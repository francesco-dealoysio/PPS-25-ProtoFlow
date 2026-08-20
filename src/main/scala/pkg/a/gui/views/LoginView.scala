package pkg.a.gui.views

import pkg.a.gui.services.LoginService
import pkg.a.gui.text.UiStyles.Login.*
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.ApplicationName
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Login
import pkg.a.gui.traits.Form
import pkg.a.gui.services.LoginService.LoginError
import pkg.b.logic.Account
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.{BorderPane, Region, StackPane, VBox}

object LoginView extends Form:

  def apply(onLoginSuccess: Account => Unit, onRegistrationRequest: () => Unit): BorderPane =

    val username = stringField(prompt = Prompts.Username)
    val password = passwordFormField(Prompts.Password)

    val result =
      createResultMessage(
        baseStyle = MessageStyle,
        errorStyle = MessageErrorStyle
      )

    def clearFields(): Unit =
      resetFields(username, password)
      result.clear()
      username.requestFocus()

    def access(): Unit =
      if username.value.isEmpty || password.value.isEmpty then
        result.show(Login.EmptyCredentials, false)
        username.requestFocus()
      else
        LoginService.login(username.value, password.value) match
          case Right(user) =>
            onLoginSuccess(user)

          case Left(LoginError.EmptyCredentials) =>
            result.show(Login.EmptyCredentials, false)
            username.requestFocus()

          case Left(LoginError.InvalidCredentials) =>
            result.show(Login.InvalidCredentials, false)
            password.reset()
            password.requestFocus()

          case Left(LoginError.UnknownRole(role)) =>
            result.show(Login.unknownRole(role), false)

    username.control.onAction = _ =>
      password.requestFocus()

    password.control.onAction = _ =>
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
        username.control
      )

    val passwordBox = new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(
        fieldLabel(Labels.required(Labels.Password)),
        password.control
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