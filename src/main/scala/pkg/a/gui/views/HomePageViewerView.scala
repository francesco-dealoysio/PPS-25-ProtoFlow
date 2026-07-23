package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.traits.HomePage

object HomePageViewerView extends HomePage:

  override protected val pageTitle: String = "Homepage Utente"
  override protected val roleDescription: String = "Viewer"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Visualizzazione Protocollazioni", MenuAction.VisualizzazioneProtocollazioni),
      MenuItem("Logout", MenuAction.Logout)
    )