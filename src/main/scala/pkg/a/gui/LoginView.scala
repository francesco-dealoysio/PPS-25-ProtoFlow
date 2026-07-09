package pkg.a.gui

import pkg.c.data.Properties.*
import pkg.c.data.xmlManagement.Entities.Account
import pkg.c.data.xmlManagement.Xml.*
import pkg.d.util.Util.md5
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Region, StackPane, VBox}

object LoginView:

  def apply(
             onLoginSuccess: Account => Unit,
             onRegistrationRequest: () => Unit
           ): BorderPane =

    val usernameField = new TextField:
      promptText = "Inserisci username"
      styleClass += "form-field"

    val passwordField = new PasswordField:
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

    def checkCredentials(username: String, password: String): Option[Account] =
      val fs = java.io.File.separator
      val baseFolder = System.getProperty("user.dir") + fs + "protoflow"

      val databaseFolder =
        getPropsFileProperty(
          baseFolder + fs + "protoflow.properties",
          "database.folder"
        )

      val accounts =
        loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
          .map(_.asInstanceOf[Account])

      accounts.find(account =>
        account.username == username && account.password == md5(password)
      )

    def access(): Unit =
      val username = usernameField.text.value.trim
      val password = passwordField.text.value.trim

      if username.isEmpty || password.isEmpty then
        showMessage("Inserisci username e password.")
        usernameField.requestFocus()
      else
        checkCredentials(username, password) match
          case Some(account) if Set("admin", "oper", "viewer").contains(account.ruolo) =>
            onLoginSuccess(account)

          case Some(account) =>
            showMessage(s"Application Error: ruolo '${account.ruolo}' non riconosciuto.")

          case None =>
            showMessage("Accesso negato. Username o password non corretti.")
            passwordField.clear()
            passwordField.requestFocus()

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

    val formGrid = new GridPane:
      hgap = 14
      vgap = 10
      styleClass += "registration-grid"

      add(formLabel("Username *"), 0, 0)
      add(usernameField, 0, 1)

      add(formLabel("Password *"), 0, 2)
      add(passwordField, 0, 3)

    val clearButton = new Button("Pulisci"):
      styleClass += "secondary-button"
      onAction = _ => clearFields()

    val accessButton = new Button("Accedi"):
      styleClass += "primary-button"
      defaultButton = true
      onAction = _ => access()

    val buttonsBox = new HBox:
      alignment = Pos.CenterRight
      spacing = 12
      children = Seq(clearButton, accessButton)

    val registrationButton = new Button("Richiedi registrazione"):
      maxWidth = Double.MaxValue
      styleClass += "secondary-button"
      onAction = _ => onRegistrationRequest()

    val card = new VBox:
      alignment = Pos.Center
      spacing = 18
      padding = Insets(36, 46, 36, 46)
      styleClass ++= Seq("registration-card", "login-card")
      children = Seq(
        header,
        formGrid,
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