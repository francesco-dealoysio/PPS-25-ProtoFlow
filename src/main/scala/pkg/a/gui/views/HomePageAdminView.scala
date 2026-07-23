package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.traits.HomePage

object HomePageAdminView extends HomePage:

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