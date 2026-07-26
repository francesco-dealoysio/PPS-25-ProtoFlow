package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import pkg.d.util.Util.{inDatabaseFilePathName, md5}
import pkg.d.util.XmlToPdf
import scalafx.scene.control.{ComboBox, PasswordField}
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Accounts, Common, Fields}

object AccountEditView extends Form:

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

    val surnameField = textField(Fields.Prompts.Surname, initialSurname)
    val nameField = textField(Fields.Prompts.Name, initialName)
    val emailField = textField(Fields.Prompts.Email, initialEmail)
    val phoneField = textField(Fields.Prompts.Phone, initialPhone)
    val roleField =
      new ComboBox[String](AccountViewModel.roles):
        value = initialRole
        promptText = Fields.Prompts.SelectRole
        maxWidth = Double.MaxValue
        styleClass += UiStyles.Common.FormField

    val areaField = textField(Fields.Prompts.Area, initialArea)
    val assignmentField = textField(Fields.Prompts.Assignment, initialAssignment)
    val usernameField = textField(Fields.Prompts.Username, initialUsername)

    val passwordField =
      new PasswordField:
        promptText = Fields.Prompts.KeepPassword
        maxWidth = Double.MaxValue
        styleClass += UiStyles.Common.FormField
    val monitoredTextFields =
      Seq(
        surnameField,
        nameField,
        emailField,
        phoneField,
        areaField,
        assignmentField,
        usernameField,
        passwordField
      )

    val monitoredComboBoxes = Seq(roleField)

    val initialFormValues =
      Seq(
        initialSurname,
        initialName,
        initialEmail,
        initialPhone,
        initialArea,
        initialAssignment,
        initialUsername,
        "", // password inizialmente vuota
        initialRole // ComboBox viene dopo i TextInputControl
      )

    val surnameError = fieldErrorLabel()
    val nameError = fieldErrorLabel()
    val emailError = fieldErrorLabel()
    val roleError = fieldErrorLabel()
    val usernameError = fieldErrorLabel()

    val resultMessage = messageLabel(UiStyles.Accounts.Message)

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
        UiStyles.Accounts.MessageSuccess,
        UiStyles.Accounts.MessageError
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

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val updated = accountLogic.recordUpdate(currentAccount())

          showMessage(
            label = resultMessage,
            message =
              if updated then Accounts.Edit.Success
              else Accounts.Edit.Error,
            success = updated,
            successStyle = UiStyles.Accounts.MessageSuccess,
            errorStyle = UiStyles.Accounts.MessageError
          )
          if updated then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val print =
      secondaryButton(
        text = Common.Buttons.Print,
        action = () =>
          val printed =
            XmlToPdf.printDetails(
              xmlPath = inDatabaseFilePathName("accounts.xml"),
              recordId = selectedAccount.getId,
              pdfFileName = s"account_${selectedAccount.getId}",
              title = Accounts.Edit.PrintTitle
            )

          showMessage(
            label = resultMessage,
            message =
              if printed then Accounts.Edit.PrintSuccess
              else Accounts.Edit.PrintError,
            success = printed,
            successStyle = UiStyles.Accounts.MessageSuccess,
            errorStyle = UiStyles.Accounts.MessageError
          )
      )

    val form = formGrid(
      Seq(
        FormRow(Fields.Labels.required(Fields.Labels.Surname), surnameField, surnameError),
        FormRow(Fields.Labels.required(Fields.Labels.Name), nameField, nameError),
        FormRow(Fields.Labels.required(Fields.Labels.Email), emailField, emailError),
        FormRow(Fields.Labels.Phone, phoneField, fieldErrorLabel()),
        FormRow(Fields.Labels.required(Fields.Labels.Role), roleField, roleError),
        FormRow( Fields.Labels.Area, areaField, fieldErrorLabel()),
        FormRow(Fields.Labels.Assignment, assignmentField, fieldErrorLabel()),
        FormRow(Fields.Labels.required(Fields.Labels.Username), usernameField, usernameError),
        FormRow(Fields.Labels.Password, passwordField, fieldErrorLabel())
      )
    )

    formPage(
      titleText = Accounts.Edit.Title,
      subtitleText = Accounts.Edit.Subtitle,
      titleStyle = UiStyles.Accounts.Title,
      subtitleStyle = UiStyles.Accounts.Subtitle,
      rootStyle = UiStyles.Accounts.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, print, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredTextFields, monitoredComboBoxes, initialFormValues)
    )
