package pkg

// Entry point to run the application
import pkg.b.logic.Init
import pkg.a.gui.Navigator
import scalafx.application.JFXApp3
import scalafx.scene.image.Image

object toRunApp extends JFXApp3:

  override def start(): Unit = {
    Init.init
    stage = new JFXApp3.PrimaryStage:
      title = "ProtoFlow"
      width = 460
      height = 560
      resizable = false
    
    Option(getClass.getResourceAsStream("/img/message.jpg"))
      .foreach(stream => stage.icons.add(new Image(stream)))
    
    val navigator = Navigator(stage)
    navigator.showLogin()
    //navigator.showRoleAddView()
    
    stage.show()
  }
