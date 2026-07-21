package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.traits.HomePage

object OperatorHomePageView extends HomePage:

  override protected val pageTitle: String = "Homepage Operatore"

  override protected val roleDescription: String = "Operatore Protocollo"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Prese in carico", MenuAction.PreseInCarico),
      MenuItem("Protocollazione", MenuAction.Protocollo),
      MenuItem("Archiviazione", MenuAction.Archiviazione),
      MenuItem("Logout", MenuAction.Logout)
    )
