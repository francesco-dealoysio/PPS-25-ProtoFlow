package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Classification
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*
import pkg.d.util.Logger.*
import pkg.d.util.Util.inDatabaseFilePathName
import pkg.d.util.XmlToPdf
import pkg.a.gui.text.{UiStyles, UiText}
import UiText.{Fields, Classifications, Common}

object ClassificationManagementView extends Management:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Classification => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val classificationLogic = new Classification()
    val classifications = ObservableBuffer.empty[Classification]

    val result =
      createResultMessage(
        baseStyle = UiStyles.Classifications.Message,
        successStyle = UiStyles.Classifications.MessageSuccess,
        errorStyle = UiStyles.Classifications.MessageError
      )

    val table = new TableView[Classification](classifications):
        columnResizePolicy = TableView.ConstrainedResizePolicy
        placeholder = new Label(Classifications.Management.Empty)
        styleClass += UiStyles.Classifications.Table

    val classificationColumn = new TableColumn[Classification, String]:
        text = Fields.Labels.Classification
        cellValueFactory = cell =>
          StringProperty(cell.value.getClassification)

    val descriptionColumn = new TableColumn[Classification, String]:
        text = Fields.Labels.Description
        cellValueFactory = cell =>
          StringProperty(cell.value.getDescription)

    table.columns ++= Seq(
      classificationColumn,
      descriptionColumn
    )

    def loadClassifications(): Unit =
      result.clear()
      try
        val loaded =
          classificationLogic
            .getRecords[Classification]()
            .sortBy: classification =>
              classification
                .getId
                .toIntOption
                .getOrElse(Int.MaxValue)

        classifications.setAll(loaded*)

        table.selectionModel.value
          .clearSelection()

        if loaded.isEmpty then
          result.show(Classifications.Management.Empty, success = true)

      catch
        case exception: Exception =>
          classifications.clear()
          result.show(Classifications.Management.LoadError, success = false)
          logger(exception)

    def deleteSelectedClassification(): Unit =
      selectedItem(table) match
        case None =>
          result.show(Classifications.Management.SelectToDelete, success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = Classifications.Management.DeleteTitle,
              header = Classifications.Management.DeleteConfirmation,
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

              result.show(Classifications.Management.deleted(selected.getClassification), success = true)
            else
              result.show(Classifications.Management.DeleteError, success = false)

    def printClassifications(): Unit =
      if classifications.isEmpty then
        result.show(Classifications.Management.PrintEmpty, success = false)
      else
        val printed =
          XmlToPdf.printList(
            xmlPath = inDatabaseFilePathName("classifications.xml"),
            pdfFileName = "elenco-classifiche.pdf",
            title = Classifications.Management.PrintTitle
          )

        if printed then
          result.show(Classifications.Management.PrintSuccess, success = true)
        else
          result.show(Classifications.Management.PrintError, success = false)

    clearResultOnSelection(table, result)

    val addButton = primaryButton(text = Common.Buttons.Add, action = () =>
      result.clear()
      onAdd())

    val editButton =
      secondaryButton(
        text = Common.Buttons.Edit,
        action = () =>
          selectedItem(table) match
            case Some(selected) =>
              result.clear()
              onEdit(selected)

            case None =>
              result.show(Classifications.Management.SelectToEdit, success = false)
      )

    val deleteButton = dangerButton(text = Common.Buttons.Delete, action = () => deleteSelectedClassification())
    val exitButton = closeButton(onExit)
    val print = printButton(action = () => printClassifications())
    disableWithoutSelection(table, editButton, deleteButton)
    val bottomActions = actionBar(Seq(exitButton, print, editButton, deleteButton, addButton))

    val header =
      titleBox(
        titleText = Classifications.Management.Title,
        subtitleText = Classifications.Management.Subtitle,
        titleStyle = UiStyles.Classifications.Title,
        subtitleStyle = UiStyles.Classifications.Subtitle
      )

    loadClassifications() // Prima lettura dal file XML.

    managementPage(
      rootStyle = UiStyles.Classifications.Root,
      growNode = Some(table),
      pageChildren = Seq(header, table, result.label, bottomActions)
    )