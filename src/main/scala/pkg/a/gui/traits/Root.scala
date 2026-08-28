package pkg.a.gui.traits

import pkg.d.util.DateTime
import scalafx.Includes.jfxNode2sfx
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.*
import pkg.a.gui.text.UiStyles.App.*
import pkg.a.gui.text.UiText.Common.*

/**
 * Provides the root application layout and manages the rendering
 * of views within the main content area.
 */
trait Root extends Common:

  /**
   * Creates the main application layout with header, menu, content area, and footer.
   * @param currentUser   the name of the current user
   * @param roleName      the role of the current user
   * @param contentArea   the area in which application views are displayed
   * @param menu          the application navigation menu
   * @param onProfileOpen the action executed when the user profile is opened
   * @return the configured root layout
   */
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

  /**
   * Renders a view in the content area if the current view can be left.
   * @param contentArea the area in which the view is rendered
   * @param view        the view to display
   */
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