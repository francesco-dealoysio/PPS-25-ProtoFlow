package pkg

import pkg.a.gui.navigation.AppNavigator
import pkg.b.logic.Init
import scalafx.application.JFXApp3
import scalafx.scene.image.Image
import pkg.a.gui.text.UiText.Common.ApplicationName

object RunApp extends JFXApp3:

  override def start(): Unit =

    Init.init()
    stage = new JFXApp3.PrimaryStage:
      title = ApplicationName
    
    Option(getClass.getResourceAsStream("/img/message.jpg"))
      .foreach(stream => stage.icons.add(new Image(stream)))
    
    val navigator = AppNavigator(stage)
    navigator.showLogin()
    
    stage.show()