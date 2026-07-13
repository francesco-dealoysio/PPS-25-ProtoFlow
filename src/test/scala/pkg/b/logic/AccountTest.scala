package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.xmlManagement.Xml.{creatEmptyXmlFile, insertElemIntoXML}
import pkg.d.util.Properties.getPropsFileProperty
import pkg.d.util.Util.md5

class AccountTest:

  @Before
  val fs = java.io.File.separator
  val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  val databaseFolder = getPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder")
  val xmlFileName = "test.xml"
  val xmlFilePathName = databaseFolder + fs + xmlFileName

  creatEmptyXmlFile(xmlFilePathName, "test_records")

  val account1 = Account(
    "1",
    "de aloysio",
    "francesco",
    "francesco.dealoysio@studio.unibo.it",
    "06/11111111",
    "admin",
    "presidenza",
    "presidente",
    "frank",
    md5("topolino")
  )

  @Test
  def testGetRecords: Unit =
    assertTrue(true)

  @Test
  def testGetRecordById: Unit =
    assertTrue(true)

  @Test
  def testGetRecordsByFilter: Unit =
    assertTrue(true)

  @Test
  def testRecordInsert: Unit = {
    insertElemIntoXML(xmlFilePathName, account1)

    assertTrue(true)
  }

  @Test
  def testGetRecordUpdate: Unit =
    assertTrue(true)

  @Test
  def testGetRecordDelete: Unit =
    assertTrue(true)



