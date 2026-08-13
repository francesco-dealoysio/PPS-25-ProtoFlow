package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.a.gui.text.UiText.Accounts.{Edit as EditText, Profile as ProfileText}
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Validation.Account as Validation
import pkg.a.gui.validation.AccountValidator
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
    val validator = new AccountValidator()
    val profileMode = mode == EditMode.Profile

    val id = stringField("", selectedAccount.getId)
    id.control.setDisable(true)
    val surname = stringField("", selectedAccount.getSurname)
    val name = stringField("", selectedAccount.getName)
    val email = stringField("", selectedAccount.getEmail)
    val phone = stringField("", selectedAccount.getPhone)
    val role = stringComboField(roleLogic.getRecords[Role]().map(_.getRole.trim), "", selectedAccount.getRole)
    val area = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), "", selectedAccount.getArea)
    val assignment = stringField("", selectedAccount.getAssignment)
    val username = stringField("", selectedAccount.getUsername)
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
          validator.validateProfile(email.value)
        else
          validator.validate(
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
        formRow(Labels.Email, email),
        formRow(Labels.Phone, phone),
        formRow(Labels.Password, password)
      )

    val accountRows =
      Seq(
        formRow(Labels.Id, id),
        formRow(Labels.Surname, surname),
        formRow(Labels.Name, name),
        formRow(Labels.Email, email),
        formRow(Labels.Phone, phone),
        formRow(Labels.Role, role),
        formRow(Labels.Area, area),
        formRow(Labels.Assignment, assignment),
        formRow(Labels.Username, username),
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