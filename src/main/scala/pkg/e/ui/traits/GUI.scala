package pkg.e.ui.traits

import pkg.b.logic.Account
import pkg.d.util.DateTime
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.Image
import scalafx.scene.layout.{BorderPane, HBox, Priority, Region}

/**
 * An interface modelling a basic GUI for the project.
 * It has an header, a body and a footer.
 * The header contains the logo, the name and the description of the project.
 * The footer contains the name and role of current user and dynamic date and time.
 * The body is filled by the traits/classes/objects that extend the trait.
 * */
trait GUI extends JFXApp3:

  val user: Account
  val parentMask: JFXApp3

  def Title: String = "GUI Trait"
  def Width: Int = 780
  def Height: Int = 680

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
        stylesheets.add("/ui.css")

        val header = new HBox {
          spacing = 10
          val defaultHeight = 40
          minHeight = defaultHeight
          prefHeight = defaultHeight
          maxHeight = defaultHeight
          styleClass += "app-header"

          alignment = Pos.CenterLeft
          padding = Insets(10)

          val logo =
            new Button("☰"):
              styleClass += "app-logo"

          val project =
            new Label("Protoflow"):
              styleClass += "app-title"

          children = Seq(
            logo,
            project
          )
        }

        val footer = new HBox {
          spacing = 10
          val defaultHeight = 40
          minHeight = defaultHeight
          prefHeight = defaultHeight
          maxHeight = defaultHeight
          styleClass += "app-footer"
          alignment = Pos.CenterLeft
          padding = Insets(10)

          val spacer = new Region()
          HBox.setHgrow(spacer, Priority.Always)
          
          val dateTime = new Label("")
          dateTime.text <== DateTime.dynamicDateTimeProperty()

          val role = user.getRole match {
            case "admin" => "Amministratore"
            case "oper" => "Operatore Protocollo"
            case "viewer" => "Visualizzatore"
            case _ => "Ospite"
          }

          children = Seq(
            new Label(user.getName + " " + user.getSurname + " (" + role + ")"),
            spacer,
            dateTime
          )
        }

        root = new BorderPane {
          style = "-fx-background-color: lightgray;"
          top = header
          center = body
          bottom = footer
        }

    Option(getClass.getResourceAsStream("/img/message.jpg"))
      .foreach(stream => stage.icons.add(new Image(stream)))