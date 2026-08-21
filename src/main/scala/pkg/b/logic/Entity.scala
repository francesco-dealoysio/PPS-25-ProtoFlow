package pkg.b.logic

import pkg.c.data.Xml.{getRecordsFromXML, insertElemIntoXML, removeElemFromXML, searchFieldValue, updateElemOfXML}
import pkg.d.util.Logger.logger
import pkg.d.util.Util.inDatabaseFilePathName

trait Entity:
  def xmlFile: String

  def getRecords[T](xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    try
      getRecordsFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e); Seq.empty[T]

  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    try {
      getRecordsFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(_.asInstanceOf[T]).filter(predicate)
    } catch
      case e: Exception =>
        logger(e); Seq.empty[T]

  def getRecordById[T](id: String, xmlFilePathName: String = defaultXmlFilePathName): T =
    val method = asInstanceOf[T].getClass.getDeclaredMethod("getId")
    val predicate: (x: T) => Boolean = x => method.invoke(x) == id
    val constructor = asInstanceOf[T].getClass.getDeclaredConstructor()

    constructor.setAccessible(true)

    try
      getRecordsFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(_.asInstanceOf[T]).find(predicate).getOrElse(constructor.newInstance())
    catch
      case e: Exception =>
        logger(e); constructor.newInstance()

  def recordInsert[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    var result = false
    try
      val method = asInstanceOf[T].getClass.getDeclaredMethod("getId")
      val id = method.invoke(obj).toString
      if !fieldExists("id", id, xmlFilePathName) then
        result = insertElemIntoXML(xmlFilePathName, obj)
      else
        throw new RuntimeException("Valori duplicati (id)!")
    catch
      case e: Exception =>
        logger(e)
    result

  def recordUpdate[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      updateElemOfXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        logger(e); false

  def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        logger(e); false

  protected def defaultXmlFilePathName: String =
    try
      inDatabaseFilePathName(xmlFile)
    catch
      case e: Exception =>
        logger(e); ""

  protected def fieldExists(fieldName: String, fieldValue: String, xmlFilePathName: String): Boolean =
    try
      searchFieldValue(xmlFilePathName, fieldName, fieldValue)
    catch
      case e: Exception =>
        logger(e); false

  protected def countRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Int =
    try
      getRecordsFromXML(xmlFilePathName, classType)
        .map(_.asInstanceOf[T]).count(predicate)
    catch
      case e: Exception =>
        logger(e); 0