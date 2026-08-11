package pkg.e.ui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{MenuButton, MenuItem}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.geometry.Insets

object VerticalMenu extends JFXApp3 {

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Vertical Menu Example"
      scene = new Scene(400, 300) {
        val fileMenu = new MenuButton("File") {
          items = List(
            new MenuItem("New") {
              onAction = _ => println("New clicked")
            },
            new MenuItem("Open") {
              onAction = _ => println("Open clicked")
            },
            new MenuItem("Exit") {
              onAction = _ => sys.exit(0)
            }
          )
        }

        val editMenu = new MenuButton("Edit") {
          items = List(
            new MenuItem("Cut") {
              onAction = _ => println("Cut clicked")
            },
            new MenuItem("Copy") {
              onAction = _ => println("Copy clicked")
            },
            new MenuItem("Paste") {
              onAction = _ => println("Paste clicked")
            }
          )
        }

        val helpMenu = new MenuButton("Help") {
          items = List(
            new MenuItem("About") {
              onAction = _ => println("About clicked")
            }
          )
        }

        // VBox to hold menus vertically
        val verticalMenu = new VBox(10) {
          padding = Insets(10)
          children = List(fileMenu, editMenu, helpMenu)
        }

        root = new BorderPane {
          left = verticalMenu
        }
      }
    }
  }
}

@main def tryVerticalMenu: Unit =
  VerticalMenu.main(Array.empty)

