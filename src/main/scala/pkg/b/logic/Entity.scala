package pkg.b.logic


//import scala.reflect.ClassTag

trait Entity:
  def xmlFile: String
  def getRecords(xmlFilePathName: String): Seq[Any]
  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Seq[T]
  def getRecordById(id: String, xmlFilePathName: String): Any
  def recordInsert(obj: Any, xmlFilePathName: String): Boolean
  def recordUpdate(obj: Any, xmlFilePathName: String): Boolean
  def recordDelete(id: String, xmlFilePathName: String): Boolean

/*
  def _getRecords[T: ClassTag](xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      getRecordFromXML(xmlFilePathName, summon[ClassTag[T]].runtimeClass)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e)
        Seq.empty[T]
*/
/*
  def _getRecords[T](xmlFilePathName: String = defaultXmlFilePathName, classType: Class[T]): Seq[T] =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e)
        Seq.empty[T]
*/

  def _getRecords[T](xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      getRecordFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e)
        Seq.empty[T]

  protected def defaultXmlFilePathName: String =
    import pkg.d.util.Util.inDatabaseFilePathName
    inDatabaseFilePathName(xmlFile)

  protected def fieldExists(fieldName: String, fieldValue: String, xmlFilePathName: String): Boolean =
    import pkg.c.data.Xml.searchFieldValue
    searchFieldValue(xmlFilePathName, fieldName, fieldValue)

  protected def countRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Int =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(_.asInstanceOf[T]).count(predicate)
    catch
      case e: Exception =>
        logger(e); 0

object Entity:

  def saluti: Unit =
    println("Ciao")
/*
  def _getRecords[T](xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      //getRecordFromXML(xmlFilePathName, this.getClass)
      getRecordFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e)
        Seq.empty[T]
*/

@main def tryEntity: Unit =
  Entity.saluti