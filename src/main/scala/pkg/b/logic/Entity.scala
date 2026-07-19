package pkg.b.logic

trait Entity:
  def xmlFile: String
  def getRecords(xmlFilePathName: String): Seq[Any]
  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Seq[T]
  def getRecordById(id: String, xmlFilePathName: String): Any
  def recordInsert(obj: Any, xmlFilePathName: String): Boolean
  def recordUpdate(obj: Any, xmlFilePathName: String): Boolean
  def recordDelete(id: String, xmlFilePathName: String): Boolean

  protected def defaultXmlFilePathName: String =
    import pkg.c.data.Properties.*
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
    databaseFolder + fs + xmlFile

  protected def fieldExists(fieldName: String, fieldValue: String, xmlFilePathName: String): Boolean =
    import pkg.c.data.Xml.searchFieldValue
    searchFieldValue(xmlFilePathName, fieldName, fieldValue)

  protected def countRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Int =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.*

    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(_.asInstanceOf[T]).count(predicate)
    catch
      case e: Exception =>
        logger(e)
        println(s"Errore in countRecordsByFilter: ${e.getMessage}"); 0