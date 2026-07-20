package pkg.a.gui

import pkg.b.logic.Account
import pkg.c.data.guiStructures.AccountViewModel
import pkg.d.util.Util.md5

import scalafx.scene.control.{ComboBox, PasswordField, TextField}
import scalafx.scene.layout.BorderPane

object AccountAddView extends FormView:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()

    val surnameField =
      new TextField:
        promptText = "Inserisci il cognome"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val nameField =
      new TextField:
        promptText = "Inserisci il nome"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val emailField =
      new TextField:
        promptText = "Inserisci l'email"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val phoneField =
      new TextField:
        promptText = "Inserisci il telefono"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val roleField =
      new ComboBox[String](AccountViewModel.roles):
        promptText = "Seleziona il ruolo"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val areaField =
      new TextField:
        promptText = "Inserisci l'area"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val assignmentField =
      new TextField:
        promptText = "Inserisci la mansione"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val usernameField =
      new TextField:
        promptText = "Inserisci lo username"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

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

      errors.foreach:
        case AccountViewModel.SurnameRequiredError =>
          showFieldError(surnameField, surnameError, AccountViewModel.SurnameRequiredError)

        case AccountViewModel.NameRequiredError =>
          showFieldError(nameField, nameError, AccountViewModel.NameRequiredError)

        case AccountViewModel.EmailRequiredError =>
          showFieldError(emailField, emailError, AccountViewModel.EmailRequiredError)

        case AccountViewModel.EmailInvalidError =>
          showFieldError(emailField, emailError, AccountViewModel.EmailInvalidError)

        case AccountViewModel.RoleRequiredError =>
          showFieldError(roleField, roleError, AccountViewModel.RoleRequiredError)

        case AccountViewModel.UsernameRequiredError =>
          showFieldError(usernameField, usernameError, AccountViewModel.UsernameRequiredError)

        case AccountViewModel.DuplicateUsernameError =>
          showFieldError(usernameField, usernameError, AccountViewModel.DuplicateUsernameError)

        case AccountViewModel.PasswordRequiredError =>
          showFieldError(passwordField, passwordError, AccountViewModel.PasswordRequiredError)

        case _ => ()

      errors.isEmpty

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

    val save =
      saveButton: () =>
        if validateForm() then
          val existingAccounts = accountLogic.getRecords()
          val newAccount = currentAccount(viewModel.nextId(existingAccounts))
          val saved = accountLogic.recordInsert(newAccount)

          showMessage(
            label = resultMessage,
            message =
              if saved then
                "Account inserito correttamente."
              else
                "Errore durante l'inserimento dell'account.",
            success = saved,
            successStyle =
              "accounts-message-success",
            errorStyle =
              "accounts-message-error"
          )

          if saved then onSaved()

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

    formPage(
      titleText = "Aggiunta account",
      subtitleText =
        "Inserisci i dati del nuovo account utente.",
      titleStyle = "accounts-title",
      subtitleStyle = "accounts-subtitle",
      rootStyle = "accounts-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(exit, reset, save)
    )
