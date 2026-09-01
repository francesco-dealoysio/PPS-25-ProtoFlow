package pkg.a.gui.navigation

import scalafx.Includes.jfxNode2sfx
import scalafx.scene.layout.{Pane, StackPane}

final class HomeNavigator(contentArea: StackPane, dashboardFactory: () => Pane, confirmUnsavedChanges: () => Boolean):

  def show(view: => Pane): Unit =
    if canLeaveCurrentView then
      contentArea.children = Seq(view)

  def dashboard(): Unit =
    show(dashboardFactory())

  private def canLeaveCurrentView: Boolean =
    contentArea.children.headOption
      .flatMap: node =>
        Option(
          node.delegate
            .getProperties
            .get("has-unsaved-changes")
        )
      .map:
        _.asInstanceOf[() => Boolean]
      .forall: hasUnsavedChanges =>
        !hasUnsavedChanges() || confirmUnsavedChanges()