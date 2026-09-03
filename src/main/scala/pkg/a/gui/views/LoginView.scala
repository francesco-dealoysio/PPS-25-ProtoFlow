package pkg.a.gui.views

import pkg.a.gui.services.LoginService
import pkg.a.gui.services.LoginService.LoginError
import pkg.a.gui.text.UiStyles.Login.*
import pkg.a.gui.text.UiText.Common.ApplicationName
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Login
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.layout.{BorderPane, HBox, Region, StackPane, VBox}

object LoginView extends Form:

  def apply(onLoginSuccess: Account => Unit, onRegistrationRequest: () => Unit): BorderPane =

    val username = stringField(prompt = Prompts.Username)
    val password = passwordFormField(Prompts.Password)
    val result = loginResult()
    val resetAction = () => resetLoginForm(username, password, result)
    val loginAction = () => attemptLogin(username, password, result, onLoginSuccess)

    configureFieldActions(username, password, loginAction)
    val actions = loginActions(resetAction, loginAction)
    val registrationButton = secondaryButton(Buttons.RequestRegistration, onRegistrationRequest)
    registrationButton.maxWidth = 220
    val card =
      loginCard(
        header = loginHeader(),
        form = loginForm(username.control, password.control),
        resultLabel = result.label,
        actions = actions,
        registrationButton = registrationButton
      )

    new BorderPane:
      styleClass += RootStyle
      center = card


  private def loginResult(): ResultMessage =
    val result = createResultMessage(MessageStyle, MessageErrorStyle)
    result.label.minWidth = 320
    result.label.prefWidth = 320
    result.label.maxWidth = 320
    result

  private def resetLoginForm(username: FormField[TextField], password: FormField[PasswordField], result: ResultMessage): Unit =
    resetFields(username, password)
    result.clear()
    username.requestFocus()

  private def attemptLogin(username: FormField[TextField], password: FormField[PasswordField], result: ResultMessage, onLoginSuccess: Account => Unit): Unit =
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

  private def configureFieldActions(username: FormField[TextField], password: FormField[PasswordField], loginAction: () => Unit): Unit =
    username.control.onAction = _ =>
      password.requestFocus()
    password.control.onAction = _ =>
      loginAction()

  private def loginActions(resetAction: () => Unit, loginAction: () => Unit): HBox =
    val clearButton = resetButton(resetAction)
    val loginButton = primaryButton(Buttons.Login, loginAction)
    actionBar(Seq(clearButton, loginButton), barAlignment = Pos.Center)

  private def loginHeader(): VBox =
    val titleSection =
      titleBox(
        titleText = ApplicationName,
        subtitleText = Login.ApplicationSubtitle,
        titleStyle = TitleStyle,
        subtitleStyle = SubtitleStyle
      )
    titleSection.alignment = Pos.Center
    new VBox:
      alignment = Pos.Center
      spacing = 10
      children = Seq(loginLogo(), titleSection)

  private def loginLogo(): StackPane =
    new StackPane:
      minWidth = 58
      minHeight = 58
      maxWidth = 58
      maxHeight = 58
      styleClass += LogoStyle
      children = new Label("PF"):
        styleClass += LogoTextStyle

  private def loginForm(username: Node, password: Node): VBox =
    new VBox:
      spacing = 16
      alignment = Pos.Center
      children = Seq(
        loginField(Labels.required(Labels.Username), username),
        loginField(Labels.required(Labels.Password), password)
      )

  private def loginField(label: String, control: Node): VBox =
    new VBox:
      spacing = 6
      alignment = Pos.CenterLeft
      maxWidth = 220
      children = Seq(fieldLabel(label), control)

  private def loginCard(header: VBox, form: VBox, resultLabel: Label, actions: HBox, registrationButton: Button): VBox =
    new VBox:
      alignment = Pos.Center
      spacing = 18
      padding = Insets(36, 46, 36, 46)
      styleClass ++= Seq(CardStyle, LoginCardStyle)
      children = Seq(
        header,
        form,
        resultLabel,
        actions,
        new Region:
          minHeight = 6,
          registrationButton
      )