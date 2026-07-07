package pkg.c.data.guiStructures

class HomePageViewModel(val config: HomePageConfig):

  private var selectedSection: MenuAction = MenuAction.Dashboard

  def currentSection: MenuAction =
    selectedSection

  def select(action: MenuAction): Unit =
    selectedSection = action