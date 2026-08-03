package pkg.a.gui.views

import pkg.b.logic.{Account, Entity, LoadedDocument}
import pkg.d.util.Util.*
import pkg.d.util.IdGen
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField, ComboBox, TextArea, DatePicker, Alert, ButtonType}
import scalafx.scene.control.Control
import scalafx.scene.image.Image
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javafx.collections.FXCollections
import scala.util.{Try, Success, Failure}
import scalafx.stage.{Stage, StageStyle, Modality}


import scala.swing.{Component, GridBagPanel}

class LoadDocumentView(user: Account) extends JFXApp3:
//object LoadDocumentView extends JFXApp3:

  //def apply(onExit: () => Unit = () => ()): Unit =

  // onOpen
  override def start(): Unit =

    println("start(): Hello!") // test

    // Represents the main application window
    stage = new JFXApp3.PrimaryStage:

      title.value = "Presa in carico documento"
      width = 800
      height = 600
      //fullScreen = true
      resizable = false
      //alwaysOnTop = true
      //centerOnScreen()
      //maximized = true

      //initStyle(StageStyle.Transparent)
      //initModality(Modality.WINDOW_MODAL) // not possible for PrimaryStage
      // Intercept the close request and consume it
      onCloseRequest = event =>
        println("Close button pressed — action suppressed.")
        event.consume() // Prevents the window from closing

      // Contains the UI elements (controls, layouts, etc.).
      scene = new Scene(800, 600) {

        var dirty = false

        var objEntity: Entity = new LoadedDocument

        //println(objEntity.getClass)
        //objEntity.getClass.getDeclaredFields.foreach(f => println(f.getName))

        // dati per provare fieldsLoad
        objEntity.asInstanceOf[LoadedDocument].setDocumentDate("16-09-1990")
        objEntity.asInstanceOf[LoadedDocument].setDocumentProtocol("2024/1023/INPS.CUD")
        objEntity.asInstanceOf[LoadedDocument].setDocumentType("Email")
        objEntity.asInstanceOf[LoadedDocument].setSender("INPS")
        objEntity.asInstanceOf[LoadedDocument].setRecipient("UNUCI - Amministrazione")
        objEntity.asInstanceOf[LoadedDocument].setSubject("Decreto pensionistico 2025")
        objEntity.asInstanceOf[LoadedDocument].setRemarks("Attenzione Tesoriere")

        //fill = LightGreen

        // fields
        val dateLbl = new Label("Data:")
        var dateFld = new DatePicker(LocalDate.now())
        val protocolLbl = new Label("Protocollo:")
        var protocolFld = new TextField()
        val typeLbl = new Label("Tipo:")
        var typeFld = new ComboBox[String]()
        val senderLbl = new Label("Mittente:")
        var senderFld  = new TextField()
        val recipientLbl = new Label("Destinatario:")
        var recipientFld = new TextField()
        val subjectLbl = new Label("Oggetto:")
        var subjectFld = new TextField()
        val remarksLbl = new Label("Note:")
        var remarksFld = new TextArea()

        fieldsLoad

        // commandBar controls
        val saveBtn = new Button("Salva")
        saveBtn.disable = true
        saveBtn.onAction = _ =>
          println("Salva")
          // inserire la verifica di correttezza formale
          val confirmed =
            askConfirmation(
              titleText = "Conferma aggiunta",
              header = "Confermi l'operazione?",
              content = "Il record verrà aggiunto"
            )

          if confirmed then
            updateObj
            if LoadedDocument().recordInsert(objEntity, inDocumentsFilePathName("loaded.xml")) then
            // inserire un messaggio di suggesso dell'operazione
            close()

        val resetBtn = new Button("Ripristina")
        resetBtn.disable = true
        resetBtn.onAction = _ =>
          println("Ripristina")
          fieldsLoad
          dirty = false
          saveBtn.disable = true
          resetBtn.disable = true
          dateFld.requestFocus()
        //clearBtn.onMouseEntered = _ => stage.fullScreen = true
        //clearBtn.onMouseExited = _ =>

        val exitBtn = new Button("Chiudi") {
          onAction = _ =>
            if dirty then
              val confirmed =
                askConfirmation(
                  titleText = "Conferma uscita",
                  header = "Il record non è stato salvato, confermi l'operazione?",
                  content = "La maschera verrà chiusa"
                )
              if confirmed then
                close()
            else
              close()
        }

       val header = new HBox {
          spacing = 10
          minHeight = 80
          prefHeight = 80
          maxHeight = 80
          style = "-fx-background-color: lightblue;" // CSS inline style
          alignment = Pos.Center
          children = Seq(
            new Label("Header"),
            new Label("Logo"),
            new Label("Protoflow")
          )
        }

        val menu = new VBox {
          spacing = 8
          minWidth = 160
          prefWidth = 160
          maxWidth = 160
          style = "-fx-background-color: grey;" // CSS inline style
          alignment = Pos.TopCenter
          children = Seq(
            new Label("Menu"),
            new Label("Option A"),
            new Label("Option B")
          )
        }

        val pageTitle = new HBox {
          spacing = 10
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightgreen;" // CSS inline style
          alignment = Pos.Center
          children = Seq(
            new Label("Page Title: Presa in carico documento")
          )
        }

        val pageGrid = new GridPane {
          hgap = 16
          vgap = 8
          //maxWidth = 700
          style = "-fx-background-color: lightblue;" // CSS inline style
          padding = Insets(10)

          /*
          var controls2: Seq[(Control, Control)] = Seq(
            (dateLbl, dateFld),
            (protocolLbl, protocolFld),
            (typeLbl, typeFld),
            (senderLbl, senderFld),
            (recipientLbl, recipientFld),
            (subjectLbl, subjectFld),
            (remarksLbl, remarksFld),
          )

          controls2.zipWithIndex.foreach { case ((lbl, fld), row) =>
            add(lbl, 0, row)
            add(fld, 1, row)
          }
          */

          var controls: Seq[Control] = Seq(
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

          controls.zipWithIndex.foreach { (ctl, row) => add(ctl, row % 2, row / 2) }
          /*
          add(dateLbl, 0, 0)
          add(dateFld, 1, 0)
          add(protocolLbl, 0, 2)
          add(protocolFld, 1, 2)
          add(typeLbl, 0, 3)
          add(typeFld, 1, 3)
          add(senderLbl, 0, 4)
          add(senderFld, 1, 4)
          add(recipientLbl, 0, 5)
          add(recipientFld, 1, 5)
          add(subjectLbl, 0, 6)
          add(subjectFld, 1, 6)
          add(remarksLbl, 0, 7)
          add(remarksFld, 1, 7)
          */

          // set dirty flag
          controls.zipWithIndex.foreach {
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

        }

        val pageToolBar = new HBox {
          spacing = 10
          padding = Insets(10)
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightgreen;" // CSS inline style
          alignment = Pos.CenterRight
          children = Seq(
            new Label("Page CommandBar"),
            exitBtn,
            saveBtn,
            resetBtn
          )
        }

        val page = new BorderPane {
          top = pageTitle
          center = pageGrid
          bottom = pageToolBar
        }

        val body = new BorderPane {
          left = menu
          center = page
        }

        val footer = new HBox {
          spacing = 10
          minHeight = 40
          prefHeight = 40
          maxHeight = 40
          style = "-fx-background-color: lightblue;"
          alignment = Pos.Center
          children = Seq(
            new Label("Footer: User (Role) + DateTime"),
          )
        }

        root = new BorderPane {
          //padding = Insets(20)
          top = header
          center = body
          bottom = footer
        }

        protected def fieldsLoad: Unit =

          val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
          Try(LocalDate.parse(objEntity.asInstanceOf[LoadedDocument].getDocumentDate, formatter)) match {
            case Success(date) => dateFld.value = date
            case Failure(ex) =>
              LocalDate.parse("01-01-0001", formatter) match
                case incorrectDate => dateFld.value = incorrectDate
          }
          //dateFld.editable = false

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

        //def valid: Boolean =

        protected def updateObj: Unit =
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
      }

    Option(getClass.getResourceAsStream("/img/message.jpg"))
      .foreach(stream => stage.icons.add(new Image(stream)))

    stage.show()

  // onClose
  override def stopApp(): Unit =
    println("stopApp(): Bye!") // test
    super.stopApp()

  protected def askConfirmation(titleText: String, header: String, content: String): Boolean =
    val dialog =
      new Alert(Alert.AlertType.Confirmation):
        title = titleText
        headerText = header
        contentText = content

    dialog
      .showAndWait()
      .contains(ButtonType.OK)


@main def tryLoadDocomentView: Unit =
  //LoadDocumentView.main(Array.empty)
  val account1 = Account(
    "1",
    "de aloysio",
    "francesco",
    "francesco.dealoysio@studio.unibo.it",
    "06/11111111",
    "admin",
    "presidenza",
    "presidente",
    "frank",
    md5("topolino")
  )
  new LoadDocumentView(account1).main(Array.empty)


