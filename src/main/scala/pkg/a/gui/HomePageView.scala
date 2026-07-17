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
  private def createHeader(config: HomePageConfig, currentUser: String): HBox =
    new HBox:
      alignment = Pos.CenterLeft
      styleClass += "app-header"

      children = Seq(
        fieldLabel(text = "☰", styleName = "app-logo"),
        fieldLabel(text = config.applicationTitle, styleName = "app-title"),

        new Region:
          HBox.setHgrow(this, Priority.Always),
          fieldLabel(text = s"$currentUser\n${config.roleDescription}", styleName = "user-info")
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
            showClassificationAdd(),

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

    def showClassificationAdd(): Unit =
      contentArea.children = Seq(
        ClassificationAddView(
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

              case MenuAction.Dashboard =>
                showDashboard()

              case MenuAction.Registrazioni =>
                viewModel.select(MenuAction.Registrazioni)
                showRegistrationRequests()

              case MenuAction.Classifiche =>
                viewModel.select(MenuAction.Classifiche)
                showClassificationManagement()

              case other =>
                viewModel.select(other)

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
  private def createFooter(currentUser: String, config: HomePageConfig): HBox =
    new HBox:
      alignment = Pos.CenterRight
      styleClass += "app-footer"

      children = Seq(
        fieldLabel(
          text = s"👤 $currentUser (${config.roleDescription})    Data e ora dinamici",
          styleName = "footer-user-info"
        )
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
        fieldLabel(text = title, styleName = "placeholder-title")
      )

  /**
   * Dashboard iniziale.
   */
  private def dashboardContent(): VBox =
    new VBox:
      styleClass += "dashboard-container"

      children = Seq(
        fieldLabel(text = "Dashboard", styleName = "page-title"),
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
  private def statCard(title: String, value: String, subtitle: String): VBox =
    new VBox:
      prefWidth = 190
      styleClass += "stat-card"
      children = Seq(
        fieldLabel(text = title, styleName = "stat-card-title"),
        fieldLabel(text = value, styleName = "stat-card-value"),
        fieldLabel(text = subtitle, styleName = "stat-card-subtitle")
      )


  /**
   * Tabella dei documenti recenti.
   */
  private def createDocumentsTable(): TableView[Unit] =
    new TableView[Unit]:
      styleClass += "documents-table"
      this.placeholder = fieldLabel("Nessun documento disponibile", "table-placeholder")

      // Generiamo la sequenza di colonne ScalaFX
      val colList = Seq(
        "Protocollo",
        "Oggetto",
        "Mittente",
        "Categoria",
        "Stato",
        "Data"
      ).map: title =>
        new TableColumn[Unit, String]:
          text = title

      // Per evitare il problema dei tipi, estraiamo i delegati JavaFX nativi (.map(_.delegate))
      columns ++= colList.map(_.delegate)