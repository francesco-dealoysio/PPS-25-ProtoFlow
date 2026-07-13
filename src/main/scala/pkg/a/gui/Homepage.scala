package pkg.a.gui

//import pkg.b.logic.Entities.*
import pkg.b.logic.Account

import java.awt.Toolkit
import scala.swing.{BorderPanel, Button, Dimension, Label, MainFrame}
import scala.swing.event.ButtonClicked

class Homepage(account: Account) extends MainFrame {
  title = "ProtoFlow - Homepage"
  private val screenSize: Dimension = Toolkit.getDefaultToolkit.getScreenSize
  preferredSize = screenSize

  val role = account.getRuolo match
    case "admin"  => "Administrator"
    case "oper"   => "Operator"
    case "viewer" => "Viewer"
    case _        => "Unknown role"

  val label = new Label("Hello " + account.getNome + " " + account.getCognome + " this is your " + role + " Homepage!")
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