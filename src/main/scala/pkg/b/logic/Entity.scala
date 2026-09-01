package pkg.b.logic

import pkg.c.data.Xml.{getRecordsFromXML, insertElemIntoXML, removeElemFromXML, searchFieldValue, updateElemOfXML}
import pkg.d.util.Logger.logger
import pkg.d.util.Util.inDatabaseFilePathName

trait Entity:
  def xmlFile: String

  /**
   * Returns a sequence of records extracted from the specified xml file.
   * @param xmlFilePathName 
   * @tparam T type of sequence returned
   * @return Seq[T]
   */
  def getRecords[T](xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    try
      getRecordsFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e); Seq.empty[T]

  /**
   * Returns a sequence of records extracted from the xml file specified in the argument
   * filtered by the predicate passed in the argument.
   * @param predicate
   * @param xmlFilePathName
   * @tparam T type of sequence returned
   * @return Seq[T]
   */
  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    try {
      getRecordsFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(_.asInstanceOf[T]).filter(predicate)
    } catch
      case e: Exception =>
        logger(e); Seq.empty[T]

  /**
   * Returns, if exists, the record of type T having the id in argument
   * extracted from the xml file specified in argument.
   * @param id identifies the record to find
   * @param xmlFilePathName
   * @tparam T 
   * @return T object
   */
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

  /**
   * Inserts the obj object of type T into the XML file specified in the argument.
   * @param obj
   * @param xmlFilePathName
   * @tparam T
   * @return true on success, false otherwise.
   */
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

  /**
   * Updates the obj object of type T contained in the XML file specified in the argument.
   * @param obj
   * @param xmlFilePathName
   * @tparam T
   * @return true on success, false otherwise.
   */
  def recordUpdate[T](obj: T, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      updateElemOfXML(xmlFilePathName, obj)
    catch
      case e: Exception =>
        logger(e); false

  /**
   * Remove, if exists, the obj object having the id passed in argument from the XML file specified.
   * @param id identifies the record to delete
   * @param xmlFilePathName
   * @return true on success, false otherwise.
   */
  def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        logger(e); false

  /** Returns the default pathname where the entity-managed xml file is located. */
  protected def defaultXmlFilePathName: String =
    try
      inDatabaseFilePathName(xmlFile)
    catch
      case e: Exception =>
        logger(e); ""

  /**
   * Returns search a record with the specified field and value in the specified XML file.
   * @param fieldName
   * @param fieldValue
   * @param xmlFilePathName
   * @return true on success, false otherwise.
   */
  protected def fieldExists(fieldName: String, fieldValue: String, xmlFilePathName: String): Boolean =
    try
      searchFieldValue(xmlFilePathName, fieldName, fieldValue)
    catch
      case e: Exception =>
        logger(e); false

  /**
   * Returns the number of occurrences of records in the specified xml file
   * filtered by the predicate passed in the argument.
   * @param predicate 
   * @param xmlFilePathName
   * @param classType
   * @tparam T
   * @return
   */
  protected def countRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Int =
    try
      getRecordsFromXML(xmlFilePathName, classType)
        .map(_.asInstanceOf[T]).count(predicate)
    catch
      case e: Exception =>
        logger(e); 0