package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.control.{TextArea, TextField}
import scalafx.scene.layout.BorderPane
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform

object ClassificationAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = new ClassificationViewModel()

    val classificationField =
      new TextField:
        promptText = "Inserisci la classifica"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val descriptionArea =
      new TextArea:
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
        successStyle = "classifications-message-success",
        errorStyle = "classifications-message-error"
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors = viewModel.validate(classification = currentClassification(), existingClassifications = classificationLogic.getRecords())
      showMappedErrors(errors):
        case ClassificationViewModel.ClassificationRequiredError |
             ClassificationViewModel.DuplicateClassificationError => classificationField -> classificationError
        case ClassificationViewModel.DescriptionRequiredError => descriptionArea -> descriptionError

    def currentClassification(id: String = ""): Classification =
      Classification(
        id = id,
        classification = classificationField.text.value,
        description = descriptionArea.text.value
      )

    def resetForm(): Unit =
      classificationField.clear()
      descriptionArea.clear()
      clearErrors()

      classificationField.requestFocus()

    val save =
      saveButton: () =>
        if validateForm() then
          val existingClassifications = classificationLogic.getRecords()
          val newClassification =  currentClassification(IdGen(inIdsFilePathName("classificationId")))
          val saved = classificationLogic.recordInsert(newClassification)

          showMessage(
            label = resultMessage,
            message =
              if saved then
                "Classifica inserita correttamente."
              else
                "Errore durante l'inserimento della classifica.",
            success = saved,
            successStyle =
              "classifications-message-success",
            errorStyle =
              "classifications-message-error"
          )

          if saved then onSaved()

    val reset = resetButton(() => resetForm())

    val form = formGrid(
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

    val exit = closeButton(onExit)

    Platform.runLater {
      classificationField.requestFocus()
    }

    formPage(
      titleText = "Aggiunta classifica",
      subtitleText = "Inserisci i dati della nuova classifica.",
      titleStyle = "classifications-title",
      subtitleStyle = "classifications-subtitle",
      rootStyle = "classifications-management-root",
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(exit, reset, save)
    )