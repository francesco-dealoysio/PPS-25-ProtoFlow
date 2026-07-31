package pkg.a.gui.structures


class HomePageViewModel:

  private var selectedSection: MenuAction = MenuAction.Dashboard

  def currentSection: MenuAction =
    selectedSection

  def select(action: MenuAction): Unit =
    selectedSection = action