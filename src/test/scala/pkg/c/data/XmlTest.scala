package pkg.c.data

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Account
import pkg.c.data.Xml.{createEmptyXmlFile, getRecordFromXML, insertElemIntoXML, cleanXmlFile}
import pkg.d.util.Util.md5
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
  private var tempDirectory: Path = _
  private var account: Account = _
  private var empty: Elem = _
  private var elem: Elem = _

  @Before
  def setUp(): Unit =
    tempDirectory = Files.createTempDirectory("protoflow-xml-test-")
    xmlFilePathName = testPath("test.xml")

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
    Option(tempDirectory).foreach: directory =>
      if Files.exists(directory) then
        val paths = Files.walk(directory)
        try
          paths
            .sorted(java.util.Comparator.reverseOrder())
            .forEach(path => Files.deleteIfExists(path))
        finally
          paths.close()

  @Test
  def testCreateEmptyXmlFile: Unit =
    val file1 = testPath("file1.xml")
    val file2 = testPath("file2.xml")
    XML.save(file1, empty, enc = "UTF-8", xmlDecl = true)
    createEmptyXmlFile(file2, "root")
    val content1 =
      readAllLines(Path.of(file1))
        .asScala
        .mkString("\n")
    val content2 =
      readAllLines(Path.of(file2))
        .asScala
        .mkString("\n")
    assertEquals("Files differ in content!", content1, content2)

  @Test
  def testInsertElemIntoXML: Unit =
    createEmptyXmlFile(xmlFilePathName, "accounts")
    insertElemIntoXML(xmlFilePathName, account)
    assertEquals(account, getRecordFromXML(xmlFilePathName, classOf[Account]).head)

  @Test
  def testCleanXmlFile: Unit =
    createEmptyXmlFile(xmlFilePathName, "accounts")
    insertElemIntoXML(xmlFilePathName, account)
    assertFalse(getRecordFromXML(xmlFilePathName, classOf[Account]).isEmpty)
    cleanXmlFile(xmlFilePathName)
    assertTrue(getRecordFromXML(xmlFilePathName, classOf[Account]).isEmpty)

  private def testPath(fileName: String): String =
    tempDirectory
      .resolve(fileName)
      .toString