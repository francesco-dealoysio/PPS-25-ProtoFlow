package pkg.a.gui

import pkg.c.data.guiStructures.{HomePageViewModel, MenuItem, MenuAction}

object ViewerHomePageView extends HomePage:

  override protected val pageTitle: String = "Homepage Utente"
  override protected val roleDescription: String = "Viewer"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Visualizzazione Protocollazioni", MenuAction.VisualizzazioneProtocollazioni),
      MenuItem("Logout", MenuAction.Logout)
    )