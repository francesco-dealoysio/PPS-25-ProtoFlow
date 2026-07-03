package pkg.a.gui

import pkg.d.util.{MyJLabel, MyJTextField, RoundedBorder}

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

    // Create the GridBagPanel
    contents = new GridBagPanel {

      def gbpc(
                gridx: Int,
                gridy: Int,
                gridwidth: Int = 1,
                gridheight: Int = 1,
                weightx: Double = 0,
                weighty: Double = 0,
                fill: scala.swing.GridBagPanel.Fill.Value = Both,
                anchor: Anchor.Value = Center,
                insets: Insets = Insets(5, 4, 0, 0)): Constraints = {
        val costraints = new Constraints
        costraints.gridx = gridx
        costraints.gridy = gridy
        costraints.gridwidth = gridwidth
        costraints.gridheight = gridheight
        costraints.weightx = weightx
        costraints.weighty = weighty
        costraints.fill = fill
        costraints.anchor = anchor
        costraints.insets = insets
        costraints
      }

      // Logo
      val logoLbl = new Label("PF") {
        preferredSize = new Dimension(40, 40)
        //background = new Color(255, 0, 0)
        background = Color.LIGHT_GRAY
        foreground = Color.BLACK
        opaque = true // Needed for background color to be visible
        // Apply a line border (red, 2px thick)
        //border = BorderFactory.createLineBorder(Color.RED, 1)
        peer.setBorder(new RoundedBorder(10, Color.GRAY))
      }
      //layout(logoLbl) = c
      add(logoLbl, gbpc(0, 0, 1, 2, 0, 0, Both, Center, new Insets(5, 4, 0, 0)))

      // Title
      val titleLbl = new Label("ProtoFlow") {
        font = new Font("Arial", Font.PLAIN, 16)
      }
      add(titleLbl, gbpc(1, 0, 2, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      // Description
      val descriptionLbl = new Label("Enterprise Document Protocol System") {
        peer.setVerticalAlignment(javax.swing.SwingConstants.CENTER)
        peer.setFont(new Font("Arial", Font.PLAIN, 12))
      }
      //descriptionLbl.font = new Font("Arial", Font.PLAIN, 12) // 18px font size
      add(descriptionLbl, gbpc(1, 1, 2, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      // UsernameLbl
      val usernameLbl = Component.wrap(new MyJLabel("Username", 80, 25, LEFT));
      add(usernameLbl, gbpc(0, 2, 1, 1, 0, 0))

      // UsernameFld
      var javaUsernameFld = MyJTextField(100, 25, 15)
      var usernameFld = Component.wrap(javaUsernameFld)
      add(usernameFld, gbpc(1, 2, 1, 1, 0, 0))

      // PasswordLbl
      val passwordLbl = Component.wrap(new MyJLabel("Password", 80, 25, LEFT));
      add(passwordLbl, gbpc(0, 3, 1, 1, 0, 0))

      // PasswordFld
      val passwordFld = new PasswordField(10)
      add(passwordFld, gbpc(1, 3, 1, 1, 0, 0))

      // ResetBtn
      val resetBtn = new Button("Reset")
      resetBtn.focusable = false
      add(resetBtn, gbpc(2, 2, 1, 2, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      // AccessBtn
      val submitBtn = new Button("Accedi")
      add(submitBtn, gbpc(0, 4, 3, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      // RegistrationBtn
      val registrationBtn = new Button("Registrati")
      add(registrationBtn, gbpc(0, 5, 3, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      listenTo(submitBtn)
      listenTo(resetBtn)
      listenTo(registrationBtn)

      reactions += {
        case ButtonClicked(`submitBtn`) =>
          val username = usernameFld.peer.asInstanceOf[MyJTextField].getText.trim
          val password = passwordFld.password.mkString.trim
          if (username.nonEmpty && password.nonEmpty)
            //Dialog.showMessage(this, s"Username: $username\nPassword: $password", title = "Submitted")
            val home = new HomepageAdmin
            home.visible = true
          //new Homepage().visible = true
          else
            Dialog.showMessage(this, "Invalid input!", title = "Error", Dialog.Message.Error)
        case ButtonClicked(`resetBtn`) =>
          javaUsernameFld.setText("")
          passwordFld.peer.setText("")
          javaUsernameFld.requestFocusInWindow()
        case ButtonClicked(`registrationBtn`) =>
          Dialog.showMessage(this, "Registration functionality\nis in progress!", title = "Error", Dialog.Message.Info)
        case _ =>
          javaUsernameFld.requestFocusInWindow()
      }

    }

    pack()
    centerOnScreen()

  }
}
