package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, md5}
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane
import UiText.{Accounts, Fields}

object AccountAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()

    val surname = stringField(Fields.Prompts.Surname)
    val name = stringField(Fields.Prompts.Name)
    val email = stringField(Fields.Prompts.Email)
    val phone = stringField(Fields.Prompts.Phone)
    val role = stringComboField(AccountViewModel.roles, Fields.Prompts.SelectRole)
    val area = stringField(Fields.Prompts.Area)
    val assignment = stringField(Fields.Prompts.Assignment)
    val username = stringField(Fields.Prompts.Username)
    val password = passwordFormField(Fields.Prompts.Password)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(surname, name, email, phone, role, area, assignment, username, password)
    val resultMessage = messageLabel(UiStyles.Accounts.Message)

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      clearMessage(
        resultMessage,
        UiStyles.Accounts.MessageSuccess,
        UiStyles.Accounts.MessageError
      )

    def currentAccount(id: String = ""): Account =
      Account(
        id = id,
        surname = surname.value,
        name = name.value,
        email = email.value,
        phone = phone.value,
        role = role.value,
        area = area.value,
        assignment = assignment.value,
        username = username.value,
        password = md5(password.value)
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        viewModel.validate(
          account = currentAccount(),
          rawPassword = password.value,
          existingAccounts = accountLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case AccountViewModel.SurnameRequiredError => surname
        case AccountViewModel.NameRequiredError => name
        case AccountViewModel.EmailRequiredError | AccountViewModel.EmailInvalidError => email
        case AccountViewModel.RoleRequiredError => role
        case AccountViewModel.UsernameRequiredError | AccountViewModel.DuplicateUsernameError => username
        case AccountViewModel.PasswordRequiredError => password

    def resetForm(): Unit =
      resetFields(monitoredFields*)
      clearErrors()
      surname.requestFocus()

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
          formRow(Fields.Labels.required(Fields.Labels.Surname), surname),
          formRow(Fields.Labels.required(Fields.Labels.Name), name),
          formRow(Fields.Labels.required(Fields.Labels.Email), email),
          formRow(Fields.Labels.Phone, phone),
          formRow(Fields.Labels.required(Fields.Labels.Role), role),
          formRow(Fields.Labels.Area, area),
          formRow(Fields.Labels.Assignment, assignment),
          formRow(Fields.Labels.required(Fields.Labels.Username), username),
          formRow(Fields.Labels.required(Fields.Labels.Password), password)
        )
      )

    Platform.runLater:
      surname.requestFocus()

    formPage(
      titleText = Accounts.Add.Title,
      subtitleText = Accounts.Add.Subtitle,
      titleStyle = UiStyles.Accounts.Title,
      subtitleStyle = UiStyles.Accounts.Subtitle,
      rootStyle = UiStyles.Accounts.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )