package pkg.a.gui

import pkg.c.data.generalStructures.Role
import pkg.c.data.guiStructures.{HomePageConfig, HomePageViewModel, MenuAction}
import scalafx.geometry.Pos
import scalafx.scene.control.*
import scalafx.scene.layout.*

object HomePageView:

  def apply(
             config: HomePageConfig,
             viewModel: HomePageViewModel,
             currentUser: String,
             onLogout: () => Unit = () => (),
             onRegistrationRequests: () => Unit = () => ()
           ): BorderPane =

    val contentArea = new StackPane:
      styleClass += "content-area"
      children = Seq(dashboardContent())

    val sidebar = createSidebar(config, viewModel, contentArea, onLogout, onRegistrationRequests)

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
        new Label("☰"):
          styleClass += "app-logo",
        new Label(config.applicationTitle):
          styleClass += "app-title",
        new Region:
          HBox.setHgrow(this, Priority.Always),
        new Label(s"$currentUser\n${config.roleDescription}"):
          styleClass += "user-info"
      )

  private def roleDescription(role: Role): String =
    role match
      case Role.Viewer => "Viewer"
      case Role.Operator => "Operatore Protocollo"
      case Role.Admin => "Amministratore"

  private def createSidebar(config: HomePageConfig, viewModel: HomePageViewModel, contentArea: StackPane, onLogout: () => Unit, onRegistrationRequests: () => Unit): VBox =

    val buttons = config.menuItems.map: item =>
      new Button(item.label):
        maxWidth = Double.MaxValue
        alignment = Pos.CenterLeft
        styleClass += "sidebar-button"

        onAction = _ =>
          viewModel.select(item.action)

          item.action match
            case MenuAction.Registrazioni =>
              contentArea.children = Seq(RegistrationRequestsManagementView(
                onExit = () =>
                  contentArea.children = Seq(dashboardContent())
                )
              )

            case MenuAction.Logout =>
              onLogout()

            case _ =>
              contentArea.children = Seq(contentFor(item.action))

    new VBox:
      prefWidth = 230
      styleClass += "sidebar"
      children = buttons

  private def createFooter(currentUser: String, config: HomePageConfig): HBox =
    new HBox:
      alignment = Pos.CenterRight
      styleClass += "app-footer"
      children = Seq(
        new Label(s"👤 $currentUser (${config.roleDescription})    Data e ora dinamici"):
          styleClass += "app-footer"
      )

  private def contentFor(action: MenuAction): Pane =
    action match
      case MenuAction.Dashboard => dashboardContent()
      case MenuAction.Profilo => placeholder("Profilo")
      case MenuAction.VisualizzazioneProtocollazioni => placeholder("Visualizzazione Protocollazioni")
      case MenuAction.PreseInCarico => placeholder("Prese in carico")
      case MenuAction.Protocollo => placeholder("Protocollazione")
      case MenuAction.Archiviazione => placeholder("Archiviazione")
      case MenuAction.Statistiche => placeholder("Statistiche")
      case MenuAction.Log => placeholder("Log")
      case MenuAction.ControlloGestione => placeholder("Controllo Gestione")
      case MenuAction.Registrazioni => placeholder("Registrazioni")
      case MenuAction.AccountUtenti => placeholder("Account Utenti")
      case MenuAction.Ruoli => placeholder("Ruoli")
      case MenuAction.Classifiche => placeholder("Classifiche")
      case MenuAction.Logout => placeholder("Logout")

  private def placeholder(title: String): VBox =
    new VBox:
      styleClass += "placeholder-container"
      children = Seq(
        new Label(title):
          styleClass += "placeholder-title"
      )

  private def dashboardContent(): VBox = {
    val title = new Label("Dashboard"):
      styleClass += "page-title"

    new VBox:
      styleClass += "dashboard-container"
      children = Seq(
        title,
        createCards(),
        createDocumentsTable()
      )
  }

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
        new Label(title):
          styleClass += "stat-card-title",
        new Label(value):
          styleClass += "stat-card-value",
        new Label(subtitle):
          styleClass += "stat-card-subtitle"
      )

  private def createDocumentsTable(): TableView[DocumentRow] =
    val table = new TableView[DocumentRow]()
    table.styleClass += "documents-table"

    val protocollo = new TableColumn[DocumentRow, String]("Protocollo")
    protocollo.cellValueFactory = _.value.protocolloProperty

    val oggetto = new TableColumn[DocumentRow, String]("Oggetto")
    oggetto.cellValueFactory = _.value.oggettoProperty

    val mittente = new TableColumn[DocumentRow, String]("Mittente")
    mittente.cellValueFactory = _.value.mittenteProperty

    val categoria = new TableColumn[DocumentRow, String]("Categoria")
    categoria.cellValueFactory = _.value.categoriaProperty

    val stato = new TableColumn[DocumentRow, String]("Stato")
    stato.cellValueFactory = _.value.statoProperty

    val data = new TableColumn[DocumentRow, String]("Data")
    data.cellValueFactory = _.value.dataProperty

    table.columns ++= Seq(protocollo, oggetto, mittente, categoria, stato, data)

    table.items = scalafx.collections.ObservableBuffer(
      DocumentRow("2026/000123", "Richiesta occupazione suolo pubblico", "Mario Rossi", "Urbanistica", "Registrato", "15/06/2026"),
      DocumentRow("2026/000122", "Richiesta ferie personale", "Anna Bianchi", "Personale", "In Carico", "15/06/2026"),
      DocumentRow("2026/000121", "Preventivo fornitura materiali", "Edilizia Verdi S.r.l.", "Amministrazione", "Registrato", "14/06/2026"),
      DocumentRow("2026/000120", "Circolare interna n. 45", "Segreteria Generale", "Segreteria", "Archiviato", "14/06/2026"),
      DocumentRow("2026/000119", "Fattura n. 123/PA", "Studio Alfa", "Finanziario", "Registrato", "13/06/2026")
    )

    table