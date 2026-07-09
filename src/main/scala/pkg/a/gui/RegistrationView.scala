package pkg.a.gui

import pkg.c.data.guiStructures.{RegistrationRequest, RegistrationViewModel}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, ComboBox, Label, TextArea, TextField}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Priority, Region, VBox}

object RegistrationView:

  def apply(
             viewModel: RegistrationViewModel,
             onExit: () => Unit = () => ()
           ): BorderPane =

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

    val messageArea = new TextArea:
      editable = false
      wrapText = true
      visible.value = false
      managed.value = false
      styleClass += "form-message"

    def currentRequest(): RegistrationRequest =
      RegistrationRequest(
        name = nameField.text.value,
        surname = surnameField.text.value,
        email = emailField.text.value,
        phone = phoneField.text.value,
        requestedRole = Option(roleCombo.value.value).getOrElse(""),
        requestedArea = Option(areaCombo.value.value).getOrElse(""),
        assignment = assignmentField.text.value
      )

    def showMessage(message: String, success: Boolean): Unit =
      messageArea.text.value = message
      messageArea.visible.value = true
      messageArea.managed.value = true

      messageArea.styleClass.removeAll(
        "form-message-success",
        "form-message-error"
      )

      if success then
        messageArea.styleClass += "form-message-success"
      else
        messageArea.styleClass += "form-message-error"

    def resetForm(): Unit =
      nameField.clear()
      surnameField.clear()
      emailField.clear()
      phoneField.clear()
      assignmentField.clear()

      roleCombo.selectionModel.value.clearSelection()
      areaCombo.selectionModel.value.clearSelection()

      messageArea.clear()
      messageArea.visible.value = false
      messageArea.managed.value = false

      messageArea.styleClass.removeAll(
        "form-message-success",
        "form-message-error"
      )

    val submitButton = new Button("Invio richiesta"):
      styleClass += "primary-button"
      onAction = _ =>
        val request = currentRequest()
        val errors = viewModel.validate(request)

        if errors.isEmpty then
          showMessage("Richiesta presa in carico.", success = true)
        else
          showMessage(
            errors.mkString("Errori riscontrati:\n- ", "\n- ", ""),
            success = false
          )

    val resetButton = new Button("Reset"):
      styleClass += "secondary-button"
      onAction = _ => resetForm()

    val exitButton = new Button("Annulla"):
      styleClass += "secondary-button"
      onAction = _ => onExit()

    val formGrid = new GridPane:
      hgap = 18
      vgap = 14
      styleClass += "registration-grid"

      add(formLabel("Nome *"), 0, 0)
      add(nameField, 0, 1)

      add(formLabel("Cognome *"), 1, 0)
      add(surnameField, 1, 1)

      add(formLabel("Indirizzo email *"), 0, 2)
      add(emailField, 0, 3)

      add(formLabel("Telefono"), 1, 2)
      add(phoneField, 1, 3)

      add(formLabel("Ruolo richiesto *"), 0, 4)
      add(roleCombo, 0, 5)

      add(formLabel("Area/Settore di appartenenza *"), 1, 4)
      add(areaCombo, 1, 5)

      add(formLabel("Incarico *"), 0, 6)
      add(assignmentField, 0, 7)

    val buttonsBox = new HBox:
      alignment = Pos.CenterRight
      spacing = 12
      children = Seq(exitButton, resetButton, submitButton)

    val card = new VBox:
      maxWidth = Double.MaxValue
      maxHeight = Double.MaxValue
      styleClass += "registration-card"
      children = Seq(
        new Label("Registrazione"):
          styleClass += "registration-title"
        ,
        new Label("Compila il modulo per richiedere l'accreditamento al sistema ProtoFlow."):
          styleClass += "registration-subtitle"
        ,
        formGrid,
        messageArea,
        buttonsBox
      )

    new BorderPane:
      styleClass += "registration-root"
      center = card

  private def formLabel(text: String): Label =
    new Label(text):
      styleClass += "form-label"