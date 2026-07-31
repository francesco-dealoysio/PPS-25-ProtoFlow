package pkg.a.gui

import org.scalatest.OneInstancePerTest
import org.scalatest.funsuite.AnyFunSuite
import pkg.a.gui.structures.{HomePageViewModel, MenuAction}

class HomePageViewModelTest extends AnyFunSuite with OneInstancePerTest:

  private val viewModel = new HomePageViewModel

  test("la sezione iniziale deve essere Dashboard"):
    assert(viewModel.currentSection == MenuAction.Dashboard)

  test("select deve cambiare la sezione corrente"):
    viewModel.select(MenuAction.VisualizzazioneProtocollazioni)
    assert(viewModel.currentSection == MenuAction.VisualizzazioneProtocollazioni)

  test("select deve permettere di tornare alla Dashboard"):
    viewModel.select(MenuAction.Classifiche)
    viewModel.select(MenuAction.Dashboard)
    assert(viewModel.currentSection == MenuAction.Dashboard)

  test("il logout deve essere memorizzato come sezione corrente"):
    viewModel.select(MenuAction.Logout)
    assert(viewModel.currentSection == MenuAction.Logout)