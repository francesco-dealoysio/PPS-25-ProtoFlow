package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Account
import pkg.d.util.Util.{inDatabaseFilePathName, md5}
import pkg.d.util.XmlToPdf
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText
import UiText.{Accounts, Common, Fields}
import scalafx.scene.Node

object AccountEditView extends Form:

  def apply(selectedAccount: Account, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()

    val surname =  stringField(Fields.Prompts.Surname, selectedAccount.getSurname)
    val name = stringField(Fields.Prompts.Name, selectedAccount.getName)
    val email = stringField(Fields.Prompts.Email, selectedAccount.getEmail)
    val phone = stringField(Fields.Prompts.Phone, selectedAccount.getPhone)
    val role = stringComboField(AccountViewModel.roles, Fields.Prompts.SelectRole, selectedAccount.getRole)
    val area = stringField(Fields.Prompts.Area, selectedAccount.getArea)
    val assignment = stringField(Fields.Prompts.Assignment, selectedAccount.getAssignment)
    val username = stringField(Fields.Prompts.Username, selectedAccount.getUsername)
    val password = passwordFormField(Fields.Prompts.KeepPassword)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(surname, name, email, phone, role, area, assignment, username, password)
    val resultMessage = messageLabel()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      clearMessage(resultMessage)

    def currentAccount(): Account =
      val updatedPassword =
        password.value match
          case "" => selectedAccount.getPassword
          case raw => md5(raw)

      Account(
        id = selectedAccount.getId,
        surname = surname.value,
        name = name.value,
        email = email.value,
        phone = phone.value,
        role = role.value,
        area = area.value,
        assignment = assignment.value,
        username = username.value,
        password = updatedPassword
      )

    def resetForm(): Unit =
      resetFields(monitoredFields *)
      clearErrors()
      surname.requestFocus()

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        viewModel.validate(
          account = currentAccount(),
          rawPassword = password.value,
          existingAccounts = accountLogic.getRecords(),
          currentAccountId = Some(selectedAccount.getId),
          requirePassword = false
        )

      showFormFieldErrors(errors):
        case AccountViewModel.SurnameRequiredError => surname
        case AccountViewModel.NameRequiredError => name
        case AccountViewModel.EmailRequiredError | AccountViewModel.EmailInvalidError => email
        case AccountViewModel.RoleRequiredError => role
        case AccountViewModel.UsernameRequiredError | AccountViewModel.DuplicateUsernameError => username

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
            success = updated
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
            success = printed
          )
      )

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
          formRow(Fields.Labels.Password, password))
      )

    formPage(
      titleText = Accounts.Edit.Title,
      subtitleText = Accounts.Edit.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, print, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )
