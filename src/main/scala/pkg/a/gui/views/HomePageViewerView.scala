package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText.Menu
import pkg.a.gui.traits.HomePage

object HomePageViewerView extends HomePage:

  override protected val pageTitle: String = "Homepage Utente"
  override protected val roleDescription: String = "Viewer"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem(Menu.Dashboard, MenuAction.Dashboard),
      MenuItem(Menu.Profile, MenuAction.Profilo),
      MenuItem(Menu.Protocols, MenuAction.VisualizzazioneProtocollazioni),
      MenuItem(Menu.Logout, MenuAction.Logout)
    )