package pkg.e.ui.traits

import pkg.b.logic.Entity
import pkg.e.ui.managements.AccountManagement
import pkg.e.ui.traits.GUI
import pkg.e.ui.operations.{AccountAdd, DocumentLoad, Login}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.scene.layout.{BorderPane, GridPane, HBox, VBox}
import scalafx.collections.ObservableBuffer

trait Management extends GUI:

  override val Title: String = "Management Trait"

  var objEntity: Entity = null
  
  var xmlFilePathName: String = ""

  var pageTitle: String = "Titolo Gestione"
 
  var insertBtn: Button = _
  var updateBtn: Button = _
  var deleteBtn: Button = _
  var viewBtn: Button = _
  var printListBtn: Button = _
  var exitBtn: Button = _

  var currentId = "0"
  var userMask: Management = _
  //var maskToCall

  override def start(): Unit =
    super.start()

  def menu = new VBox {}

  def managementPageTitle = new HBox {
    styleClass += "operation-page-title"
    spacing = 10
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
    alignment = Pos.Center

    val managementPageTitleLbl = new Label(pageTitle):
      styleClass += "operation-page-title-lbl"

    children = Seq(
      managementPageTitleLbl
    )
  }

  def managementPageGrid = new BorderPane {}

  def managementPageToolBar = new HBox {
    styleClass += "operation-page-toolbar"
    spacing = 10
    padding = Insets(10)
    minHeight = 40
    prefHeight = 40
    maxHeight = 40
    alignment = Pos.CenterRight

    insertBtn = new Button("Aggiungi") {
      onAction = _ =>
        println("Call \"AccountInsert\"")
        println(s"Selected Id: $currentId")
        AccountAdd(user, userMask).start()
    }

    updateBtn = new Button("Modifica") {
      onAction = _ =>
        println("Call \"AccountUpdate\"")
    }
    
    deleteBtn = new Button("Elimina") {
      onAction = _ =>
        println("Call \"AccountDelete\"")
    }
    
    viewBtn = new Button("Visualizza") {
      onAction = _ =>
        println("Call \"AccountView\"")
    }
    
    printListBtn = new Button("Stampa Elenco") {
      onAction = _ =>
      println("Call \"AccountPrintList\"")
    }

    exitBtn = new Button("Chiudi") {
      onAction = _ =>
        parentMask.start()
    }

    children = Seq(
      insertBtn,
      updateBtn,
      deleteBtn,
      viewBtn,
      printListBtn,
      exitBtn
    )
  }

  def page = new BorderPane {
    top = managementPageTitle
    center = managementPageGrid
    bottom = managementPageToolBar
  }

  override def body = new BorderPane {
    left = menu
    center = page
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
