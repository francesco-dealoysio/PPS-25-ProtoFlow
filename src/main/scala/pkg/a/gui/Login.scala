package pkg.a.gui

import scala.swing.*
import scala.swing.Component
import scala.swing.GridBagPanel.*
import scala.swing.GridBagPanel.Fill.*
import scala.swing.GridBagPanel.Anchor.*
import scala.swing.event.*
import java.awt.{Color, Dimension, Insets}
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.Font
import javax.swing.border.AbstractBorder
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.SpringLayout.Constraints
import javax.swing.text.*
import javax.swing.SwingConstants.*

object Login extends SimpleSwingApplication {

  def top: Frame = new MainFrame {
    val defaultSize: Dimension = new Dimension(330, 220)
    title = "ProtoFlow - Portale Gestione Protocollo"

    //minimumSize = new Dimension(420, 280)
    preferredSize = defaultSize
    resizable = false

    pack()
    centerOnScreen()

  }
}
