package pkg.a.gui.views

import pkg.a.gui.traits.Operation
import pkg.b.logic.{Account, LoadedDocument}
import pkg.d.util.IdGen
import pkg.d.util.Util.{inDocumentsFilePathName, inIdsFilePathName}
import pkg.d.util.DateTime.{localDate, localTime}
import scala.util.{Failure, Success, Try}
import scalafx.scene.control.{Control, Alert, ComboBox, DatePicker, Label, TextArea, TextField}
import javafx.collections.FXCollections
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EntityAdd(val user: Account) extends Operation:

  override val Title: String = "Aggiunta Entità"
  override val Width = 780
  override val Height = 600

  pageTitle = "Aggiunta Entità"
  operationType = "insert"
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
      execBtn.disable = false
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
      new Alert(Alert.AlertType.Error) {
        val found = if counter > 1 then "Trovati" else "Trovato"
        val error = if counter > 1 then "errori" else "errore"
        title = "Errori"
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
  import pkg.d.util.Util.cipher

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
    cipher("topolino")
  )

  EntityAdd(account1).main(Array.empty)
