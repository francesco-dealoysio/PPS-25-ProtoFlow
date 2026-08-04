package pkg.a.gui.traits

import pkg.d.util.DateTime
import scalafx.Includes.jfxNode2sfx
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.*

trait Root extends Common:

  private val applicationTitle: String = "ProtoFlow"

  protected def createRoot(currentUser: String, roleDescription: String, contentArea: StackPane, menu: VBox, onProfileOpen: () => Unit): BorderPane =

    val menuVisible = BooleanProperty(true)

    def toggleMenu(): Unit =
      menuVisible.value = !menuVisible.value

    menu.visible <== menuVisible
    menu.managed <== menuVisible

    new BorderPane:
      top =
        createHeader(
          currentUser = currentUser,
          roleDescription = roleDescription,
          onMenuToggle = () => toggleMenu()
        )

      left = menu
      center = contentArea

      bottom =
        createFooter(
          currentUser = currentUser,
          roleDescription = roleDescription,
          onProfileOpen = onProfileOpen
        )

  protected def render(contentArea: StackPane, view: => Pane): Unit =
    if canLeaveCurrentView(contentArea) then contentArea.children = Seq(view)

  private def canLeaveCurrentView(contentArea: StackPane): Boolean =
    contentArea.children.headOption
      .flatMap: node =>
        Option(
          node.delegate
            .getProperties
            .get("has-unsaved-changes")
        )
      .map:
        _.asInstanceOf[() => Boolean]
      .forall: check =>
        !check() ||
          askConfirmation(
            titleText = "Modifiche non salvate",
            header = "Vuoi uscire senza salvare?",
            content = "Le informazioni inserite o modificate non verranno mantenute."
          )

  private def createHeader(currentUser: String, roleDescription: String, onMenuToggle: () => Unit): HBox =
    val spacer = new Region
    HBox.setHgrow(spacer, Priority.Always)

    val menuButton =
      new Button("☰"):
        styleClass += "menu-toggle-button"
        onAction = _ => onMenuToggle()

    new HBox:
      alignment = Pos.CenterLeft
      styleClass += "app-header"
      children = Seq(
        menuButton,
        fieldLabel(applicationTitle, "app-title"),
        spacer,
        fieldLabel(s"$currentUser\n$roleDescription", "user-info")
      )

  private def createFooter(currentUser: String, roleDescription: String,  onProfileOpen: () => Unit): HBox =
    val dateTimeLabel = fieldLabel("", "footer-date-time")
    val userInfoLabel = fieldLabel(s"👤 $currentUser ($roleDescription)", "footer-user-info")
    
    dateTimeLabel.text <==
      DateTime.dynamicDateTimeProperty()
    
    userInfoLabel.onMouseClicked = _ =>
      onProfileOpen()
      
    new HBox:
      alignment = Pos.CenterRight
      spacing = 20
      styleClass += "app-footer"
      children = Seq(userInfoLabel, dateTimeLabel)