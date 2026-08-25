package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.ManagedDocument
import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu.*
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, AuthorizationEngine, Classification, DocumentLog, Registration, Role}
import scalafx.scene.layout.Pane

object HomePageAdminView extends HomePage:

  override protected val pageTitle: String = "Homepage Amministratore"

  override protected def dashboardView(currentAccount: Account): Pane =
    AdminDashboardView(currentAccount, pageTitle)

  override protected def menuItems: Seq[MenuItem] =
    Seq(MenuItem(Dashboard, MenuAction.Dashboard)) ++
      AuthorizationEngine.permittedActions("admin").map(action => MenuItem(labels(action), action)) ++
      Seq(MenuItem(Logout, MenuAction.Logout))

  override protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit =
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

  private def showRegistrationRequests(navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      RegistrationRequestsManagementView(
        onProcess = selected =>
          showRegistrationRequestProcess(
            selected = selected,
            navigator = navigator,
            currentUsername = currentUsername
          ),
        onExit = () => navigator.dashboard()
      )
    )

  private def showRegistrationRequestProcess(selected: Registration, navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      RegistrationRequestProcessView(
        request = selected,
        operatorUsername = currentUsername,
        onProcessed = () =>
          showRegistrationRequests(
            navigator = navigator,
            currentUsername = currentUsername
          ),
        onExit = () =>
          showRegistrationRequests(
            navigator = navigator,
            currentUsername = currentUsername
          )
      )
    )

  private def showAccountManagement(navigator: Navigator): Unit =
    navigator.show(
      AccountManagementView(
        onAdd = () => showAccountAdd(navigator),
        onEdit = selected => showAccountEdit(selected = selected, navigator = navigator),
        onExit = () => navigator.dashboard()
      )
    )

  private def showAccountAdd(navigator: Navigator): Unit =
    navigator.show(
      AccountAddView(
        onSaved = () => showAccountManagement(navigator),
        onExit = () => showAccountManagement(navigator)
      )
    )

  private def showAccountEdit(selected: Account, navigator: Navigator): Unit =
    navigator.show(
      AccountEditView(
        selectedAccount = selected,
        onSaved = () => showAccountManagement(navigator),
        onExit = () => showAccountManagement(navigator)
      )
    )

  private def showRoleManagement(navigator: Navigator): Unit =
    navigator.show(
      RoleManagementView(
        onAdd = () => showRoleAdd(navigator),
        onEdit = selected => showRoleEdit(selected, navigator),
        onExit = () => navigator.dashboard()
      )
    )

  private def showRoleAdd(navigator: Navigator): Unit =
    navigator.show(
      RoleAddView(
        onSaved = () => showRoleManagement(navigator),
        onExit = () => showRoleManagement(navigator)
      )
    )

  private def showRoleEdit(selected: Role, navigator: Navigator): Unit =
    navigator.show(
      RoleEditView(
        selectedRole = selected,
        onSaved = () => showRoleManagement(navigator),
        onExit = () => showRoleManagement(navigator)
      )
    )

  private def showClassificationManagement(navigator: Navigator): Unit =
    navigator.show(
      ClassificationManagementView(
        onAdd = () => showClassificationAdd(navigator),
        onEdit = selected => showClassificationEdit(selected, navigator),
        onExit = () => navigator.dashboard()
      )
    )

  private def showClassificationAdd(navigator: Navigator): Unit =
    navigator.show(
      ClassificationAddView(
        onSaved = () => showClassificationManagement(navigator),
        onExit = () => showClassificationManagement(navigator)
      )
    )

  private def showClassificationEdit(selected: Classification, navigator: Navigator): Unit =
    navigator.show(
      ClassificationEditView(
        selectedClassification = selected,
        onSaved = () => showClassificationManagement(navigator),
        onExit = () => showClassificationManagement(navigator)
      )
    )

  private def showAuthorizationRulesManagement(navigator: Navigator): Unit =
    navigator.show(
      AuthorizationRulesManagementView(
        onAdd = () => showAuthorizationRuleAdd(navigator),
        onExit = () => navigator.dashboard()
      )
    )

  private def showAuthorizationRuleAdd(navigator: Navigator): Unit =
    navigator.show(
      AuthorizationRuleAddView(
        onSaved = () => showAuthorizationRulesManagement(navigator),
        onExit = () => showAuthorizationRulesManagement(navigator)
      )
    )

  private def showProfileEdit(selected: Account, navigator: Navigator): Unit =
    navigator.show(
      AccountEditView.profile(
        selectedAccount = selected,
        onSaved = () => navigator.dashboard(),
        onExit = () => navigator.dashboard()
      )
    )

  private def showStatistics(navigator: Navigator): Unit =
    navigator.show(
      StatisticsView(
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentManagementControl(navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      DocumentManagementControlView(
        onViewDetails = selected => showDocumentManagementDetails(selected, navigator, currentUsername),
        onSummary = selected => showDocumentManagementSummary(selected, navigator, currentUsername),
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentManagementSummary(selected: ManagedDocument, navigator: Navigator, currentUsername: String): Unit =
    val summary = DocumentManagementControlService.getDocumentManagementSummary(selected)
    navigator.show(
      DocumentManagementSummaryView(
        summary = summary,
        generatedBy = currentUsername,
        onExit = () => showDocumentManagementControl(navigator, currentUsername)
      )
    )

  private def showDocumentManagementDetails(selected: ManagedDocument, navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      DocumentManagementDetailsView(
        selectedDocument = selected,
        onExit = () => showDocumentManagementControl(navigator, currentUsername)
      )
    )

  private def showDocumentLogManagement(navigator: Navigator): Unit =
    navigator.show(
      DocumentLogManagementView(
        onView = selected => showDocumentLogDetails(selected, navigator),
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentLogDetails(selected: DocumentLog, navigator: Navigator): Unit =
    navigator.show(
      DocumentLogDetailsView(
        selectedLog = selected,
        onExit = () => showDocumentLogManagement(navigator)
      )
    )