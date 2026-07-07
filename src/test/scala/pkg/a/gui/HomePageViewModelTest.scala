package pkg.a.gui

import org.scalatest.funsuite.AnyFunSuite
import pkg.c.data.guiStructures.{HomePageConfig, HomePageViewModel, MenuAction, Role}


class HomePageViewModelTest extends AnyFunSuite{

  test("la homepage Viewer deve avere titolo e menu corretti"):
    val home = HomePageConfig.forRole(Role.Viewer)

    assert(home.pageTitle == "Homepage Utente")
    assert(home.menuItems.map(_.label) == Seq(
      "Dashboard",
      "Profilo",
      "Visualizzazione Protocollazioni",
      "Logout"
    ))

  test("la homepage Operatore deve avere titolo e menu corretti"):
    val home = HomePageConfig.forRole(Role.Operator)

    assert(home.pageTitle == "Homepage Operatore")
    assert(home.menuItems.map(_.label) == Seq(
      "Dashboard",
      "Profilo",
      "Prese in carico",
      "Protocollazione",
      "Archiviazione",
      "Logout"
    ))

  test("la homepage Amministratore deve avere il titolo e menu corretti"):
    val home = HomePageConfig.forRole(Role.Admin)

    assert(home.pageTitle == "Homepage Amministratore")
    assert(home.menuItems.map(_.label) == Seq(
      "Dashboard",
      "Profilo",
      "Statistiche",
      "Log",
      "Controllo Gestione",
      "Registrazioni",
      "Account Utenti",
      "Ruoli",
      "Classifiche",
      "Logout"
    ))

  test("cliccando su una voce di menu cambia la sezione corrente"):
    val vm = HomePageViewModel(HomePageConfig.forRole(Role.Operator))

    vm.select(MenuAction.Protocollo)

    assert(vm.currentSection == MenuAction.Protocollo)

  test("il logout viene riconosciuto come azione speciale"):
    val vm = HomePageViewModel(HomePageConfig.forRole(Role.Admin))

    vm.select(MenuAction.Logout)

    assert(vm.currentSection == MenuAction.Logout)
}
