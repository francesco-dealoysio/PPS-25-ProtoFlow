package pkg.a.gui

import pkg.b.logic.Classification

import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*

object ClassificationManagementView:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Classification => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    /*
     * Oggetto del livello logic.
     */
    val classificationLogic = new Classification()

    /*
     * Dati mostrati nella tabella.
     */
    val classifications = ObservableBuffer.empty[Classification]

    /*
     * Messaggio informativo o di errore.
     */
    val messageLabel =
      new Label:
        visible = false
        managed = false
        wrapText = true
        maxWidth = Double.MaxValue
        styleClass += "classifications-message"

    def showMessage(message: String, success: Boolean): Unit =
      messageLabel.text = message
      messageLabel.visible = true
      messageLabel.managed = true

      messageLabel.styleClass.removeAll(
        "classifications-message-success",
        "classifications-message-error"
      )

      messageLabel.styleClass +=
        (if success then
          "classifications-message-success"
        else
          "classifications-message-error")

    def clearMessage(): Unit =

      messageLabel.text = ""
      messageLabel.visible = false
      messageLabel.managed = false

      messageLabel.styleClass.removeAll(
        "classifications-message-success",
        "classifications-message-error"
      )

    /*
     * Tabella principale.
     */
    val table = new TableView[Classification](classifications):

        columnResizePolicy = TableView.ConstrainedResizePolicy

        placeholder =
          new Label(
            "Non sono presenti classifiche nel sistema."
          )

        styleClass += "classifications-table"

    /*
     * Colonna Classifica.
     */
    val classificationColumn = new TableColumn[Classification, String]:

        text = "Classifica"

        cellValueFactory = cell =>
          StringProperty(
            cell.value.getClassification
          )

    /*
     * Colonna Descrizione
     */
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

    /*
     * Recupera la riga selezionata.
     */
    def selectedClassification(): Option[Classification] =
        Option(
          table.selectionModel.value
            .selectedItem
            .value
        )

    /*
     * Legge i record dal file XML.
     */
    def loadClassifications(): Unit =

      clearMessage()

      try
        val loaded =
          classificationLogic
            .getRecords()
            .sortBy: classification =>
              classification
                .getId
                .toIntOption
                .getOrElse(Int.MaxValue)

        classifications.setAll(loaded*)

        table.selectionModel.value
          .clearSelection()

        if loaded.isEmpty then
          showMessage(
            "Non sono presenti classifiche nel sistema.",
            success = true
          )

      catch
        case exception: Exception =>

          classifications.clear()

          showMessage(
            "Errore durante il caricamento delle classifiche.",
            success = false
          )

    /*
     * Pulisce il messaggio quando viene selezionata una nuova riga.
     */
    table.selectionModel.value
      .selectedItem
      .onChange:
        (_, _, selected) =>

          if selected != null then
            clearMessage()

    /*
     * Aggiunta: verrà collegata alla relativa view.
     */
    val addButton =
      new Button("Aggiunta"):

        styleClass += "primary-button"

        onAction = _ =>
          clearMessage()
          onAdd()

    /*
     * Modifica: passa alla callback il vero oggetto
     * Classification selezionato.
     */
    val editButton = new Button("Modifica"):

        styleClass += "secondary-button"

        disable <==
          table.selectionModel.value
            .selectedItem
            .isNull

        onAction = _ =>

          selectedClassification() match

            case Some(selected) =>
              clearMessage()
              onEdit(selected)

            case None =>
              showMessage(
                "Seleziona una classifica da modificare.",
                success = false
              )

    /*
     * Eliminazione dal file XML.
     */
    val deleteButton = new Button("Eliminazione"):

        styleClass += "danger-button"

        disable <==
          table.selectionModel.value
            .selectedItem
            .isNull

        onAction = _ =>

          selectedClassification() match

            case None =>
              showMessage(
                "Seleziona una classifica da eliminare.",
                success = false
              )

            case Some(selected) =>

              /*
               * Popup di conferma.
               */
              val confirmation =
                new Alert(
                  Alert.AlertType.Confirmation
                ):

                  title =
                    "Eliminazione Classifica"

                  headerText =
                    "Confermi l'eliminazione della classifica selezionata?"

                  contentText =
                    s"""Classifica: ${selected.getClassification}
                       |Codice: ${selected.getId}
                       |
                       |L'operazione non può essere annullata.""".stripMargin

              confirmation.showAndWait() match

                case Some(ButtonType.OK) =>

                  /*
                   * recordDelete richiama internamente
                   * Xml.removeElemFromXML.
                   */
                  val deleted =
                    classificationLogic.recordDelete(
                      selected.getId
                    )

                  if deleted then

                    /*
                     * Non togliamo soltanto la riga
                     * dall'ObservableBuffer:
                     * rileggiamo il file XML aggiornato.
                     */
                    loadClassifications()

                    showMessage(
                      s"La classifica '${selected.getClassification}' è stata eliminata correttamente.",
                      success = true
                    )

                  else
                    showMessage(
                      "Non è stato possibile eliminare la classifica.",
                      success = false
                    )

                case _ =>
                  ()

    /*
     * Chiusura della sezione e ritorno alla dashboard.
     */
    val closeButton =
      new Button("Chiudi"):

        styleClass += "secondary-button"

        onAction = _ =>
          onExit()

    /*
     * Menu richiesto dalla user story.
     */
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

    /*
     * Pulsante in fondo alla pagina.
     */
    val bottomActions =
      new HBox:

        alignment = Pos.CenterRight

        children = Seq(
          closeButton
        )

    /*
     * Titolo e sottotitolo.
     */
    val titleBox =
      new VBox:

        spacing = 5

        children = Seq(
          new Label("Gestione Classifiche"):
            styleClass += "classifications-title",

          new Label(
            "Visualizza e seleziona le classifiche utilizzate per la catalogazione dei documenti."
          ):
            styleClass += "classifications-subtitle"
          )

    /*
     * Layout centrale.
     */
    val content =
      new VBox:

        spacing = 18
        padding = Insets(20)

        VBox.setVgrow(
          table,
          Priority.Always
        )

        children = Seq(
          titleBox,
          navigationMenu,
          table,
          messageLabel,
          bottomActions
        )

    /*
     * Prima lettura dal file XML.
     */
    loadClassifications()

    new BorderPane:
      styleClass +=
        "classifications-management-root"

      center = content