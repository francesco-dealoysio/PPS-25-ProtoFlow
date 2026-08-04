package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.a.gui.text.UiText
import pkg.a.gui.text.UiText.{Accounts, Common, Fields}
import pkg.b.logic.Account
import pkg.d.util.Util.{inDatabaseFilePathName, cipher}
import pkg.d.util.XmlToPdf
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane

object AccountEditView extends Form:

  private enum EditMode:
    case Account, Profile

  def apply(selectedAccount: Account, onSaved: () => Unit, onExit: () => Unit): BorderPane =
    edit(selectedAccount, onSaved, onExit, EditMode.Account)

  def profile(selectedAccount: Account, onSaved: () => Unit, onExit: () => Unit): BorderPane =
    edit(selectedAccount, onSaved, onExit, EditMode.Profile)

  private def edit(selectedAccount: Account, onSaved: () => Unit, onExit: () => Unit, mode: EditMode): BorderPane =

    val accountLogic = new Account()
    val viewModel = new AccountViewModel()
    val profileMode = mode == EditMode.Profile

    val surname = stringField(Fields.Prompts.Surname, selectedAccount.getSurname)
    val name = stringField(Fields.Prompts.Name, selectedAccount.getName)
    val email = stringField(Fields.Prompts.Email, selectedAccount.getEmail)
    val phone = stringField(Fields.Prompts.Phone, selectedAccount.getPhone)
    val role = stringComboField(AccountViewModel.roles, Fields.Prompts.SelectRole, selectedAccount.getRole)
    val area = stringField(Fields.Prompts.Area, selectedAccount.getArea)
    val assignment = stringField(Fields.Prompts.Assignment, selectedAccount.getAssignment)
    val username = stringField(Fields.Prompts.Username, selectedAccount.getUsername)
    val password = passwordFormField(Fields.Prompts.KeepPassword)
    val accountFields: Seq[FormField[? <: Node]] = Seq(surname, name, email, phone, role, area, assignment, username, password)
    val profileFields: Seq[FormField[? <: Node]] = Seq(email, phone, password)

    val monitoredFields = if profileMode then profileFields else accountFields
    val resultMessage = messageLabel()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      clearMessage(resultMessage)

    def currentAccount(): Account =
      val updatedPassword =
        password.value match
          case "" =>
            selectedAccount.getPassword

          case raw =>
            cipher(raw)

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
      resetFields(monitoredFields*)
      clearErrors()

      if profileMode then email.requestFocus()
      else surname.requestFocus()

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        if profileMode then
          viewModel.validateProfile(email.value)
        else
          viewModel.validate(
            account = currentAccount(),
            rawPassword = password.value,
            existingAccounts = accountLogic.getRecords(),
            currentAccountId = Some(selectedAccount.getId),
            requirePassword = false
          )

      if profileMode then
        showFormFieldErrors(errors):
          case AccountViewModel.EmailRequiredError |
               AccountViewModel.EmailInvalidError =>
            email
      else
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
          val (successMsg, errorMsg) =
            if profileMode then (Accounts.Profile.Success, Accounts.Profile.Error)
            else (Accounts.Edit.Success, Accounts.Edit.Error)
          showMessage(
            label = resultMessage,
            message = if updated then successMsg else errorMsg,
            success = updated
          )

          if updated then
            formSaved = true
            if profileMode then
              selectedAccount.setEmail(email.value)
              selectedAccount.setPhone(phone.value)
              if password.value.nonEmpty then
                selectedAccount.setPassword(password.value)

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
            message = if printed then Accounts.Edit.PrintSuccess else Accounts.Edit.PrintError,
            success = printed
          )
      )

    val profileRows =
      Seq(
        formRow(Fields.Labels.required(Fields.Labels.Email), email),
        formRow(Fields.Labels.Phone, phone),
        formRow(Fields.Labels.Password, password)
      )

    val accountRows =
      Seq(
        formRow(Fields.Labels.required(Fields.Labels.Surname), surname),
        formRow(Fields.Labels.required(Fields.Labels.Name), name),
        formRow(Fields.Labels.required(Fields.Labels.Email), email),
        formRow(Fields.Labels.Phone, phone),
        formRow(Fields.Labels.required(Fields.Labels.Role), role),
        formRow(Fields.Labels.Area, area),
        formRow(Fields.Labels.Assignment, assignment),
        formRow(Fields.Labels.required(Fields.Labels.Username), username),
        formRow(Fields.Labels.Password, password)
      )

    val form = formGrid(if profileMode then profileRows else accountRows)

    val actions = Seq(Some(exit), Option.unless(profileMode)(print), Some(reset), Some(save)).flatten

    val (titleText, subtitleText) =
      if profileMode then (Accounts.Profile.Title, Accounts.Profile.Subtitle)
      else (Accounts.Edit.Title, Accounts.Edit.Subtitle)
    formPage(
      titleText = titleText,
      subtitleText =subtitleText,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(actions),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )