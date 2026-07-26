package pkg.a.gui.views

import pkg.a.gui.structures.{MenuAction, MenuItem}
import pkg.a.gui.text.UiText
import pkg.a.gui.traits.HomePage
import UiText.Menu

object HomePageAdminView extends HomePage:

  override protected val pageTitle: String = "Homepage Amministratore"

  override protected val roleDescription: String = "Amministratore"

  override protected val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem(Menu.Dashboard, MenuAction.Dashboard),
      MenuItem(Menu.Profile, MenuAction.Profilo),
      MenuItem(Menu.Statistics, MenuAction.Statistiche),
      MenuItem(Menu.Log, MenuAction.Log),
      MenuItem(Menu.ManagementControl, MenuAction.ControlloGestione),
      MenuItem(Menu.Registrations, MenuAction.Registrazioni),
      MenuItem(Menu.UserAccounts, MenuAction.AccountUtenti),
      MenuItem(Menu.Roles, MenuAction.Ruoli),
      MenuItem(Menu.Classifications, MenuAction.Classifiche),
      MenuItem(Menu.Logout, MenuAction.Logout)
    )