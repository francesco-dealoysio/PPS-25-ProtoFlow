package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.text.UiStyles.Common.DescriptionAreaStyle
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Classifications.Edit as Text
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Validation.Classification as Validation

object ClassificationEditView extends Form:

  def apply(selectedClassification: Classification, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = ClassificationViewModel()

    val classification =
      stringField(
        prompt = Prompts.Classification,
        initialValue = selectedClassification.getClassification
      )

    val description =
      areaField(
        prompt = Prompts.Description,
        styleName = DescriptionAreaStyle,
        initialValue = selectedClassification.getDescription
      )

    val monitoredFields = Seq(classification, description)
    val result = createResultMessage()

    def currentClassification(): Classification =
      Classification(
        id = selectedClassification.getId,
        classification = classification.value,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(classification, description)
      result.clear()

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
        case Validation.ClassificationRequired | Validation.DuplicateClassification => classification
        case Validation.DescriptionRequired => description

    var formSaved = false

    val save =
      saveButton: () =>
        if validateForm() then
          val updated = classificationLogic.recordUpdate[Classification](currentClassification())
          result.show(
            message = if updated then Text.Success else Text.Error,
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
          formRow(Labels.required(Labels.Classification), classification),
          formRow(Labels.required(Labels.Description), description)
        )
      )

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )