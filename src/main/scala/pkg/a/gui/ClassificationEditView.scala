package pkg.a.gui

import pkg.b.logic.Classification
import pkg.c.data.guiStructures.ClassificationViewModel
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*

object ClassificationEditView:

  def apply(
             selectedClassification: Classification,
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = ClassificationViewModel()

    /*
     * Conserviamo i valori originari.
     * Il pulsante Reset deve ripristinare questi,
     * non svuotare semplicemente il form.
     */
    val initialClassification =
      selectedClassification.getClassification

    val initialDescription =
      selectedClassification.getDescription

    val classificationField =
      new TextField:
        text = initialClassification
        promptText = "Inserisci la classifica"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val descriptionArea =
      new TextArea:
        text = initialDescription
        promptText = "Inserisci la descrizione"
        wrapText = true
        prefRowCount = 5
        maxWidth = Double.MaxValue
        styleClass += "classification-description-area"

    val classificationError =
      new Label:
        visible = false
        managed = false
        wrapText = true
        styleClass += "field-error"

    val descriptionError =
      new Label:
        visible = false
        managed = false
        wrapText = true
        styleClass += "field-error"

    val resultMessage =
      new Label:
        visible = false
        managed = false
        wrapText = true
        maxWidth = Double.MaxValue
        styleClass += "classifications-message"

    def showResult(
                    message: String,
                    success: Boolean
                  ): Unit =
      resultMessage.text = message
      resultMessage.visible = true
      resultMessage.managed = true

      resultMessage.styleClass.removeAll(
        "classifications-message-success",
        "classifications-message-error"
      )

      resultMessage.styleClass +=
        (if success then
          "classifications-message-success"
        else
          "classifications-message-error")

    def clearErrors(): Unit =
      classificationError.text = ""
      classificationError.visible = false
      classificationError.managed = false

      descriptionError.text = ""
      descriptionError.visible = false
      descriptionError.managed = false

      resultMessage.text = ""
      resultMessage.visible = false
      resultMessage.managed = false

      classificationField.styleClass.remove(
        "form-field-error"
      )

      descriptionArea.styleClass.remove(
        "form-field-error"
      )

    def showClassificationError(
                                 message: String
                               ): Unit =
      classificationError.text = message
      classificationError.visible = true
      classificationError.managed = true

      if !classificationField.styleClass.contains(
        "form-field-error"
      )
      then
        classificationField.styleClass +=
          "form-field-error"

    def showDescriptionError(
                              message: String
                            ): Unit =
      descriptionError.text = message
      descriptionError.visible = true
      descriptionError.managed = true

      if !descriptionArea.styleClass.contains(
        "form-field-error"
      )
      then
        descriptionArea.styleClass +=
          "form-field-error"


    def currentClassification(): Classification =
      Classification(
        id = selectedClassification.getId,
        classification = classificationField.text.value.trim,
        description = descriptionArea.text.value.trim
      )

    /*
     * Controlla obbligatorietà e univocità.
     *
     * Nella verifica dei duplicati escludiamo
     * il record attualmente modificato.
     */
    def validateForm(): Boolean =
      clearErrors()

      val errors =
        viewModel.validate(
          classification = currentClassification(),
          existingClassifications = classificationLogic.getRecords(),
          currentClassificationId =
            Some(selectedClassification.getId)
        )

      errors.foreach:
        case ClassificationViewModel.ClassificationRequiredError =>
          showClassificationError(
            ClassificationViewModel.ClassificationRequiredError
          )

        case ClassificationViewModel.DescriptionRequiredError =>
          showDescriptionError(
            ClassificationViewModel.DescriptionRequiredError
          )

        case ClassificationViewModel.DuplicateClassificationError =>
          showClassificationError(
            ClassificationViewModel.DuplicateClassificationError
          )

        case _ => ()

      errors.isEmpty

    val saveButton = new Button("Salva"):

        styleClass += "primary-button"

        onAction = _ =>
          if validateForm() then
            val classificationToUpdate = currentClassification()
            classificationToUpdate.setClassification(classificationToUpdate.getClassification.trim)
            classificationToUpdate.setDescription(classificationToUpdate.getDescription.trim)

            val updated =
              classificationLogic.recordUpdate(classificationToUpdate)

            if updated then
              showResult(
                "Classifica modificata correttamente.",
                success = true
              )
              onSaved()
            else
              showResult(
                "Errore durante la modifica della classifica.",
                success = false
              )

    val resetButton = new Button("Reset"):
        styleClass += "secondary-button"

        onAction = _ =>
          classificationField.text =
            initialClassification

          descriptionArea.text =
            initialDescription

          clearErrors()

    val closeButton = new Button("Chiudi"):
        styleClass += "secondary-button"

        onAction = _ =>
          onExit()

    val formGrid =
      new GridPane:

        hgap = 16
        vgap = 8
        maxWidth = 700

        columnConstraints = Seq(
          new ColumnConstraints:
            minWidth = 130,

          new ColumnConstraints:
            hgrow = Priority.Always
          )

        add(new Label("Classifica *"):
            styleClass += "form-label",0, 0)

        add(classificationField, 1, 0)

        add(classificationError, 1, 1)

        add(new Label("Descrizione *"):
            styleClass += "form-label", 0,2)

        add(descriptionArea, 1, 2)

        add(descriptionError, 1, 3)

    val actions =
      new HBox:

        spacing = 12
        alignment = Pos.CenterRight

        children = Seq(
          closeButton,
          resetButton,
          saveButton
        )

    val content =
      new VBox:

        spacing = 20
        padding = Insets(25)
        maxWidth = 800

        children = Seq(
          new Label("Modifica classifica"):
            styleClass += "classifications-title",

          new Label(
            "Modifica i dati della classifica selezionata."
          ):
            styleClass += "classifications-subtitle",
            formGrid,
            resultMessage,
            actions
        )

    new BorderPane:
      styleClass += "classifications-management-root"

      center =
        new StackPane:
          alignment = Pos.TopCenter
          children = Seq(content)
