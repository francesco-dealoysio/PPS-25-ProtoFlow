package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.Logger.*

case class Role(
                private var id: String = "",
                private var role: String = "",
                private var name: String = "",
                private var description: String = ""
                ) extends Entity:
  def this() =
    this("","","", "")

  def setId(value: String): Unit = id = value
  def setRole(value: String): Unit = role = value
  def setName(value: String): Unit = name = value
  def setDescription(value: String): Unit = description = value

  def getId: String = id
  def getRole: String = role
  def getName: String = name
  def getDescription: String = description

  override def xmlFile = "roles.xml"

  override def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Role]
      val id = record.id
      val role = record.role

      if !(fieldExists("id", id, xmlFilePathName) || fieldExists("role", role, xmlFilePathName)) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (id o ruolo)!")
    catch
      case e: Exception =>
        logger(e)
    result

  override def recordUpdate[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Role]
      val id = record.id
      val role = record.role

      val  found = countRecordsByFilter[Role](a => a.id != id && a.role == role, xmlFilePathName, classOf[Role])
      if found == 0 then
        result = updateElemOfXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (ruolo)!")
    catch
      case e: Exception =>
        logger(e)
        false
    result

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    val roles = getRecords[Role](xmlFilePathName)
    roles.find(_.getId == id) match
      case Some(role) =>
        if AuthorizationEngine.canDeleteRole(role.getRole) then
          super.recordDelete(id, xmlFilePathName)
        else
          false
      case None =>
        false