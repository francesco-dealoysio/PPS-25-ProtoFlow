package pkg.a.gui

import pkg.a.gui.structures.{HomePageViewModel, RegistrationViewModel}
import pkg.a.gui.traits.HomePage
import pkg.a.gui.views.{LoginView, RegistrationView}
import pkg.b.logic.Account
import pkg.d.util.Logger.logger
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

  private def showHome(account: Account): Unit =
    homePageFor(account) match
      case Some(homePage) =>
        val viewModel = new HomePageViewModel

        stage.title = "ProtoFlow"
        stage.width = 1100
        stage.height = 700
        stage.resizable = true

        val scene = new Scene(1100, 700):
          root = homePage(
            viewModel = viewModel,
            currentUser = account.getName,
            currentUsername = account.getUsername,
            onLogout = () =>
              showLogin()
          )

        addPageStylesheets(
          scene,
          "/homepages.css",
          "/registration-requests-management.css"
        )

        stage.scene = scene

      case None =>
        showLogin()

  private def addPageStylesheets(scene: Scene, pageStylesheets: String*): Unit =
    addStylesheet(scene, "/common.css")
    pageStylesheets.foreach: stylesheet =>
      addStylesheet(scene, stylesheet)

  private def addStylesheet(scene: Scene, path: String): Unit =
    Option(getClass.getResource(path))
      .foreach: css =>
        scene.stylesheets.add(css.toExternalForm)

  private def homePageFor(account: Account): Option[HomePage] =
    loadHomePage(account.getRole)

  private def loadHomePage(roleName: String): Option[HomePage] =
    val normalizedRole = roleName.trim.toLowerCase
    val homePageObjectName = "HomePage" + normalizedRole.capitalize + "View"
    val completeClassName = s"pkg.a.gui.views.$homePageObjectName$$"
    try
      Some(
        Class
        .forName(completeClassName)
        .getField("MODULE$")
        .get(null)
        .asInstanceOf[HomePage]
      )
    catch
      case exception: Exception =>
        logger(exception)
        None