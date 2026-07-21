package pkg.a.gui

import pkg.c.data.guiStructures.{MenuAction, MenuItem}

object AdminHomePageView extends HomePage:

  override protected val pageTitle: String = "Homepage Amministratore"

  override protected val roleDescription: String = "Amministratore"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Statistiche", MenuAction.Statistiche),
      MenuItem("Log", MenuAction.Log),
      MenuItem("Controllo Gestione", MenuAction.ControlloGestione),
      MenuItem("Registrazioni", MenuAction.Registrazioni),
      MenuItem("Account Utenti", MenuAction.AccountUtenti),
      MenuItem("Ruoli", MenuAction.Ruoli),
      MenuItem("Classifiche", MenuAction.Classifiche),
      MenuItem("Logout", MenuAction.Logout)
    )