package pkg.a.gui.navigation

import scalafx.scene.layout.Pane

private[gui] object NavigationFlows:

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