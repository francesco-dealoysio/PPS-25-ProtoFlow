package pkg.a.gui.views

import pkg.a.gui.text.UiStyles.Common.DescriptionAreaStyle
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Classifications.Edit as Text
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.Validation.Classification as Validation
import pkg.a.gui.validation.ClassificationValidator

object ClassificationEditView extends Form:

  def apply(selectedClassification: Classification, onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val validator = new ClassificationValidator()

    val id = stringField("", selectedClassification.getId)
    id.control.setDisable(true)
    val classification = stringField("", selectedClassification.getClassification)
    val description = areaField("", DescriptionAreaStyle, selectedClassification.getDescription)

    val monitoredFields = Seq(classification, description)
    val result = createResultMessage()

    def currentClassification(): Classification =
      Classification(
        id = selectedClassification.getId,
        classification = classification.value,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(monitoredFields*)
      result.clear()

    def resetForm(): Unit =
      resetFields(monitoredFields*)
      clearErrors()
      classification.requestFocus()

    def validateForm(): Boolean =
      clearErrors()

      val errors =
        validator.validate(
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
          formRow(Labels.Id, id),
          formRow(Labels.Classification, classification),
          formRow(Labels.Description, description)
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