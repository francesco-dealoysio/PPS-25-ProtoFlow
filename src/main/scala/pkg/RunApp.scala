package pkg

import pkg.a.gui.navigation.AppNavigator
import pkg.a.gui.text.UiText.Common.ApplicationName
import pkg.b.logic.Init
import scalafx.application.JFXApp3
import scalafx.scene.image.Image

object RunApp extends JFXApp3:

  override def start(): Unit =
    Init.init()

    stage = new JFXApp3.PrimaryStage:
      title = ApplicationName

    Option(getClass.getResource("/img/message.jpg"))
      .map(_.toExternalForm)
      .foreach(url => stage.icons.add(new Image(url)))

    val navigator = AppNavigator(stage)
    navigator.showLogin()

    stage.show()