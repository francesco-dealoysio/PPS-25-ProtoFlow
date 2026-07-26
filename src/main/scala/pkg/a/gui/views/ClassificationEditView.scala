package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, Classifications}

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
    val classificationField = textField(Fields.Prompts.Classification, initialClassification)
    val descriptionArea = textArea(Fields.Prompts.Description, UiStyles.Classifications.DescriptionArea, initialDescription)


    val classificationError = fieldErrorLabel()
    val descriptionError = fieldErrorLabel()
    val monitoredFields = Seq(classificationField, descriptionArea)
    val initialFormValues = Seq(initialClassification, initialDescription)
    val resultMessage = messageLabel(UiStyles.Classifications.Message)

    def clearErrors(): Unit =
      clearFieldErrors(
        classificationField -> classificationError,
        descriptionArea -> descriptionError
      )
      clearMessage(
        resultMessage,
        UiStyles.Classifications.MessageSuccess,
        UiStyles.Classifications.MessageError
      )

    def currentClassification(): Classification =
      Classification(
        id = selectedClassification.getId,
        classification = classificationField.text.value.trim,
        description = descriptionArea.text.value.trim
      )

    def resetForm(): Unit =
      classificationField.text = initialClassification
      descriptionArea.text = initialDescription
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

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val updated = classificationLogic.recordUpdate[Classification](currentClassification())

          showMessage(
            label = resultMessage,
            message =
              if updated then Classifications.Edit.Success
              else Classifications.Edit.Error,
            success = updated,
            successStyle = UiStyles.Classifications.MessageSuccess,
            errorStyle = UiStyles.Classifications.MessageError
          )
          if updated then
            formSaved = true
            onSaved()

    val reset = resetButton(() => resetForm())
    val exit = closeButton(onExit)

    val form =
      formGrid(
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

    formPage(
      titleText = Classifications.Edit.Title,
      subtitleText = Classifications.Edit.Subtitle,
      titleStyle = UiStyles.Classifications.Title,
      subtitleStyle = UiStyles.Classifications.Subtitle,
      rootStyle = UiStyles.Classifications.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () =>
        hasFormChanges(
          formSaved = formSaved,
          textFields = monitoredFields,
          initialValues = initialFormValues
        )
    )