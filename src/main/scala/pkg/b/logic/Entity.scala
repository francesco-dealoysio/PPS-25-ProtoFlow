package pkg.b.logic

trait Entity:
  def getRecordById(id: String, xmlFilePathName: String): Any
  def getRecords(xmlFilePathName: String): Seq[Any]
  def recordInsert(obj: Any, xmlFilePathName: String): Boolean
  def recordUpdate(obj: Any, xmlFilePathName: String): Boolean
  def recordDelete(id: String, xmlFilePathName: String): Boolean
  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Seq[T]
  /*
  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String = defaultXmlFilePathName, classType: Class[T]): Seq[T] =
    try
      getRecordFromXML(xmlFilePathName, classType)
        .map(a => a.asInstanceOf[Account]).filter(predicate)
    catch
      case e: Exception =>
        println(s"Errore in getRecordByFilter: ${e.getMessage}")
        Seq.empty[Account]
 */       
  def xmlFile: String
