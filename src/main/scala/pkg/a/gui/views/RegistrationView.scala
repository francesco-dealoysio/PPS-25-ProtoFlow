package pkg.a.gui.views

import pkg.a.gui.text.UiStyles
import pkg.a.gui.text.UiText.{Common, Fields, Registration}
import pkg.a.gui.structures.RegistrationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.{RegistrationRequestService, Registration as RegistrationModel}
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, GridPane}

object RegistrationView extends Form:

  def apply(
             viewModel: RegistrationViewModel,
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()

    val name = stringField(Registration.NamePrompt)
    val surname = stringField(Registration.SurnamePrompt)
    val email = stringField(Registration.EmailPrompt)
    val phone = stringField(Registration.PhonePrompt)
    val role = stringComboField(Seq("Viewer", "Operatore Protocollo", "Amministratore"), Registration.RolePrompt)
    val area = stringComboField(Seq("Urbanistica", "Personale", "Amministrazione", "Segreteria", "Finanziario", "Area Tecnica"), Registration.AreaPrompt)
    val assignment = stringField(Registration.AssignmentPrompt)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(name, surname, email, phone, role, area, assignment)
    val resultMessage = messageLabel(UiStyles.Registration.Message)

    def currentRequest(): RegistrationModel =
      RegistrationModel(
        id = "",
        name = name.value,
        surname = surname.value,
        email = email.value,
        phone = phone.value,
        role = role.value,
        area = area.value,
        assignment = assignment.value
      )

    def hasChanges: Boolean =
      hasFormChanges(
        formSaved = false,
        fields = monitoredFields
      )

    def clearFields(): Unit =
      resetFields(monitoredFields*)
      name.requestFocus()

    def resetForm(): Unit =
      clearFields()
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
      add(name.control, 0, 1)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Surname)), 1, 0)
      add(surname.control, 1, 1)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Email)), 0, 2)
      add(email.control, 0, 3)

      add(fieldLabel(Fields.Labels.Phone), 1, 2)
      add(phone.control, 1, 3)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Role)), 0, 4)
      add(role.control, 0, 5)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Area)), 1, 4)
      add(area.control, 1, 5)

      add(fieldLabel(Fields.Labels.required(Fields.Labels.Assignment)), 0, 6)
      add(assignment.control, 0, 7)

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