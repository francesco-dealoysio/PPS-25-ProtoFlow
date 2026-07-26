package pkg.a.gui.views

import pkg.a.gui.structures.ClassificationViewModel
import pkg.a.gui.text.{UiStyles, UiText}
import pkg.a.gui.traits.Form
import pkg.b.logic.Classification
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName
import scalafx.application.Platform
import scalafx.scene.layout.BorderPane
import UiText.{Classifications, Fields}

object ClassificationAddView extends Form:

  def apply(onSaved: () => Unit, onExit: () => Unit): BorderPane =

    val classificationLogic = new Classification()
    val viewModel = new ClassificationViewModel()

    val classification = stringField(Fields.Prompts.Classification)
    val description = areaField(Fields.Prompts.Description, UiStyles.Classifications.DescriptionArea)
    val monitoredFields = Seq(classification, description)
    val resultMessage = messageLabel(UiStyles.Classifications.Message)

    def currentClassification(id: String = ""): Classification =
      Classification(
        id = id,
        classification = classification.value,
        description = description.value
      )

    def clearErrors(): Unit =
      clearFormFieldErrors(classification, description)
      clearMessage(
        resultMessage,
        successStyle = UiStyles.Classifications.MessageSuccess,
        errorStyle = UiStyles.Classifications.MessageError
      )

    def validateForm(): Boolean =
      clearErrors()
      val errors =
        viewModel.validate(
          classification = currentClassification(),
          existingClassifications =
            classificationLogic.getRecords()
        )

      showFormFieldErrors(errors):
        case ClassificationViewModel.ClassificationRequiredError | ClassificationViewModel.DuplicateClassificationError =>
          classification
        case ClassificationViewModel.DescriptionRequiredError =>
          description

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

          showMessage(
            label = resultMessage,
            message =
              if saved then
                Classifications.Add.Success
              else
                Classifications.Add.Error,
            success = saved,
            successStyle =
              UiStyles.Classifications.MessageSuccess,
            errorStyle =
              UiStyles.Classifications.MessageError
          )

          if saved then
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

    Platform.runLater:
      classification.requestFocus()

    formPage(
      titleText = Classifications.Add.Title,
      subtitleText = Classifications.Add.Subtitle,
      titleStyle = UiStyles.Classifications.Title,
      subtitleStyle = UiStyles.Classifications.Subtitle,
      rootStyle = UiStyles.Classifications.Root,
      form = form,
      resultMessage = resultMessage,
      actions = actionBar(
        Seq(exit, reset, save)
      ),
      hasUnsavedChanges = () => hasFormChanges(formSaved, monitoredFields)
    )