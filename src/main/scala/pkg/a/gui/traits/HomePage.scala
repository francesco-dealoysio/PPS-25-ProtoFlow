package pkg.a.gui.traits

import pkg.a.gui.structures.{HomePageViewModel, MenuAction, MenuItem}
import pkg.b.logic.{Account, AuthorizationEngine, Role}
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.*
import pkg.a.gui.text.UiStyles.HomePage.*
import pkg.a.gui.text.UiText.Common.Dialogs.Logout
import pkg.a.gui.text.UiText.Common.Dialogs.Denied

trait HomePage extends Root:

  protected def pageTitle: String
  protected def menuItems: Seq[MenuItem]
  protected def dashboardView(currentAccount: Account): Pane

  protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit

  protected final class Navigator(contentArea: StackPane, dashboardFactory: () => Pane):

    def show(view: => Pane): Unit =
      render(contentArea, view)

    def dashboard(): Unit =
      show(dashboardFactory())

  final def apply(viewModel: HomePageViewModel, currentAccount: Account, onLogout: () => Unit = () => ()): BorderPane =
    val roleLogic = new Role()
    val roleName =
      roleLogic
        .getRecords[Role]()
        .find(_.getRole.equalsIgnoreCase(currentAccount.getRole))
        .map(_.getName)
        .getOrElse(currentAccount.getRole)

    val contentArea =
      new StackPane:
        styleClass += ContentAreaStyle
        children = Seq(dashboardView(currentAccount))

    val navigator =
      new Navigator(
        contentArea = contentArea,
        dashboardFactory = () => dashboardView(currentAccount)
      )

    def navigate(action: MenuAction): Unit =
      action match
        case MenuAction.Dashboard =>
          viewModel.select(MenuAction.Dashboard)
          navigator.dashboard()

        case MenuAction.Logout =>
          val confirmed =
            askConfirmation(
              titleText = Logout.Title,
              header = Logout.Header,
              content = Logout.Content
            )

          if confirmed then
            viewModel.select(MenuAction.Logout)
            onLogout()

        case action =>
          if AuthorizationEngine.isAuthorized(currentAccount.getRole, action) then
            viewModel.select(action)

            handleAction(
              action = action,
              navigator = navigator,
              currentAccount = currentAccount
            )
          else
            showError(
              titleText = Denied.Title,
              header = Denied.Header,
              content = Denied.Content
            )

    val sidebar = createSidebar(action => navigate(action))

    createRoot(
      currentUser = currentAccount.getUsername,
      roleName = roleName,
      contentArea = contentArea,
      menu = sidebar,
      onProfileOpen = () => navigate(MenuAction.Profilo)
    )

  private def createSidebar(onNavigate: MenuAction => Unit): VBox =
    val buttons =
      menuItems.map: item =>
        new Button(item.label):
          maxWidth = Double.MaxValue
          alignment = Pos.CenterLeft
          styleClass += SidebarButtonStyle
          onAction = _ => onNavigate(item.action)

    new VBox:
      prefWidth = 230
      styleClass += SidebarStyle
      children = buttons