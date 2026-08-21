package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.Stages
import pkg.a.gui.views.DashboardView.DashboardCard
import pkg.b.logic.Account
import pkg.d.util.DateTime
import scalafx.scene.layout.VBox

object ViewerDashboardView:

  def apply(currentAccount: Account, title: String): VBox =

    val documents =
      DocumentManagementControlService
        .getManagedDocuments()
        .filter: document =>
          document.stage == Stages.Archiving &&
            document.classification.trim.equalsIgnoreCase(currentAccount.getArea.trim)

    val today = DateTime.localDate
    val currentMonth = today.take(7)
    val archivedToday = documents.count(_.archivedDate == today)
    val archivedThisMonth = documents.count(_.archivedDate.startsWith(currentMonth))
    val cards = Seq(
      DashboardCard("Documenti disponibili", documents.size.toString, "Documenti della tua area"),
      DashboardCard("Archiviati oggi", archivedToday.toString, "Nuovi documenti"),
      DashboardCard("Archiviati questo mese", archivedThisMonth.toString, "Documenti del mese"),
      DashboardCard("Area", currentAccount.getArea, "Area di appartenenza")
    )

    DashboardView(title, cards, documents)