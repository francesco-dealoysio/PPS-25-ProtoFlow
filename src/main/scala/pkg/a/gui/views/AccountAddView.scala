package pkg.a.gui.views

import pkg.a.gui.traits.Form
import pkg.b.logic.{Account, Classification, Role}
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, cipher}
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Accounts.Add as Text
import pkg.a.gui.text.UiText.Common.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Validation.Account as Validation
import pkg.a.gui.validation.AccountValidator

object AccountAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val accountLogic = new Account()
    val roleLogic = new Role()
    val roles = roleLogic.getRecords[Role]()
    val classificationLogic = new Classification()
    val validator = new AccountValidator()

    val surname = stringField(Prompts.Surname)
    val name = stringField(Prompts.Name)
    val email = stringField(Prompts.Email)
    val phone = stringField(Prompts.Phone)
    val role = stringComboField(roles.map(_.getName.trim), Prompts.SelectRole)
    val area = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), Prompts.Area)
    val assignment = stringField(Prompts.Assignment)
    val username = stringField(Prompts.Username)
    val password = passwordFormField(Prompts.Password)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(surname, name, email, phone, role, area, assignment, username, password)
    val result = createResultMessage()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def currentAccount(id: String = ""): Account =
      val selectedRole = roles.find(_.getName.trim == role.value).map(_.getRole).getOrElse("")
      Account(
        id = id,
        surname = surname.value,
        name = name.value,
        email = email.value,
        phone = phone.value,
        role = selectedRole,
        area = area.value,
        assignment = assignment.value,
        username = username.value,
        password = cipher(password.value)
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        validator.validate(
          account = currentAccount(),
          rawPassword = password.value,
          existingAccounts = accountLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case Validation.SurnameRequired => surname
        case Validation.NameRequired => name
        case Validation.EmailRequired | Validation.EmailInvalid => email
        case Validation.RoleRequired => role
        case Validation.UsernameRequired| Validation.DuplicateUsername => username
        case Validation.PasswordRequired => password

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
          result.show(
            message = if saved then Text.Success else Text.Error,
            success = saved
          )

          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Labels.required(Labels.Surname), surname),
          formRow(Labels.required(Labels.Name), name),
          formRow(Labels.required(Labels.Email), email),
          formRow(Labels.Phone, phone),
          formRow(Labels.required(Labels.Role), role),
          formRow(Labels.Area, area),
          formRow(Labels.Assignment, assignment),
          formRow(Labels.required(Labels.Username), username),
          formRow(Labels.required(Labels.Password), password)
        )
      )


    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      initialFocus = Some(surname),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )