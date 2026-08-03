package pkg.a.gui.traits

import scalafx.application.JFXApp3
import pkg.b.logic.Account
import pkg.d.util.DateTime
import scalafx.scene.layout.BorderPane
import scalafx.scene.Scene
import scalafx.scene.layout.HBox
import scalafx.geometry.{Pos, Insets}
import scalafx.scene.control.{Label}
import scalafx.scene.image.Image
//import scalafx.scene.control._
/*
import javafx.collections.FXCollections
import pkg.d.util.{DateTime, IdGen}
import pkg.d.util.Util.{inDocumentsFilePathName, inIdsFilePathName, localDate, localTime}
import scalafx.scene.image.Image
//import scalafx.scene.control.{Alert, Button, ButtonType, ComboBox, DatePicker, Label, TextArea, TextField}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafx.scene.control.Control
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}
import scalafx.scene.paint.Color
import scalafx.scene.text.TextAlignment
import scalafx.application.Platform
*/
trait GUI extends JFXApp3:

  val user: Account

  def Title: String = "GUI Trait"
  def Width: Int = 800
  def Height: Int = 600

  def body = new BorderPane

  override def start(): Unit =

    stage = new JFXApp3.PrimaryStage:

      title = Title
      width = Width
      height = Height
      resizable = false
      //alwaysOnTop = true
      centerOnScreen()

      onCloseRequest = event =>
        event.consume() // Prevents the window from closing

      scene = new Scene:

        val header = new HBox {
          spacing = 10
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightgray;"
          alignment = Pos.CenterLeft
          padding = Insets(10)
          children = Seq(
            new Label("☰"),
            new Label("Protoflow")
          )
        }

        val footer = new HBox {
          spacing = 10
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightgray;"
          alignment = Pos.CenterRight
          padding = Insets(10)
          val dateTime = new Label("")
          dateTime.text <== DateTime.dynamicDateTimeProperty()
          children = Seq(
            new Label(user.getName + " " + user.getSurname + " (" + user.getRole + ")"),
            dateTime
          )
        }

        root = new BorderPane {
          style = "-fx-background-color: lightgray;"
          padding = Insets(5)
          top = header
          center = body
          bottom = footer
        }

    Option(getClass.getResourceAsStream("/img/message.jpg"))
      .foreach(stream => stage.icons.add(new Image(stream)))
/*
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
      //clearBtn.onMouseEntered = _ => stage.fullScreen = true
      //clearBtn.onMouseExited = _ =>
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
*/

