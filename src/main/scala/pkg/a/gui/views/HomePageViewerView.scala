package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu.*
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, ArchivedDocument}
import scalafx.scene.layout.Pane

object HomePageViewerView extends HomePage:

  override protected val pageTitle: String = "Homepage Viewer"

  override protected def dashboardView(currentAccount: Account): Pane =
    ViewerDashboardView(currentAccount, pageTitle)

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
        showArchivedDocuments(currentAccount, navigator)

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

  private def showArchivedDocuments(currentAccount: Account, navigator: Navigator): Unit =
    navigator.show(
      ArchivedDocumentManagementView(
        onView = selected => showArchivedDocumentDetails(selected, currentAccount, navigator),
        onExit = () => navigator.dashboard(),
        documentFilter = document => document.getClassification.trim.equalsIgnoreCase(currentAccount.getArea.trim)
      )
    )

  private def showArchivedDocumentDetails(selected: ArchivedDocument, currentAccount: Account, navigator: Navigator): Unit =
    navigator.show(
      ArchivedDocumentDetailsView(
        selectedDocument = selected,
        onExit = () => showArchivedDocuments(currentAccount, navigator)
      )
    )