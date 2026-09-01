package pkg.a.gui.navigation

import scalafx.scene.layout.Pane

object NavigationFlows:

  /**
   * Starts a CRUD navigation flow between management, creation, and editing views.
   * @param navigator      the navigator used to change views
   * @param managementView the management view factory
   * @param addView        the creation view factory
   * @param editView       the editing view factory
   * @tparam T the type of the managed entity
   */
  def showCrud[T](
                   navigator: HomeNavigator,
                   managementView: (() => Unit, T => Unit, () => Unit) => Pane,
                   addView: (() => Unit, () => Unit) => Pane,
                   editView: (T, () => Unit, () => Unit) => Pane
                 ): Unit =
    def management(): Unit =
      val back = () => management()

      navigator.show(
        managementView(
          () => navigator.show(addView(back, back)),
          selected => navigator.show(editView(selected, back, back)),
          () => navigator.dashboard()
        )
      )

    management()

  /**
   * Starts a navigation flow between a management view and a creation view.
   * @param navigator      the navigator used to change views
   * @param managementView the management view factory
   * @param addView        the creation view factory
   */
  def showCreateFlow(
                      navigator: HomeNavigator,
                      managementView: (() => Unit, () => Unit) => Pane,
                      addView: (() => Unit, () => Unit) => Pane
                    ): Unit =
    def management(): Unit =
      val back = () => management()

      navigator.show(
        managementView(
          () => navigator.show(addView(back, back)),
          () => navigator.dashboard()
        )
      )

    management()

  /**
   * Starts a navigation flow from a management view to a selected item view.
   * @param navigator      the navigator used to change views
   * @param managementView the management view factory
   * @param selectedView   the view factory for the selected item
   * @tparam T the type of the selectable entity
   */
  def showSelectionFlow[T](
                            navigator: HomeNavigator,
                            managementView: (T => Unit, () => Unit) => Pane,
                            selectedView: (T, () => Unit) => Pane
                          ): Unit =
    def management(): Unit =
      val back = () => management()

      navigator.show(
        managementView(
          selected => navigator.show(selectedView(selected, back)),
          () => navigator.dashboard()
        )
      )

    management()