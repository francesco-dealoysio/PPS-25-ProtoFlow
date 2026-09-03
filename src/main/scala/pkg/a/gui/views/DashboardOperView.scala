package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.Stages
import pkg.a.gui.views.DashboardView.DashboardCard
import pkg.b.logic.{Account, DocumentLog}
import pkg.d.util.DateTime
import scalafx.scene.layout.VBox
import pkg.a.gui.text.UiText.Dashboards.Operator as Text

object DashboardOperView:

  def apply(currentAccount: Account, title: String): VBox =

    val documents = DocumentManagementControlService.getManagedDocuments()
    val documentsToRegister = documents.count(_.stage == Stages.Loading)
    val documentsToArchive = documents.count(_.stage == Stages.Registering)
    val archivedDocuments = documents.count(_.stage == Stages.Archiving)
    val operationsToday =
      DocumentLog()
        .getRecords[DocumentLog]()
        .count: log =>
          log.getProcessedDate == DateTime.localDate &&
            log.getProcessedBy.trim.equalsIgnoreCase(currentAccount.getUsername.trim)

    val pendingDocuments = documents.filter(_.stage != Stages.Archiving)

    val cards = Seq(
      DashboardCard(Text.ToRegisterTitle, documentsToRegister.toString, Text.ToRegisterSubtitle),
      DashboardCard(Text.ToArchiveTitle, documentsToArchive.toString, Text.ToArchiveSubtitle),
      DashboardCard(Text.ArchivedTitle, archivedDocuments.toString, Text.ArchivedSubtitle),
      DashboardCard(Text.TodayOperationsTitle, operationsToday.toString, Text.TodayOperationsSubtitle)
    )

    DashboardView(title, cards, pendingDocuments)