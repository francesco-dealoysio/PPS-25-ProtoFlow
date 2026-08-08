package pkg.a.gui.views

import pkg.a.gui.structures.AccountViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.{Account, Classification, Role}
import pkg.d.util.IdGen
import pkg.d.util.Util.{inIdsFilePathName, cipher}
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Accounts.Add as Text
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Validation.Account as Validation

object AccountAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val accountLogic = new Account()
    val roleLogic = new Role()
    val classificationLogic = new Classification()
    val viewModel = new AccountViewModel()

    val surname = stringField(Prompts.Surname)
    val name = stringField(Prompts.Name)
    val email = stringField(Prompts.Email)
    val phone = stringField(Prompts.Phone)
    val role = stringComboField(roleLogic.getRecords[Role]().map(_.getRole.trim), Prompts.SelectRole)
    val area = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), Prompts.Area)
    val assignment = stringField(Prompts.Assignment)
    val username = stringField(Prompts.Username)
    val password = passwordFormField(Prompts.Password)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(surname, name, email, phone, role, area, assignment, username, password)
    val resultMessage = messageLabel()

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      clearMessage(resultMessage)

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
        password = cipher(password.value)
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
          showMessage(
            label = resultMessage,
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

    Platform.runLater:
      surname.requestFocus()

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )