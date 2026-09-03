package pkg.a.gui.views

import pkg.a.gui.services.{DocumentManagementControlService, RegistrationRequestService}
import pkg.a.gui.services.DocumentManagementControlService.Stages
import pkg.a.gui.views.DashboardView.DashboardCard
import pkg.b.logic.Account
import scalafx.scene.layout.VBox
import pkg.a.gui.text.UiText.Dashboards.Admin as Text

object DashboardAdminView:

  def apply(currentAccount: Account, title: String): VBox =

    val documents = DocumentManagementControlService.getManagedDocuments()
    val pendingRequests = new RegistrationRequestService().getPendingRequests.size
    val accounts = Account().getRecords[Account]().size
    val archivedDocuments = documents.count(_.stage == Stages.Archiving)

    val cards = Seq(
      DashboardCard(Text.TotalDocumentsTitle, documents.size.toString, Text.TotalDocumentsSubtitle),
      DashboardCard(Text.PendingRequestsTitle, pendingRequests.toString, Text.PendingRequestsSubtitle),
      DashboardCard(Text.RegisteredAccountsTitle, accounts.toString, Text.RegisteredAccountsSubtitle),
      DashboardCard(Text.ArchivedDocumentsTitle, archivedDocuments.toString, Text.ArchivedDocumentsSubtitle)
    )

    DashboardView(title, cards, documents)