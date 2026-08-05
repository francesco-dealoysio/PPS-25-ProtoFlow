package pkg.a.gui.traits

import pkg.a.gui.structures.{HomePageViewModel, MenuAction, MenuItem}
import pkg.b.logic.Account
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout.*
import pkg.a.gui.text.UiStyles
import pkg.a.gui.text.UiText.Common.Dialogs.Logout.*
import pkg.a.gui.text.UiText.Common.Documents.NoDocuments

trait HomePage extends Root:

  protected def pageTitle: String
  protected def roleDescription: String
  protected def menuItems: Seq[MenuItem]

  protected def handleAction(action: MenuAction, navigator: Navigator, currentAccount: Account): Unit


  protected final class Navigator(contentArea: StackPane, dashboardFactory: () => Pane):

    def show(view: => Pane): Unit =
      render(contentArea, view)

    def dashboard(): Unit =
      show(dashboardFactory())

  final def apply(
                   viewModel: HomePageViewModel,
                   currentAccount: Account,
                   onLogout: () => Unit = () => ()
                 ): BorderPane =

    val contentArea =
      new StackPane:
        styleClass +=  UiStyles.HomePage.ContentAreaStyle
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
              titleText = TitleDialog,
              header = HeaderDialog,
              content = ContentDialog
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
      roleDescription = roleDescription,
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
          styleClass += UiStyles.HomePage.SidebarButtonStyle
          onAction = _ => onNavigate(item.action)

    new VBox:
      prefWidth = 230
      styleClass += UiStyles.HomePage.SidebarStyle
      children = buttons

  protected def createPlaceholder(title: String): VBox =
    new VBox:
      styleClass += UiStyles.HomePage.PlaceholderContainerStyle
      children = Seq(fieldLabel(title, UiStyles.HomePage.PlaceholderTitleStyle))

  private def dashboardContent(): VBox =
    new VBox:
      styleClass += UiStyles.HomePage.DashboardContainerStyle
      children = Seq(
        fieldLabel(pageTitle, UiStyles.HomePage.PageTitleStyle),
        createCards(),
        createDocumentsTable()
      )

  private def createCards(): HBox =
    new HBox:
      styleClass += UiStyles.HomePage.CardsContainerStyle
      children = Seq(
        statCard("Totale Documenti", "0", "Nessun documento"),
        statCard("In Carico", "0", "Nessun documento"),
        statCard("Registrati", "0", "Nessun documento"),
        statCard("Archiviati", "0", "Nessun documento")
      )

  private def statCard(title: String, value: String, subtitle: String): VBox =
    new VBox:
      prefWidth = 190
      styleClass += UiStyles.HomePage.StatCardStyle
      children = Seq(
        fieldLabel(title, UiStyles.HomePage.StatCardTitleStyle),
        fieldLabel(value, UiStyles.HomePage.StatCardValueStyle),
        fieldLabel(subtitle, UiStyles.HomePage.StatCardSubtitleStyle)
      )

  private def createDocumentsTable(): TableView[Unit] =
    new TableView[Unit]:
      styleClass += UiStyles.HomePage.DocumentsTableStyle
      placeholder = fieldLabel(NoDocuments, UiStyles.HomePage.TablePlaceholderStyle)
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