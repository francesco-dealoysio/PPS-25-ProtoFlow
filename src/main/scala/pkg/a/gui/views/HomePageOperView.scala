package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu.*
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, ArchivedDocument, AuthorizationEngine, LoadedDocument, RegisteredDocument}

object HomePageOperView extends HomePage:

  override protected val pageTitle: String = "Homepage Operatore"

  override protected def menuItems: Seq[MenuItem] =
    Seq(MenuItem(Dashboard, MenuAction.Dashboard), MenuItem(Profile, MenuAction.Profilo)) ++
      AuthorizationEngine.permittedActions("oper").map(action => MenuItem(labels(action), action)) ++
      Seq(MenuItem(Logout, MenuAction.Logout))

  override protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit =
    action match
      case MenuAction.NuovaPresaInCarico =>
        showLoadedDocumentAdd(navigator, currentAccount.getUsername)
      case MenuAction.DocumentiDaProtocollare =>
        showLoadedDocumentManagement(navigator, currentAccount.getUsername)
      case MenuAction.DocumentiDaArchiviare =>
        showRegisteredDocumentManagement(navigator, currentAccount.getUsername)
      case MenuAction.DocumentiArchiviati =>
        showArchivedDocumentManagement(navigator)
      case MenuAction.Profilo =>
        showProfileEdit(currentAccount, navigator)
      case _ =>
        ()

  private def showLoadedDocumentAdd(navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      LoadedDocumentAddView(
        operatorUsername = currentUsername,
        onSaved = () => (),
        onExit = () => showLoadedDocumentManagement(navigator, currentUsername)
      )
    )

  private def showLoadedDocumentManagement(navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      LoadedDocumentManagementView(
        onRegister = selected => showDocumentRegistration(selected, navigator, currentUsername),
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentRegistration(selected: LoadedDocument, navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      DocumentRegistrationView(
        selectedDocument = selected,
        operatorUsername = currentUsername,
        onRegistered = () => showLoadedDocumentManagement(navigator, currentUsername),
        onExit = () => showLoadedDocumentManagement(navigator, currentUsername)
      )
    )

  private def showRegisteredDocumentManagement(navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      RegisteredDocumentManagementView(
        onArchive = selected => showDocumentArchive(selected, navigator, currentUsername),
        onView = selected => showRegisteredDocumentDetails(selected, navigator, currentUsername),
        onExit = () => navigator.dashboard()
      )
    )

  private def showRegisteredDocumentDetails(selected: RegisteredDocument, navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      RegisteredDocumentDetailsView(
        selectedDocument = selected,
        onExit = () => showRegisteredDocumentManagement(navigator, currentUsername)
      )
    )

  private def showDocumentArchive(selected: RegisteredDocument, navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      DocumentArchivingView(
        selectedDocument = selected,
        operatorUsername = currentUsername,
        onArchived = () => showRegisteredDocumentManagement(navigator, currentUsername),
        onExit = () => showRegisteredDocumentManagement(navigator, currentUsername)
      )
    )

  private def showArchivedDocumentManagement(navigator: Navigator): Unit =
    navigator.show(
      ArchivedDocumentManagementView(
        onView = selected => showArchivedDocumentDetails(selected, navigator),
        onExit = () => navigator.dashboard()
      )
    )

  private def showArchivedDocumentDetails(selected: ArchivedDocument, navigator: Navigator): Unit =
    navigator.show(
      ArchivedDocumentDetailsView(
        selectedDocument = selected,
        onExit = () => showArchivedDocumentManagement(navigator)
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