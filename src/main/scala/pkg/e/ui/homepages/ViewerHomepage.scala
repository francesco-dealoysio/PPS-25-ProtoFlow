package pkg.e.ui.homepages

import pkg.e.ui.traits.Homepage
import pkg.b.logic.Account
import pkg.e.ui.Main
import pkg.e.ui.operations.*
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.text.TextAlignment

class ViewerHomepage(val user: Account) extends Homepage {

  val parentMask: Homepage = this

  override val Title: String = "Homepage Viewer"
  pageTitle = Title

  override def menu = new VBox {
    this.styleClass += "sidebar"

    val defaultWidth = 170
    minWidth = defaultWidth
    prefWidth = defaultWidth
    maxWidth = defaultWidth

    val dashboardBtn = new Button("Dashboard")
    val profileBtn = new Button("Profilo")
    val protocolsViewBtn = new Button("Protocollazioni")
    val logoutBtn = new Button("Logout")

    val menuItems = Seq(
      dashboardBtn,
      profileBtn,
      protocolsViewBtn,
      logoutBtn
    )

    menuItems.foreach(item =>
      item.styleClass += "sidebar-button"
      item.maxWidth = Double.MaxValue
      item.textAlignment = TextAlignment.Left
    )

    dashboardBtn.onAction = _ => ()
    profileBtn.onAction = _ => ()
    protocolsViewBtn.onAction = _ => ()
    logoutBtn.onAction = _ => Main.start()

    children = menuItems
  }
}
