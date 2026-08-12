package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.Logger.*
import pkg.d.util.Util.cipher

case class Account(
                 private var id: String = "",
                 private var surname: String = "",
                 private var name: String = "",
                 private var email: String = "",
                 private var phone: String = "",
                 private var role: String = "",
                 private var area: String = "",
                 private var assignment: String = "",
                 private var username: String = "",
                 private var password: String = ""
               ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "")

  def setId(value: String): Unit = id = value
  def setSurname(value: String): Unit = surname = value
  def setName(value: String): Unit = name = value
  def setEmail(value: String): Unit = email = value
  def setPhone(value: String): Unit = phone = value
  def setRole(value: String): Unit = role = value
  def setArea(value: String): Unit = area = value
  def setAssignment(value: String): Unit = assignment = value
  def setUsername(value: String): Unit = username = value
  def setPassword(value: String): Unit = password = cipher(value)

  def getId: String = id
  def getSurname: String = surname
  def getName: String = name
  def getEmail: String = email
  def getPhone: String = phone
  def getRole: String = role
  def getArea: String = area
  def getAssignment: String = assignment
  def getUsername: String = username
  def getPassword: String = password

  override def xmlFile = "accounts.xml"

  override def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Account]
      val id = record.id
      val username = record.username

      if !(fieldExists("id", id, xmlFilePathName) || fieldExists("username", username, xmlFilePathName)) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (id o username)!")
    catch
      case e: Exception =>
        logger(e)
    result

  override def recordUpdate[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Account]
      val id = record.id
      val username = record.username

      val found = countRecordsByFilter[Account](a => a.id != id && a.username == username, xmlFilePathName, classOf[Account])
      if (found == 0) then
        result = updateElemOfXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (username)!")
    catch
      case e: Exception =>
        logger(e)
    result

  override def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    val accounts = getRecords[Account](xmlFilePathName)
    accounts.find(_.getId == id) match
      case Some(account) =>
        val isLastAdmin =
          account.getRole.equalsIgnoreCase("admin") &&
            accounts.count(_.getRole.equalsIgnoreCase("admin")) <= 1

        if isLastAdmin then
          false
        else
          super.recordDelete(id, xmlFilePathName)

      case None =>
        false

@main def tryAccount: Unit =
  println("Tested in AccountTest.scala")
