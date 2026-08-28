package pkg.e.ui.managements

import pkg.e.ui.traits.{Homepage, Management}
import pkg.b.logic.Account
import pkg.d.util.Util.inDatabaseFilePathName
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.Includes.jfxNode2sfx
import scalafx.beans.property.StringProperty
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.scene.control.{TableColumn, TableView, cell}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.cell.TextFieldTableCell

class AccountManagement(val user: Account, val parentMask: Homepage) extends Management:

  override val Title: String = "Gestione Account Utenti"

  pageTitle = Title

  userMask = this

  objEntity = new Account

  val accounts = Account().getRecords[Account]()
  val records: ObservableBuffer[Record] = ObservableBuffer.from(accounts.map(a => toRecord(a)))

  xmlFilePathName = inDatabaseFilePathName("accounts.xml")

  class Record(
    val id: StringProperty,
    val surname: StringProperty,
    val name: StringProperty,
    val role: StringProperty,
    val area: StringProperty
  )

  def toRecord(r: Account): Record =
    val record: Record = new Record(
      StringProperty(r.getId),
      StringProperty(r.getSurname),
      StringProperty(r.getName),
      StringProperty(r.getRole),
      StringProperty(r.getArea)
    )
    record

  override def menu: VBox = parentMask.menu

  val table = new TableView[Record](records) {
    columns ++= List(
      new TableColumn[Record, String] {
        text = "Id"
        cellValueFactory = {
          _.value.id
        }
        prefWidth = 35
      },
      new TableColumn[Record, String] {
        text = "Cognome"
        cellValueFactory = {
          _.value.surname
        }
        prefWidth = 150
      },
      new TableColumn[Record, String] {
        text = "Nome"
        cellValueFactory = {
          _.value.name
        }
        prefWidth = 150
      },
      new TableColumn[Record, String] {
        text = "Ruolo"
        cellValueFactory = {
          _.value.role
        }
        prefWidth = 150
      },
      new TableColumn[Record, String] {
        text = "Area"
        cellValueFactory = {
          _.value.area
        }
        prefWidth = 100
      }
    )
  }

  table.selectionModel().selectedItemProperty().addListener { (_, _, newValue) =>
    if (newValue != null)
      currentId = table.selectionModel().selectedItemProperty().get().id.value
  }

  override def managementPageGrid = new BorderPane {
    center = table
  }

  table.selectionModel().selectFirst()
