package pkg.a.gui

import pkg.c.data.guiStructures.{HomePageConfig, HomePageViewModel, Role}
import scalafx.application.JFXApp3
import scalafx.scene.Scene

object ProtoFlowApp extends JFXApp3:

  override def start(): Unit =
    val currentUser = "Mario Rossi"
    val role = Role.Viewer
    val config = HomePageConfig.forRole(role)
    val viewModel = HomePageViewModel(config)

    stage = new JFXApp3.PrimaryStage:
      title = "ProtoFlow"
      scene = new Scene(1100, 700):
        root = HomePageView(config, viewModel, currentUser)