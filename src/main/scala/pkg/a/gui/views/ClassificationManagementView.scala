package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Classification
import scalafx.collections.ObservableBuffer
import pkg.d.util.Util.inDatabaseFilePathName
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.Classifications.Management as Text
import scalafx.scene.layout.BorderPane

object ClassificationManagementView extends Management:

  def apply(onAdd: () => Unit = () => (), onEdit: Classification => Unit = _ => (), onExit: () => Unit = () => ()): BorderPane =

    val classificationLogic = new Classification()
    val classifications = ObservableBuffer.empty[Classification]

    val result = createResultMessage()
    
    val table = managementTable(classifications, Text.Empty)

    table.columns ++= Seq(
      stringColumn[Classification](Labels.Classification)(_.getClassification),
      stringColumn[Classification](Labels.Description)(_.getDescription)
    )

    def loadClassifications(): Unit =
      loadTableItemsSafely(classifications, result, Text.Empty, Text.LoadError):
        classificationLogic
          .getRecords[Classification]()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

    def deleteSelectedClassification(): Unit =
      withSelectedItem(table, result, Text.SelectToDelete): selected =>
        val confirmed =
          askConfirmation(
            titleText = Text.DeleteTitle,
            header = Text.DeleteConfirmation,
            content =
              s"""Classifica: ${selected.getClassification}
                 |Codice: ${selected.getId}
                 |
                 |L'operazione non può essere annullata.""".stripMargin
          )

        if confirmed then
          val deleted = classificationLogic.recordDelete(selected.getId)
          if deleted then
            loadClassifications()
            result.show(Text.deleted(selected.getClassification), success = true)
          else
            result.show(Text.DeleteError, success = false)

    def printClassifications(): Unit =
      if classifications.isEmpty then
        result.show(Text.PrintEmpty, success = false)
      else
        val printed =
          XmlToPdf.printList(
            xmlPath = inDatabaseFilePathName("classifications.xml"),
            pdfFileName = "elenco-classifiche.pdf",
            title = Text.PrintTitle
          )

        if printed then
          result.show(Text.PrintSuccess, success = true)
        else
          result.show(Text.PrintError, success = false)

    clearResultOnSelection(table, result)

    val addButton = primaryButton(Buttons.Add, () =>
      result.clear()
      onAdd())

    val editButton = secondaryButton(Buttons.Edit, () => withSelectedItem(table, result, Text.SelectToEdit)(onEdit))
    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedClassification)
    disableWithoutSelection(table, editButton, deleteButton)
    val bottomActions = actionBar(Seq(closeButton(onExit), printButton(printClassifications), editButton, deleteButton, addButton))

    val header = titleBox(Text.Title, Text.Subtitle)

    loadClassifications()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(header, table, result.label, bottomActions)
    )