package pkg.e.ui.operations

import pkg.b.logic.Account
import pkg.d.util.Util.{inDatabaseFilePathName, inDocumentsFilePathName, inIdsFilePathName}
import pkg.e.ui.homepages.*
import pkg.e.ui.traits.{GUI, Operation}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.*
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{HBox, VBox}
import scala.util.{Failure, Success, Try}
import pkg.d.util.Util.cipher

class Login(val user: Account, val parentMask: GUI) extends Operation:

  override val Title: String = "Login"
  override val Width = 300
  override val Height = 275

  pageTitle = "Login"
  operationType = "login"
  objEntity = new Account
  xmlFilePathName = inDatabaseFilePathName("account.xml")

  var usernameLbl: Label = _
  var usernameFld: TextField = _
  var passwordLbl: Label = _
  var passwordFld: PasswordField = _

  override def menu = new VBox {}

  override def operationPageToolBar = new HBox {
    styleClass += "operation-page-toolbar"
    spacing = 10
    padding = Insets(10)
    val defaultHeight = 40
    minHeight = defaultHeight
    prefHeight = defaultHeight
    maxHeight = defaultHeight
    alignment = Pos.Center

    execBtn = new Button("Accedi") {
      disable = true
      onAction = _ =>
        if valid then {

          val username = usernameFld.text.value.trim
          val password = passwordFld.getText

          val found = Account().getRecordsByFilter[Account](a =>
            a.getUsername == username &&
            a.getPassword == cipher(password)
          )

          if (found.size > 0) then
            found.head.getRole match {
              case "admin" =>
                AdminHomepage(found.head).start()
              case "oper" =>
                OperatorHomepage(found.head).start()
              case "viewer" =>
                ViewerHomepage(found.head).start()
              case _ =>
            }
          else {
            new Alert(Alert.AlertType.Error) {
              title = "Errori"
              headerText = "Accesso negato!"
              contentText = "Credenziali errate!"
            }.showAndWait()
            usernameFld.requestFocus()
          }

        }
    }

    resetBtn = new Button("Ripristina") {
      disable = true
      onAction = _ =>
        fieldsLoad
        dirty = false
        execBtn.disable = true
        disable = true
        setLabelForegroundColor(controls)
        usernameFld.requestFocus()
    }

    exitBtn = new Button("Chiudi") {
      onAction = _ =>
        sys.exit()
    }

    val registrationBtn = new Button("Registrati") {
      onAction = _ =>
        println("Registrazione")
    }

    children = Seq(
      execBtn,
      registrationBtn,
      resetBtn,
      exitBtn
    )
  }

  override def start(): Unit =

    usernameLbl = new Label("Username *")
    usernameFld = new TextField()
    passwordLbl = new Label("Password *")
    passwordFld = new PasswordField()

    controls = Seq(
      usernameLbl,
      usernameFld,
      passwordLbl,
      passwordFld
    )

    fieldsLoad

    setDirtyOnChange(controls)

    super.start()

    usernameFld.requestFocus()

  def fieldsLoad: Unit =

    usernameFld.text = objEntity.asInstanceOf[Account].getUsername
    usernameFld.prefWidth = 180
    usernameFld.onKeyPressed = event =>
      if event.code == KeyCode.Enter then
        passwordFld.requestFocus()

    passwordFld.text = objEntity.asInstanceOf[Account].getPassword
    passwordFld.onKeyPressed = event =>
      if event.code == KeyCode.Enter then
        execBtn.fire()

  override def valid: Boolean =
    var esito: Boolean = true
    var errorMessage: String = ""
    var value: String = ""
    var counter: Int = 0

    checkEmptyField(usernameFld.text.value, usernameLbl, usernameFld)
    checkEmptyField(passwordFld.text.value, passwordLbl, passwordFld)

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
      new Alert(Alert.AlertType.Error) {
        val found = if counter > 1 then "Trovati" else "Trovato"
        val error = if counter > 1 then "errori" else "errore"
        title = "Errori"
        headerText = found + " " + counter + " " + error + "!"
        contentText = errorMessage
      }.showAndWait()

    esito

  override def objUpdate: Unit = {}

@main def tryLogin: Unit =
  println("")