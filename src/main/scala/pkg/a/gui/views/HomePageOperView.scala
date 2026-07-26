package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu
import pkg.a.gui.traits.HomePage

object HomePageOperView extends HomePage:

  override protected val pageTitle: String = "Homepage Operatore"

  override protected val roleDescription: String = "Operatore Protocollo"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem(Menu.Dashboard, MenuAction.Dashboard),
      MenuItem(Menu.Profile, MenuAction.Profilo),
      MenuItem(Menu.Assignments, MenuAction.PreseInCarico),
      MenuItem(Menu.Protocols, MenuAction.Protocollo),
      MenuItem(Menu.Archiving, MenuAction.Archiviazione),
      MenuItem(Menu.Logout, MenuAction.Logout)
    )
