package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.layout.BorderPane
import UiText.{Classifications, Fields}
import pkg.a.gui.text.UiText.Validation.Classification.*

object ClassificationEditView extends Form:

  def apply(selectedClassification: Classification, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = ClassificationViewModel()

    val classification =
      stringField(
        prompt = Fields.Prompts.Classification,
        initialValue = selectedClassification.getClassification
      )

    val description =
      areaField(
        prompt = Fields.Prompts.Description,
        styleName = UiStyles.Common.DescriptionAreaStyle,
        initialValue = selectedClassification.getDescription
      )

    val monitoredFields = Seq(classification, description)
    val resultMessage = messageLabel()

    def currentClassification(): Classification =
      Classification(
        id = selectedClassification.getId,
        classification = classification.value,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(classification, description)
      clearMessage(resultMessage)

    def resetForm(): Unit =
      resetFields(classification, description)
      clearErrors()
      classification.requestFocus()

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        viewModel.validate(
          classification = currentClassification(),
          existingClassifications = classificationLogic.getRecords(),
          currentClassificationId = Some(selectedClassification.getId)
        )

      showFormFieldErrors(errors):
        case ClassificationRequired | DuplicateClassification=>classification
        case DescriptionRequired => description

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
            success = updated
          )
          if updated then
            formSaved = true
            onSaved()

    val reset = resetButton(resetForm)
    val exit = closeButton(onExit)

    val form =
      formGrid(
        Seq(
          formRow(Fields.Labels.required(Fields.Labels.Classification), classification),
          formRow(Fields.Labels.required(Fields.Labels.Description), description)
        )
      )

    formPage(
      titleText = Classifications.Edit.Title,
      subtitleText = Classifications.Edit.Subtitle,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )