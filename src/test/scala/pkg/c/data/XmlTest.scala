package pkg.c.data

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Account
import pkg.c.data.Xml.{createEmptyXmlFile, getRecordFromXML, insertElemIntoXML}
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
    Files.deleteIfExists(Paths.get(inTestFilePathName("file1.xml")))
    Files.deleteIfExists(Paths.get(inTestFilePathName("file2.xml")))
  //  Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))

  @Test
  def testCreateEmptyXmlFile: Unit =
    XML.save(inTestFilePathName("file1.xml"), empty, enc = "UTF-8", xmlDecl = true)
    createEmptyXmlFile(inTestFilePathName("file2.xml"), "root")
    val file1 = inTestFilePathName("file1.xml")
    val file2 = inTestFilePathName("file2.xml")
    val content1 = readAllLines(Paths.get(file1)).asScala.mkString("\n")
    val content2 = readAllLines(Paths.get(file2)).asScala.mkString("\n")
    assertEquals("Files differ in content!", content1, content2)


  @Test
  def testinsertElemIntoXML: Unit =
    createEmptyXmlFile(inTestFilePathName("test.xml"), "accounts")
    insertElemIntoXML(inTestFilePathName("test.xml"), account)
    assertEquals(getRecordFromXML(inTestFilePathName("test.xml"), classOf[Account])(0), account)

  @Test
  def testCleanXmlFile: Unit =
    assertTrue(false)
