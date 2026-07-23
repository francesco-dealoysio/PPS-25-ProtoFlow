package pkg.b.logic

import pkg.b.logic.Entity
import pkg.c.data.Xml.*
import pkg.d.util.Logger.*

case class Registration(
                    private var id: String = "",
                    private var surname: String = "",
                    private var name: String = "",
                    private var email: String = "",
                    private var phone: String = "",
                    private var role: String = "",
                    private var area: String = "",
                    private var assignment: String = "",
                    private var date: String = "",
                    private var state: String = "",
                    private var result: String = "",
                    private var motivation: String = ""
                  ) extends Entity:
  def this() =
    this("", "", "", "", "", "", "", "", "", "", "", "")
  
  def setId(value: String): Unit = id = value
  def setSurname(value: String): Unit = surname = value
  def setName(value: String): Unit = name = value
  def setEmail(value: String): Unit = email = value
  def setPhone(value: String): Unit = phone = value
  def setRole(value: String): Unit = role = value
  def setArea(value: String): Unit = area = value
  def setAssignment(value: String): Unit = assignment = value
  def setDate(value: String): Unit = date = value
  def setState(value: String): Unit = state = value
  def setResult(value: String): Unit = result = value
  def setMotivation(value: String): Unit = motivation = value

  def getId: String = id
  def getSurname: String = surname
  def getName: String = name
  def getEmail: String = email
  def getPhone: String = phone
  def getRole: String = role
  def getArea: String = area
  def getAssignment: String = assignment
  def getDate: String = date
  def getState: String = state
  def getResult: String = result
  def getMotivation: String = motivation

  override def xmlFile = "registrations.xml"

  override def recordInsert(obj: Any, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val record = obj.asInstanceOf[Registration]
      val id = record.id
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (id)!")
    catch
      case e: Exception =>
        logger(e)
    result

@main def tryRegistration: Unit =
  println("Tested in RegistrationTest.scala")
