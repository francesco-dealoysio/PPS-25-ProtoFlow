package pkg.c.data

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.{Account, Role}
import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile, getRecordsFromXML, insertElemIntoXML, removeElemFromXML, saveXML, searchFieldValue, updateElemOfXML}
import pkg.d.util.Util.{inTestFilePathName, cipher}
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
  private var emptyAccount: Account = _
  private var role1: Role = _
  private var role2: Role = _
  private var role3: Role = _
  private var empty: Elem = _
  private var elem: Elem = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    emptyAccount = new Account

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
      cipher("topolino")
    )

    role1 = Role(
      "1",
      "admin",
      "Amministrazione"
    )

    role2 = Role(
      "2",
      "oper",
      "Protocollazione"
    )

    role3 = Role(
      "3",
      "viewer",
      "Visualizzazione"
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
          {cipher("topolino")}
        </password>
      </record>

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(inTestFilePathName("file1.xml")))
    Files.deleteIfExists(Paths.get(inTestFilePathName("file2.xml")))
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
  def testGetRecordsFromXML: Unit =
    createEmptyXmlFile(xmlFilePathName, "roles")
    insertElemIntoXML(xmlFilePathName, role1)
    insertElemIntoXML(xmlFilePathName, role2)
    insertElemIntoXML(xmlFilePathName, role3)
    assertEquals(getRecordsFromXML(inTestFilePathName("test.xml"), classOf[Role]), Seq(role1, role2, role3))

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
    createEmptyXmlFile(xmlFilePathName, "accounts")
    insertElemIntoXML(xmlFilePathName, account)
    account.setName("Paolo")
    assertTrue(updateElemOfXML(xmlFilePathName, account))
    val updatedRecord = Account().getRecordById[Account]("1", xmlFilePathName)
    assertEquals(updatedRecord.getName, "Paolo")

  @Test
  def testRemoveElemFromXLM: Unit =
    createEmptyXmlFile(inTestFilePathName("test.xml"), "accounts")
    insertElemIntoXML(inTestFilePathName("test.xml"), account)
    assertEquals(Account().getRecordById[Account]("1", xmlFilePathName), account)
    assertTrue(removeElemFromXML(inTestFilePathName("test.xml"), "1"))
    assertEquals(Account().getRecordById[Account](account.getId, xmlFilePathName), emptyAccount)

  @Test
  def testSearchFieldValue: Unit =
    createEmptyXmlFile(xmlFilePathName, "roles")
    insertElemIntoXML(xmlFilePathName, role1)
    insertElemIntoXML(xmlFilePathName, role2)
    insertElemIntoXML(xmlFilePathName, role3)
    assertTrue(searchFieldValue(xmlFilePathName, "description", "Amministrazione"))
