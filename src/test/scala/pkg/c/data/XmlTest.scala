package pkg.c.data

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.{Account, Role}
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, getRecordsFromXML, insertElemIntoXML, saveXML}
import pkg.d.util.Util.{inTestFilePathName, md5}

import java.*
import java.io.{File, IOException}
import java.nio.*
import java.nio.file.Files.readAllLines
import java.nio.file.*
import scala.xml.Elem
import scala.jdk.CollectionConverters.*
import scala.xml.XML

class XmlTest:

  private var xmlFilePathName: String = _
  private var account: Account = _
  private var empty: Elem = _
  private var elem: Elem = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")

    account = Account(
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

    empty = <root/>

    elem =
      <record>
        <id>1</id>
        <surname>de aloysio</surname>
        <name>francesco</name>
        <email>francesco.dealoysio@studio.unibo.it</email>
        <phone>06/11111111</phone>
        <role>admin</role>
        <area>presidenza</area>
        <assignment>presidente</assignment>
        <username>frank</username>
        <password>
          {md5("topolino")}
        </password>
      </record>

  @After
  def tearDown(): Unit =
    //Files.deleteIfExists(Paths.get(inTestFilePathName("file1.xml")))
    //Files.deleteIfExists(Paths.get(inTestFilePathName("file2.xml")))
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testCreateEmptyXmlFile: Unit =
    createEmptyXmlFile(inTestFilePathName("file1.xml"), "root")
    val file1 = inTestFilePathName("file1.xml")
    val content1 = readAllLines(Paths.get(file1)).asScala.mkString("\n")
    assertEquals("Files differ in content!", "<root></root>", content1)

  @Test
  def testCleanXmlFile: Unit =
    createEmptyXmlFile(inTestFilePathName("file1.xml"), "accounts")
    createEmptyXmlFile(inTestFilePathName("file2.xml"), "accounts")
    insertElemIntoXML(inTestFilePathName("file2.xml"), account)
    cleanXmlFile(inTestFilePathName("file2.xml"))
    val file1 = inTestFilePathName("file1.xml")
    val file2 = inTestFilePathName("file2.xml")
    val content1 = readAllLines(Paths.get(file1)).asScala.mkString("\n")
    val content2 = readAllLines(Paths.get(file2)).asScala.mkString("\n")
    assertEquals("Files differ in content!", content1, content2)

  @Test
  def testRecordsFromXML: Unit =
    assertTrue(false)

  @Test
  def testSaveXML: Unit =
    val role = new Role("1", "admin", "Attività di amministrazione del sistema")
    createEmptyXmlFile(inTestFilePathName("file1.xml"), "roles")
    insertElemIntoXML(inTestFilePathName("file1.xml"), role)

    var roles: Elem = <roles>
      <record>
        <id>1</id>
        <role>admin</role>
        <description>Attività di amministrazione del sistema</description>
      </record>
    </roles>

    saveXML(inTestFilePathName("file2.xml"), roles)

    val file1 = inTestFilePathName("file1.xml")
    val file2 = inTestFilePathName("file2.xml")
    val content1 = readAllLines(Paths.get(file1)).asScala.mkString("\n")
    val content2 = readAllLines(Paths.get(file2)).asScala.mkString("\n")
    assertEquals("Files differ in content!", content1, content2)

  @Test
  def testInsertElemIntoXML: Unit =
    createEmptyXmlFile(inTestFilePathName("test.xml"), "accounts")
    insertElemIntoXML(inTestFilePathName("test.xml"), account)
    assertEquals(getRecordsFromXML(inTestFilePathName("test.xml"), classOf[Account])(0), account)

  @Test
  def testUpdateElemOfXLM: Unit =
    assertTrue(false)

  @Test
  def testRemoveElemFromXLM: Unit =
    assertTrue(false)

  @Test
  def testSearchFieldValue: Unit =
    assertTrue(false)
