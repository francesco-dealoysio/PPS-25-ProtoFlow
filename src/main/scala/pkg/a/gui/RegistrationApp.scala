package pkg.a.gui

import pkg.c.data.guiStructures.RegistrationViewModel
import scalafx.application.JFXApp3
import scalafx.scene.Scene

object RegistrationApp extends JFXApp3:

  override def start(): Unit =
    val viewModel = RegistrationViewModel()

    val css = Option(getClass.getResource("/registration.css"))
      .getOrElse(sys.error("CSS non trovato: src/main/resources/registration.css"))

    stage = new JFXApp3.PrimaryStage:
      title = "ProtoFlow - Registrazione"
      scene = new Scene(900, 650):
        root = RegistrationView(
          viewModel = viewModel,
          onExit = () => stage.close()
        )
        stylesheets.add(css.toExternalForm)