package pkg.e.ui

import pkg.b.logic.Account
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{TableColumn, TableView, cell}
import scalafx.collections.ObservableBuffer

import scalafx.scene.control.cell.TextFieldTableCell
import scalafx.util.converter.IntStringConverter
import scalafx.util.StringConverter

import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.scene.layout.VBox
import pkg.b.logic.Account
import pkg.d.util.Util.inDatabaseFilePathName

// Model class with JavaFX properties
class Record(
  val id: StringProperty,
  val surname: StringProperty,
  val name: StringProperty,
  val role: StringProperty,
  val area: StringProperty
)

object TableViewExample extends JFXApp3 {

  override def start(): Unit = {

    def toRecord(r: Account): Record =
      val record: Record = new Record(
        StringProperty(r.getId),
        StringProperty(r.getSurname),
        StringProperty(r.getName),
        StringProperty(r.getRole),
        StringProperty(r.getArea)
      )
      record

    val accounts = Account().getRecords[Account]()
    val records: ObservableBuffer[Record] = ObservableBuffer.from(accounts.map(a => toRecord(a)))

    // TableView
    val table = new TableView[Record](records) {
      columns ++= List(
        new TableColumn[Record, String] {
          text = "Id"
          cellValueFactory = { _.value.id }
          prefWidth = 25
        },
        new TableColumn[Record, String] {
          text = "Cognome"
          cellValueFactory = { _.value.surname }
          prefWidth = 150
        },
        new TableColumn[Record, String] {
          text = "Nome"
          cellValueFactory = { _.value.name }
          prefWidth = 150
        },
        new TableColumn[Record, String] {
          text = "Ruolo"
          cellValueFactory = { _.value.role }
          prefWidth = 150
        },
        new TableColumn[Record, String] {
          text = "Area"
          cellValueFactory = { _.value.area }
          prefWidth = 100
        }
      )
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Gestione Account"
      scene = new Scene {
        content = new VBox {
          spacing = 10
          children = Seq(table)
        }
      }
    }
  }
}

@main def tryTableViewExample: Unit =
  TableViewExample.main(Array.empty)


