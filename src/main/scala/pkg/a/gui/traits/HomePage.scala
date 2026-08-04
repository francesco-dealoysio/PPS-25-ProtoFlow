package pkg.a.gui.traits

import pkg.a.gui.structures.{HomePageViewModel, MenuAction, MenuItem}
import pkg.b.logic.Account
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout.*

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
        styleClass += "content-area"
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
              titleText = "Conferma logout",
              header = "Vuoi uscire da ProtoFlow?",
              content = "La sessione corrente verrà terminata."
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
          styleClass += "sidebar-button"
          onAction = _ => onNavigate(item.action)

    new VBox:
      prefWidth = 230
      styleClass += "sidebar"
      children = buttons

  protected def createPlaceholder(title: String): VBox =
    new VBox:
      styleClass += "placeholder-container"
      children = Seq(fieldLabel(title, "placeholder-title"))

  private def dashboardContent(): VBox =
    new VBox:
      styleClass += "dashboard-container"
      children = Seq(
        fieldLabel(pageTitle, "page-title"),
        createCards(),
        createDocumentsTable()
      )

  private def createCards(): HBox =
    new HBox:
      styleClass += "cards-container"
      children = Seq(
        statCard("Totale Documenti", "0", "Nessun documento"),
        statCard("In Carico", "0", "Nessun documento"),
        statCard("Registrati", "0", "Nessun documento"),
        statCard("Archiviati", "0", "Nessun documento")
      )

  private def statCard(title: String, value: String, subtitle: String): VBox =
    new VBox:
      prefWidth = 190
      styleClass += "stat-card"
      children = Seq(
        fieldLabel(title, "stat-card-title"),
        fieldLabel(value, "stat-card-value"),
        fieldLabel(subtitle, "stat-card-subtitle")
      )

  private def createDocumentsTable(): TableView[Unit] =
    new TableView[Unit]:
      styleClass += "documents-table"
      placeholder = fieldLabel("Nessun documento disponibile", "table-placeholder")
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