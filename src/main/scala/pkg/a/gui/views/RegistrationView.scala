package pkg.a.gui.views

import pkg.a.gui.structures.RegistrationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.{Registration, RegistrationRequestService}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ComboBox}
import scalafx.scene.layout.{BorderPane, GridPane}

object RegistrationView extends Form:

  def apply(
             viewModel: RegistrationViewModel,
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()

    val nameField = textField("Inserisci il nome")
    val surnameField = textField("Inserisci il cognome")
    val emailField = textField("nome.cognome@email.it")
    val phoneField = textField("Inserisci il telefono")

    val roleCombo = new ComboBox[String]:
      items = ObservableBuffer("Viewer", "Operatore Protocollo", "Amministratore")
      promptText = "Seleziona ruolo"
      styleClass += "form-field"

    val areaCombo = new ComboBox[String]:
      items = ObservableBuffer(
        "Urbanistica",
        "Personale",
        "Amministrazione",
        "Segreteria",
        "Finanziario",
        "Area Tecnica"
      )
      promptText = "Seleziona area"
      styleClass += "form-field"

    val assignmentField = textField("Inserisci incarico")

    val monitoredTextFields = Seq(
        nameField,
        surnameField,
        emailField,
        phoneField,
        assignmentField
      )

    val monitoredComboBoxes = Seq(roleCombo, areaCombo)

    val resultMessage = messageLabel("form-message")
    var formSaved = false
    def currentRequest(): Registration =
      Registration(
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
      clearMessage(resultMessage, successStyle = "form-message-success", errorStyle = "form-message-error")

    def submitRequest(): Unit =
      val request = currentRequest()
      val errors = viewModel.validate(request)

      if errors.nonEmpty then
        showMessage(
          label = resultMessage,
          message =
            errors.mkString(
              "Errori riscontrati:\n- ",
              "\n- ",
              ""
            ),
          success = false,
          successStyle = "form-message-success",
          errorStyle = "form-message-error"
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
              "Richiesta presa in carico e salvata correttamente.",
              success = true,
              "form-message-success",
              "form-message-error"
            )

          case Left(error) =>
            showMessage(
              resultMessage,
              error,
              success = false,
              "form-message-success",
              "form-message-error"
            )

    val submit = primaryButton("Invio richiesta", () => submitRequest())
    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit = onExit, text = "Annulla")

    val formGrid = new GridPane:
      hgap = 18
      vgap = 14
      styleClass += "registration-grid"

      add(fieldLabel("Nome *"), 0, 0)
      add(nameField, 0, 1)

      add(fieldLabel("Cognome *"), 1, 0)
      add(surnameField, 1, 1)

      add(fieldLabel("Indirizzo email *"), 0, 2)
      add(emailField, 0, 3)

      add(fieldLabel("Telefono"), 1, 2)
      add(phoneField, 1, 3)

      add(fieldLabel("Ruolo richiesto *"), 0, 4)
      add(roleCombo, 0, 5)

      add(fieldLabel("Area/Settore di appartenenza *"), 1, 4)
      add(areaCombo, 1, 5)

      add(fieldLabel("Incarico *"), 0, 6)
      add(assignmentField, 0, 7)

    val buttonsBox = actionBar(Seq(exit, reset, submit))

    formPage(
      titleText = "Registrazione",
      subtitleText = "Compila il modulo per richiedere l'accreditamento al sistema ProtoFlow.",
      titleStyle = "registration-title",
      subtitleStyle = "registration-subtitle",
      rootStyle = "registration-root",
      contentStyle = Some("registration-card"),
      form = formGrid,
      resultMessage = resultMessage,
      actions = buttonsBox,
      hasUnsavedChanges = () => hasChanges
    )