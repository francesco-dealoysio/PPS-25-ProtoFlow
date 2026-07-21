package pkg.b.logic

//import pkg.c.data.Xml.{getRecordFromXML, removeElemFromXML}
//import pkg.d.util.Logger.logger

trait Entity:
  def xmlFile: String
  //def getRecords(xmlFilePathName: String): Seq[Any]
  //def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String, classType: Class[T]): Seq[T]
  //def getRecordById(id: String, xmlFilePathName: String): Any
  def recordInsert(obj: Any, xmlFilePathName: String): Boolean
  def recordUpdate(obj: Any, xmlFilePathName: String): Boolean
  //def recordDelete(id: String, xmlFilePathName: String): Boolean

  def getRecords[T](xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      getRecordFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(r => r.asInstanceOf[T])
    catch
      case e: Exception =>
        logger(e)
        Seq.empty[T]

  def getRecordsByFilter[T](predicate: T => Boolean, xmlFilePathName: String = defaultXmlFilePathName): Seq[T] =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    try
      getRecordFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(_.asInstanceOf[T]).filter(predicate)
    catch
      case e: Exception =>
        logger(e)
        Seq.empty[T]
/*
  def testPredicate[T](id: String, xmlFilePathName: String = defaultXmlFilePathName): T =
    val className = asInstanceOf[T].getClass.getSimpleName
    println(className)
    val found = getRecordsByFilter[Account](o => o.getId == id, xmlFilePathName)
    //found

    val constructor = asInstanceOf[T].getClass.getDeclaredConstructor()
    constructor.setAccessible(true)
    constructor.newInstance().asInstanceOf[T]

    found.asInstanceOf[T]
*/
  def getRecordById[T](predicate: T => Boolean, xmlFilePathName: String = defaultXmlFilePathName): T =
    import pkg.c.data.Xml.getRecordFromXML
    import pkg.d.util.Logger.logger
    val constructor = asInstanceOf[T].getClass.getDeclaredConstructor()
    constructor.setAccessible(true)
    try
      val found = getRecordFromXML(xmlFilePathName, asInstanceOf[T].getClass)
        .map(_.asInstanceOf[T]).filter(predicate)
      if (found.length > 0) then
        found.head
      else
        constructor.newInstance().asInstanceOf[T]
    catch
      case e: Exception =>
        logger(e)
        constructor.newInstance().asInstanceOf[T]

  def recordDelete(id: String, xmlFilePathName: String = defaultXmlFilePathName): Boolean =
    import pkg.c.data.Xml.removeElemFromXML
    import pkg.d.util.Logger.logger
    try
      removeElemFromXML(xmlFilePathName, id)
    catch
      case e: Exception =>
        logger(e)
        false

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

@main def tryEntity: Unit =
  println
 /*
  import pkg.d.util.Util.inDatabaseFilePathName
  println
  val a = Account().testPredicate[Account]("1", inDatabaseFilePathName("accounts.xml"))
  println(a.getId)

  */