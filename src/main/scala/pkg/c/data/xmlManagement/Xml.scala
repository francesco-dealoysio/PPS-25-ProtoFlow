package pkg.c.data.xmlManagement

import pkg.b.logic.Entities.*
import pkg.b.logic.Account
import pkg.d.util.Properties.*

import java.io.{File, PrintWriter}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, Node, PrettyPrinter, XML}

object Xml:

  private def recordUpdate[Any](obj: Any, fieldName: String, value: String): Any =
    val field = obj.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(obj, value)
    obj

  // cambiare il nome in getRecordsFromXML
  def loadXML(xmlFilePathName: String, classType: Class[?]): Seq[Any] =
    val xmlTry: Try[Elem] = Try(XML.loadFile(xmlFilePathName))

    xmlTry match
      case Success(xmlData) =>
        (xmlData \\ "record").flatMap { node =>

          val constructor = classType.getDeclaredConstructor()
          var record = constructor.newInstance()

          val fieldsMap = node.child.collect { case e: Elem => e.label -> e.text.trim }.toMap
          fieldsMap.foreach { case (k, v) => record = recordUpdate(record, s"$k", s"$v") }

          for i <- node yield record

        }
      case Failure(ex) =>
        println(s"Error loading XML: ${ex.getMessage}")
        Seq.empty

  def writeXML(xmlFilePathName: String, xmlElem: Elem): Unit =
    XML.save(xmlFilePathName, xmlElem, "UTF-8", xmlDecl = true)

  def saveXML(xmlFilePathName: String, xmlElem: Elem): Unit =
    val pw = {
      new PrintWriter(new File(xmlFilePathName))
    }
    try pw.write(new PrettyPrinter(80, 2).format(xmlElem))
    finally pw.close()

  //recordToElem
  def recordToElem[Any](obj: Any): Elem =
    val fields = obj.getClass.getDeclaredFields
    val children: Seq[Node] = fields.map { field =>
      field.setAccessible(true)
      val value = Option(field.get(obj)).map(_.toString).getOrElse("")
      scala.xml.Elem(null, field.getName, scala.xml.Null, scala.xml.TopScope, true, scala.xml.Text(value))
    }
    scala.xml.Elem(null, "record", scala.xml.Null, scala.xml.TopScope, true, children *)

  def insertElemIntoXML(xmlFilePathName: String, xmlElem: Elem): Unit =

    val xmlTry = Try(XML.loadFile(xmlFilePathName))

    xmlTry match {
      case Success(root) =>
        val updatedXml = root match
          case Elem(_, root.label, _, _, children @ _*) =>
            root.copy(child = root.child :+ xmlElem)
            //<accounts>{children ++ xmlElem}</accounts>
          case other =>
            println("Unexpected XML structure.")
            return

        saveXML(xmlFilePathName, updatedXml)
        println(s"Element appended to $xmlFilePathName")

      case Failure(ex) =>
        println(s"Error loading XML: ${ex.getMessage}")
    }

  //removeElemFromXML

/*
    val people = List(
      ("Bob", 40, "New York"),
      ("Clara", 28, "London")
    )

    val xmlList: Seq[Elem] = people.map(p => createPersonXml(p._1, p._2, p._3))

    // Wrap in a root node
    val rootXml: Elem = <people>
      {xmlList}
    </people>
*/

  @main def tryXml(): Unit =

    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = baseFolder + fs + "database"

    //val elem = recordToElem(Ruolo("5", "Manager", "Compiti di gestione"))
    val elem = recordToElem(Account().getRecordById("2"))
    //writeXML(databaseFolder + fs + "testElem.xml", elem)
    saveXML(databaseFolder + fs + "testElem.xml", elem)

    val pp = PrettyPrinter(80, 2) // width=80, indent=2 spaces
    println(pp.format(elem))

    insertElemIntoXML(databaseFolder + fs + "test.xml", elem)

/*
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")

    // test loadXML
    println("\n\nInizio Test")
    val accounts = loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
    accounts.foreach(println)
    println("XXXXXXXXXXXXXXXXXXX")

    loadXML(databaseFolder + fs + "ruoli.xml", classOf[Ruolo]).foreach(println)
/*
    val classifiche = loadXML(databaseFolder + fs + "classifiche.xml", classOf[Classifica])
    classifiche.foreach(println)
*/
    // test filtri
    val admins = accounts.map(a => a.asInstanceOf[Account]).filter(_.ruolo == "viewer")
    //println(admins)
    //admins.foreach(a => println(a.cognome + " " + a.nome))
    println(admins(0))
    println(admins(0).cognome + " " + admins(0).nome)
    println

    val selection: Seq[Account] = accounts.map(a => a.asInstanceOf[Account]).filter(_.id == "2")
    selection.foreach(r => println(r.cognome))
    println

    if selection.length > 0 then
      val p: Account = selection(0)
      println(s"Selected: ${p.cognome}")
    println

    for (record <- accounts)
      println(record.asInstanceOf[Account].cognome)
*/