package pkg.a.gui

import pkg.b.logic.Classification

import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import pkg.d.util.Logger.*

object ClassificationManagementView extends ManagementView:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Classification => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val classificationLogic = new Classification()
    val classifications = ObservableBuffer.empty[Classification]


    val resultMessage = messageLabel("classifications-message")
    val successMessageStyle = "classifications-message-success"
    val errorMessageStyle = "classifications-message-error"

    def showResult(message: String, success: Boolean): Unit =
      showMessage(
        label = resultMessage,
        message = message,
        success = success,
        successStyle = successMessageStyle,
        errorStyle = errorMessageStyle
      )

    def clearResult(): Unit =
      clearMessage(
        resultMessage,
        successMessageStyle,
        errorMessageStyle
      )

    val table = new TableView[Classification](classifications):

        columnResizePolicy = TableView.ConstrainedResizePolicy

        placeholder =
          new Label(
            "Non sono presenti classifiche nel sistema."
          )

        styleClass += "classifications-table"

    val classificationColumn = new TableColumn[Classification, String]:
        text = "Classifica"
        cellValueFactory = cell =>
          StringProperty(
            cell.value.getClassification
          )


    val descriptionColumn = new TableColumn[Classification, String]:
        text = "Descrizione"
        cellValueFactory = cell =>
          StringProperty(
            cell.value.getDescription
          )

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
      clearResult()
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
          showResult("Non sono presenti classifiche nel sistema.", success = true)

      catch
        case exception: Exception =>
          classifications.clear()
          logger(exception)
          showResult("Errore durante il caricamento delle classifiche.", success = false)

    def deleteSelectedClassification(): Unit =
      selectedClassification() match
        case None =>
          showResult(
            "Seleziona una classifica da eliminare.",
            success = false
          )

        case Some(selected) =>
          val confirmed =
            askConfirmation(
              titleText = "Eliminazione classifica",
              header =
                "Confermi l'eliminazione della classifica selezionata?",
              content =
                s"""Classifica: ${selected.getClassification}
                   |Codice: ${selected.getId}
                   |
                   |L'operazione non può essere annullata.""".stripMargin
            )

          if confirmed then
            val deleted =
              classificationLogic.recordDelete(
                selected.getId
              )

            if deleted then
              loadClassifications()

              showResult(
                s"La classifica '${selected.getClassification}' è stata eliminata correttamente.",
                success = true
              )
            else
              showResult(
                "Non è stato possibile eliminare la classifica.",
                success = false
              )

     // Pulisce il messaggio quando viene selezionata una nuova riga.
    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>

          if selected != null then
            clearResult()


    val addButton = primaryButton(text = "Aggiunta", action = () =>
      clearResult()
      onAdd())

    val editButton =
      secondaryButton(
        text = "Modifica",
        action = () =>
          selectedClassification() match
            case Some(selected) =>
              clearResult()
              onEdit(selected)

            case None =>
              showResult(
                "Seleziona una classifica da modificare.",
                success = false
              )
      )

    editButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull


    val deleteButton = new Button("Eliminazione"):
      styleClass += "danger-button"
      onAction = _ =>
        deleteSelectedClassification()

    deleteButton.disable <==
      table.selectionModel.value
        .selectedItem
        .isNull


    val exitButton = closeButton(onExit)


    val navigationMenu =
      new HBox:
        spacing = 12
        alignment = Pos.CenterLeft
        styleClass += "classifications-toolbar"
        children = Seq(
          addButton,
          editButton,
          deleteButton
        )


     // Pulsante in fondo alla pagina.
    val bottomActions = actionBar(exitButton)


    val header =
      titleBox(
        titleText = "Gestione Classifiche",
        subtitleText = "Visualizza e seleziona le classifiche utilizzate per la catalogazione dei documenti.",
        titleStyle = "classifications-title",
        subtitleStyle = "classifications-subtitle"
      )

    val content =
      new VBox:

        spacing = 18
        padding = Insets(20)

        VBox.setVgrow(
          table,
          Priority.Always
        )

        children = Seq(
          header,
          navigationMenu,
          table,
          resultMessage,
          bottomActions
        )

    loadClassifications() // Prima lettura dal file XML.

    new BorderPane:
      styleClass +=
        "classifications-management-root"

      center = content