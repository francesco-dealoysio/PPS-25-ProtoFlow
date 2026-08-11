package pkg.e.ui.homepages

import pkg.e.ui.traits.Homepage
import pkg.b.logic.Account
import pkg.e.ui.Main
import pkg.e.ui.operations.*
import pkg.e.ui.managements.*
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.text.TextAlignment

class OperatorHomepage(val user: Account) extends Homepage {

  val parentMask: Homepage = this

  override val Title: String = "Homepage Operatore"

  pageTitle = Title

  override def menu = new VBox {
    this.styleClass += "sidebar"

    val defaultWidth = 170
    minWidth = defaultWidth
    prefWidth = defaultWidth
    maxWidth = defaultWidth

    val dashboardBtn = new Button("Dashboard")
    val profileBtn = new Button("Profilo")
    val loadBtn = new Button("Presa in carico")
    val protocolBtn = new Button("Protocollazione")
    val archiveBtn = new Button("Archiviazione")
    val searchBtn = new Button("Ricerca")
    val logoutBtn = new Button("Logout")

    val menuItems = Seq(
      dashboardBtn,
      profileBtn,
      loadBtn,
      protocolBtn,
      archiveBtn,
      searchBtn,
      logoutBtn
    )

    menuItems.foreach(item =>
      item.styleClass += "sidebar-button"
      item.maxWidth = Double.MaxValue
      item.textAlignment = TextAlignment.Left
    )

    dashboardBtn.onAction = _ => ()
    profileBtn.onAction = _ => ()
    loadBtn.onAction = _ =>
      DocumentLoad(user, parentMask).start()
    protocolBtn.onAction = _ => ()
    archiveBtn.onAction = _ => ()
    searchBtn.onAction = _ => ()
    logoutBtn.onAction = _ => Main.start()
    /*
      if askConfirmation(
        titleText = "Conferma uscita",
        header = "Confermi l'operazione?",
        content = "L'applicazione verrà terminata"
      ) then {
        Main.start()
      }
    */

    children = menuItems
  }
}