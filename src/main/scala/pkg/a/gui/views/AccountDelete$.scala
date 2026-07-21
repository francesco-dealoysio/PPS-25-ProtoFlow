package pkg.a.gui.views

import pkg.a.gui.traits.Root
import pkg.b.logic.Account
import pkg.d.util.Logger.*

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.*

object AccountDelete$ extends Root:

  def apply(
             selectedAccount: Account,
             onDeleted: () => Unit,
             onExit: () => Unit
           ): BorderPane =

    val accountLogic = new Account()

    val result =
      createResultMessage(
        baseStyle = "accounts-message",
        successStyle = "accounts-message-success",
        errorStyle = "accounts-message-error"
      )

    def detailRow(label: String, value: String): HBox =
      new HBox:
        spacing = 8
        children = Seq(
          fieldLabel(label),

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

    val deleteButton =
      dangerButton(
        text = "Elimina",
        action = () =>
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
            try
              val deleted = accountLogic.recordDelete(selectedAccount.getId)

              if deleted then
                onDeleted()
              else
                result.show("Non è stato possibile eliminare l'account.", success = false)
            catch
              case exception: Exception =>
                logger(exception)
                result.show("Non è stato possibile eliminare l'account.", success = false)
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
          result.label,
          actionBar(cancelButton, deleteButton)
        )

    new BorderPane:
      styleClass += "accounts-management-root"

      center =
        new StackPane:
          alignment = Pos.TopCenter
          children = Seq(content)
