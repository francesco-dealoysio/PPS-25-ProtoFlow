package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*

case class Role(
                private var id: String = "",
                private var role: String = "",
                private var description: String = ""
                ) extends Entity:
  def this() =
    this("","","")

  def setId(value: String): Unit = id = value
  def setRole(value: String): Unit = role = value
  def setDescription(value: String): Unit = description = value

  def getId: String = id
  def getRole: String = role
  def getDescription: String = description

  override def xmlFile = "roles.xml"

  override def getRecords(xmlFilePathName: String = defaultXmlFilePathName): Seq[Role] =
    try
      getRecordFromXML(xmlFilePathName, classOf[Role])
        .map(r => r.asInstanceOf[Role])
    catch
      case e: Exception =>
        println(s"Errore in getRecords: ${e.getMessage}")
        Seq.empty[Role]

  override def getRecordById(id: String, xmlFilePathName: String = defaultXmlFilePathName): Role =
    try
      getRecordFromXML(xmlFilePathName, classOf[Role])
        .map(a => a.asInstanceOf[Role]).filter(_.id == id).head
    catch
      case e: Exception =>
        println(s"Errore in getRecordById: ${e.getMessage}")
        new Role
  
  override def getRecordsByFilter[Role](predicate: Role => Boolean, xmlFilePathName: String = defaultXmlFilePathName, classType: Class[Role]): Seq[Role] =
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(_.asInstanceOf[Role]).filter(predicate)
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        Seq.empty[Role]

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Role]
      val id = record.id
      val role = record.role
      if !(fieldExists("id", id, xmlFilePathName) || fieldExists("role", role, xmlFilePathName)) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        println(s"Errore in recordInsert: valori duplicati (id o ruolo)")
    catch
      case e: Exception =>
        println(s"Errore in recordInsert: ${e.getMessage}")
    result

  override def recordUpdate(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Role]
      val id = record.id
      val role = record.role
      val  found = countRecordsByFilter[Role](a => a.id != id && a.role == role, xmlFilePathName, classOf[Role])
      if (found == 0) then
        result = updateElemOfXML(xmlFilePathName, obj)
      else
        println(s"Errore in recordUpdate: valori duplicati (ruolo)")
    catch
      case e: Exception =>
        println(s"Errore in recordUpdate: ${e.getMessage}")
        false
    result

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        println(s"Errore in recordDelete: ${e.getMessage}")
        false

@main def tryRole: Unit =
    println("Tested in RoleTest.scala")