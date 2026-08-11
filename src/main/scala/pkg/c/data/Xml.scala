package pkg.c.data

import pkg.b.logic.Account
import pkg.d.util.Logger.*
import Properties.*
import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}
import scala.xml.*

object Xml:

  def createEmptyXmlFile(xmlFilePathName: String, rootTagName: String): Unit =
    val xmlString = s"<$rootTagName></$rootTagName>"
    val path = Paths.get(xmlFilePathName)
    Files.write(path, xmlString.getBytes(StandardCharsets.UTF_8))
    println(s"XML file created at: $xmlFilePathName")

  def cleanXmlFile(xmlFilePathName: String): Unit =
    val xmlTry: Try[Elem] = Try(XML.loadFile(xmlFilePathName))
    xmlTry match
      case Success(xmlData) =>
        val cleanedXml = xmlData.copy(child = Nil)
        saveXML(xmlFilePathName, cleanedXml)
      case Failure(ex) =>
        logger(ex match { case e: Exception => e })

  private def recordUpdate(obj: AnyRef, fieldName: String, value: String): AnyRef =
    try
      val field = obj.getClass.getDeclaredField(fieldName)
      field.setAccessible(true)
      field.set(obj, value)
      obj
    catch
      case e: Exception =>
        logger(e)
        obj

  def getRecordsFromXML(xmlFilePathName: String, classType: Class[?]): Seq[AnyRef] =
    val xmlTry: Try[Elem] = Try(XML.loadFile(xmlFilePathName))

    xmlTry match
      case Success(xmlData) =>
        (xmlData \\ "record").flatMap { node =>
          try
            val constructor = classType.getDeclaredConstructor()
            constructor.setAccessible(true)

            var record = constructor.newInstance().asInstanceOf[AnyRef]
            val fieldsMap = node.child.collect { case e: Elem => e.label -> e.text.trim }.toMap

            fieldsMap.foreach { case (k, v) => record = recordUpdate(record, k, v) }
            Some(record)
          catch
            case e: Exception =>
              logger(e)
              None
        }
      case Failure(ex) =>
        logger(ex match { case e: Exception => e })
        println (s"Error loading XML: ${ex.getMessage}")
        Seq.empty

  def saveXML(xmlFilePathName: String, xmlElem: Elem): Unit =
    val pw = new PrintWriter(xmlFilePathName, StandardCharsets.UTF_8.name())
    try pw.write(new PrettyPrinter(80, 2).format(xmlElem))
    finally pw.close()

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
        logger(ex match { case e: Exception => e })
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
          logger(ex match { case e: Exception => e })
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
        case Elem(_, _, _, _, children*) =>
          result = (root \\ fieldName).exists(_.text.trim == fieldValue)
        case other =>
          println("Unexpected XML structure.")
      case Failure(ex) =>
        logger(ex match { case e: Exception => e })
        println(s"Error loading XML: ${ex.getMessage}")
      result

  @main def tryXml(): Unit =
    println("Tested in XmlTest.scala")