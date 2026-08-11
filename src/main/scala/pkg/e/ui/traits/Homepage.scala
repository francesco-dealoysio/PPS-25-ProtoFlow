package pkg.e.ui.traits

import pkg.e.ui.traits.GUI
import scalafx.geometry.Pos
import scalafx.scene.control.{Alert, Button, ButtonType, Label}
import scalafx.scene.layout.{HBox, VBox, BorderPane}
import scalafx.scene.text.TextAlignment

trait Homepage extends GUI:

  override val Title: String = "Homepage Trait"

  var pageTitle: String = "Homepage <ruolo>"

  override def start(): Unit =
    super.start()

  def menu = new VBox {}

  def operationPageTitle = new HBox {
    styleClass += "operation-page-title"
    spacing = 10
    val defaultHeight = 40
    minHeight = defaultHeight
    prefHeight = defaultHeight
    maxHeight = defaultHeight
    alignment = Pos.Center

    val operationPageTitleLbl = new Label(pageTitle):
      styleClass += "operation-page-title-lbl"

    children = Seq(
      operationPageTitleLbl
    )
  }

  def page = new BorderPane {
    top = operationPageTitle
    //center = operationPageGrid
  }

  override def body = new BorderPane {
    left = menu
    center = page
  }

  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content
    dialog
      .showAndWait()
      .contains(ButtonType.OK)
