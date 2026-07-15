package pkg.a.gui

import pkg.b.logic.Classification
import pkg.c.data.guiStructures.{HomePageConfig, HomePageViewModel, MenuAction}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label, TableColumn, TableView}
import scalafx.scene.layout.{BorderPane, HBox, Pane, Priority, Region, StackPane, VBox}

object HomePageView:

  def apply(
             config: HomePageConfig,
             viewModel: HomePageViewModel,
             currentUser: String,
             onLogout: () => Unit = () => ()
           ): BorderPane =

    /*
     * Area centrale della homepage.
     *
     * Inizialmente contiene la dashboard.
     * Successivamente può contenere le altre
     * sezioni interne.
     */
    val contentArea = new StackPane:
      styleClass += "content-area"
      children = Seq(
        dashboardContent()
      )

    val sidebar =
      createSidebar(
        config = config,
        viewModel = viewModel,
        contentArea = contentArea,
        onLogout = onLogout
      )

    new BorderPane:
      left = sidebar

      top =
        createHeader(
          config = config,
          currentUser = currentUser
        )

      center = contentArea

      bottom =
        createFooter(
          currentUser = currentUser,
          config = config
        )

  /**
   * Crea l'intestazione della homepage.
   */
  private def createHeader(
                            config: HomePageConfig,
                            currentUser: String
                          ): HBox =
    new HBox:
      alignment = Pos.CenterLeft
      styleClass += "app-header"

      children = Seq(
        new Label("☰"):
          styleClass += "app-logo",

        new Label(config.applicationTitle):
          styleClass += "app-title",

        new Region:
          HBox.setHgrow(
            this,
            Priority.Always
          ),
          new Label(
            s"$currentUser\n${config.roleDescription}"
          ):
            styleClass += "user-info"
          )

  /**
   * Crea la sidebar e gestisce l'apertura
   * delle sezioni interne della homepage.
   */
  private def createSidebar(
                             config: HomePageConfig,
                             viewModel: HomePageViewModel,
                             contentArea: StackPane,
                             onLogout: () => Unit
                           ): VBox =

    /**
     * Ripristina la dashboard nell'area centrale.
     */
    def showDashboard(): Unit =
      viewModel.select(
        MenuAction.Dashboard
      )

      contentArea.children = Seq(
        dashboardContent()
      )


    def showClassificationManagement(): Unit =
      contentArea.children = Seq(
        ClassificationManagementView(
          onAdd = () =>
            println("Apertura Aggiunta Classifica"),

          onEdit = selected =>
            showClassificationEdit(selected),

          onExit = () =>
            showDashboard()
        )
      )

    def showClassificationEdit(selected: Classification): Unit =
      contentArea.children = Seq(
        ClassificationEditView(
          selectedClassification = selected,

          onSaved = () =>
            showClassificationManagement(),

          onExit = () =>
            showClassificationManagement()
        )
      )

    /**
     * Mostra la gestione delle richieste di registrazione
     * mantenendo visibili header, sidebar e footer.
     */
    def showRegistrationRequests(): Unit =
      contentArea.children = Seq(
        RegistrationRequestsManagementView(
          onExit = () =>
            showDashboard()
        )
      )


    val buttons =
      config.menuItems.map: item =>

        new Button(item.label):
          maxWidth = Double.MaxValue
          alignment = Pos.CenterLeft
          styleClass += "sidebar-button"

          onAction = _ =>
            viewModel.select(
              item.action
            )

            item.action match
              case MenuAction.Dashboard =>
                showDashboard()

              case MenuAction.Registrazioni =>
                showRegistrationRequests()

              case MenuAction.Classifiche =>
                showClassificationManagement()

              case MenuAction.Logout =>
                onLogout()

              case other =>
                contentArea.children = Seq(
                  contentFor(other)
                )

    new VBox:
      prefWidth = 230
      styleClass += "sidebar"
      children = buttons

  /**
   * Crea il piè di pagina.
   */
  private def createFooter(
                            currentUser: String,
                            config: HomePageConfig
                          ): HBox =
    new HBox:
      alignment = Pos.CenterRight
      styleClass += "app-footer"

      children = Seq(
        new Label(
          s"👤 $currentUser (${config.roleDescription})    Data e ora dinamici"
        ):
          styleClass += "footer-user-info"
      )

  /**
   * Contenuti ancora non implementati.
   */
  private def contentFor(
                          action: MenuAction
                        ): Pane =
    action match
      case MenuAction.Dashboard =>
        dashboardContent()

      case MenuAction.Profilo =>
        placeholder("Profilo")

      case MenuAction.VisualizzazioneProtocollazioni =>
        placeholder(
          "Visualizzazione Protocollazioni"
        )

      case MenuAction.PreseInCarico =>
        placeholder("Prese in carico")

      case MenuAction.Protocollo =>
        placeholder("Protocollazione")

      case MenuAction.Archiviazione =>
        placeholder("Archiviazione")

      case MenuAction.Statistiche =>
        placeholder("Statistiche")

      case MenuAction.Log =>
        placeholder("Log")

      case MenuAction.ControlloGestione =>
        placeholder("Controllo Gestione")

      case MenuAction.Registrazioni =>
        placeholder("Registrazioni")

      case MenuAction.AccountUtenti =>
        placeholder("Account Utenti")

      case MenuAction.Ruoli =>
        placeholder("Ruoli")

      case MenuAction.Classifiche =>
        placeholder("Classifiche")

      case MenuAction.Logout =>
        placeholder("Logout")

  /**
   * Contenuto temporaneo per le sezioni
   * non ancora implementate.
   */
  private def placeholder(
                           title: String
                         ): VBox =
    new VBox:
      styleClass += "placeholder-container"

      children = Seq(
        new Label(title):
          styleClass += "placeholder-title"
      )

  /**
   * Dashboard iniziale.
   */
  private def dashboardContent(): VBox =
    val title =
      new Label("Dashboard"):
        styleClass += "page-title"

    new VBox:
      styleClass += "dashboard-container"

      children = Seq(
        title,
        createCards(),
        createDocumentsTable()
      )

  /**
   * Indicatori sintetici della dashboard.
   */
  private def createCards(): HBox =
    new HBox:
      styleClass += "cards-container"

      children = Seq(
        statCard(
          "Totale Documenti",
          "1.248",
          "+12% da ieri"
        ),

        statCard(
          "In Carico",
          "32",
          "+5 da ieri"
        ),

        statCard(
          "Registrati",
          "980",
          "+20 da ieri"
        ),

        statCard(
          "Archiviati",
          "236",
          "+8 da ieri"
        )
      )

  /**
   * Singola card statistica.
   */
  private def statCard(
                        title: String,
                        value: String,
                        subtitle: String
                      ): VBox =
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

  /**
   * Tabella dei documenti recenti.
   */
  private def createDocumentsTable():
  TableView[DocumentRow] =

    val table =
      new TableView[DocumentRow]():

        styleClass +=
          "documents-table"

    val protocolColumn =
      new TableColumn[
        DocumentRow,
        String
      ]("Protocollo")

    protocolColumn.cellValueFactory =
      _.value.protocolloProperty

    val subjectColumn =
      new TableColumn[
        DocumentRow,
        String
      ]("Oggetto")

    subjectColumn.cellValueFactory =
      _.value.oggettoProperty

    val senderColumn =
      new TableColumn[
        DocumentRow,
        String
      ]("Mittente")

    senderColumn.cellValueFactory =
      _.value.mittenteProperty

    val categoryColumn =
      new TableColumn[
        DocumentRow,
        String
      ]("Categoria")

    categoryColumn.cellValueFactory =
      _.value.categoriaProperty

    val statusColumn =
      new TableColumn[
        DocumentRow,
        String
      ]("Stato")

    statusColumn.cellValueFactory =
      _.value.statoProperty

    val dateColumn =
      new TableColumn[
        DocumentRow,
        String
      ]("Data")

    dateColumn.cellValueFactory =
      _.value.dataProperty

    table.columns ++= Seq(
      protocolColumn,
      subjectColumn,
      senderColumn,
      categoryColumn,
      statusColumn,
      dateColumn
    )

    table.items = ObservableBuffer(
      DocumentRow(
        "2026/000123",
        "Richiesta occupazione suolo pubblico",
        "Mario Rossi",
        "Urbanistica",
        "Registrato",
        "15/06/2026"
      ),

      DocumentRow(
        "2026/000122",
        "Richiesta ferie personale",
        "Anna Bianchi",
        "Personale",
        "In Carico",
        "15/06/2026"
      ),

      DocumentRow(
        "2026/000121",
        "Preventivo fornitura materiali",
        "Edilizia Verdi S.r.l.",
        "Amministrazione",
        "Registrato",
        "14/06/2026"
      ),

      DocumentRow(
        "2026/000120",
        "Circolare interna n. 45",
        "Segreteria Generale",
        "Segreteria",
        "Archiviato",
        "14/06/2026"
      ),

      DocumentRow(
        "2026/000119",
        "Fattura n. 123/PA",
        "Studio Alfa",
        "Finanziario",
        "Registrato",
        "13/06/2026"
      )
    )

    table