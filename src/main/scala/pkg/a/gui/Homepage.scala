package pkg.a.gui

import pkg.c.data.Entities.*
import pkg.c.data.Entities.Account

import java.awt.Toolkit
import scala.swing.{BorderPanel, Button, Dimension, Label, MainFrame}
import scala.swing.event.ButtonClicked

class HomepageAdmin(account: Account) extends MainFrame {
  title = "ProtoFlow - Administrator Homepage"
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  preferredSize = screenSize
  
  val label = new Label("Hello " + account.nome + " " + account.cognome + " this is your Administrator Homepage!")
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
