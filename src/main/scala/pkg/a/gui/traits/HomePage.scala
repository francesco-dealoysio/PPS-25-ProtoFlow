package pkg.a.gui.traits

import pkg.a.gui.navigation.HomeNavigator
import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiStyles.HomePage.*
import pkg.a.gui.text.UiText.Common.Dialogs.{Denied, UnsavedChanges, Logout as LogoutDialog}
import pkg.a.gui.text.UiText.Menu.{Dashboard, labels, Logout as LogoutLabel}
import pkg.b.logic.{Account, AuthorizationEngine, Role}
import scalafx.scene.control.Button
import scalafx.scene.layout.*

/**
 * Defines the common structure and navigation behaviour of role-based home pages,
 * including menu actions, authorization, and view navigation flows.
 */
trait HomePage extends Root:

  /**
   * Returns the title of the home page.
   */
  protected def pageTitle: String

  /**
   * Creates the dashboard view for the current account.
   * @param currentAccount the authenticated account
   * @return the dashboard pane
   */
  protected def dashboardView(currentAccount: Account): Pane

  /**
   * Handles a menu action by navigating to the corresponding view.
   * @param action         the selected menu action
   * @param navigator      the navigator used to change the current view
   * @param currentAccount the authenticated account
   */
  protected def handleAction(action: MenuAction, navigator: HomeNavigator, currentAccount: Account): Unit

  /**
   * Builds the home page for the authenticated account.
   * @param currentAccount the authenticated account
   * @param onLogout       the action executed after logout confirmation
   * @return the configured home page
   */
  final def apply(currentAccount: Account, onLogout: () => Unit = () => ()): BorderPane =
    val roleLogic = new Role()
    val roleName =
      roleLogic
        .getRecords[Role]()
        .find(_.getRole.equalsIgnoreCase(currentAccount.getRole))
        .map(_.getName)
        .getOrElse(currentAccount.getRole)

    val contentArea =
      new StackPane:
        children = Seq(dashboardView(currentAccount))

    val navigator =
      new HomeNavigator(
        contentArea = contentArea,
        dashboardFactory = () => dashboardView(currentAccount),
        confirmUnsavedChanges = () =>
          askConfirmation(
            titleText = UnsavedChanges.Title,
            header = UnsavedChanges.Header,
            content = UnsavedChanges.Content
          )
      )

    def navigate(action: MenuAction): Unit =
      action match
        case MenuAction.Dashboard =>
          navigator.dashboard()

        case MenuAction.Logout =>
          val confirmed =
            askConfirmation(
              titleText = LogoutDialog.Title,
              header = LogoutDialog.Header,
              content = LogoutDialog.Content
            )

          if confirmed then
            onLogout()

        case action =>
          if AuthorizationEngine.isAuthorized(currentAccount.getRole, action) then

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

    val sidebar = createSidebar(currentAccount.getRole, navigate)

    createRoot(
      currentUser = currentAccount.getUsername,
      roleName = roleName,
      contentArea = contentArea,
      menu = sidebar,
      onProfileOpen = () => navigate(MenuAction.Profilo)
    )

  private def menuItems(role: String): Seq[MenuItem] =
    val permitted = AuthorizationEngine
      .permittedActions(role)
      .map(action => MenuItem(labels(action), action))
    MenuItem(Dashboard, MenuAction.Dashboard) +: permitted :+ MenuItem(LogoutLabel, MenuAction.Logout)

  private def createSidebar(role: String, onNavigate: MenuAction => Unit): VBox =
    val buttons =
      menuItems(role).map: item =>
        new Button(item.label):
          maxWidth = Double.MaxValue
          styleClass += SidebarButtonStyle
          onAction = _ => onNavigate(item.action)

    new VBox:
      styleClass += SidebarStyle
      children = buttons