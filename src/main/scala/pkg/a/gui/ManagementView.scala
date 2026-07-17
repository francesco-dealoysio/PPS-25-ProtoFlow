package pkg.a.gui

import scalafx.scene.control.{Alert, ButtonType}

trait ManagementView extends AppView:

  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content

    dialog
      .showAndWait()
      .contains(ButtonType.OK)
