package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.structures.{HomePageViewModel, MenuAction}

class HomePageViewModelTest:

  private val viewModel = new HomePageViewModel

  @Test
  def testInitialSectionIsDashboard(): Unit =
    assertEquals(MenuAction.Dashboard, viewModel.currentSection)

  @Test
  def testSelectChangesCurrentSection(): Unit =
    viewModel.select(MenuAction.VisualizzazioneProtocollazioni)
    assertEquals(MenuAction.VisualizzazioneProtocollazioni, viewModel.currentSection)

  @Test
  def testSelectCanReturnToDashboard(): Unit =
    viewModel.select(MenuAction.Classifiche)
    viewModel.select(MenuAction.Dashboard)
    assertEquals(MenuAction.Dashboard, viewModel.currentSection)

  @Test
  def testLogoutIsStoredAsCurrentSection(): Unit =
    viewModel.select(MenuAction.Logout)
    assertEquals(MenuAction.Logout, viewModel.currentSection)