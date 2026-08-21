package pkg.a.gui.views

import pkg.a.gui.services.{DocumentManagementControlService, RegistrationRequestService}
import pkg.a.gui.services.DocumentManagementControlService.Stages
import pkg.a.gui.views.DashboardView.DashboardCard
import pkg.b.logic.Account
import scalafx.scene.layout.VBox

object AdminDashboardView:

  def apply(currentAccount: Account, title: String): VBox =

    val documents = DocumentManagementControlService.getManagedDocuments()
    val pendingRequests = new RegistrationRequestService().getPendingRequests.size
    val accounts = Account().getRecords[Account]().size
    val archivedDocuments = documents.count(_.stage == Stages.Archiving)

    val cards = Seq(
      DashboardCard("Documenti totali", documents.size.toString, "Documenti nel sistema"),
      DashboardCard("Richieste pendenti", pendingRequests.toString, "Richieste da elaborare"),
      DashboardCard("Account registrati", accounts.toString, "Utenti presenti"),
      DashboardCard("Documenti archiviati", archivedDocuments.toString, "Documenti completati")
    )

    DashboardView(title, cards, documents)