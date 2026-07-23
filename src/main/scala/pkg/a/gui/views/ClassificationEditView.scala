package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.control.{TextArea, TextField}
import scalafx.scene.layout.BorderPane

object ClassificationEditView extends Form:

  def apply(
             selectedClassification: Classification,
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = ClassificationViewModel()

    val initialClassification = selectedClassification.getClassification
    val initialDescription = selectedClassification.getDescription

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

    val classificationError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()

    val resultMessage = messageLabel("classifications-message")

    def clearErrors(): Unit =
      clearFieldErrors(
        classificationField -> classificationError,
        descriptionArea -> descriptionError
      )
      clearMessage(
        resultMessage,
        "classifications-message-success",
        "classifications-message-error"
      )

    def currentClassification(): Classification =
      Classification(
        id = selectedClassification.getId,
        classification = classificationField.text.value.trim,
        description = descriptionArea.text.value.trim
      )

    def resetForm(): Unit =
      classificationField.text =
        initialClassification

      descriptionArea.text =
        initialDescription

      clearErrors()
      classificationField.requestFocus()

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        viewModel.validate(
          classification = currentClassification(),
          existingClassifications = classificationLogic.getRecords(),
          currentClassificationId =
            Some(selectedClassification.getId)
        )

      showMappedErrors(errors):
        case ClassificationViewModel.ClassificationRequiredError |
             ClassificationViewModel.DuplicateClassificationError => classificationField -> classificationError
        case ClassificationViewModel.DescriptionRequiredError => descriptionArea -> descriptionError

    val save =
      saveButton: () =>
        if validateForm() then
          val updated = classificationLogic.recordUpdate[Classification](currentClassification())

          showMessage(
            label = resultMessage,
            message =
              if updated then
                "Classifica modificata correttamente."
              else
                "Errore durante la modifica della classifica.",
            success = updated,
            successStyle =
              "classifications-message-success",
            errorStyle =
              "classifications-message-error"
          )
          if updated then onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          FormRow(
            "Classifica *",
            classificationField,
            classificationError
          ),
          FormRow(
            "Descrizione *",
            descriptionArea,
            descriptionError
          )
        )
      )

    formPage(
      titleText = "Modifica classifica",
      subtitleText =
        "Modifica i dati della classifica selezionata.",
      titleStyle = "classifications-title",
      subtitleStyle = "classifications-subtitle",
      rootStyle = "classifications-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(exit, reset, save)
    )