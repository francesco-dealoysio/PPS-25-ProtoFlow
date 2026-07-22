package pkg.a.gui.views

import pkg.a.gui.structures.{RegistrationRequest, RegistrationViewModel}
import pkg.a.gui.traits.Form
import pkg.b.logic.RegistrationRequestService
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ComboBox, TextField}
import scalafx.scene.layout.{BorderPane, GridPane}

object RegistrationView extends Form:

  def apply(
             viewModel: RegistrationViewModel,
             onExit: () => Unit = () => ()
           ): BorderPane =

    val service = new RegistrationRequestService()
    
    val nameField = new TextField:
      promptText = "Inserisci il nome"
      styleClass += "form-field"

    val surnameField = new TextField:
      promptText = "Inserisci il cognome"
      styleClass += "form-field"

    val emailField = new TextField:
      promptText = "nome.cognome@email.it"
      styleClass += "form-field"

    val phoneField = new TextField:
      promptText = "Inserisci il telefono"
      styleClass += "form-field"

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

    val assignmentField = new TextField:
      promptText = "Inserisci incarico"
      styleClass += "form-field"

    val resultMessage = messageLabel("form-message")

    def currentRequest(): RegistrationRequest =
      RegistrationRequest(
        id = "",
        name = nameField.text.value,
        surname = surnameField.text.value,
        email = emailField.text.value,
        phone = phoneField.text.value,
        requestedRole = Option(roleCombo.value.value).getOrElse(""),
        requestedArea = Option(areaCombo.value.value).getOrElse(""),
        assignment = assignmentField.text.value
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
          name = request.name,
          surname = request.surname,
          email = request.email,
          phone = request.phone,
          requestedRole = request.requestedRole,
          requestedArea = request.requestedArea,
          assignment = request.assignment
        ) match
          case Right(_) =>
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

    val buttonsBox = actionBar(exit, reset, submit)

    formPage(
      titleText = "Registrazione",
      subtitleText = "Compila il modulo per richiedere l'accreditamento al sistema ProtoFlow.",
      titleStyle = "registration-title",
      subtitleStyle = "registration-subtitle",
      rootStyle = "registration-root",
      contentStyle = Some("registration-card"),
      form = formGrid,
      resultMessage = resultMessage,
      actions = buttonsBox
    )