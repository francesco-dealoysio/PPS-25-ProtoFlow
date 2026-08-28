package pkg.a.gui.traits

import pkg.a.gui.structures.{HomePageViewModel, MenuAction, MenuItem}
import pkg.a.gui.text.UiStyles.HomePage.*
import pkg.a.gui.text.UiText.Common.Dialogs.{Denied, Logout as LogoutDialog}
import pkg.a.gui.text.UiText.Menu.{Dashboard, Logout as LogoutLabel, labels}
import pkg.b.logic.{Account, AuthorizationEngine, Role}
import scalafx.geometry.Pos
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
  protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit

  /**
   * Manages navigation within the home page content area.
   * @param contentArea      the container in which views are rendered
   * @param dashboardFactory the function used to create the dashboard view
   */
  protected final class Navigator(contentArea: StackPane, dashboardFactory: () => Pane):
    /**
     * Displays the given view in the content area.
     * @param view the view to display
     */
    def show(view: => Pane): Unit =
      render(contentArea, view)

    /**
     * Displays the dashboard view.
     */
    def dashboard(): Unit =
      show(dashboardFactory())

  /**
   * Builds the home page for the authenticated account.
   * @param viewModel      the view model managing the selected menu action
   * @param currentAccount the authenticated account
   * @param onLogout       the action executed after logout confirmation
   * @return the configured home page
   */
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
              titleText = LogoutDialog.Title,
              header = LogoutDialog.Header,
              content = LogoutDialog.Content
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

    val sidebar = createSidebar(currentAccount.getRole, action => navigate(action))

    createRoot(
      currentUser = currentAccount.getUsername,
      roleName = roleName,
      contentArea = contentArea,
      menu = sidebar,
      onProfileOpen = () => navigate(MenuAction.Profilo)
    )

  /**
   * Starts a CRUD navigation flow between management, creation, and editing views.
   * @param navigator      the navigator used to change views
   * @param managementView the management view factory
   * @param addView        the creation view factory
   * @param editView       the editing view factory
   * @tparam T the type of the managed entity
   */
  protected final def showCrud[T](
                                   navigator: Navigator,
                                   managementView: (() => Unit, T => Unit, () => Unit) => Pane,
                                   addView: (() => Unit, () => Unit) => Pane,
                                   editView: (T, () => Unit, () => Unit) => Pane
                                 ): Unit =
    def management(): Unit =
      val back = () => management()
      navigator.show(
        managementView(
          () => navigator.show(addView(back, back)),
          selected => navigator.show(editView(selected, back, back)),
          () => navigator.dashboard()
        )
      )
    management()

  /**
   * Starts a navigation flow from a management view to a selected item view.
   * @param navigator      the navigator used to change views
   * @param managementView the management view factory
   * @param selectedView   the view factory for the selected item
   * @tparam T the type of the selectable entity
   */
  protected final def showSelectionFlow[T](
                                            navigator: Navigator,
                                            managementView: (T => Unit, () => Unit) => Pane,
                                            selectedView: (T, () => Unit) => Pane
                                          ): Unit =
    def management(): Unit =
      val back = () => management()
      navigator.show(
        managementView(
          selected => navigator.show(selectedView(selected, back)),
          () => navigator.dashboard()
        )
      )
    management()

  /**
   * Starts a navigation flow between a management view and a creation view.
   * @param navigator      the navigator used to change views
   * @param managementView the management view factory
   * @param addView        the creation view factory
   */
  protected final def showCreateFlow(
                                      navigator: Navigator,
                                      managementView: (() => Unit, () => Unit) => Pane,
                                      addView: (() => Unit, () => Unit) => Pane
                                    ): Unit =
    def management(): Unit =
      val back = () => management()
      navigator.show(
        managementView(
          () => navigator.show(addView(back, back)),
          () => navigator.dashboard()
        )
      )
    management()

  private def menuItems(role: String): Seq[MenuItem] =
    Seq(MenuItem(Dashboard, MenuAction.Dashboard)) ++
      AuthorizationEngine
        .permittedActions(role)
        .map(action => MenuItem(labels(action), action)) ++
      Seq(MenuItem(LogoutLabel, MenuAction.Logout))

  private def createSidebar(role: String, onNavigate: MenuAction => Unit): VBox =
    val buttons =
      menuItems(role).map: item =>
        new Button(item.label):
          maxWidth = Double.MaxValue
          alignment = Pos.CenterLeft
          styleClass += SidebarButtonStyle
          onAction = _ => onNavigate(item.action)

    new VBox:
      prefWidth = 230
      styleClass += SidebarStyle
      children = buttons