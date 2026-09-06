package pkg.a.gui.navigation

import pkg.a.gui.text.UiText.Common.WindowTitles
import pkg.a.gui.traits.HomePage
import pkg.a.gui.validators.RegistrationValidator
import pkg.a.gui.views.{LoginView, RegistrationView}
import pkg.b.logic.Account
import pkg.d.util.Logger.logger
import scalafx.application.JFXApp3
import scalafx.scene.Scene

class AppNavigator(stage: JFXApp3.PrimaryStage):

  private val LoginWindowWidth = 460
  private val LoginWindowHeight = 560
  private val RegistrationWindowWidth = 900
  private val RegistrationWindowHeight = 650
  private val HomeWindowWidth = 1100
  private val HomeWindowHeight = 800

  def showLogin(): Unit =
    stage.title = WindowTitles.Login
    stage.minWidth = 0
    stage.minHeight = 0
    stage.resizable = false

    val scene = new Scene(LoginWindowWidth, LoginWindowHeight):
      root = LoginView(
        onLoginSuccess = user =>
          showHome(user),

        onRegistrationRequest = () =>
          showRegistration()
      )

    addPageStylesheets(scene, "/login.css")

    stage.scene = scene
    stage.sizeToScene()
    stage.centerOnScreen()

  private def showRegistration(): Unit =
    stage.title = WindowTitles.Registration
    stage.minWidth = 0
    stage.minHeight = 0
    stage.resizable = false

    val scene = new Scene(RegistrationWindowWidth, RegistrationWindowHeight):
      root = RegistrationView(
        validator = RegistrationValidator(),
        onExit = () => showLogin()
      )

    addPageStylesheets(scene, "/registration.css")
    stage.scene = scene
    stage.sizeToScene()
    stage.centerOnScreen()

  private def showHome(account: Account): Unit =
    homePageFor(account) match
      case Some(homePage) =>

        stage.title = WindowTitles.Home
        stage.resizable = true

        val scene = new Scene(HomeWindowWidth, HomeWindowHeight):
          root = homePage(
            currentAccount = account,
            onLogout = () => showLogin()
          )

        addPageStylesheets(scene, "/homepages.css", "/statistics.css", "/registration-requests.css")
        stage.scene = scene
        stage.sizeToScene()
        stage.minWidth = HomeWindowWidth
        stage.minHeight = HomeWindowHeight
        stage.centerOnScreen()

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