package pkg.a.gui.traits

import pkg.a.gui.structures.{HomePageViewModel, MenuAction, MenuItem}
import pkg.b.logic.{Account, Role}
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout.*
import pkg.a.gui.text.UiStyles.HomePage.*
import pkg.a.gui.text.UiText.Common.Dialogs.Logout.*
import pkg.a.gui.text.UiText.Common.Documents.NoDocuments

trait HomePage extends Root:

  protected def pageTitle: String
  protected def menuItems: Seq[MenuItem]

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
        children = Seq(dashboardContent())

    val navigator =
      new Navigator(
        contentArea = contentArea,
        dashboardFactory = () => dashboardContent()
      )

    def navigate(action: MenuAction): Unit =
      action match
        case MenuAction.Dashboard =>
          viewModel.select(MenuAction.Dashboard)
          navigator.dashboard()

        case MenuAction.Logout =>
          val confirmed =
            askConfirmation(
              titleText = Title,
              header = Header,
              content = Content
            )

          if confirmed then
            viewModel.select(MenuAction.Logout)
            onLogout()

        case action =>
          viewModel.select(action)

          handleAction(
            action = action,
            navigator = navigator,
            currentAccount = currentAccount
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

  private def dashboardContent(): VBox =
    new VBox:
      styleClass += DashboardContainerStyle
      children = Seq(
        fieldLabel(pageTitle, PageTitleStyle),
        createCards(),
        createDocumentsTable()
      )

  private def createCards(): HBox =
    new HBox:
      styleClass += CardsContainerStyle
      children = Seq(
        statCard("Totale Documenti", "0", "Nessun documento"),
        statCard("In Carico", "0", "Nessun documento"),
        statCard("Registrati", "0", "Nessun documento"),
        statCard("Archiviati", "0", "Nessun documento")
      )

  private def statCard(title: String, value: String, subtitle: String): VBox =
    new VBox:
      prefWidth = 190
      styleClass += StatCardStyle
      children = Seq(
        fieldLabel(title, StatCardTitleStyle),
        fieldLabel(value, StatCardValueStyle),
        fieldLabel(subtitle, StatCardSubtitleStyle)
      )

  private def createDocumentsTable(): TableView[Unit] =
    new TableView[Unit]:
      styleClass += DocumentsTableStyle
      placeholder = fieldLabel(NoDocuments, TablePlaceholderStyle)
      columns ++=
        Seq(
          "Protocollo",
          "Oggetto",
          "Mittente",
          "Categoria",
          "Stato",
          "Data"
        ).map: title =>
          new TableColumn[Unit, String]:
            text = title