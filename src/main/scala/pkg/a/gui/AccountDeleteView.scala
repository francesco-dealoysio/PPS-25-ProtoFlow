package pkg.a.gui

import pkg.b.logic.Account

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.*

object AccountDeleteView extends ManagementView:

  def apply(
             selectedAccount: Account,
             onDeleted: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val accountLogic = new Account()

    val resultMessage = messageLabel("accounts-message")

    def showResult(message: String, success: Boolean): Unit =
      showMessage(
        label = resultMessage,
        message = message,
        success = success,
        successStyle = "accounts-message-success",
        errorStyle = "accounts-message-error"
      )

    def detailRow(label: String, value: String): HBox =
      new HBox:
        spacing = 8
        children = Seq(
          new Label(label):
            styleClass += "form-label",

          new Label(value):
            styleClass += "accounts-detail-value"
        )

    val details =
      new VBox:
        spacing = 10
        styleClass += "accounts-detail-box"
        children = Seq(
          detailRow("Cognome:", selectedAccount.getSurname),
          detailRow("Nome:", selectedAccount.getName),
          detailRow("Email:", selectedAccount.getEmail),
          detailRow("Telefono:", selectedAccount.getPhone),
          detailRow("Ruolo:", selectedAccount.getRole),
          detailRow("Area:", selectedAccount.getArea),
          detailRow("Mansione:", selectedAccount.getAssignment),
          detailRow("Username:", selectedAccount.getUsername)
        )

    val deleteButton = new Button("Elimina"):
      styleClass += "danger-button"
      onAction = _ =>
        val confirmed =
          askConfirmation(
            titleText = "Eliminazione account",
            header =
              "Confermi l'eliminazione dell'account selezionato?",
            content =
              s"""Account: ${selectedAccount.getUsername}
                 |Nominativo: ${selectedAccount.getName} ${selectedAccount.getSurname}
                 |
                 |L'operazione non può essere annullata.""".stripMargin
          )

        if confirmed then
          val deleted = accountLogic.recordDelete(selectedAccount.getId)

          if deleted then
            onDeleted()
          else
            showResult(
              "Non è stato possibile eliminare l'account.",
              success = false
            )

    val cancelButton = closeButton(onExit, text = "Annulla")

    val header =
      titleBox(
        titleText = "Eliminazione account",
        subtitleText = "Verifica i dati dell'account prima di confermarne l'eliminazione.",
        titleStyle = "accounts-title",
        subtitleStyle = "accounts-subtitle"
      )

    val content =
      new VBox:
        spacing = 20
        padding = Insets(25)
        maxWidth = 800
        children = Seq(
          header,
          details,
          resultMessage,
          actionBar(cancelButton, deleteButton)
        )

    new BorderPane:
      styleClass += "accounts-management-root"

      center =
        new StackPane:
          alignment = Pos.TopCenter
          children = Seq(content)
