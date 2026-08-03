package pkg.a.gui.traits

import scalafx.application.JFXApp3
import pkg.b.logic.Account
import pkg.d.util.DateTime
import scalafx.scene.layout.BorderPane
import scalafx.scene.Scene
import scalafx.scene.layout.HBox
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.{Label}
import scalafx.scene.image.Image

trait GUI extends JFXApp3:

  val user: Account

  def Title: String = "GUI Trait"
  def Width: Int = 800
  def Height: Int = 600

  def body = new BorderPane

  override def start(): Unit =

    stage = new JFXApp3.PrimaryStage:

      title = Title
      width = Width
      height = Height
      resizable = false
      centerOnScreen()

      onCloseRequest = event =>
        event.consume()

      scene = new Scene:

        val header = new HBox {
          spacing = 10
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightgray;"
          alignment = Pos.CenterLeft
          padding = Insets(10)
          children = Seq(
            new Label("☰"),
            new Label("Protoflow")
          )
        }

        val footer = new HBox {
          spacing = 10
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightgray;"
          alignment = Pos.CenterRight
          padding = Insets(10)
          val dateTime = new Label("")
          dateTime.text <== DateTime.dynamicDateTimeProperty()
          children = Seq(
            new Label(user.getName + " " + user.getSurname + " (" + user.getRole + ")"),
            dateTime
          )
        }

        root = new BorderPane {
          style = "-fx-background-color: lightgray;"
          padding = Insets(5)
          top = header
          center = body
          bottom = footer
        }

    Option(getClass.getResourceAsStream("/img/message.jpg"))
      .foreach(stream => stage.icons.add(new Image(stream)))