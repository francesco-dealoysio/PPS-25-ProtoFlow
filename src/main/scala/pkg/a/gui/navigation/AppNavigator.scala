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

  def showLogin(): Unit =
    stage.title = WindowTitles.Login
    stage.minWidth = 0
    stage.minHeight = 0
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
    stage.sizeToScene()
    stage.centerOnScreen()

  private def showRegistration(): Unit =
    stage.title = WindowTitles.Registration
    stage.minWidth = 0
    stage.minHeight = 0
    stage.resizable = false

    val scene = new Scene(900, 650):
      root = RegistrationView(
        validator = RegistrationValidator(),

        onExit = () =>
          showLogin()
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

        val scene = new Scene(1100, 800):
          root = homePage(
            currentAccount = account,
            onLogout = () => showLogin()
          )

        addPageStylesheets(
          scene,
          "/homepages.css",
          "/registration-requests-management.css"
        )

        stage.scene = scene
        stage.sizeToScene()
        stage.minWidth = 1100
        stage.minHeight = 800
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