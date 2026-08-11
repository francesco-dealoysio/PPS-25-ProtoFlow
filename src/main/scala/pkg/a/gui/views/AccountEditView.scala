package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.a.gui.text.UiText.Accounts.{Edit as EditText, Profile as ProfileText}
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Validation.Account as Validation
import pkg.b.logic.{Account, Classification, Role}
import pkg.d.util.Util.{cipher, inDatabaseFilePathName}
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
    val roleLogic = new Role()
    val classificationLogic = new Classification()
    val viewModel = new AccountViewModel()
    val profileMode = mode == EditMode.Profile

    val surname = stringField(Prompts.Surname, selectedAccount.getSurname)
    val name = stringField(Prompts.Name, selectedAccount.getName)
    val email = stringField(Prompts.Email, selectedAccount.getEmail)
    val phone = stringField(Prompts.Phone, selectedAccount.getPhone)
    val role = stringComboField(roleLogic.getRecords[Role]().map(_.getRole.trim), Prompts.SelectRole, selectedAccount.getRole)
    val area = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), Prompts.Area, selectedAccount.getArea)
    val assignment = stringField(Prompts.Assignment, selectedAccount.getAssignment)
    val username = stringField(Prompts.Username, selectedAccount.getUsername)
    val password = passwordFormField(Prompts.KeepPassword)
    val accountFields: Seq[FormField[? <: Node]] = Seq(surname, name, email, phone, role, area, assignment, username, password)
    val profileFields: Seq[FormField[? <: Node]] = Seq(email, phone, password)

    val monitoredFields = if profileMode then profileFields else accountFields
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

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
          case Validation.EmailRequired | Validation.EmailInvalid=> email
      else
        showFormFieldErrors(errors):
          case Validation.SurnameRequired => surname
          case Validation.NameRequired => name
          case Validation.EmailRequired | Validation.EmailInvalid => email
          case Validation.RoleRequired => role
          case Validation.UsernameRequired | Validation.DuplicateUsername => username

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val updated = accountLogic.recordUpdate(currentAccount())
          val (successMsg, errorMsg) =
            if profileMode then (ProfileText.Success, ProfileText.Error)
            else (EditText.Success, EditText.Error)

          result.show(
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
        text = Buttons.Print,
        action = () =>
          val printed =
            XmlToPdf.printDetails(
              xmlPath = inDatabaseFilePathName("accounts.xml"),
              recordId = selectedAccount.getId,
              pdfFileName = s"account_${selectedAccount.getId}",
              title = EditText.PrintTitle
            )

          result.show(
            message = if printed then EditText.PrintSuccess else EditText.PrintError,
            success = printed
          )
      )

    val profileRows =
      Seq(
        formRow(Labels.required(Labels.Email), email),
        formRow(Labels.Phone, phone),
        formRow(Labels.Password, password)
      )

    val accountRows =
      Seq(
        formRow(Labels.required(Labels.Surname), surname),
        formRow(Labels.required(Labels.Name), name),
        formRow(Labels.required(Labels.Email), email),
        formRow(Labels.Phone, phone),
        formRow(Labels.required(Labels.Role), role),
        formRow(Labels.Area, area),
        formRow(Labels.Assignment, assignment),
        formRow(Labels.required(Labels.Username), username),
        formRow(Labels.Password, password)
      )

    val form = formGrid(if profileMode then profileRows else accountRows)

    val actions = Seq(Some(exit), Option.unless(profileMode)(print), Some(reset), Some(save)).flatten

    val (titleText, subtitleText) =
      if profileMode then (ProfileText.Title, ProfileText.Subtitle)
      else (EditText.Title, EditText.Subtitle)
    formPage(
      titleText = titleText,
      subtitleText =subtitleText,
      form = form,
      resultMessage = result.label,
      actions = actionBar(actions),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )