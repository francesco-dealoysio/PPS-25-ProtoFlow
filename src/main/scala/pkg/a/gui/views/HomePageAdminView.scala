package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.ManagedDocument
import pkg.a.gui.structures.MenuAction
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, Classification, DocumentLog, Registration, Role}
import scalafx.scene.layout.Pane
import pkg.a.gui.navigation.HomeNavigator
import pkg.a.gui.navigation.NavigationFlows.*
import pkg.a.gui.text.UiText.HomePages

object HomePageAdminView extends HomePage:

  override protected val pageTitle: String = HomePages.AdminTitle

  override protected def dashboardView(currentAccount: Account): Pane =
    DashboardAdminView(currentAccount, pageTitle)

  override protected def handleAction(action: MenuAction, navigator: HomeNavigator, currentAccount: Account): Unit =
    action match

      case MenuAction.Profilo =>
        showProfileEdit(currentAccount, navigator)

      case MenuAction.Statistiche =>
        showStatistics(navigator)

      case MenuAction.Log =>
        showDocumentLogManagement(navigator)

      case MenuAction.ControlloGestione =>
        showDocumentManagementControl(navigator, currentAccount.getUsername)

      case MenuAction.Registrazioni =>
        showRegistrationRequests(navigator, currentAccount.getUsername)

      case MenuAction.AccountUtenti =>
        showAccountManagement(navigator)

      case MenuAction.Ruoli =>
        showRoleManagement(navigator)

      case MenuAction.Classifiche =>
        showClassificationManagement(navigator)

      case MenuAction.GestioneAutorizzazioni =>
        showAuthorizationRulesManagement(navigator)

      case _ =>
        ()

  private def showRegistrationRequests(navigator: HomeNavigator, currentUsername: String): Unit =
    showSelectionFlow[Registration](
      navigator,
      (onProcess, onExit) => RegistrationRequestsManagementView(onProcess, onExit),
      (selected, back) => RegistrationRequestProcessView(selected, currentUsername, back, back)
    )

  private def showAccountManagement(navigator: HomeNavigator): Unit =
    showCrud[Account](
      navigator,
      (onAdd, onEdit, onExit) => AccountManagementView(onAdd, onEdit, onExit),
      (onSaved, onExit) => AccountAddView(onSaved, onExit),
      (selected, onSaved, onExit) => AccountEditView(selected, onSaved, onExit)
    )

  private def showRoleManagement(navigator: HomeNavigator): Unit =
    showCrud[Role](
      navigator,
      (onAdd, onEdit, onExit) => RoleManagementView(onAdd, onEdit, onExit),
      (onSaved, onExit) => RoleAddView(onSaved, onExit),
      (selected, onSaved, onExit) => RoleEditView(selected, onSaved, onExit)
    )

  private def showClassificationManagement(navigator: HomeNavigator): Unit =
    showCrud[Classification](
      navigator,
      (onAdd, onEdit, onExit) => ClassificationManagementView(onAdd, onEdit, onExit),
      (onSaved, onExit) => ClassificationAddView(onSaved, onExit),
      (selected, onSaved, onExit) => ClassificationEditView(selected, onSaved, onExit)
    )

  private def showAuthorizationRulesManagement(navigator: HomeNavigator): Unit =
    showCreateFlow(
      navigator,
      (onAdd, onExit) => AuthorizationRulesManagementView(onAdd, onExit),
      (onSaved, onExit) => AuthorizationRuleAddView(onSaved, onExit)
    )

  private def showProfileEdit(selected: Account, navigator: HomeNavigator): Unit =
    navigator.show(
      AccountEditView.profile(
        selectedAccount = selected,
        onSaved = () => navigator.dashboard(),
        onExit = () => navigator.dashboard()
      )
    )

  private def showStatistics(navigator: HomeNavigator): Unit =
    navigator.show(
      StatisticsView(
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentManagementControl(navigator: HomeNavigator, currentUsername: String): Unit =
    navigator.show(
      DocumentManagementControlView(
        onViewDetails = selected => showDocumentManagementDetails(selected, navigator, currentUsername),
        onSummary = selected => showDocumentManagementSummary(selected, navigator, currentUsername),
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentManagementSummary(selected: ManagedDocument, navigator: HomeNavigator, currentUsername: String): Unit =
    val summary = DocumentManagementControlService.getDocumentManagementSummary(selected)
    navigator.show(
      DocumentManagementSummaryView(
        summary = summary,
        generatedBy = currentUsername,
        onExit = () => showDocumentManagementControl(navigator, currentUsername)
      )
    )

  private def showDocumentManagementDetails(selected: ManagedDocument, navigator: HomeNavigator, currentUsername: String): Unit =
    navigator.show(
      DocumentManagementDetailsView(
        selectedDocument = selected,
        onExit = () => showDocumentManagementControl(navigator, currentUsername)
      )
    )

  private def showDocumentLogManagement(navigator: HomeNavigator): Unit =
    showSelectionFlow[DocumentLog](
      navigator,
      (onView, onExit) => DocumentLogManagementView(onView, onExit),
      (selected, onExit) => DocumentLogDetailsView(selected, onExit)
    )