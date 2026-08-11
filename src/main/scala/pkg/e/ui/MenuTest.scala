package pkg.e.ui

import pkg.b.logic.Account
import pkg.e.ui.traits.GUI
import scalafx.application.JFXApp3
import scalafx.geometry.Pos
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{MenuBar, Menu, MenuItem, Control, Label}
import scalafx.scene.layout.{BorderPane, HBox, VBox}

class MenuTest extends JFXApp3:

  override def start(): Unit =

    stage = new JFXApp3.PrimaryStage:
      width = 400
      height = 400

      scene = new Scene:

        val header = new HBox {
          style = "-fx-background-color: green;"
          setHeight(this, 50, 50, 50)
          alignment = Pos.Center
          children = Seq(
            new Label("Header")
          )
        }

        // Create a MenuItem with an action
        val menuItem1 = new MenuItem("Say Hello") {
          onAction = _ => println("Hello from MenuItem!")
        }
        val menuItem2 = new MenuItem("Logout") {
          onAction = _ => sys.exit()
        }

        // Create a Menu and add the MenuItem
        val menu1 = new Menu("Home") {
          items = List(
            new MenuItem("About") {onAction = _ => println("Hello from MenuItem!")},
            new MenuItem("Exit") {onAction = _ => sys.exit()}
          )
        }

        // Create a Menu and add the MenuItem
        val menu2 = new Menu("Log") {
          items = List(menuItem1, menuItem2)
        }

        // Create a MenuBar
        val menuBar = new MenuBar {
          menus = List(menu1, menu2)
        }


        val sidebar = new VBox {
          style = "-fx-background-color: red;"
          setWidth(this, 100, 100, 100)
          alignment = Pos.Center
          children = Seq(
            new Label("Menù")
          )
        }

        val gridView = new VBox {
          style = "-fx-background-color: yellow;"
          alignment = Pos.Center
          children = Seq(
            new Label("GridView")
          )
        }

        val body = new BorderPane {

          top = new VBox {
            children = Seq(menuBar)
          }
          left = sidebar
          center = gridView
        }

        val footer = new HBox {
          style = "-fx-background-color: green;"
          setHeight(this, 50, 50, 50)
          alignment = Pos.Center
          children = Seq(
            new Label("Footer")
          )
        }

        root = new BorderPane {
          top = menuBar
          center = body
          bottom = footer
        }

  def setWidth[T <: Node](control: T, width: Double): Unit =
    setWidth(control, width, width, width)

  def setWidth[T <: Node](control: T, pref: Double, min: Double, max: Double): Unit =
    control match
      case ctl: scalafx.scene.layout.Region =>
        ctl.prefWidth = pref
        ctl.minWidth = min
        ctl.maxWidth = max
      case _ =>
        println(s"Width setting not supported for ${control.getClass.getSimpleName}")

  def setHeight[T <: Node](control: T, height: Double): Unit =
    setWidth(control, height, height, height)

  def setHeight[T <: Node](control: T, pref: Double, min: Double, max: Double): Unit =
    control match
      case ctl: scalafx.scene.layout.Region =>
        ctl.prefHeight = pref
        ctl.minHeight = min
        ctl.maxHeight = max
      case _ =>
        println(s"Height setting not supported for ${control.getClass.getSimpleName}")

@main def tryMenuTest: Unit =
  val menu = new MenuTest()
  menu.main(Array.empty)