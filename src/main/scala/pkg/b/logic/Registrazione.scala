package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.xmlManagement.Xml.*
import pkg.d.util.Properties.*

case class Registrazione(
                    private var id: String = "",
                    private var cognome: String = "",
                    private var nome: String = "",
                    private var email: String = "",
                    private var telefono: String = "",
                    private var ruolo: String = "",
                    private var area: String = "",
                    private var incarico: String = "",
                    private var data: String = "",
                    private var stato: String = "",
                    private var esito: String = "",
                    private var motivazione: String = ""
                  ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "")
  
  def setId(value: String): Unit = id = value
  def setCognome(value: String): Unit = cognome = value
  def setNome(value: String): Unit = nome = value
  def setEmail(value: String): Unit = email = value
  def setTelefono(value: String): Unit = telefono = value
  def setRuolo(value: String): Unit = ruolo = value
  def setArea(value: String): Unit = area = value
  def setIncarico(value: String): Unit = incarico = value
  def setData(value: String): Unit = data = value
  def setStato(value: String): Unit = stato = value
  def setEsito(value: String): Unit = esito = value
  def setMotivazione(value: String): Unit = motivazione = value

  def getId: String = id
  def getCognome: String = cognome
  def getNome: String = nome
  def getEmail: String = email
  def getTelefono: String = telefono
  def getRuolo: String = ruolo
  def getArea: String = area
  def getIncarico: String = incarico
  def getData: String = data
  def getStato: String = stato
  def getEsito: String = esito
  def getMotivazione: String = motivazione

  def defaultXmlFilePathName: String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  override def xmlFile = "richieste.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Registrazione] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Registrazione])
        .map(r => r.asInstanceOf[Registrazione])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Registrazione]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Registrazione =
    try
      getRecordFromXML(xmlFilePathName, classOf[Registrazione])
        .map(a => a.asInstanceOf[Registrazione]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Registrazione

  //
  override def getRecordsByFilter(condition: Boolean, xmlFilePathName: String = defaultXmlFilePathName): Int =
    val NONE = 0
    try
      getRecordFromXML(xmlFilePathName, classOf[Registrazione])
        .map(a => a.asInstanceOf[Registrazione]).count(a => a.ruolo == "viewer")
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        NONE

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      insertElemIntoXML(xmlFilePathName, obj)
      result = true
    catch
      case e: Exception =>
        println(s"Errore in recordInsert: ${e.getMessage}")
    result

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Unit =
    try
      updateElemOfXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        println(s"Errore in recordDelete: ${e.getMessage}")
        false

@main def tryRegistrazione: Unit =
  println("Tested in RegistrazioneTest.scala")
