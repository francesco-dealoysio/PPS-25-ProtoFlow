package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.layout.BorderPane
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, Classifications}

object ClassificationAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = new ClassificationViewModel()

    val classificationField =  textField(Fields.Prompts.Classification)
    val descriptionArea = textArea(Fields.Prompts.Description, UiStyles.Classifications.DescriptionArea)
    val classificationError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()

    val resultMessage = messageLabel(UiStyles.Classifications.Message)
    val monitoredFields = Seq(classificationField, descriptionArea)

    def clearErrors(): Unit =
      clearFieldErrors(
        classificationField -> classificationError,
        descriptionArea -> descriptionError
      )
      clearMessage(
        resultMessage,
        successStyle = UiStyles.Classifications.MessageSuccess,
        errorStyle = UiStyles.Classifications.MessageError
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

    var formSaved = false
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
                Classifications.Add.Success
              else
                Classifications.Add.Error,
            success = saved,
            successStyle = UiStyles.Classifications.MessageSuccess,
            errorStyle = UiStyles.Classifications.MessageError
          )

          if saved then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())

    val form = formGrid(
      Seq(
        FormRow(
          Fields.Labels.required(Fields.Labels.Classification),
          classificationField,
          classificationError
        ),
        FormRow(
          Fields.Labels.required(Fields.Labels.Description),
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
      titleText = Classifications.Add.Title,
      subtitleText = Classifications.Add.Subtitle,
      titleStyle = UiStyles.Classifications.Title,
      subtitleStyle = UiStyles.Classifications.Subtitle,
      rootStyle = UiStyles.Classifications.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )