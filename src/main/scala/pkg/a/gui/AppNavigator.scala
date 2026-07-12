package pkg.a.gui

import pkg.a.gui.{HomePageView, LoginView, RegistrationView, RoleAddView}
import pkg.c.data.generalStructures.Role
import pkg.c.data.guiStructures.{HomePageConfig, HomePageViewModel, RegistrationViewModel}
import scalafx.application.JFXApp3
import pkg.b.logic.LoginService.LoggedUser
import scalafx.scene.Scene

class AppNavigator(stage: JFXApp3.PrimaryStage):

  def showLogin(): Unit =
    stage.title = "ProtoFlow - Login"
    stage.width = 460
    stage.height = 560
    stage.resizable = false

    val scene = new Scene(460, 560):
      root = LoginView(
        onLoginSuccess = account => showHome(account),
        onRegistrationRequest = () => showRegistration()
      )

    addStylesheet(scene, "/login.css")
    stage.scene = scene

  def showRegistration(): Unit =
    stage.title = "ProtoFlow - Registrazione"
    stage.width = 900
    stage.height = 650
    stage.resizable = true

    val scene = new Scene(900, 650):
      root = RegistrationView(
        viewModel = RegistrationViewModel(),
        onExit = () => showLogin()
      )

    addStylesheet(scene, "/registration.css")
    stage.scene = scene

  private def showHome(user: LoggedUser): Unit =
    val role = roleFrom(user.role)
    val config = HomePageConfig.forRole(role)
    val viewModel = HomePageViewModel(config)

    val currentUser = user.fullName

    stage.title = "ProtoFlow"
    stage.width = 1100
    stage.height = 700
    stage.resizable = true

    val scene = new Scene(1100, 700):
      root = HomePageView(
        config = config,
        viewModel = viewModel,
        currentUser = currentUser,
        onLogout = () => showLogin()
      )

    addStylesheet(scene, "/homepages.css")
    stage.scene = scene

  def showRoleAddView(): Unit =
    
    stage.title = "Aggiunta Ruolo"
    stage.width = 1100
    stage.height = 700
    stage.resizable = true

    val scene = new Scene(800, 600):
      root = RoleAddView()

    addStylesheet(scene, "/homepages.css")
    stage.scene = scene


  private def roleFrom(role: String): Role =
    role.toLowerCase match
      case "admin"  => Role.Admin
      case "oper"   => Role.Operator
      case "viewer" => Role.Viewer
      case other    => throw IllegalArgumentException(s"Ruolo non riconosciuto: $other")

  private def addStylesheet(scene: Scene, path: String): Unit =
    Option(getClass.getResource(path))
      .foreach(css => scene.stylesheets.add(css.toExternalForm))