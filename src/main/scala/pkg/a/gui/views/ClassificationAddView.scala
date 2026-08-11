package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.text.UiStyles.Common.*
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform
import scalafx.scene.layout.BorderPane
import pkg.a.gui.text.UiText.Classifications.Add as Text
import pkg.a.gui.text.UiText.Fields.{Labels, Prompts}
import pkg.a.gui.text.UiText.Validation.Classification as Validation

object ClassificationAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = new ClassificationViewModel()

    val classification = stringField(Prompts.Classification)
    val description = areaField(Prompts.Description, DescriptionAreaStyle)
    val monitoredFields = Seq(classification, description)
    val result = createResultMessage()

    def currentClassification(id: String = ""): Classification =
      Classification(
        id = id,
        classification = classification.value,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(classification, description)
      result.clear()

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        viewModel.validate(
          classification = currentClassification(),
          existingClassifications = classificationLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case Validation.ClassificationRequired | Validation.DuplicateClassification => classification
        case Validation.DescriptionRequired => description

    def resetForm(): Unit =
      resetFields(classification, description)
      clearErrors()
      classification.requestFocus()

    var formSaved = false
    val save =
      saveButton: () =>
        if validateForm() then
          val newClassification = currentClassification(IdGen(inIdsFilePathName("classificationId")))
          val saved = classificationLogic.recordInsert(newClassification)

          result.show(
            message = if saved then Text.Success else Text.Error,
            success = saved
          )

          if saved then
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

    Platform.runLater:
      classification.requestFocus()

    formPage(
      titleText = Text.Title,
      subtitleText = Text.Subtitle,
      form = form,
      resultMessage = result.label,
      actions = actionBar(Seq(exit, reset, save)),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )