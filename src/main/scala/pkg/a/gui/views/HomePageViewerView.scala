package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu.*
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, ArchivedDocument}

object HomePageViewerView extends HomePage:

  override protected val pageTitle: String = "Homepage Viewer"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem(Dashboard, MenuAction.Dashboard),
      MenuItem(Profile, MenuAction.Profilo),
      MenuItem(ArchivedDocuments, MenuAction.VisualizzazioneArchiviazioni),
      MenuItem(Logout, MenuAction.Logout)
    )

  override protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit =
    action match

      case MenuAction.Profilo =>
        showProfileEdit(currentAccount, navigator)

      case MenuAction.VisualizzazioneArchiviazioni =>
        showArchivedDocuments(navigator)

      case _ =>
        ()

  private def showProfileEdit(selected: Account, navigator: Navigator): Unit =
    navigator.show(
      AccountEditView.profile(
        selectedAccount = selected,
        onSaved = () => navigator.dashboard(),
        onExit = () => navigator.dashboard()
      )
    )

  private def showArchivedDocuments(navigator: Navigator): Unit =
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
        onExit = () => showArchivedDocuments(navigator)
      )
    )