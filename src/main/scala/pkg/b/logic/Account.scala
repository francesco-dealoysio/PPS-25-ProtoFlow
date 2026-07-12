package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.xmlManagement.Xml.*

case class Account(
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
  val user = Account("1", "Francesco", "de Aloysio")
  //user.nome = "pippo"
  println(user)

  Account().getRecords.foreach(println)
  println(Account().getRecordById("2").id)

  println(Account().getRecordsByFilter(true))
