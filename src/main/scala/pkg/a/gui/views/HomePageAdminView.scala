package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, Classification, Registration, Role}

object HomePageAdminView extends HomePage:

  override protected val pageTitle: String = "Homepage Amministratore"

  override protected val roleDescription: String = "Amministratore"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem(Menu.Dashboard, MenuAction.Dashboard),
      MenuItem(Menu.Profile, MenuAction.Profilo),
      MenuItem(Menu.Statistics, MenuAction.Statistiche),
      MenuItem(Menu.Log, MenuAction.Log),
      MenuItem(Menu.ManagementControl, MenuAction.ControlloGestione),
      MenuItem(Menu.Registrations, MenuAction.Registrazioni),
      MenuItem(Menu.UserAccounts, MenuAction.AccountUtenti),
      MenuItem(Menu.Roles, MenuAction.Ruoli),
      MenuItem(Menu.Classifications, MenuAction.Classifiche),
      MenuItem(Menu.Logout, MenuAction.Logout)
    )

  override protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit =
    action match

      case MenuAction.Profilo =>
        showProfileEdit(currentAccount, navigator)

      case MenuAction.Statistiche =>
        navigator.show(createPlaceholder("Statistiche"))

      case MenuAction.Log =>
        navigator.show(createPlaceholder("Log"))

      case MenuAction.ControlloGestione =>
        navigator.show(createPlaceholder("Controllo di gestione"))

      case MenuAction.Registrazioni =>
        showRegistrationRequests(navigator, currentAccount.getUsername)

      case MenuAction.AccountUtenti =>
        showAccountManagement(navigator)

      case MenuAction.Ruoli =>
        showRoleManagement(navigator)

      case MenuAction.Classifiche =>
        showClassificationManagement(navigator)

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
        onExit = () =>
          navigator.dashboard()
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

  private def showProfileEdit(selected: Account, navigator: Navigator): Unit =
    navigator.show(
      AccountEditView.profile(
        selectedAccount = selected,
        onSaved = () => navigator.dashboard(),
        onExit = () => navigator.dashboard()
      )
    )