package pkg.a.gui.traits

import pkg.a.gui.structures.{HomePageViewModel, MenuAction, MenuItem}
import pkg.a.gui.views.*
import pkg.b.logic.{Account, Classification, Registration, Role}
import pkg.d.util.DateTime
import scalafx.Includes.jfxNode2sfx
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout.*

trait HomePage extends Root:

  protected def pageTitle: String
  protected def roleDescription: String
  protected def menuItems: Seq[MenuItem]

  final def title: String = pageTitle
  final def role: String = roleDescription
  final def items: Seq[MenuItem] = menuItems

  private val applicationTitle: String = "ProtoFlow"

  final def apply(
                   viewModel: HomePageViewModel,
                   currentUser: String,
                   currentUsername: String,
                   onLogout: () => Unit = () => ()
                 ): BorderPane =

    val menuVisible = BooleanProperty(true)

    def toggleMenu(): Unit =
      menuVisible.value = !menuVisible.value

    val contentArea =
      new StackPane:
        styleClass += "content-area"
        children = Seq(dashboardContent())

    val sidebar =
      createSidebar(
        viewModel = viewModel,
        currentUsername = currentUsername,
        contentArea = contentArea,
        onLogout = onLogout
      )

    sidebar.visible <== menuVisible
    sidebar.managed <== menuVisible

    new BorderPane:
      top =
        createHeader(
          currentUser = currentUser,
          onMenuToggle = () => toggleMenu()
        )

      left = sidebar
      center = contentArea
      bottom = createFooter(currentUser)

  private def createHeader(currentUser: String, onMenuToggle: () => Unit): HBox =

    val spacer = new Region
    HBox.setHgrow(spacer, Priority.Always)

    val menuButton =
      new Button("☰"):
        styleClass += "menu-toggle-button"
        onAction = _ => onMenuToggle()

    new HBox:
      alignment = Pos.CenterLeft
      styleClass += "app-header"
      children = Seq(
        menuButton,
        fieldLabel(applicationTitle, "app-title"),
        spacer,
        fieldLabel(
          s"$currentUser\n$roleDescription",
          "user-info"
        )
      )

  private def createSidebar(
                             viewModel: HomePageViewModel,
                             currentUsername: String,
                             contentArea: StackPane,
                             onLogout: () => Unit
                           ): VBox =

    def render(view: => Pane): Unit =
      val canLeave =
        contentArea.children.headOption
          .flatMap: node =>
            Option(
              node.delegate
                .getProperties
                .get("has-unsaved-changes")
            )
          .map:
            _.asInstanceOf[() => Boolean]
          .forall: check =>
            !check() ||
              askConfirmation(
                titleText = "Modifiche non salvate",
                header = "Vuoi uscire senza salvare?",
                content =
                  "Le informazioni inserite o modificate non verranno mantenute."
              )

      if canLeave then
        contentArea.children = Seq(view)

    def showDashboard(): Unit =
      viewModel.select(MenuAction.Dashboard)
      render(dashboardContent())

    def showClassificationEdit(selected: Classification): Unit =
      render(
        ClassificationEditView(
          selectedClassification = selected,
          onSaved = () => showClassificationManagement(),
          onExit = () => showClassificationManagement()
        )
      )

    def showClassificationAdd(): Unit =
      render(
        ClassificationAddView(
          onSaved = () => showClassificationManagement(),
          onExit = () => showClassificationManagement()
        )
      )

    def showClassificationManagement(): Unit =
      render(
        ClassificationManagementView(
          onAdd = () => showClassificationAdd(),
          onEdit = selected => showClassificationEdit(selected),
          onExit = () => showDashboard()
        )
      )

    def showRegistrationRequests(): Unit =
      render(
        RegistrationRequestsManagementView(
          onProcess = selected => showRegistrationRequestProcess(selected),
          onExit = () => showDashboard()
        )
      )

    def showRegistrationRequestProcess(selected: Registration): Unit =
      render(
        RegistrationRequestProcessView(
          request = selected,
          operatorUsername = currentUsername,
          onProcessed = () => showRegistrationRequests(),
          onExit = () => showRegistrationRequests()
        )
      )

    def showAccountManagement(): Unit =
      render(
        AccountManagementView(
          onAdd = () => showAccountAdd(),
          onEdit = selected => showAccountEdit(selected),
          onExit = () => showDashboard()
        )
      )

    def showAccountAdd(): Unit =
      render(
        AccountAddView(
          onSaved = () => showAccountManagement(),
          onExit = () => showAccountManagement()
        )
      )

    def showAccountEdit(selected: Account): Unit =
      render(
        AccountEditView(
          selectedAccount = selected,
          onSaved = () => showAccountManagement(),
          onExit = () => showAccountManagement()
        )
      )

    def showRoleManagement(): Unit =
      render(
        RoleManagementView(
          onAdd = () => showRoleAdd(),
          onEdit = selected => showRoleEdit(selected),
          onExit = () => showDashboard()
        )
      )

    def showRoleAdd(): Unit =
      render(
        RoleAddView(
          onSaved = () => showRoleManagement(),
          onExit = () => showRoleManagement()
        )
      )

    def showRoleEdit(selected: Role): Unit =
      render(
        RoleEditView(
          selectedRole = selected,
          onSaved = () => showRoleManagement(),
          onExit = () => showRoleManagement()
        )
      )

    def showLoadedDocumentAdd(): Unit =
      render(
        LoadedDocumentAddView(
          operatorUsername = currentUsername,
          onSaved = () => showDashboard(),
          onExit = () => showDashboard()
        )
      )

    val buttons =
      menuItems.map: item =>
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
                  case MenuAction.Dashboard =>
                    showDashboard()

                  case MenuAction.Registrazioni =>
                    showRegistrationRequests()

                  case MenuAction.Classifiche =>
                    showClassificationManagement()

                  case MenuAction.AccountUtenti =>
                    showAccountManagement()

                  case MenuAction.Ruoli =>
                    showRoleManagement()

                  case MenuAction.PreseInCarico =>
                    showLoadedDocumentAdd()

                  case other =>
                    render(contentFor(other))

    new VBox:
      prefWidth = 230
      styleClass += "sidebar"
      children = buttons

  private def createFooter(currentUser: String): HBox =
    val dateTimeLabel = fieldLabel("", "footer-date-time")

    dateTimeLabel.text <==
      DateTime.dynamicDateTimeProperty()

    new HBox:
      alignment = Pos.CenterRight
      spacing = 20
      styleClass += "app-footer"
      children = Seq(
        fieldLabel(s"👤 $currentUser ($roleDescription)", "footer-user-info"),
        dateTimeLabel
      )

  private def contentFor(action: MenuAction): Pane =
    action match
      case MenuAction.Dashboard =>
        dashboardContent()

      case other =>
        createPlaceholder(
          placeholderTitles.getOrElse(other, other.toString)
        )

  private val placeholderTitles: Map[MenuAction, String] =
    Map(
      MenuAction.Profilo ->
        "Profilo",

      MenuAction.VisualizzazioneProtocollazioni ->
        "Visualizzazione Protocollazioni",

      MenuAction.PreseInCarico ->
        "Prese in carico",

      MenuAction.Protocollo ->
        "Protocollazione",

      MenuAction.Archiviazione ->
        "Archiviazione",

      MenuAction.Statistiche ->
        "Statistiche",

      MenuAction.Log ->
        "Log",

      MenuAction.ControlloGestione ->
        "Controllo Gestione",

      MenuAction.AccountUtenti ->
        "Account Utenti",

      MenuAction.Ruoli ->
        "Ruoli"
    )

  private def createPlaceholder(title: String): VBox =
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