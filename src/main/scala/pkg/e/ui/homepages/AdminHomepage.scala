package pkg.e.ui.homepages

import pkg.b.logic.Account
import pkg.e.ui.Main
import pkg.e.ui.traits.Homepage
import pkg.e.ui.managements.AccountManagement
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.text.TextAlignment

class AdminHomepage(val user: Account) extends Homepage {

  val parentMask: Homepage = this

  override val Title: String = "Homepage Amministratore"

  pageTitle = Title

  override def menu = new VBox {
    this.styleClass += "sidebar"

    val defaultWidth = 170
    minWidth = defaultWidth
    prefWidth = defaultWidth
    maxWidth = defaultWidth

    val dashboardBtn = new Button("Dashboard")
    val profileBtn = new Button("Profilo")
    val statisticsBtn = new Button("Statistiche")
    val logsBtn = new Button("Log")
    val managementControlBtn = new Button("Controllo Gestione")
    val registrationsBtn = new Button("Registrazione")
    val accountsBtn = new Button("Account Utenti")
    val rolesBtn = new Button("Ruoli")
    val classificationsBtn = new Button("Classifiche")
    val logoutBtn = new Button("Logout")

    val menuItems = Seq(
      dashboardBtn,
      profileBtn,
      statisticsBtn,
      logsBtn,
      managementControlBtn,
      registrationsBtn,
      accountsBtn,
      rolesBtn,
      classificationsBtn,
      logoutBtn
    )

    menuItems.foreach(item =>
      item.styleClass += "sidebar-button"
      item.maxWidth = Double.MaxValue
      item.textAlignment = TextAlignment.Left
    )

    dashboardBtn.onAction = _ => ()
    profileBtn.onAction = _ => ()
    statisticsBtn.onAction = _ => ()
    logsBtn.onAction = _ => ()
    managementControlBtn.onAction = _ => ()
    registrationsBtn.onAction = _ => ()
    accountsBtn.onAction = _ =>
      AccountManagement(user, parentMask).start()
    rolesBtn.onAction = _ => ()
    classificationsBtn.onAction = _ => ()
    logoutBtn.onAction = _ => Main.start()

    children = menuItems
  }
}


