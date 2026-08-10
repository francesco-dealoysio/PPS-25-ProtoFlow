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

  def menu = new VBox {
    this.styleClass += "sidebar"

    val defaultWidth = 170
    minWidth = defaultWidth
    prefWidth = defaultWidth
    maxWidth = defaultWidth



    val menuItem1Btn = new Button("Menu item 1")
    val menuItem2Btn = new Button("Menu item 2")
    val menuItemNBtn = new Button("Menu item N")
    val logoutBtn = new Button("Logout")

    val menuItems = Seq(
      menuItem1Btn,
      menuItem2Btn,
      menuItemNBtn,
      logoutBtn
    )

    menuItems.foreach(item =>
      item.styleClass += "sidebar-button"
      item.maxWidth = Double.MaxValue
      item.textAlignment = TextAlignment.Left
    )

    menuItem1Btn.onAction = _ => ()
    menuItem2Btn.onAction = _ => ()
    menuItemNBtn.onAction = _ => ()
    logoutBtn.onAction = _ =>
      if askConfirmation(
        titleText = "Conferma uscita",
        header = "Confermi l'operazione?",
        content = "L'applicazione verrà terminata"
      ) then
        sys.exit(0)
        //stage.close()

    children = menuItems
  }

  def operationPageTitle = new HBox {
    styleClass += "operation-page-title"
    spacing = 10
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
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
