package pkg.a.gui

import pkg.b.logic.Classification
import pkg.c.data.guiStructures.{HomePageConfig, HomePageViewModel, MenuAction}
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label, TableColumn, TableView}
import scalafx.scene.layout.{BorderPane, HBox, Pane, Priority, Region, StackPane, VBox}

object HomePageView extends AppView:

  def apply(
             config: HomePageConfig,
             viewModel: HomePageViewModel,
             currentUser: String,
             onLogout: () => Unit = () => ()
           ): BorderPane =
    
    val contentArea = new StackPane:
      styleClass += "content-area"
      children = Seq(dashboardContent())

    val sidebar = createSidebar(config, viewModel, contentArea, onLogout)

    new BorderPane:
      left = sidebar
      top = createHeader(config, currentUser)
      center = contentArea
      bottom = createFooter(currentUser, config)
  
  private def createHeader(config: HomePageConfig, currentUser: String): HBox =
    new HBox:
      alignment = Pos.CenterLeft
      styleClass += "app-header"
      children = Seq(
        fieldLabel("☰", "app-logo"),
        fieldLabel(config.applicationTitle, "app-title"),
        new Region:
          HBox.setHgrow(this, Priority.Always),
          fieldLabel(s"$currentUser\n${config.roleDescription}", "user-info")
      )

  private def createSidebar(config: HomePageConfig, viewModel: HomePageViewModel, contentArea: StackPane, onLogout: () => Unit): VBox =
    
    def render(view: Pane): Unit =
      contentArea.children = Seq(view)

    def showDashboard(): Unit =
      viewModel.select(MenuAction.Dashboard)
      render(dashboardContent())

    def showClassificationEdit(selected: Classification): Unit =
      render(ClassificationEditView(selected, onSaved = () => showClassificationManagement(), onExit = () => showClassificationManagement()))

    def showClassificationAdd(): Unit =
      render(ClassificationAddView(onSaved = () => showClassificationManagement(), onExit = () => showClassificationManagement()))

    def showClassificationManagement(): Unit =
      render(ClassificationManagementView(onAdd = () => showClassificationAdd(), onEdit = selected => showClassificationEdit(selected), onExit = () => showDashboard()))

    def showRegistrationRequests(): Unit =
      render(RegistrationRequestsManagementView(onExit = () => showDashboard()))

    val buttons =
      config.menuItems.map: item =>

        new Button(item.label):
          maxWidth = Double.MaxValue
          alignment = Pos.CenterLeft
          styleClass += "sidebar-button"

          onAction = _ =>
            item.action match
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
                action match
                  case MenuAction.Dashboard => showDashboard()
                  case MenuAction.Registrazioni => showRegistrationRequests()
                  case MenuAction.Classifiche => showClassificationManagement()
                  case other => render(contentFor(other))
                
    new VBox:
      prefWidth = 230
      styleClass += "sidebar"
      children = buttons
  
  private def createFooter(currentUser: String, config: HomePageConfig): HBox =
    new HBox:
      alignment = Pos.CenterRight
      styleClass += "app-footer"
      children = Seq(
        fieldLabel(s"👤 $currentUser (${config.roleDescription})    Data e ora dinamici", "footer-user-info")
      )
  
  private def contentFor(action: MenuAction): Pane =
    action match
      case MenuAction.Dashboard =>
        dashboardContent()

      case other =>
        placeholder(placeholderTitles.getOrElse(other, other.toString))


  private val placeholderTitles: Map[MenuAction, String] =
    Map(
      MenuAction.Profilo -> "Profilo",
      MenuAction.VisualizzazioneProtocollazioni -> "Visualizzazione Protocollazioni",
      MenuAction.PreseInCarico -> "Prese in carico",
      MenuAction.Protocollo -> "Protocollazione",
      MenuAction.Archiviazione -> "Archiviazione",
      MenuAction.Statistiche -> "Statistiche",
      MenuAction.Log -> "Log",
      MenuAction.ControlloGestione -> "Controllo Gestione",
      MenuAction.Registrazioni -> "Registrazioni",
      MenuAction.AccountUtenti -> "Account Utenti",
      MenuAction.Ruoli -> "Ruoli",
      MenuAction.Classifiche -> "Classifiche",
      MenuAction.Logout -> "Logout"
    )
  
  private def placeholder(title: String): VBox =
    new VBox:
      styleClass += "placeholder-container"
      children = Seq(fieldLabel(title,"placeholder-title"))


  private def dashboardContent(): VBox =
    new VBox:
      styleClass += "dashboard-container"
      children = Seq(
        fieldLabel("Dashboard","page-title"),
        createCards(),
        createDocumentsTable()
      )
  
  private def createCards(): HBox =
    new HBox:
      styleClass += "cards-container"
      children = Seq(
        statCard("Totale Documenti", "1.248", "+12% da ieri"),
        statCard("In Carico", "32", "+5 da ieri"),
        statCard("Registrati", "980", "+20 da ieri"),
        statCard("Archiviati", "236", "+8 da ieri")
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
    val table = new TableView[Unit]()
  
    table.styleClass += "documents-table"
    table.placeholder = fieldLabel("Nessun documento disponibile", "table-placeholder")
    table.columns ++=
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
    table