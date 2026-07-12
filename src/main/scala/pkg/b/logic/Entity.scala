package pkg.b.logic

//import pkg.b.logic.Entities.Account
//import pkg.c.data.xmlManagement.Xml.loadXML
import pkg.d.util.Properties.getPropsFileProperty

trait Entity:
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  def getRecordById(id: String): Any
  def getRecords: Seq[Any]
  def getRecordsByFilter(condition: Boolean): Int

object Entity:
  def recordInsert(obj: AnyRef): Unit = ???
  def recordUpdate(obj: AnyRef): Unit = ???
  def recordDelete(id: String): Unit = ???
/*
case class User(
                    id: String = "",
                    cognome: String = "",
                    nome: String = "",
                    email: String = "",
                    telefono: String = "",
                    ruolo: String = "",
                    area: String = "",
                    incarico: String = "",
                    username: String = "",
                    password: String = ""
                  ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "")

  override def getRecords: Seq[Account] =
    loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
      .map(r => r.asInstanceOf[Account])

  override def getRecordById(id: String): Account =
    loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
      .map(a => a.asInstanceOf[Account]).filter(_.id == id)(0)

  override def getRecordsByFilter(condition: Boolean): Int =
    loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
      //.map(a => a.asInstanceOf[Account]).count(a => a.ruolo == "viewer" && a.nome == "francesco")
      .map(a => a.asInstanceOf[Account]).count(a => a.ruolo == "viewer")


@main def tryEntity: Unit =
  println("Test Entity")
  val user = User("1", "Francesco", "de Aloysio")
  //user.nome = "pippo"
  println(user)

  User().getRecords.foreach(println)
  println(User().getRecordById("2").id)

  println(User().getRecordsByFilter(true))
*/
