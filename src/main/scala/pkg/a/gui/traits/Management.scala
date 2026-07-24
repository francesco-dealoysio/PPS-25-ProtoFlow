package pkg.a.gui.traits

import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, Priority, VBox}

trait Management extends Root:


  protected def printButton(action: () => Unit, text: String = "Stampa"): Button =
    secondaryButton(text = text, action = action)

  protected def managementPage(
                                rootStyle: String,
                                pageChildren: Seq[Node],
                                growNode: Option[Node] = None,
                                spacingValue: Double = 18,
                                paddingValue: Insets = Insets(20)
                              ): BorderPane =

    val content =
      new VBox:
        spacing = spacingValue
        padding = paddingValue

        growNode.foreach: node =>
          VBox.setVgrow(node, Priority.Always)

        children = pageChildren

    new BorderPane:
      styleClass += rootStyle
      center = content