package pkg.a.gui.traits

import pkg.d.util.DateTime
import scalafx.Includes.jfxNode2sfx
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.*
import pkg.a.gui.text.UiStyles.App.*
import pkg.a.gui.text.UiText.Common.*


trait Root extends Common:

  protected def createRoot(currentUser: String, roleName: String, contentArea: StackPane, menu: VBox, onProfileOpen: () => Unit): BorderPane =

    val menuVisible = BooleanProperty(true)

    def toggleMenu(): Unit =
      menuVisible.value = !menuVisible.value

    menu.visible <== menuVisible
    menu.managed <== menuVisible

    new BorderPane:
      top =
        createHeader(
          currentUser = currentUser,
          roleName = roleName,
          onMenuToggle = () => toggleMenu()
        )

      left = menu
      center = contentArea

      bottom =
        createFooter(
          currentUser = currentUser,
          roleName = roleName,
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
            titleText = Dialogs.UnsavedChanges.Title,
            header = Dialogs.UnsavedChanges.Header,
            content = Dialogs.UnsavedChanges.Content
          )

  private def createHeader(currentUser: String, roleName: String, onMenuToggle: () => Unit): HBox =
    val spacer = new Region
    HBox.setHgrow(spacer, Priority.Always)

    val menuButton =
      new Button(MenuIcon):
        styleClass += MenuToggleButtonStyle
        onAction = _ => onMenuToggle()

    new HBox:
      alignment = Pos.CenterLeft
      styleClass += HeaderStyle
      children = Seq(
        menuButton,
        fieldLabel(ApplicationName, TitleStyle),
        spacer,
        fieldLabel(headerUserInfo(currentUser, roleName), UserInfoStyle)
      )

  private def createFooter(currentUser: String, roleName: String, onProfileOpen: () => Unit): HBox =
    val dateTimeLabel = fieldLabel("", FooterDateTimeStyle)
    val userInfoLabel = fieldLabel(footerUserInfo(currentUser, roleName), FooterUserInfoStyle)
    
    dateTimeLabel.text <==
      DateTime.dynamicDateTimeProperty()
    
    userInfoLabel.onMouseClicked = _ =>
      onProfileOpen()
      
    new HBox:
      alignment = Pos.CenterRight
      spacing = 20
      styleClass += FooterStyle
      children = Seq(userInfoLabel, dateTimeLabel)