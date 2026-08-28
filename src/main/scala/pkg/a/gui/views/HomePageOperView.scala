package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction}
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, ArchivedDocument, LoadedDocument, RegisteredDocument}
import scalafx.scene.layout.Pane

object HomePageOperView extends HomePage:

  override protected val pageTitle: String = "Homepage Operatore"

  override protected def dashboardView(currentAccount: Account): Pane =
    OperDashboardView(currentAccount, pageTitle)

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
    showSelectionFlow[LoadedDocument](
      navigator,
      (onRegister, onExit) => LoadedDocumentManagementView(onRegister, onExit),
      (selected, back) => DocumentRegistrationView(selected, currentUsername, back, back)
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
    showSelectionFlow[ArchivedDocument](
      navigator,
      (onView, onExit) => ArchivedDocumentManagementView(onView, onExit),
      (selected, back) => ArchivedDocumentDetailsView(selected, back)
    )

  private def showProfileEdit(selected: Account, navigator: Navigator): Unit =
    navigator.show(
      AccountEditView.profile(
        selectedAccount = selected,
        onSaved = () => navigator.dashboard(),
        onExit = () => navigator.dashboard()
      )
    )