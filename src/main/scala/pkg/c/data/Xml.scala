package pkg.c.data

//import pkg.b.logic.Entities.*
import pkg.b.logic.Account
import Properties.*

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}
import scala.xml.*

object Xml:
/*
  // Thomas
  private def emptyXml: Elem =
    <registrationRequests></registrationRequests>

  // Thomas
  private def loadXml(): Elem =
    val file = File(filePath)

    if file.exists() then
      XML.loadFile(file)
    else
      saveXml(emptyXml)
      emptyXml

  // Thomas
  def findAll(): List[RegistrationRequest] =
    val xml = loadXml()
    (xml \ "request").toList.map(fromXml)

  // Thomas
  def findById(id: String): Option[RegistrationRequest] =
    findAll().find(_.id == id)

  // Thomas
  private def saveAll(requests: List[RegistrationRequest]): Unit =
    val xml =
      <registrationRequests>
        {requests.map(toXml)}
      </registrationRequests>

    saveXml(xml)
 */

  def createEmptyXmlFile(xmlFilePathName: String, rootTagName: String): Unit =

    if !rootTagName.matches("^[A-Za-z_][A-Za-z0-9._-]*$") then
      throw new IllegalArgumentException(s"Invalid XML tag name: '$rootTagName'")

    val xmlContent: Elem = Elem(null, rootTagName, scala.xml.Null, scala.xml.TopScope, minimizeEmpty = true)
    val prettyPrinter = PrettyPrinter(80, 2)
    val xmlString = """<?xml version="1.0" encoding="UTF-8"?>""" + "\n" + prettyPrinter.format(xmlContent)
    val path = Paths.get(xmlFilePathName)

    Files.write(path, xmlString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    println(s"XML file created at: $xmlFilePathName")

  def cleanXmlFile(xmlFilePathName: String): Unit =
    val xmlTry: Try[Elem] = Try(XML.loadFile(xmlFilePathName))
    xmlTry match
      case Success(xmlData) =>
        val cleanedXml = xmlData.copy(child = Nil)
        saveXML(xmlFilePathName, cleanedXml)
      case Failure(ex) =>
        println(s"Error loading XML: ${ex.getMessage}")

  private def recordUpdate[Any](obj: Any, fieldName: String, value: String): Any =
    try
      val field = obj.getClass.getDeclaredField(fieldName)
      field.setAccessible(true)
      field.set(obj, value)
      obj
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")
        obj

  def getRecordFromXML(xmlFilePathName: String, classType: Class[?]): Seq[Any] =
    val xmlTry: Try[Elem] = Try(XML.loadFile(xmlFilePathName))
    xmlTry match
      case Success(xmlData) =>
        (xmlData \\ "record").flatMap { node =>
          val constructor = classType.getDeclaredConstructor()
          var record = constructor.newInstance()
          val fieldsMap = node.child.collect { case e: Elem => e.label -> e.text.trim }.toMap
          val result =
          fieldsMap.foreach { case (k, v) => record = recordUpdate(record, s"$k", s"$v") }
          for i <- node yield record
        }
      case Failure(ex) =>
        println(s"Error loading XML: ${ex.getMessage}")
        Seq.empty

  // ???
  def writeXML(xmlFilePathName: String, xmlElem: Elem): Unit =
    XML.save(xmlFilePathName, xmlElem, "UTF-8", xmlDecl = true)

  // ???
  def saveXML(xmlFilePathName: String, xmlElem: Elem): Unit =
    val pw = new PrintWriter(new File(xmlFilePathName))
    try pw.write(new PrettyPrinter(80, 2).format(xmlElem))
    finally pw.close()

  // ???
  private def recordToElem(obj: Any): Elem =
    val fields = obj.getClass.getDeclaredFields
    val children: Seq[Node] = fields.map { field =>
      field.setAccessible(true)
      val value = Option(field.get(obj)).map(_.toString).getOrElse("")
      scala.xml.Elem(null, field.getName, scala.xml.Null, scala.xml.TopScope, true, scala.xml.Text(value))
    }
    scala.xml.Elem(null, "record", scala.xml.Null, scala.xml.TopScope, true, children *)

  def insertElemIntoXML(xmlFilePathName: String, obj: Any): Boolean =
    var result = false
    val xmlElem = recordToElem(obj)
    val xmlTry = Try(XML.loadFile(xmlFilePathName))

    xmlTry match
      case Success(root) =>
        val updatedXml = root match
          case Elem(_, root.label, _, _, children@_*) =>
            root.copy(child = root.child :+ xmlElem)
          case other =>
            println("Unexpected XML structure.")
            return false
        saveXML(xmlFilePathName, updatedXml)
        println(s"Element appended to $xmlFilePathName")
        result = true
      case Failure(ex) =>
        println(s"Error loading XML: ${ex.getMessage}")
    result

  def updateElemOfXML(xmlFilePathName: String, obj: Any): Boolean =
    var result = false
    val id = obj.getClass.getDeclaredField("id")
    id.setAccessible(true)
    val readId = id.get(obj).toString
    if searchFieldValue(xmlFilePathName, "id", readId) then
      removeElemFromXML(xmlFilePathName, id.get(obj).toString)
      insertElemIntoXML(xmlFilePathName, obj)
      result = true
    else
      println(s"Record with id: ${id} not found.")
    result

  def removeElemFromXML(xmlFilePathName: String, id: String): Boolean =
    var result = false
    val xmlTry = Try(XML.loadFile(xmlFilePathName))

    if (searchFieldValue(xmlFilePathName, "id", id)) then
      xmlTry match
        case Success(root) =>
          val updatedXml = root match
            case Elem(_, root.label, _, _, children @ _*) =>
              root.copy(child = (root \ "record").filterNot(rec => (rec \ "id").text.trim == id))
            case other =>
              println("Unexpected XML structure.")
              return false
            saveXML(xmlFilePathName, updatedXml)
            println("Record removed successfully.")
            result = true
        case Failure(ex) =>
          println(s"Error loading XML: ${ex.getMessage}")
    else
      println(s"Record with id: ${id} not found.")
    result

  def searchFieldValue(xmlFilePathName: String, fieldName: String, fieldValue: String): Boolean =
    val xmlTry = Try(XML.loadFile(xmlFilePathName))
    var result = false
    xmlTry match
      case Success(root) =>
        root match
        case Elem(_, _, _, _, children@_*) =>
          result = (root \\ fieldName).exists(_.text.trim == fieldValue)
        case other =>
          println("Unexpected XML structure.")
      case Failure(ex) =>
        println(s"Error loading XML: ${ex.getMessage}")
      result

  @main def tryXml(): Unit =

    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = baseFolder + fs + "database"

    //val record = recordToElem(Ruolo("5", "Manager", "Compiti di gestione"))
    val record = Account().getRecordById("2")
    val elem = recordToElem(record)
    val pp = PrettyPrinter(80, 2)
    println(pp.format(elem))

    val account1 = Account().getRecordById("1")
    val account3 = Account().getRecordById("3")
    println(account1)
    println(account3.getRuolo)
    account3.setRuolo("paperino")
    updateElemOfXML(databaseFolder + fs + "accounts.xml", account3)

    createEmptyXmlFile(databaseFolder + fs + "nuovo.xml", "libri")
    insertElemIntoXML(databaseFolder + fs + "nuovo.xml", account3)

    //writeXML(databaseFolder + fs + "testElem.xml", elem)
    //saveXML(databaseFolder + fs + "testElem.xml", elem)
    //insertElemIntoXML(databaseFolder + fs + "test.xml", elem)
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