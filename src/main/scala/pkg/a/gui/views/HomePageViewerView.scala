package pkg.a.gui.views

import pkg.a.gui.structures.MenuAction
import pkg.a.gui.traits.HomePage
import pkg.b.logic.{Account, ArchivedDocument}
import scalafx.scene.layout.Pane

object HomePageViewerView extends HomePage:

  override protected val pageTitle: String = "Homepage Viewer"

  override protected def dashboardView(currentAccount: Account): Pane =
    ViewerDashboardView(currentAccount, pageTitle)

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
    showSelectionFlow[ArchivedDocument](
      navigator,
      (onView, onExit) =>
        ArchivedDocumentManagementView(onView, onExit,
          documentFilter =
            _.getClassification
              .trim
              .equalsIgnoreCase(currentAccount.getArea.trim)
        ),
      (selected, back) => ArchivedDocumentDetailsView(selected, back)
    )