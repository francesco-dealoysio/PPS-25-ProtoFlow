package pkg.e.ui.operations

import javafx.collections.FXCollections
import pkg.b.logic.Account
import pkg.d.util.DateTime.{localDate, localTime}
import pkg.d.util.IdGen
import pkg.d.util.Util.{cipher, inDatabaseFilePathName, inDocumentsFilePathName, inIdsFilePathName}
import pkg.e.ui.traits.{GUI, Homepage, Management, Operation}
import scalafx.scene.control.*
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.VBox
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}

class AccountAdd(val user: Account, val parentMask: Management) extends Operation:

  override val Title: String = "Aggiunta Account Utente"
  pageTitle = Title

  operationType = "insert"

  objEntity = new Account

  xmlFilePathName = inDatabaseFilePathName("accounts.xml")

  override def menu: VBox = parentMask.menu

  var surnameLbl: Label = _
  var surnameFld: TextField = _
  var nameLbl: Label = _
  var nameFld: TextField = _
  var emailLbl: Label = _
  var emailFld: TextField = _
  var phoneLbl: Label = _
  var phoneFld: TextField = _
  var roleLbl: Label = _
  var roleFld: ComboBox[String] = _
  var areaLbl: Label = _
  var areaFld: ComboBox[String] = _
  var assignmentLbl: Label = _
  var assignmentFld: TextField = _
  var usernameLbl: Label = _
  var usernameFld: TextField = _
  var passwordLbl: Label = _
  var passwordFld: PasswordField = _

  override def start(): Unit =

    surnameLbl = new Label("Cognome *")
    surnameFld = new TextField()
    nameLbl = new Label("Nome *")
    nameFld = new TextField()
    emailLbl = new Label("Email *")
    emailFld = new TextField()
    phoneLbl = new Label("Telefono *")
    phoneFld = new TextField()
    roleLbl = new Label("Ruolo *")
    roleFld = new ComboBox[String]()
    areaLbl = new Label("Area *")
    areaFld = new ComboBox[String]()
    assignmentLbl = new Label("Incarico *")
    assignmentFld = new TextField()
    usernameLbl = new Label("Username *")
    usernameFld = new TextField()
    passwordLbl = new Label("Password *")
    passwordFld = new PasswordField()

    controls = Seq(
      surnameLbl,
      surnameFld,
      nameLbl,
      nameFld,
      emailLbl,
      emailFld,
      phoneLbl,
      phoneFld,
      roleLbl,
      roleFld,
      areaLbl,
      areaFld,
      assignmentLbl,
      assignmentFld,
      usernameLbl,
      usernameFld,
      passwordLbl,
      passwordFld
    )

    fieldsLoad

    setDirtyOnChange(controls)

    super.start()

    surnameFld.requestFocus()

  def fieldsLoad: Unit =

    surnameFld.text = objEntity.asInstanceOf[Account].getSurname
    surnameFld.onKeyPressed = event =>
      if event.code == KeyCode.Enter then
        nameFld.requestFocus()

    nameFld.text = objEntity.asInstanceOf[Account].getName
    emailFld.text = objEntity.asInstanceOf[Account].getEmail
    phoneFld.text = objEntity.asInstanceOf[Account].getPhone

    // caricare la combobox dal file xml
    roleFld.setItems(FXCollections.observableArrayList("admin", "Operatore", "Visualizzatore"))
    roleFld.getSelectionModel.select(objEntity.asInstanceOf[Account].getRole)
    roleFld.promptText = "Selezionare un ruolo"

    // caricare la combobox dal file xml
    areaFld.setItems(FXCollections.observableArrayList("Amministrazione", "Personale", "Segreteria"))
    areaFld.getSelectionModel.select(objEntity.asInstanceOf[Account].getArea)
    areaFld.promptText = "Selezionare un'area"

    assignmentFld.text = objEntity.asInstanceOf[Account].getAssignment
    usernameFld.text = objEntity.asInstanceOf[Account].getUsername
    passwordFld.text = objEntity.asInstanceOf[Account].getPassword

  override def valid: Boolean =
    var esito: Boolean = true
    var errorMessage: String = ""
    var value: String = ""
    var counter: Int = 0

    checkEmptyField(surnameFld.text.value, surnameLbl, surnameFld)
    checkEmptyField(nameFld.text.value, nameLbl, nameFld)
    // verificare il formato
    checkEmptyField(emailFld.text.value, emailLbl, emailFld)
    // verificare il formato
    checkEmptyField(phoneFld.text.value, phoneLbl, phoneFld)
    checkEmptyField(roleFld.value.value, roleLbl, roleFld)
    checkEmptyField(areaFld.value.value, areaLbl, areaFld)
    checkEmptyField(assignmentFld.text.value, assignmentLbl, assignmentFld)
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

  override def objUpdate: Unit =
    objEntity.asInstanceOf[Account].setId(IdGen(inIdsFilePathName("loadedDocumentId")))
    objEntity.asInstanceOf[Account].setSurname(surnameFld.text.value.trim)
    objEntity.asInstanceOf[Account].setName(nameFld.text.value.trim)
    objEntity.asInstanceOf[Account].setEmail(emailFld.text.value.trim)
    objEntity.asInstanceOf[Account].setPhone(phoneFld.text.value.trim)
    objEntity.asInstanceOf[Account].setRole(roleFld.value.value)
    objEntity.asInstanceOf[Account].setArea(areaFld.value.value)
    objEntity.asInstanceOf[Account].setAssignment(assignmentFld.text.value.trim)
    objEntity.asInstanceOf[Account].setUsername(usernameFld.text.value.trim)
    objEntity.asInstanceOf[Account].setPassword(cipher(passwordFld.getText))

@main def tryAccountAdd: Unit =
  println("")
