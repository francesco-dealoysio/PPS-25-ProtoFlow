package pkg.c.data.xmlManagement

import Entities.*
import pkg.c.data.Properties.*

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

object Xml:

  private def recordUpdate[Any](obj: Any, fieldName: String, value: String): Any =
    val field = obj.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.set(obj, value)
    obj

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

  @main def tryXml(): Unit =

    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
/*
    // test writeXML
    writeXML(databaseFolder + fs + "accounts.xml", DummyData.accounts)
    writeXML(databaseFolder + fs + "ruoli.xml", DummyData.ruoli)
    writeXML(databaseFolder + fs + "classifiche.xml", DummyData.classifiche)
*/
    // test loadXML
    println("\n\nInizio Test")
    val accounts = loadXML(databaseFolder + fs + "accounts.xml", classOf[Account])
    accounts.foreach(println)
    println("XXXXXXXXXXXXXXXXXXX")
/*
    loadXML(databaseFolder + fs + "ruoli.xml", classOf[Ruolo]).foreach(println)

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
