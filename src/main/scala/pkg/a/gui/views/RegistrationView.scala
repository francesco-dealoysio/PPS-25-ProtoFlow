package pkg.a.gui.views

import pkg.a.gui.services.RegistrationRequestService
import pkg.a.gui.text.UiStyles.Registration.*
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.Common.Fields.Prompts
import pkg.a.gui.text.UiText.Registration as Text
import pkg.a.gui.text.UiText.Common.Dialogs
import pkg.a.gui.traits.Form
import pkg.a.gui.validation.RegistrationValidator
import pkg.b.logic.{Classification, Role, Registration as RegistrationModel}
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, GridPane}

object RegistrationView extends Form:

  def apply(validator: RegistrationValidator, onExit: () => Unit = () => ()): BorderPane =

    val service = new RegistrationRequestService()
    val roleLogic = new Role()
    val classificationLogic = new Classification()
    val name = stringField(prompt = Prompts.Name)
    val surname = stringField(prompt = Prompts.Surname)
    val email = stringField(prompt = Text.EmailPrompt)
    val phone = stringField(prompt = Prompts.Phone)
    val role = stringComboField(roleLogic.getRecords[Role]().map(_.getRole.trim), prompt =Text.RolePrompt)
    val area = stringComboField(classificationLogic.getRecords[Classification]().map(_.getClassification.trim), prompt = Text.AreaPrompt)
    val assignment = stringField(prompt = Text.AssignmentPrompt)

    val monitoredFields: Seq[FormField[? <: Node]] = Seq(name, surname, email, phone, role, area, assignment)
    val result = createResultMessage()

    def currentRequest(): RegistrationModel =
      RegistrationModel(
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
      result.clear()

    def submitRequest(): Unit =
      val request = currentRequest()
      val errors = validator.validate(request)

      if errors.nonEmpty then
        result.show(
          message = errors.mkString(
            Text.ValidationHeader,
            Text.ValidationSeparator,
            ""
          ),
          success = false
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
            result.show(
              message = Text.SubmitSuccess,
              success = true
            )

          case Left(error) =>
            result.show(
              message = error,
              success = false
            )

    val submit = primaryButton(Buttons.RequestRegistration, submitRequest)
    val reset = resetButton(resetForm)

    def exitRegistration(): Unit =
      val canExit =
        if hasChanges then
          askConfirmation(
            titleText = Dialogs.UnsavedChanges.Title,
            header = Dialogs.UnsavedChanges.Header,
            content = Text.ExitDialog.Content
          )
        else
          true

      if canExit then onExit()

    val exit = closeButton(exitRegistration, Buttons.Close)

    val formGrid = new GridPane:
      hgap = 18
      vgap = 14
      styleClass += GridStyle

      add(fieldLabel(Labels.required(Labels.Name)), 0, 0)
      add(name.control, 0, 1)

      add(fieldLabel(Labels.required(Labels.Surname)), 1, 0)
      add(surname.control, 1, 1)

      add(fieldLabel(Labels.required(Labels.Email)), 0, 2)
      add(email.control, 0, 3)

      add(fieldLabel(Labels.Phone), 1, 2)
      add(phone.control, 1, 3)

      add(fieldLabel(Labels.required(Labels.Role)), 0, 4)
      add(role.control, 0, 5)

      add(fieldLabel(Labels.required(Labels.Area)), 1, 4)
      add(area.control, 1, 5)

      add(fieldLabel(Labels.required(Labels.Assignment)), 0, 6)
      add(assignment.control, 0, 7)

    val buttonsBox = actionBar(Seq(exit, reset, submit))

    formPage(
      header = FormHeader(Text.Title, Text.Subtitle),
      form = formGrid,
      resultMessage = result.label,
      actions = buttonsBox,
      initialFocus = Some(name),
      config = PageConfig(contentStyle = Some(CardStyle))
    )