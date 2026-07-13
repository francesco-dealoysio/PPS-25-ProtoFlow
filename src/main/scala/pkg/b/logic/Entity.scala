package pkg.b.logic

trait Entity:
  def getRecordById(id: String, xmlFilePathName: String): Any
  def getRecords(xmlFilePathName: String): Seq[Any]
  def recordInsert(obj: Any, xmlFilePathName: String): Unit
  def recordUpdate(obj: Any, xmlFilePathName: String): Unit
  def recordDelete(id: String, xmlFilePathName: String): Unit
  def getRecordsByFilter(condition: Boolean, xmlFilePathName: String): Int
  def xmlFile: String
