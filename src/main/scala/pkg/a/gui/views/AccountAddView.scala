package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, md5}
import scalafx.application.Platform
import scalafx.scene.control.{ComboBox, PasswordField}
import scalafx.scene.layout.BorderPane
import UiText.{Accounts, Fields}

object AccountAddView extends Form:

  def apply(
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()

    val surnameField = textField(Fields.Prompts.Surname)
    val nameField = textField(Fields.Prompts.Name)
    val emailField = textField(Fields.Prompts.Email)
    val phoneField = textField(Fields.Prompts.Phone)
    val roleField =
      new ComboBox[String](AccountViewModel.roles):
        promptText = Fields.Prompts.SelectRole
        maxWidth = Double.MaxValue
        styleClass += UiStyles.Common.FormField
    val areaField = textField(Fields.Prompts.Area)
    val assignmentField = textField(Fields.Prompts.Assignment)
    val usernameField = textField(Fields.Prompts.Username)

    val passwordField =
      new PasswordField:
        promptText = Fields.Prompts.Password
        maxWidth = Double.MaxValue
        styleClass += UiStyles.Common.FormField

    val surnameError = fieldErrorLabel()
    val nameError = fieldErrorLabel()
    val emailError = fieldErrorLabel()
    val roleError = fieldErrorLabel()
    val usernameError = fieldErrorLabel()
    val passwordError = fieldErrorLabel()

    val resultMessage = messageLabel(UiStyles.Accounts.Message)
    val monitoredTextFields = Seq(surnameField, nameField, emailField, phoneField, areaField, assignmentField, usernameField, passwordField)
    val monitoredComboBoxes = Seq(roleField)

    def clearErrors(): Unit =
      clearFieldErrors(
        surnameField -> surnameError,
        nameField -> nameError,
        emailField -> emailError,
        roleField -> roleError,
        usernameField -> usernameError,
        passwordField -> passwordError
      )

      clearMessage(resultMessage, UiStyles.Accounts.MessageSuccess, UiStyles.Accounts.MessageError)

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

      if errors.contains(AccountViewModel.RoleRequiredError) then
        showFieldError(roleField, roleError, AccountViewModel.RoleRequiredError)

      showMappedErrors(errors):
        case AccountViewModel.SurnameRequiredError =>
          surnameField -> surnameError

        case AccountViewModel.NameRequiredError =>
          nameField -> nameError

        case AccountViewModel.EmailRequiredError |
             AccountViewModel.EmailInvalidError =>
          emailField -> emailError

        case AccountViewModel.UsernameRequiredError |
             AccountViewModel.DuplicateUsernameError =>
          usernameField -> usernameError

        case AccountViewModel.PasswordRequiredError =>
          passwordField -> passwordError

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
              if saved then Accounts.Add.Success
              else Accounts.Add.Error,
            success = saved,
            successStyle = UiStyles.Accounts.MessageSuccess,
            errorStyle = UiStyles.Accounts.MessageError
          )

          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          FormRow(Fields.Labels.required(Fields.Labels.Surname), surnameField, surnameError),
          FormRow(Fields.Labels.required(Fields.Labels.Name), nameField, nameError),
          FormRow(Fields.Labels.required(Fields.Labels.Email), emailField, emailError),
          FormRow(Fields.Labels.Phone, phoneField, fieldErrorLabel()),
          FormRow(Fields.Labels.required(Fields.Labels.Role), roleField, roleError),
          FormRow(Fields.Labels.Area, areaField, fieldErrorLabel()),
          FormRow(Fields.Labels.Assignment, assignmentField, fieldErrorLabel()),
          FormRow(Fields.Labels.required(Fields.Labels.Username), usernameField, usernameError),
          FormRow(Fields.Labels.required(Fields.Labels.Password), passwordField, passwordError)
        )
      )

    Platform.runLater:
      surnameField.requestFocus()

    formPage(
      titleText = Accounts.Add.Title,
      subtitleText = Accounts.Add.Subtitle,
      titleStyle = UiStyles.Accounts.Title,
      subtitleStyle = UiStyles.Accounts.Subtitle,
      rootStyle = UiStyles.Accounts.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () =>
        hasFormChanges(
          formSaved,
          monitoredTextFields,
          monitoredComboBoxes
        )
    )
