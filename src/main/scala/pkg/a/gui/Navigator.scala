package pkg.a.gui

import pkg.a.gui.structures.{HomePageViewModel, RegistrationViewModel}
import pkg.a.gui.traits.HomePage
import pkg.a.gui.views.{LoginView, RegistrationView, RoleAddView}
import pkg.b.logic.LoginService.LoggedUser
import scalafx.application.JFXApp3
import scalafx.scene.Scene

class Navigator(stage: JFXApp3.PrimaryStage):

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

    addPageStylesheets(scene, "/login.css")

    stage.scene = scene


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
    addPageStylesheets(scene, "/registration.css")

    stage.scene = scene

  private def showHome(user: LoggedUser): Unit =
    val homePage = HomePage.forRole(user.role)
    val viewModel = new HomePageViewModel

    stage.title = "ProtoFlow"
    stage.width = 1100
    stage.height = 700
    stage.resizable = true

    val scene = new Scene(1100, 700):
      root = homePage(
        viewModel = viewModel,
        currentUser = user.fullName,
        onLogout = () =>
          showLogin()
      )

    addPageStylesheets(
      scene,
      "/homepages.css",
      "/registration-requests-management.css",
      "/classifications-management.css",
      "/accounts-management.css"
    )

    stage.scene = scene

  def showRoleAddView(): Unit =
    stage.title = "Aggiunta Ruolo"
    stage.width = 800
    stage.height = 600
    stage.resizable = true

    val scene = new Scene(800, 600):
      root = RoleAddView()

    addPageStylesheets(scene, "/homepages.css")

    stage.scene = scene

  private def addPageStylesheets(scene: Scene, pageStylesheets: String*): Unit =
    addStylesheet(scene, "/common.css")
    pageStylesheets.foreach: stylesheet =>
      addStylesheet(scene, stylesheet)

  private def addStylesheet(scene: Scene, path: String): Unit =
    Option(getClass.getResource(path))
      .foreach: css =>
        scene.stylesheets.add(css.toExternalForm)