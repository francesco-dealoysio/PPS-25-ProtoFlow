package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import pkg.d.util.Util.{inDatabaseFilePathName, md5}
import pkg.d.util.XmlToPdf

import scalafx.scene.control.{ComboBox, PasswordField, TextField}
import scalafx.scene.layout.BorderPane

object AccountEdit$ extends Form:

  def apply(
             selectedAccount: Account,
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()

    val initialSurname = selectedAccount.getSurname
    val initialName = selectedAccount.getName
    val initialEmail = selectedAccount.getEmail
    val initialPhone = selectedAccount.getPhone
    val initialRole = selectedAccount.getRole
    val initialArea = selectedAccount.getArea
    val initialAssignment = selectedAccount.getAssignment
    val initialUsername = selectedAccount.getUsername

    val surnameField =
      new TextField:
        text = initialSurname
        promptText = "Inserisci il cognome"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val nameField =
      new TextField:
        text = initialName
        promptText = "Inserisci il nome"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val emailField =
      new TextField:
        text = initialEmail
        promptText = "Inserisci l'email"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val phoneField =
      new TextField:
        text = initialPhone
        promptText = "Inserisci il telefono"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val roleField =
      new ComboBox[String](AccountViewModel.roles):
        value = initialRole
        promptText = "Seleziona il ruolo"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val areaField =
      new TextField:
        text = initialArea
        promptText = "Inserisci l'area"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val assignmentField =
      new TextField:
        text = initialAssignment
        promptText = "Inserisci la mansione"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val usernameField =
      new TextField:
        text = initialUsername
        promptText = "Inserisci lo username"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val passwordField =
      new PasswordField:
        promptText = "Lascia vuoto per non modificare la password"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val surnameError = fieldErrorLabel()
    val nameError = fieldErrorLabel()
    val emailError = fieldErrorLabel()
    val roleError = fieldErrorLabel()
    val usernameError = fieldErrorLabel()

    val resultMessage = messageLabel("accounts-message")

    def clearErrors(): Unit =
      clearFieldErrors(
        surnameField -> surnameError,
        nameField -> nameError,
        emailField -> emailError,
        roleField -> roleError,
        usernameField -> usernameError
      )
      clearMessage(
        resultMessage,
        "accounts-message-success",
        "accounts-message-error"
      )

    def currentAccount(): Account =
      val updatedPassword =
        passwordField.text.value.trim match
          case "" => selectedAccount.getPassword
          case raw => md5(raw)

      Account(
        id = selectedAccount.getId,
        surname = surnameField.text.value.trim,
        name = nameField.text.value.trim,
        email = emailField.text.value.trim,
        phone = phoneField.text.value.trim,
        role = Option(roleField.value.value).getOrElse(""),
        area = areaField.text.value.trim,
        assignment = assignmentField.text.value.trim,
        username = usernameField.text.value.trim,
        password = updatedPassword
      )

    def resetForm(): Unit =
      surnameField.text = initialSurname
      nameField.text = initialName
      emailField.text = initialEmail
      phoneField.text = initialPhone
      roleField.value = initialRole
      areaField.text = initialArea
      assignmentField.text = initialAssignment
      usernameField.text = initialUsername
      passwordField.clear()

      clearErrors()
      surnameField.requestFocus()

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        viewModel.validate(
          account = currentAccount(),
          rawPassword = passwordField.text.value,
          existingAccounts = accountLogic.getRecords(),
          currentAccountId = Some(selectedAccount.getId),
          requirePassword = false
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

    val save =
      saveButton: () =>
        if validateForm() then
          val updated = accountLogic.recordUpdate(currentAccount())

          showMessage(
            label = resultMessage,
            message =
              if updated then
                "Account modificato correttamente."
              else
                "Errore durante la modifica dell'account.",
            success = updated,
            successStyle =
              "accounts-message-success",
            errorStyle =
              "accounts-message-error"
          )
          if updated then onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val print =
      secondaryButton(
        text = "Stampa",
        action = () =>
          val printed =
            XmlToPdf.printDetails(
              xmlPath = inDatabaseFilePathName("accounts.xml"),
              recordId = selectedAccount.getId,
              pdfFileName = s"account_${selectedAccount.getId}",
              title = "Scheda Account Utente"
            )

          showMessage(
            label = resultMessage,
            message =
              if printed then
                "Scheda account stampata correttamente in PDF."
              else
                "Errore durante la stampa della scheda account.",
            success = printed,
            successStyle =
              "accounts-message-success",
            errorStyle =
              "accounts-message-error"
          )
      )

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
        FormRow("Password", passwordField, fieldErrorLabel())
      )
    )

    formPage(
      titleText = "Modifica account",
      subtitleText =
        "Modifica i dati dell'account selezionato. Lascia vuoto il campo password per non modificarla.",
      titleStyle = "accounts-title",
      subtitleStyle = "accounts-subtitle",
      rootStyle = "accounts-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(exit, print, reset, save)
    )
