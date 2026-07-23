package pkg.a.gui.views

import pkg.a.gui.traits.Management
import pkg.b.logic.Classification
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.*
import scalafx.scene.layout.*
import pkg.d.util.Logger.*

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
        baseStyle = "classifications-message",
        successStyle = "classifications-message-success",
        errorStyle = "classifications-message-error"
      )

    val table = new TableView[Classification](classifications):
        columnResizePolicy = TableView.ConstrainedResizePolicy
        placeholder = new Label("Non sono presenti classifiche nel sistema.")
        styleClass += "classifications-table"

    val classificationColumn = new TableColumn[Classification, String]:
        text = "Classifica"
        cellValueFactory = cell =>
          StringProperty(cell.value.getClassification)

    val descriptionColumn = new TableColumn[Classification, String]:
        text = "Descrizione"
        cellValueFactory = cell =>
          StringProperty(cell.value.getDescription)

    table.columns ++= Seq(
      classificationColumn,
      descriptionColumn
    )

    def selectedClassification(): Option[Classification] =
        Option(
          table.selectionModel.value
            .selectedItem
            .value
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
          result.show("Non sono presenti classifiche nel sistema.", success = true)

      catch
        case exception: Exception =>
          classifications.clear()
          result.show("Errore durante il caricamento delle classifiche.", success = false)
          logger(exception)

    def deleteSelectedClassification(): Unit =
      selectedClassification() match
        case None =>
          result.show("Seleziona una classifica da eliminare.", success = false)

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = "Eliminazione classifica",
              header = "Confermi l'eliminazione della classifica selezionata?",
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

              result.show(s"La classifica '${selected.getClassification}' è stata eliminata correttamente.", success = true)
            else
              result.show("Non è stato possibile eliminare la classifica.", success = false)

     // Pulisce il messaggio quando viene selezionata una nuova riga.
    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>

          if selected != null then
            result.clear()


    val addButton = primaryButton(text = "Aggiungi", action = () =>
      result.clear()
      onAdd())

    val editButton =
      secondaryButton(
        text = "Modifica",
        action = () =>
          selectedClassification() match
            case Some(selected) =>
              result.clear()
              onEdit(selected)

            case None =>
              result.show("Seleziona una classifica da modificare.", success = false)
      )

    editButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val deleteButton = dangerButton(text = "Elimina", action = () => deleteSelectedClassification())

    deleteButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull

    val exitButton = closeButton(onExit)

    val bottomActions =
      actionBar(
        exitButton,
        editButton,
        deleteButton,
        addButton
      )

    val header =
      titleBox(
        titleText = "Gestione Classifiche",
        subtitleText = "Visualizza e seleziona le classifiche utilizzate per la catalogazione dei documenti.",
        titleStyle = "classifications-title",
        subtitleStyle = "classifications-subtitle"
      )

    loadClassifications() // Prima lettura dal file XML.

    managementPage(
      rootStyle = "classifications-management-root",
      growNode = Some(table),
      pageChildren = Seq(header, table, result.label, bottomActions)
    )