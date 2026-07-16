package pkg.a.gui

import pkg.b.logic.Classification

import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*

object ClassificationAddView:

  def apply(
             onSaved: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    /*
     * Oggetto del livello logic.
     * La view non accede direttamente al package data.
     */
    val classificationLogic =
      new Classification()

    /*
     * Campi inizialmente vuoti, come richiesto dalla US 19.
     */
    val classificationField =
      new TextField:
        promptText = "Inserisci la classifica"
        maxWidth = Double.MaxValue
        styleClass += "form-field"

    val descriptionArea =
      new TextArea:
        promptText = "Inserisci la descrizione"
        wrapText = true
        prefRowCount = 5
        maxWidth = Double.MaxValue
        styleClass += "classification-description-area"

    /*
     * Errori specifici dei due campi.
     */
    val classificationError =
      new Label:
        visible = false
        managed = false
        wrapText = true
        styleClass += "field-error"

    val descriptionError =
      new Label:
        visible = false
        managed = false
        wrapText = true
        styleClass += "field-error"

    /*
     * Messaggio generale relativo al salvataggio.
     */
    val resultMessage =
      new Label:
        visible = false
        managed = false
        wrapText = true
        maxWidth = Double.MaxValue
        styleClass += "classifications-message"

    def showResult(
                    message: String,
                    success: Boolean
                  ): Unit =
      resultMessage.text = message
      resultMessage.visible = true
      resultMessage.managed = true

      resultMessage.styleClass.removeAll(
        "classifications-message-success",
        "classifications-message-error"
      )

      resultMessage.styleClass +=
        (if success then
          "classifications-message-success"
        else
          "classifications-message-error")

    def clearErrors(): Unit =
      classificationError.text = ""
      classificationError.visible = false
      classificationError.managed = false

      descriptionError.text = ""
      descriptionError.visible = false
      descriptionError.managed = false

      resultMessage.text = ""
      resultMessage.visible = false
      resultMessage.managed = false

      classificationField.styleClass.remove(
        "form-field-error"
      )

      descriptionArea.styleClass.remove(
        "form-field-error"
      )

    def showClassificationError(
                                 message: String
                               ): Unit =
      classificationError.text = message
      classificationError.visible = true
      classificationError.managed = true

      if !classificationField.styleClass.contains(
        "form-field-error"
      )
      then
        classificationField.styleClass +=
          "form-field-error"

    def showDescriptionError(
                              message: String
                            ): Unit =
      descriptionError.text = message
      descriptionError.visible = true
      descriptionError.managed = true

      if !descriptionArea.styleClass.contains(
        "form-field-error"
      )
      then
        descriptionArea.styleClass +=
          "form-field-error"

    /*
     * Controlli richiesti dalla user story:
     * - obbligatorietà;
     * - univocità della classifica.
     */
    def validateForm(): Boolean =
      clearErrors()

      val classificationValue =
        classificationField.text.value.trim

      val descriptionValue =
        descriptionArea.text.value.trim

      var valid =
        true

      if classificationValue.isEmpty then
        showClassificationError(
          "Il campo Classifica è obbligatorio."
        )
        valid = false

      if descriptionValue.isEmpty then
        showDescriptionError(
          "Il campo Descrizione è obbligatorio."
        )
        valid = false

      /*
       * La verifica viene fatta sui record
       * realmente letti da classifications.xml.
       */
      if classificationValue.nonEmpty then
        val duplicate =
          classificationLogic
            .getRecords()
            .exists: record =>
              record
                .getClassification
                .trim
                .equalsIgnoreCase(classificationValue)

        if duplicate then
          showClassificationError(
            "Esiste già una classifica con questo nome."
          )
          valid = false

      valid

    /*
     * Genera il nuovo identificativo basandosi
     * sui record realmente presenti nell'XML.
     */
    def nextId(): String =
      val maximumId =
        classificationLogic
          .getRecords()
          .flatMap: record =>
            record.getId.toIntOption
          .maxOption
          .getOrElse(0)

      (maximumId + 1).toString

    /*
     * Reset dell'aggiunta: i campi devono tornare vuoti.
     */
    def resetForm(): Unit =
      classificationField.clear()
      descriptionArea.clear()
      clearErrors()

      classificationField.requestFocus()

    val saveButton =
      new Button("Salva"):

        styleClass += "primary-button"

        onAction = _ =>
          if validateForm() then
            val newClassification =
              Classification(
                id = nextId(),
                classification =
                  classificationField.text.value.trim,
                description =
                  descriptionArea.text.value.trim
              )

            /*
             * Inserimento reale tramite Classification,
             * che richiama Xml.insertElemIntoXML.
             */
            val saved =
              classificationLogic.recordInsert(
                newClassification
              )

            if saved then
              showResult(
                "Classifica aggiunta correttamente.",
                success = true
              )

              /*
               * Ritorna alla gestione classifiche.
               * La gestione rileggerà classifications.xml.
               */
              onSaved()
            else
              showResult(
                "Non è stato possibile salvare la classifica.",
                success = false
              )

    val resetButton =
      new Button("Reset"):

        styleClass += "secondary-button"

        onAction = _ =>
          resetForm()

    val closeButton =
      new Button("Chiudi"):

        styleClass += "secondary-button"

        onAction = _ =>
          onExit()

    /*
     * Corpo del form.
     */
    val formGrid =
      new GridPane:

        hgap = 16
        vgap = 8
        maxWidth = 700

        columnConstraints = Seq(
          new ColumnConstraints:
            minWidth = 130,

          new ColumnConstraints:
            hgrow = Priority.Always
          )

        add(new Label("Classifica *"):
            styleClass += "form-label",0, 0)

        add(classificationField, 1, 0)

        add(classificationError, 1, 1)

        add(new Label("Descrizione *"): 
          styleClass += "form-label",0,2)

        add(descriptionArea, 1, 2)

        add(descriptionError, 1, 3)

    /*
     * Piè di pagina previsto dalla US.
     * Chiudi è l'apposito controllo di abbandono.
     */
    val actions =
      new HBox:

        spacing = 12
        alignment = Pos.CenterRight

        children = Seq(
          closeButton,
          resetButton,
          saveButton
        )

    val content =
      new VBox:

        spacing = 20
        padding = Insets(25)
        maxWidth = 800

        children = Seq(
          new Label("Aggiunta Classifica"):
            styleClass += "classifications-title",

          new Label("Inserisci i dati della nuova classifica."):
            styleClass += "classifications-subtitle",
            formGrid,
            resultMessage,
            actions
            )

    new BorderPane:
      styleClass +=
        "classifications-management-root"

      center =
        new StackPane:
          alignment = Pos.TopCenter
          children = Seq(content)