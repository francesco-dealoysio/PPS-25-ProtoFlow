package pkg.a.gui

import pkg.b.logic.LoginService.LoggedUser
import pkg.c.data.generalStructures.Role
import pkg.c.data.guiStructures.{
  HomePageConfig,
  HomePageViewModel,
  RegistrationViewModel
}

import scalafx.application.JFXApp3
import scalafx.scene.Scene

class AppNavigator(stage: JFXApp3.PrimaryStage):

  /**
   * Mostra la schermata di login.
   */
  def showLogin(): Unit =
    stage.title = "ProtoFlow - Login"
    stage.width = 460
    stage.height = 560
    stage.resizable = false

    val scene = new Scene(460, 560):
      root = LoginView(
        onLoginSuccess = user =>
          showHome(user),

        onRegistrationRequest = () =>
          showRegistration()
      )

    addPageStylesheets(
      scene,
      "/login.css"
    )

    stage.scene = scene

  /*
   * Mostra la schermata autonoma per la richiesta
   * di registrazione.
   */
  private def showRegistration(): Unit =
    stage.title = "ProtoFlow - Registrazione"
    stage.width = 900
    stage.height = 650
    stage.resizable = true

    val scene = new Scene(900, 650):
      root = RegistrationView(
        viewModel = RegistrationViewModel(),

        onExit = () =>
          showLogin()
      )

    addPageStylesheets(
      scene,
      "/registration.css"
    )

    stage.scene = scene

  /**
   * Mostra la homepage dell'utente autenticato.
   *
   * Le viste di gestione richieste e classifiche
   * vengono inserite successivamente nel contentArea
   * della stessa HomePageView.
   */
  private def showHome(user: LoggedUser): Unit =
    val role =
      roleFrom(user.role)

    val config =
      HomePageConfig.forRole(role)

    val viewModel =
      HomePageViewModel(config)

    stage.title = "ProtoFlow"
    stage.width = 1100
    stage.height = 700
    stage.resizable = true

    val scene = new Scene(1100, 700):
      root = HomePageView(
        config = config,
        viewModel = viewModel,
        currentUser = user.fullName,
        onLogout = () =>
          showLogin()
      )

    /*
     * Questi CSS appartengono tutti alla stessa Scene.
     *
     * RegistrationRequestsManagementView e
     * ClassificationManagementView vengono mostrate
     * dentro il contentArea della homepage.
     */
    addPageStylesheets(
      scene,
      "/homepages.css",
      "/registration-requests-management.css",
      "/classifications-management.css",
      "/accounts-management.css"
    )

    stage.scene = scene

  /**
   * Schermata autonoma già esistente per l'aggiunta di un ruolo.
   *
   * Può restare nel navigator perché sostituisce
   * completamente la scena corrente.
   */
  def showRoleAddView(): Unit =
    stage.title = "Aggiunta Ruolo"
    stage.width = 800
    stage.height = 600
    stage.resizable = true

    val scene = new Scene(800, 600):
      root = RoleAddView()

    addPageStylesheets(
      scene,
      "/homepages.css"
    )

    stage.scene = scene

  /**
   * Converte il ruolo ricevuto dal login nel corrispondente enum applicativo.
   */
  private def roleFrom(role: String): Role =
    role.toLowerCase match
      case "admin" =>
        Role.Admin

      case "oper" =>
        Role.Operator

      case "viewer" =>
        Role.Viewer

      case other =>
        throw IllegalArgumentException(
          s"Ruolo non riconosciuto: $other"
        )

  /**
   * Carica sempre common.css per primo, seguito dai CSS specifici della scena.
   */
  private def addPageStylesheets(
                                  scene: Scene,
                                  pageStylesheets: String*
                                ): Unit =
    addStylesheet(
      scene,
      "/common.css"
    )

    pageStylesheets.foreach: stylesheet =>
      addStylesheet(
        scene,
        stylesheet
      )

  /**
   * Aggiunge un foglio di stile solo se la risorsa è stata trovata.
   */
  private def addStylesheet(
                             scene: Scene,
                             path: String
                           ): Unit =
    Option(getClass.getResource(path))
      .foreach: css =>
        scene.stylesheets.add(
          css.toExternalForm
        )