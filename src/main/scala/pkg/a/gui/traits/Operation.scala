package pkg.a.gui.traits

import pkg.b.logic.Entity
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, ButtonType, ComboBox, Control, DatePicker, Label, TextArea, TextField}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.TextAlignment

trait Operation extends GUI:
  def fieldsLoad: Unit
  def valid: Boolean
  def objUpdate: Unit

  override val Title: String = "Operation Trait"

  var operation: String = ""
  var objEntity: Entity = null
  var xmlFilePathName: String = ""

  var pageTitle: String = "Titolo Operazione"
  var controls: Seq[Control] = Seq.empty[Control]
  var dirty = false

  var saveBtn: Button = _
  var resetBtn: Button = _
  var exitBtn: Button = _

  override def start(): Unit =
    super.start()

  def menu = new VBox {
    //spacing = 8
    minWidth = 160
    prefWidth = 160
    maxWidth = 160
    /*
    minWidth = 0
    prefWidth = 0
    maxWidth = 0
    */
    style = "-fx-background-color: black;"

    val dashboardBtn = new Button("Dashboard")
    val profileBtn = new Button("Profilo")
    val loadBtn = new Button("Presa in carico")
    val protocolBtn = new Button("Protocollazione")
    val archiveBtn = new Button("Archiviazione")
    val searchBtn = new Button("Ricerca")
    val logoutBtn = new Button("Logout")

    val menuItems = Seq(
      dashboardBtn,
      profileBtn,
      loadBtn,
      protocolBtn,
      archiveBtn,
      searchBtn,
      logoutBtn
    )

    menuItems.foreach(item =>
      item.maxWidth = Double.MaxValue
      item.textAlignment = TextAlignment.Left

      item.style =
        """
          |-fx-background-color: transparent;
          |-fx-border-color: transparent;
          |-fx-padding: 0;
          |-fx-text-fill: white;
          |-fx-font-size: 14px;
                """.stripMargin

      item.onMouseEntered = _ =>
        item.style = "-fx-background-color: lightgreen; -fx-text-fill: black;"

      item.onMouseExited = _ =>
        item.style = "-fx-background-color: trasparent; -fx-text-fill: white;"

      item.onMouseClicked = _ =>
        item.style = "-fx-background-color: white; -fx-text-fill: black;"
    )

    dashboardBtn.onAction = _ => ()
    profileBtn.onAction = _ => ()
    loadBtn.onAction = _ => ()
    protocolBtn.onAction = _ => ()
    archiveBtn.onAction = _ => ()
    searchBtn.onAction = _ => ()
    logoutBtn.onAction = _ => sys.exit(0)

    children = menuItems
  }

  def operationPageTitle = new HBox {
    spacing = 10
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
    style = "-fx-background-color: lightgreen;"
    alignment = Pos.Center
    children = Seq(
      new Label(pageTitle)
    )
  }

  def operationPageGrid = new GridPane {
    hgap = 16
    vgap = 8
    style = "-fx-background-color: lightblue;"
    padding = Insets(10)
    controls.zipWithIndex.foreach { (ctl, row) => add(ctl, row % 2, row / 2) }
  }

  def operationPageToolBar = new HBox {
    spacing = 10
    padding = Insets(10)
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
    style = "-fx-background-color: lightgreen;"
    alignment = Pos.CenterRight

    saveBtn = new Button("Salva") {
      disable = true
      onAction = _ =>
        if valid then
          val confirmed =
            askConfirmation(
              titleText = "Conferma aggiunta",
              header = "Confermi l'operazione?",
              content = "Il record verrà aggiunto"
            )
          if confirmed then
            objUpdate
            if objEntity.recordInsert(objEntity, xmlFilePathName) then
              new Alert(Alert.AlertType.Information) {
                title = "Esito Operazione"
                headerText = "Aggiunta Record"
                contentText = "Operazione eseguita con successo!"
              }.showAndWait()
            stage.close()
    }

    resetBtn = new Button("Ripristina") {
      disable = true
      onAction = _ =>
        fieldsLoad
        dirty = false
        saveBtn.disable = true
        disable = true
        setLabelForegroundColor(controls)
    }

    exitBtn = new Button("Chiudi") {
      onAction = _ =>
        if dirty then
          val confirmed =
            askConfirmation(
              titleText = "Conferma uscita",
              header = "Il record non è stato salvato, confermi l'operazione?",
              content = "La maschera verrà chiusa"
            )
          if confirmed then
            stage.close()
        else
          stage.close()
    }

    children = Seq(
      exitBtn,
      saveBtn,
      resetBtn
    )
  }

  def page = new BorderPane {
    top = operationPageTitle
    center = operationPageGrid
    bottom = operationPageToolBar
  }

  override def body = new BorderPane {
    left = menu
    center = page
  }

  protected def setDirtyOnChange(controlSeq: Seq[Control]): Unit =
    controlSeq.zipWithIndex.foreach {
      case (ctl, row) if (row % 2 == 1) =>
        ctl match {
          case t: (TextField | TextArea) =>
            t.text.onChange { (_, _, _) =>
              dirty = true
              saveBtn.disable = false
              resetBtn.disable = false
            }
          case v: (ComboBox[String] | DatePicker) =>
            v.value.onChange { (_, _, _) =>
              dirty = true
              saveBtn.disable = false
              resetBtn.disable = false
            }
        }
      case _ => ()
    }

  protected def setLabelForegroundColor(controlSeq: Seq[Control], bkColor: Color = Color.Black): Unit =
    controlSeq.zipWithIndex.foreach {
      case (ctl, row) if (row % 2 == 0) =>
        ctl match
          case t: Label =>
            t.textFill = bkColor
          case _ => ()
      case _ => ()
    }

  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content

    dialog
      .showAndWait()
      .contains(ButtonType.OK)