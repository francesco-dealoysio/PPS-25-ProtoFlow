package pkg.a.gui

import java.awt.Toolkit
import scala.swing.{BorderPanel, Button, Dimension, Label, MainFrame}
import scala.swing.event.ButtonClicked

class HomepageAdmin extends MainFrame {
  title = "ProtoFlow - Administrator Homepage"
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  preferredSize = screenSize
  
  val label = new Label("Hello from Administrator Homepage!")
  val closeButton = new Button("Close")

  contents = new BorderPanel {
    layout(label) = BorderPanel.Position.Center
    layout(closeButton) = BorderPanel.Position.South
  }

  listenTo(closeButton)
  reactions += {
    case ButtonClicked(`closeButton`) =>
      this.close() // Close only this window
  }
}
