package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.Stages
import pkg.a.gui.text.UiText.Dashboards.Viewer as Text
import pkg.a.gui.views.DashboardView.DashboardCard
import pkg.b.logic.Account
import pkg.d.util.DateTime
import scalafx.scene.layout.VBox

object DashboardViewerView:

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
      DashboardCard(Text.AvailableDocumentsTitle, documents.size.toString, Text.AvailableDocumentsSubtitle),
      DashboardCard(Text.ArchivedTodayTitle, archivedToday.toString, Text.ArchivedTodaySubtitle),
      DashboardCard(Text.ArchivedThisMonthTitle, archivedThisMonth.toString, Text.ArchivedThisMonthSubtitle),
      DashboardCard(Text.AreaTitle, currentAccount.getArea, Text.AreaSubtitle)
    )

    DashboardView(title, cards, documents)