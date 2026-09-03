package pkg.a.gui.navigation

import scalafx.Includes.jfxNode2sfx
import scalafx.scene.layout.{Pane, StackPane}

private[gui] object HomeNavigator:
  final case class ViewNavigationState(hasUnsavedChanges: () => Boolean)

private[gui] final class HomeNavigator(contentArea: StackPane, dashboardFactory: () => Pane, confirmUnsavedChanges: () => Boolean):
  import HomeNavigator.ViewNavigationState

  def show(view: => Pane): Unit =
    if canLeaveCurrentView then
      contentArea.children = Seq(view)

  def dashboard(): Unit =
    show(dashboardFactory())

  private def canLeaveCurrentView: Boolean =
    currentViewState.forall: state =>
      !state.hasUnsavedChanges() || confirmUnsavedChanges()

  private def currentViewState: Option[ViewNavigationState] =
    contentArea.children.headOption
      .flatMap: node =>
        Option(node.delegate.getUserData)
          .collect:
            case state: ViewNavigationState => state