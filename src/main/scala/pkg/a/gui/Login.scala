package pkg.a.gui

import pkg.d.util.{MyJLabel, MyJTextField, RoundedBorder}
import pkg.d.util.Util.loadImage
import pkg.d.util.Util.md5

import pkg.d.util.Properties.*
import pkg.c.data.xmlManagement.Xml.*
import pkg.c.data.xmlManagement.Entities.*

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

import java.awt.Robot
import java.awt.event.KeyEvent

import javax.swing.border.AbstractBorder
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.SpringLayout.Constraints
import javax.swing.text.*
import javax.swing.SwingConstants.*

import java.awt.image.BufferedImage

object Login extends SimpleSwingApplication {

  def top: Frame = new MainFrame {
    title = "ProtoFlow - Portal"
    preferredSize = new Dimension(330, 220)
    resizable = false

    val image: BufferedImage = loadImage("img/message.jpg")
    iconImage = image

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
/*
      def getResourcePath(filePathName: String): String =
        import java.net.URL

        val resourceUrl: URL | Null = getClass.getClassLoader.getResource(filePathName)

        if resourceUrl == null then
          println(s"Risorsa '$filePathName' non trovata nel classpath.")
          //Dialog.showMessage(this, s"Risorsa '$filePathName' non trovata nel classpath.", title = "Debug", Dialog.Message.Plain)
        else
          println(s"URL della risorsa: $resourceUrl xxxxx")
          //Dialog.showMessage(this, s"URL della risorsa: $resourceUrl", title = "Debug", Dialog.Message.Plain)

        resourceUrl.toString
*/
/*
      def readPropsFile: Unit =
        import scala.io.Source

        val resourceStream = getClass.getResourceAsStream("/protoflow.properties")
        if resourceStream == null then
          println("Risorsa non trovata!")
          Dialog.showMessage(this, "Risorsa non trovata!", title = "Debug", Dialog.Message.Plain)
        else
          try
            val content = Source.fromInputStream(resourceStream).mkString
            Dialog.showMessage(this, content, title = "Debug", Dialog.Message.Plain)
            println("Contenuto del file:")
            println(content)
          finally
            resourceStream.close()
*/
      def checkCredentials(username: String, password: String): AnyRef =
        var result: Account = null
        val fs = java.io.File.separator
        val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
        val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")

        val accounts = loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
        val found = accounts.map(a => a.asInstanceOf[Account]).filter(_.username == username)

        if (found != Nil && found(0).password == md5(password))
          result = found(0)

        result

      val logoLbl = new Label("PF") {
        preferredSize = new Dimension(40, 40)
        background = Color.LIGHT_GRAY
        foreground = Color.BLACK
        opaque = true
        peer.setBorder(new RoundedBorder(10, Color.GRAY))
      }
      add(logoLbl, gbpc(0, 0, 1, 2, 0, 0, Both, Center, new Insets(5, 4, 0, 0)))

      val titleLbl = new Label("ProtoFlow") {
        font = new Font("Arial", Font.PLAIN, 16)
      }
      add(titleLbl, gbpc(1, 0, 2, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      val descriptionLbl = new Label("Enterprise Document Protocol System") {
        peer.setVerticalAlignment(javax.swing.SwingConstants.CENTER)
        peer.setFont(new Font("Arial", Font.PLAIN, 12))
      }
      add(descriptionLbl, gbpc(1, 1, 2, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      val usernameLbl = Component.wrap(new MyJLabel("Username *", 80, 25, LEFT));
      add(usernameLbl, gbpc(0, 2, 1, 1, 0, 0))

      var javaUsernameFld = MyJTextField(100, 25, 15)
      var usernameFld = Component.wrap(javaUsernameFld)
      add(usernameFld, gbpc(1, 2, 1, 1, 0, 0))

      val passwordLbl = Component.wrap(new MyJLabel("Password *", 80, 25, LEFT));
      add(passwordLbl, gbpc(0, 3, 1, 1, 0, 0))

      val passwordFld = new PasswordField(10)
      add(passwordFld, gbpc(1, 3, 1, 1, 0, 0))

      val clearBtn = new Button("Clear")
      clearBtn.focusable = false
      clearBtn.peer.setMnemonic(KeyEvent.VK_C)
      add(clearBtn, gbpc(2, 2, 1, 2, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      val accessBtn = new Button("Access")
      accessBtn.peer.setMnemonic(KeyEvent.VK_A)
      add(accessBtn, gbpc(0, 4, 3, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      val registrationBtn = new Button("Registration")
      registrationBtn.peer.setMnemonic(KeyEvent.VK_R)
      add(registrationBtn, gbpc(0, 5, 3, 1, 0, 0, Both, Center, new Insets(5, 4, 0, 5)))

      listenTo(usernameFld.keys)
      listenTo(passwordFld.keys)
      listenTo(accessBtn.keys)
      listenTo(registrationBtn.keys)
      listenTo(accessBtn)
      listenTo(clearBtn)
      listenTo(registrationBtn)

      reactions += {

        case KeyPressed(src, Key.Enter, _, _) =>
          src match
            case c: Component if c eq usernameFld =>
              (new Robot()).keyPress(KeyEvent.VK_TAB)
            case c: Component if c eq passwordFld =>
              accessBtn.doClick()
            case c: Component if c eq accessBtn =>
              accessBtn.doClick()
            case c: Component if c eq registrationBtn =>
              registrationBtn.doClick()

        case ButtonClicked(`accessBtn`) =>
          val username = usernameFld.peer.asInstanceOf[MyJTextField].getText.trim
          val password = passwordFld.password.mkString.trim
          if (username.nonEmpty && password.nonEmpty)
            val obj = checkCredentials(username, password)
            if (obj != null) then
              val account = obj.asInstanceOf[Account]
              val role = account.ruolo
              val home = new Homepage(account)
              if (home != null) then
                home.visible = true
              else
                Dialog.showMessage(this, "Application Error: \"" + role + "\" role not found!", title = "Error", Dialog.Message.Error)
            else
              Dialog.showMessage(this, "Access denied!", title = "Error", Dialog.Message.Error)
          else
            Dialog.showMessage(this, "Invalid input!", title = "Error", Dialog.Message.Error)

          javaUsernameFld.requestFocusInWindow()

        case ButtonClicked(`clearBtn`) =>
          javaUsernameFld.setText("")
          passwordFld.peer.setText("")
          javaUsernameFld.requestFocusInWindow()

        case ButtonClicked(`registrationBtn`) =>
          val registration = new Registration
          registration.visible = true
          javaUsernameFld.requestFocusInWindow()

        case _ =>
      }

    }

    pack()
    centerOnScreen()

  }
}
