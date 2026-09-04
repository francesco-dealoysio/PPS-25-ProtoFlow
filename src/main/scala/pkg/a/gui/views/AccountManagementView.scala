package pkg.a.gui.views

import pkg.a.gui.text.UiText.Accounts.Management as Text
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.traits.Management
import pkg.b.logic.{Account, AuthorizationEngine}
import pkg.b.logic.pdf.{PdfTableCreator, PdfViewer}
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.BorderPane

object AccountManagementView extends Management:

  def apply(
             onAdd: () => Unit = () => (),
             onEdit: Account => Unit = _ => (),
             onExit: () => Unit = () => ()
           ): BorderPane =

    val accountLogic = new Account()
    val accounts = ObservableBuffer.empty[Account]
    val result = createResultMessage()
    val table = managementTable(accounts, Text.Empty)

    table.columns ++= Seq(
      stringColumn[Account](Labels.Surname)(_.getSurname),
      stringColumn[Account](Labels.Name)(_.getName),
      stringColumn[Account](Labels.Username)(_.getUsername),
      stringColumn[Account](Labels.Email)(_.getEmail),
      stringColumn[Account](Labels.Phone)(_.getPhone),
      stringColumn[Account](Labels.Role)(_.getRole),
      stringColumn[Account](Labels.Area)(_.getArea),
      stringColumn[Account](Labels.Assignment)(_.getAssignment)
    )

    def loadAccounts(): Unit =
      loadTableItemsSafely(accounts, result, Text.Empty, Text.LoadError):
        accountLogic
          .getRecords[Account]()
          .sortBy(_.getId.toIntOption.getOrElse(Int.MaxValue))

    def deleteSelectedAccount(): Unit =
      withSelectedItem(table, result, Text.SelectToDelete): selected =>
        val confirmed =
          askConfirmation(
            titleText = Text.DeleteTitle,
            header = Text.DeleteConfirmation,
            content =
              s"""Account: ${selected.getUsername}
                 |Nominativo: ${selected.getName} ${selected.getSurname}
                 |Codice: ${selected.getId}
                 |
                 |L'operazione non può essere annullata.""".stripMargin
          )

        if confirmed then
          val adminCount = accounts.count(_.getRole.equalsIgnoreCase("admin"))
          if !AuthorizationEngine.canDeleteAccount(selected.getRole, adminCount) then
            result.show(Text.LastAdminDeleteError, success = false)
          else
            val deleted = accountLogic.recordDelete(selected.getId)
            if deleted then
              loadAccounts()
              result.show(Text.deletedAccount(selected.getUsername), success = true)
            else
              result.show(Text.DeleteError, success = false)

    def accountRow(account: Account): Seq[String] =
      Seq(
        account.getSurname,
        account.getName,
        account.getUsername,
        account.getEmail,
        account.getPhone,
        account.getRole,
        account.getArea,
        account.getAssignment
      )

    def printAccounts(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileName}.pdf")
      val printed =
        PdfTableCreator.createTablePdf(
          pdfPathName = pdfPath,
          title = Text.PrintTitle,
          headers = Seq(
            Labels.Surname,
            Labels.Name,
            Labels.Username,
            Labels.Email,
            Labels.Phone,
            Labels.Role,
            Labels.Area,
            Labels.Assignment
          ),
          rows = accounts.toSeq.map(accountRow),
          columnWeights = Seq(1.2f, 1.2f, 1.4f, 2f, 1.3f, 1f, 1.2f, 1.5f)
        )

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    clearResultOnSelection(table, result)

    val addButton = primaryButton(Buttons.Add, () =>
      result.clear()
      onAdd())

    val editButton = secondaryButton(Buttons.Edit, () => withSelectedItem(table, result, Text.SelectToEdit)(onEdit))
    val deleteButton = dangerButton(Buttons.Delete, deleteSelectedAccount)
    disableWithoutSelection(table, editButton, deleteButton)
    val bottomActions = actionBar(Seq(closeButton(onExit), printButton(printAccounts), editButton, deleteButton, addButton))
    val header = titleBox(Text.Title, Text.Subtitle)

    loadAccounts()

    managementPage(
      growNode = Some(table),
      pageChildren = Seq(
        header,
        table,
        result.label,
        bottomActions
      )
    )
