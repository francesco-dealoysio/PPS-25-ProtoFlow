package pkg.e.ui.traits

import pkg.b.logic.Entity
import pkg.e.ui.traits.GUI
import pkg.e.ui.homepages.OperatorHomepage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.{BorderPane, GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.TextAlignment

trait Operation extends GUI:
  def fieldsLoad: Unit
  def valid: Boolean
  def objUpdate: Unit

  override val Title: String = "Operation Trait"

  var operationType: String = ""
  var objEntity: Entity = null
  var xmlFilePathName: String = ""

  var pageTitle: String = "Titolo Operazione"
  var controls: Seq[Control] = Seq.empty[Control]
  var dirty = false

  var execBtn: Button = _
  var resetBtn: Button = _
  var exitBtn: Button = _

  override def start(): Unit =
    super.start()

  def menu = new VBox {
    this.styleClass += "sidebar"

    //spacing = 8
    minWidth = 160
    prefWidth = 160
    maxWidth = 160

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
      item.styleClass += "sidebar-button"
      item.maxWidth = Double.MaxValue
      item.textAlignment = TextAlignment.Left
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
    styleClass += "operation-page-title"
    spacing = 10
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
    //style = "-fx-background-color: grey;"
    alignment = Pos.Center

    val operationPageTitleLbl = new Label(pageTitle):
      styleClass += "operation-page-title-lbl"

    children = Seq(
      operationPageTitleLbl
    )
  }

  def operationPageGrid = new GridPane {
    hgap = 16
    vgap = 8
    padding = Insets(10)
    controls.zipWithIndex.foreach { (ctl, row) => add(ctl, row % 2, row / 2) }
  }

  def operationPageToolBar = new HBox {
    styleClass += "operation-page-toolbar"
    spacing = 10
    padding = Insets(10)
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
    alignment = Pos.CenterRight

    var execBtnText = "Salva"
    var askConfirmationText = ""
    var operationText = ""

    operationType match
      /*
      case "login" =>
        execBtnText = "Accedi"
      */
      case "insert" =>
        askConfirmationText = "aggiunto"
        operationText = "Aggiunta"
      case "update" =>
        askConfirmationText = "modificato"
        operationText = "Modifica"
      case "delete" =>
        execBtnText = "Elimina"
        askConfirmationText = "eliminato"
        operationText = "Eliminazione"

    execBtn = new Button(execBtnText) {
      disable = true
      onAction = _ =>
        if valid then

          var confirmed = true
          if operationType != "login" then
            confirmed =
              askConfirmation(
                titleText = "Richiesta conferma",
                header = "Confermi l'operazione?",
                content = "Il record verrà " + askConfirmationText
              )

          val operationResult = false
          if confirmed then
            val operationResult = operationType match
              /*
              case "login" =>
                println(user.getSurname)
                OperatorHome(user).main(Array.empty)
                false
              */
              case "insert" =>
                objUpdate
                objEntity.recordInsert(objEntity, xmlFilePathName)
              case "update" =>
                objUpdate
                objEntity.recordUpdate(objEntity, xmlFilePathName)
              case "delete" =>
                val id = objEntity.getClass.getDeclaredMethod("getId").invoke(objEntity).toString
                objEntity.recordDelete(id, xmlFilePathName)
              case _ => false

            if operationResult then
              new Alert(Alert.AlertType.Information) {
                title = "Esito Operazione"
                headerText = operationText + " Record"
                contentText = "Operazione eseguita con successo!"
              }.showAndWait()
            parentMask.start()
    }

    resetBtn = new Button("Ripristina") {
      disable = true
      onAction = _ =>
        fieldsLoad
        dirty = false
        execBtn.disable = true
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
          if confirmed then {
            if operationType == "login" then
              sys.exit()
            else
              parentMask.start()
          }
        else {
          if operationType == "login" then
            sys.exit()
          else
            parentMask.start()
        }
    }

    children = Seq(
      exitBtn,
      execBtn,
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
              execBtn.disable = false
              resetBtn.disable = false
            }
          case v: (ComboBox[String] | DatePicker) =>
            v.value.onChange { (_, _, _) =>
              dirty = true
              execBtn.disable = false
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