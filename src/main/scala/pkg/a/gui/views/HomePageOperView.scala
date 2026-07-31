package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{ArchivedDocument, LoadedDocument, RegisteredDocument}

object HomePageOperView extends HomePage:

  override protected val pageTitle: String = "Homepage Operatore"

  override protected val roleDescription: String = "Operatore Protocollo"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem(Menu.Dashboard, MenuAction.Dashboard),
      MenuItem(Menu.Profile, MenuAction.Profilo),
      MenuItem(Menu.NewAssignment, MenuAction.NuovaPresaInCarico),
      MenuItem(Menu.DocumentsToRegister, MenuAction.DocumentiDaProtocollare),
      MenuItem(Menu.DocumentsToArchive, MenuAction.DocumentiDaArchiviare),
      MenuItem(Menu.ArchivedDocuments, MenuAction.DocumentiArchiviati),
      MenuItem(Menu.Logout, MenuAction.Logout)
    )

  override protected def handleAction(action: MenuAction, navigator: Navigator, currentUsername: String): Unit =
    action match
      case MenuAction.NuovaPresaInCarico =>
        showLoadedDocumentAdd(navigator, currentUsername)
      case MenuAction.DocumentiDaProtocollare =>
        showLoadedDocumentManagement(navigator, currentUsername)
      case MenuAction.DocumentiDaArchiviare =>
        showRegisteredDocumentManagement(navigator, currentUsername)
      case MenuAction.DocumentiArchiviati =>
        showArchivedDocumentManagement(navigator)
      case MenuAction.Profilo =>
        navigator.show(createPlaceholder("Profilo"))
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
        onExit = () => navigator.dashboard()
      )
    )

  private def showDocumentArchive(selected: RegisteredDocument, navigator: Navigator, currentUsername: String): Unit =
    navigator.show(
      ArchivedDocumentView(
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