/*
class EntityAdd(val user: Account) extends Operation:
  import pkg.b.logic.{Entity, Account, LoadedDocument}
  import scalafx.scene.control.Control
  import java.time.LocalDate

  override val Title: String = "Aggiunta Entità"
  override val Width = 780
  override val Height = 600
  pageTitle = "Aggiunta Entità"

  operation = "insert"
  objEntity = new LoadedDocument
  xmlFilePathName = inDocumentsFilePathName("loaded.xml")

  var dateLbl: Label = _
  var dateFld: DatePicker = _
  var protocolLbl: Label = _
  var protocolFld: TextField = _
  var typeLbl: Label = _
  var typeFld: ComboBox[String] = _
  var senderLbl: Label = _
  var senderFld: TextField = _
  var recipientLbl: Label = _
  var recipientFld: TextField = _
  var subjectLbl: Label = _
  var subjectFld: TextField = _
  var remarksLbl: Label = _
  var remarksFld: TextArea = _

  override def start(): Unit =

    dateLbl = new Label("Data *")
    dateFld = new DatePicker(LocalDate.now())
    protocolLbl = new Label("Protocollo")
    protocolFld = new TextField()
    typeLbl = new Label("Tipo *")
    typeFld = new ComboBox[String]()
    senderLbl = new Label("Mittente *")
    senderFld = new TextField()
    recipientLbl = new Label("Destinatario *")
    recipientFld = new TextField()
    subjectLbl = new Label("Oggetto *")
    subjectFld = new TextField()
    remarksLbl = new Label("Note")
    remarksFld = new TextArea()

    controls = Seq(
      dateLbl,
      dateFld,
      protocolLbl,
      protocolFld,
      typeLbl,
      typeFld,
      senderLbl,
      senderFld,
      recipientLbl,
      recipientFld,
      subjectLbl,
      subjectFld,
      remarksLbl,
      remarksFld
    )

    fieldsLoad

    setDirtyOnChange(controls)

    super.start()

  def fieldsLoad: Unit =

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    Try(LocalDate.parse(objEntity.asInstanceOf[LoadedDocument].getDocumentDate, formatter)) match {
      case Success(date) => dateFld.value = date
      case Failure(ex) =>
        LocalDate.parse(LocalDate.now().format(formatter), formatter) match
          case incorrectDate => dateFld.value = incorrectDate
    }
    dateFld.editable = false

    protocolFld.text = objEntity.asInstanceOf[LoadedDocument].getDocumentProtocol

    typeFld.setItems(FXCollections.observableArrayList("Lettera", "Email", "Plico"))
    typeFld.getSelectionModel.select(objEntity.asInstanceOf[LoadedDocument].getDocumentType)
    typeFld.promptText = "Selezionare un tipo"
    typeFld.value.onChange { (_, _, _) =>
      dirty = true
      saveBtn.disable = false
      resetBtn.disable = false
    }

    senderFld.text = objEntity.asInstanceOf[LoadedDocument].getSender

    recipientFld.text = objEntity.asInstanceOf[LoadedDocument].getRecipient

    subjectFld.text = objEntity.asInstanceOf[LoadedDocument].getSubject

    remarksFld.text = objEntity.asInstanceOf[LoadedDocument].getRemarks
    remarksFld.promptText = "Type something here..."
    remarksFld.wrapText = true
    remarksFld.prefRowCount = 10

  override def valid: Boolean =
    var esito: Boolean = true
    var errorMessage: String = ""
    var value: String = ""
    var counter: Int = 0

    checkEmptyField(typeFld.value.value, typeLbl, typeFld)
    checkEmptyField(senderFld.text.value, senderLbl, senderFld)
    checkEmptyField(recipientFld.text.value, recipientLbl, recipientFld)
    checkEmptyField(subjectFld.text.value, subjectLbl, subjectFld)

    def checkEmptyField(value: String, lbl: Label, fld: Control): Unit =
      if (value == "") then
        errorMessage += lbl.text.value + ": <campo obbligatorio>\n"
        lbl.style = "-fx-text-fill: red;"
        if counter == 0 then
          fld.requestFocus()
        counter += 1;
        esito = false
      else
        lbl.style = "-fx-text-fill: black;"

    if (!"".equals(errorMessage)) then
      new Alert(Alert.AlertType.Information) {
        val found = if counter > 1 then "Trovati" else "Trovato"
        val error = if counter > 1 then "errori" else "errore"
        title = "Riepilogo errori"
        headerText = found + " " + counter + " " + error + "!"
        contentText = errorMessage
      }.showAndWait()

    esito

  override def objUpdate: Unit =
    objEntity.asInstanceOf[LoadedDocument].setId(IdGen(inIdsFilePathName("loadedDocumentId")))
    val formatted = dateFld.value.value match
      case date => date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    objEntity.asInstanceOf[LoadedDocument].setDocumentDate(formatted)
    objEntity.asInstanceOf[LoadedDocument].setDocumentProtocol(protocolFld.text.value.trim)
    objEntity.asInstanceOf[LoadedDocument].setDocumentType(typeFld.value.value)
    objEntity.asInstanceOf[LoadedDocument].setSender(senderFld.text.value.trim)
    objEntity.asInstanceOf[LoadedDocument].setRecipient(recipientFld.text.value.trim)
    objEntity.asInstanceOf[LoadedDocument].setSubject(subjectFld.text.value.trim)
    objEntity.asInstanceOf[LoadedDocument].setRemarks(remarksFld.text.value.trim)
    objEntity.asInstanceOf[LoadedDocument].setState("Loaded")
    objEntity.asInstanceOf[LoadedDocument].setProcessedDate(localDate)
    objEntity.asInstanceOf[LoadedDocument].setProcessedTime(localTime)
    objEntity.asInstanceOf[LoadedDocument].setProcessedBy(user.getName + " " + user.getSurname)

@main def tryEntityAdd: Unit =
  println("")
  import pkg.b.logic.Account
  import pkg.d.util.Util.md5

  val account1 = Account(
    "1",
    "de Aloysio",
    "Francesco",
    "francesco.dealoysio@studio.unibo.it",
    "06/11111111",
    "admin",
    "presidenza",
    "presidente",
    "frank",
    md5("topolino")
  )

  EntityAdd(account1).main(Array.empty)
*/