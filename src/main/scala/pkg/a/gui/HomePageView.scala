package pkg.a.gui


import pkg.c.data.{HomePageConfig, HomePageViewModel, MenuAction, Role}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object HomePageView:

  def apply(
             config: HomePageConfig,
             viewModel: HomePageViewModel,
             currentUser: String
           ): BorderPane =

    val contentArea = new StackPane:
      padding = Insets(20)
      children = Seq(dashboardContent())

    val sidebar = createSidebar(config, viewModel, contentArea)

    new BorderPane:
      left = sidebar
      top = createHeader(config, currentUser)
      center = contentArea
      bottom = createFooter(currentUser, config.role)

  private def createHeader(config: HomePageConfig, currentUser: String): HBox =
    new HBox:
      padding = Insets(15)
      alignment = Pos.CenterLeft
      spacing = 20
      style = "-fx-background-color: #ffffff; -fx-border-color: #e5e7eb;"

      children = Seq(
        new Label("☰"):
          font = Font(22),
        new Label("ProtoFlow"):
          font = Font.font(20)
          style = "-fx-font-weight: bold;",
        new Region:
          HBox.setHgrow(this, Priority.Always),
        new Label(s"$currentUser\nOperatore Protocollo"):
          style = "-fx-text-fill: #1f2937;"
      )

  private def createSidebar(config: HomePageConfig, viewModel: HomePageViewModel, contentArea: StackPane): VBox =

    val buttons = config.menuItems.map: item =>
      new Button(item.label):
        maxWidth = Double.MaxValue
        alignment = Pos.CenterLeft
        style =
          "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12;"
        onAction = _ =>
          viewModel.select(item.action)
          contentArea.children = Seq(contentFor(item.action))

    new VBox:
      prefWidth = 230
      padding = Insets(20, 10, 20, 10)
      spacing = 8
      style = "-fx-background-color: linear-gradient(to bottom, #08213f, #031526);"
      children = buttons

  private def createFooter(currentUser: String, role: Role): HBox =
    new HBox:
      padding = Insets(10)
      alignment = Pos.CenterRight
      style = "-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb;"
      children = Seq(
        new Label(s"👤 $currentUser ($role)    Data e ora dinamici")
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
      padding = Insets(30)
      children = Seq(
        new Label(title):
          font = Font.font(26)
      )

  private def dashboardContent(): VBox =
    new VBox:
      spacing = 20
      padding = Insets(20)
      children = Seq(
        new Label("Dashboard"):
          font = Font.font(28)
          style = "-fx-font-weight: bold;",
          createCards(),
        createDocumentsTable()
      )

  private def createCards(): HBox =
    new HBox:
      spacing = 20
      children = Seq(
        statCard("Totale Documenti", "1.248", "+12% da ieri"),
        statCard("In Carico", "32", "+5 da ieri"),
        statCard("Registrati", "980", "+20 da ieri"),
        statCard("Archiviati", "236", "+8 da ieri")
      )

  private def statCard(title: String, value: String, subtitle: String): VBox =
    new VBox:
      prefWidth = 190
      padding = Insets(15)
      spacing = 8
      style = "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-background-radius: 8; -fx-border-radius: 8;"
      children = Seq(
        new Label(title):
          style = "-fx-text-fill: #6b7280;",
        new Label(value):
          font = Font.font(26)
          style = "-fx-font-weight: bold;",
        new Label(subtitle):
          style = "-fx-text-fill: #22c55e;"
      )

  private def createDocumentsTable(): TableView[DocumentRow] =
    val table = new TableView[DocumentRow]()

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
