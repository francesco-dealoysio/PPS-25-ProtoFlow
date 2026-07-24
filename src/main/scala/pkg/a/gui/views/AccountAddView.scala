package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, md5}

import scalafx.application.Platform
import scalafx.scene.control.{ComboBox, PasswordField, TextField}
import scalafx.scene.layout.BorderPane

object AccountAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()

    val surnameField = textField(prompt = "Inserisci il cognome")
    val nameField = textField(prompt = "Inserisci il nome")
    val emailField = textField(prompt = "Inserisci l'email")
    val phoneField = textField(prompt = "Inserisci il telefono")
    val roleField =
      new ComboBox[String](AccountViewModel.roles):
        promptText = "Seleziona il ruolo"
        maxWidth = Double.MaxValue
        styleClass += "form-field"
    val areaField = textField(prompt = "Inserisci l'area")
    val assignmentField = textField(prompt = "Inserisci la mansione")
    val usernameField = textField(prompt = "Inserisci lo username")
    val passwordField =
      new PasswordField:
        promptText = "Inserisci la password"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val surnameError = fieldErrorLabel()
    val nameError = fieldErrorLabel()
    val emailError = fieldErrorLabel()
    val roleError = fieldErrorLabel()
    val usernameError = fieldErrorLabel()
    val passwordError = fieldErrorLabel()

    val resultMessage = messageLabel("accounts-message")

    def clearErrors(): Unit =
      clearFieldErrors(
        surnameField -> surnameError,
        nameField -> nameError,
        emailField -> emailError,
        roleField -> roleError,
        usernameField -> usernameError,
        passwordField -> passwordError
      )
      clearMessage(
        resultMessage,
        successStyle = "accounts-message-success",
        errorStyle = "accounts-message-error"
      )

    def currentAccount(id: String = ""): Account =
      Account(
        id = id,
        surname = surnameField.text.value.trim,
        name = nameField.text.value.trim,
        email = emailField.text.value.trim,
        phone = phoneField.text.value.trim,
        role = Option(roleField.value.value).getOrElse(""),
        area = areaField.text.value.trim,
        assignment = assignmentField.text.value.trim,
        username = usernameField.text.value.trim,
        password = md5(passwordField.text.value)
      )

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        viewModel.validate(
          account = currentAccount(),
          rawPassword = passwordField.text.value,
          existingAccounts = accountLogic.getRecords()
        )

      // roleField è un ComboBox, non un TextInputControl: showMappedErrors non lo accetta.
      if errors.contains(AccountViewModel.RoleRequiredError) then
        showFieldError(roleField, roleError, AccountViewModel.RoleRequiredError)

      showMappedErrors(errors):
        case AccountViewModel.SurnameRequiredError => surnameField -> surnameError
        case AccountViewModel.NameRequiredError => nameField -> nameError
        case AccountViewModel.EmailRequiredError |
             AccountViewModel.EmailInvalidError => emailField -> emailError
        case AccountViewModel.UsernameRequiredError |
             AccountViewModel.DuplicateUsernameError => usernameField -> usernameError
        case AccountViewModel.PasswordRequiredError => passwordField -> passwordError

    def resetForm(): Unit =
      surnameField.clear()
      nameField.clear()
      emailField.clear()
      phoneField.clear()
      roleField.value = null
      areaField.clear()
      assignmentField.clear()
      usernameField.clear()
      passwordField.clear()
      clearErrors()

      surnameField.requestFocus()

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val newAccount = currentAccount(IdGen(inIdsFilePathName("accountId")))
          val saved = accountLogic.recordInsert(newAccount)

          showMessage(
            label = resultMessage,
            message =
              if saved then
                "Account inserito correttamente."
              else
                "Errore durante l'inserimento dell'account.",
            success = saved,
            successStyle = "accounts-message-success",
            errorStyle = "accounts-message-error"
          )

          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form = formGrid(
      Seq(
        FormRow("Cognome *", surnameField, surnameError),
        FormRow("Nome *", nameField, nameError),
        FormRow("Email *", emailField, emailError),
        FormRow("Telefono", phoneField, fieldErrorLabel()),
        FormRow("Ruolo *", roleField, roleError),
        FormRow("Area", areaField, fieldErrorLabel()),
        FormRow("Mansione", assignmentField, fieldErrorLabel()),
        FormRow("Username *", usernameField, usernameError),
        FormRow("Password *", passwordField, passwordError)
      )
    )

    Platform.runLater {
      surnameField.requestFocus()
    }

    formPage(
      titleText = "Aggiunta account",
      subtitleText = "Inserisci i dati del nuovo account utente.",
      titleStyle = "accounts-title",
      subtitleStyle = "accounts-subtitle",
      rootStyle = "accounts-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(exit, reset, save),
      hasUnsavedChanges = () =>
        !formSaved &&
          (
            surnameField.text.value.trim.nonEmpty ||
              nameField.text.value.trim.nonEmpty ||
              emailField.text.value.trim.nonEmpty ||
              phoneField.text.value.trim.nonEmpty ||
              Option(roleField.value.value).exists(_.trim.nonEmpty) ||
              areaField.text.value.trim.nonEmpty ||
              assignmentField.text.value.trim.nonEmpty ||
              usernameField.text.value.trim.nonEmpty ||
              passwordField.text.value.nonEmpty
            )
    )
