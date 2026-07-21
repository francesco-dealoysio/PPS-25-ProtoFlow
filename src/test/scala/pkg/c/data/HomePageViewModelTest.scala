package pkg.c.data

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.OneInstancePerTest
import pkg.a.gui.structures.{HomePageViewModel, MenuAction}

class HomePageViewModelTest extends AnyFunSuite with OneInstancePerTest:

  private val viewModel = new HomePageViewModel

  test("la sezione iniziale deve essere Dashboard"):
    assert(viewModel.currentSection == MenuAction.Dashboard)

  test("select deve cambiare la sezione corrente"):
    viewModel.select(MenuAction.Protocollo)
    assert(viewModel.currentSection == MenuAction.Protocollo)

  test("select deve permettere di tornare alla Dashboard"):
    viewModel.select(MenuAction.Archiviazione)
    viewModel.select(MenuAction.Dashboard)
    assert(viewModel.currentSection == MenuAction.Dashboard)

  test("il logout deve essere memorizzato come sezione corrente"):
    viewModel.select(MenuAction.Logout)
    assert(viewModel.currentSection == MenuAction.Logout)