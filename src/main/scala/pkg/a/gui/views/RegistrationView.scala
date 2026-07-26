package pkg.a.gui.views

import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.text.UiText.{Common, Fields, Registration}
import pkg.a.gui.structures.RegistrationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.{Registration => RegistrationModel, RegistrationRequestService}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ComboBox}
import scalafx.scene.layout.{BorderPane, GridPane}

object RegistrationView extends Form:

  def apply(
             viewModel: RegistrationViewModel,
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()

    val nameField = textField(Registration.NamePrompt)
    val surnameField = textField(Registration.SurnamePrompt)
    val emailField = textField(Registration.EmailPrompt)
    val phoneField = textField(Registration.PhonePrompt)

    val roleCombo = new ComboBox[String]:
      items = ObservableBuffer("Viewer", "Operatore Protocollo", "Amministratore")
      promptText = Registration.RolePrompt
      styleClass += UiStyles.Common.FormField

    val areaCombo = new ComboBox[String]:
      items = ObservableBuffer(
        "Urbanistica",
        "Personale",
        "Amministrazione",
        "Segreteria",
        "Finanziario",
        "Area Tecnica"
      )
      promptText = Registration.AreaPrompt
      styleClass += UiStyles.Common.FormField

    val assignmentField = textField(Registration.AssignmentPrompt)

    val monitoredTextFields = Seq(
      nameField,
      surnameField,
      emailField,
      phoneField,
      assignmentField
    )

    val monitoredComboBoxes = Seq(roleCombo, areaCombo)

    val resultMessage = messageLabel(UiStyles.Registration.Message)
    var formSaved = false

    def currentRequest(): RegistrationModel =
      RegistrationModel(
        id = "",
        name = nameField.text.value,
        surname = surnameField.text.value,
        email = emailField.text.value,
        phone = phoneField.text.value,
        role = Option(roleCombo.value.value).getOrElse(""),
        area = Option(areaCombo.value.value).getOrElse(""),
        assignment = assignmentField.text.value
      )

    def hasChanges: Boolean =
      hasFormChanges(
        formSaved = formSaved,
        textFields = monitoredTextFields,
        comboBoxes = monitoredComboBoxes
      )

    def clearFields(): Unit =
      nameField.clear()
      surnameField.clear()
      emailField.clear()
      phoneField.clear()
      assignmentField.clear()

      roleCombo.selectionModel.value.clearSelection()
      areaCombo.selectionModel.value.clearSelection()

      nameField.requestFocus()

    def resetForm(): Unit =
      clearFields()
      formSaved = false
      clearMessage(
        resultMessage,
        successStyle = UiStyles.Registration.MessageSuccess,
        errorStyle = UiStyles.Registration.MessageError
      )

    def submitRequest(): Unit =
      val request = currentRequest()
      val errors = viewModel.validate(request)

      if errors.nonEmpty then
        showMessage(
          label = resultMessage,
          message = errors.mkString(
            Registration.ValidationHeader,
            Registration.ValidationSeparator,
            ""
          ),
          success = false,
          successStyle = UiStyles.Registration.MessageSuccess,
          errorStyle = UiStyles.Registration.MessageError
        )
      else
        service.submitRequest(
          name = request.getName,
          surname = request.getSurname,
          email = request.getEmail,
          phone = request.getPhone,
          requestedRole = request.getRole,
          requestedArea = request.getArea,
          assignment = request.getAssignment
        ) match
          case Right(_) =>
            formSaved = true
            clearFields()

            showMessage(
              resultMessage,
              Registration.SubmitSuccess,
              success = true,
              UiStyles.Registration.MessageSuccess,
              UiStyles.Registration.MessageError
            )

          case Left(error) =>
            showMessage(
              resultMessage,
              error,
              success = false,
              UiStyles.Registration.MessageSuccess,
              UiStyles.Registration.MessageError
            )

    val submit = primaryButton(Common.Buttons.RequestRegistration, () => submitRequest())
    val reset = resetButton(() => resetForm())

    def exitRegistration(): Unit =
      val canExit =
        if hasChanges then
          askConfirmation(
            titleText = Registration.ExitDialog.Title,
            header = Registration.ExitDialog.Header,
            content = Registration.ExitDialog.Content
          )
        else
          true

      if canExit then onExit()

    val exit = closeButton(onExit = () => exitRegistration(), text = Common.Buttons.Close)

    val formGrid = new GridPane:
      hgap = 18
      vgap = 14
      styleClass += UiStyles.Registration.Grid

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Name)), 0, 0)
      add(nameField, 0, 1)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Surname)), 1, 0)
      add(surnameField, 1, 1)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Email)), 0, 2)
      add(emailField, 0, 3)

      add(fieldLabel(Fields.Labels.Phone), 1, 2)
      add(phoneField, 1, 3)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Role)), 0, 4)
      add(roleCombo, 0, 5)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Area)), 1, 4)
      add(areaCombo, 1, 5)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Assignment)), 0, 6)
      add(assignmentField, 0, 7)

    val buttonsBox = actionBar(Seq(exit, reset, submit))

    formPage(
      titleText = Registration.Title,
      subtitleText = Registration.Subtitle,
      titleStyle = UiStyles.Registration.Title,
      subtitleStyle = UiStyles.Registration.Subtitle,
      rootStyle = UiStyles.Registration.Root,
      contentStyle = Some(UiStyles.Registration.Card),
      form = formGrid,
      resultMessage = resultMessage,
      actions = buttonsBox
    )