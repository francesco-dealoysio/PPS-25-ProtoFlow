package pkg.c.data

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.Properties.*
import pkg.d.util.Util.inTestFilePathName
import java.io.File
import java.nio.file.{Files, Paths}
import java.util.Properties

class PropertiesTest:

  val propsFile: String = inTestFilePathName("testfile.properties")
  val comment: String = "Configuration file"

  @Before
  def setUp(): Unit =
    PropertiesTest.filePathName = propsFile

  @After
  def tearDown(): Unit = ()

  @Test
  def testCreatePropsFile(): Unit =
    createPropsFile(propsFile, comment)
    assertTrue(File(propsFile).exists())

  @Test
  def testSetPropsFileProperty(): Unit =
    val key: String = "classifica"
    val value: String = "amministrazione"
    setPropsFileProperty(propsFile: String, key: String, value: String)
    assertEquals("amministrazione", getPropsFileProperty(propsFile, key))

  @Test
  def testGetPropsFileProperty(): Unit =
    val key: String = "classifica"
    val value: String = "amministrazione"
    setPropsFileProperty(propsFile: String, key: String, value: String)
    assertEquals("amministrazione", getPropsFileProperty(propsFile, key))
    assertEquals("<not set>", getPropsFileProperty(propsFile, "inesistente"))

  @Test
  def testRemovePropsFileProperty(): Unit =
    clearPropsFileProperties(propsFile)
    removePropsFileProperty(propsFile, "colore") // inesistente
    setPropsFileProperty(propsFile, "colore", "rosso")
    assertEquals("rosso", getPropsFileProperty(propsFile, "colore"))
    removePropsFileProperty(propsFile, "colore")
    assertEquals("<not set>", getPropsFileProperty(propsFile, "colore"))

  @Test
  def testClearPropsFileProperties(): Unit =
    setPropsFileProperty(propsFile, "colore", "rosso")
    setPropsFileProperty(propsFile, "auto", "topolina")
    assertEquals("rosso", getPropsFileProperty(propsFile, "colore"))
    assertEquals("topolina", getPropsFileProperty(propsFile, "auto"))
    clearPropsFileProperties(propsFile)
    assertEquals("<not set>", getPropsFileProperty(propsFile, "colore"))
    assertEquals("<not set>", getPropsFileProperty(propsFile, "auto"))

  @Test
  def testGetPropsFileProperties(): Unit =
    setPropsFileProperty(propsFile, "colore", "rosso")
    setPropsFileProperty(propsFile, "auto", "topolina")
    val props = getPropsFileProperties(propsFile)
    assertEquals("rosso", props.getProperty("colore"))
    assertEquals("topolina", props.getProperty("auto"))

  @Test
  def testSetPropsFileProperties(): Unit =
    val props = new Properties()
    props.setProperty("figura", "triangolo")
    props.setProperty("altezza", "10")
    setPropsFileProperties(propsFile, props)
    assertEquals("triangolo", getPropsFileProperty(propsFile, "figura"))
    assertEquals("10", getPropsFileProperty(propsFile, "altezza"))

object PropertiesTest:

  var filePathName: String = _

  @BeforeClass
  def beforeAll(): Unit = ()

  @AfterClass
  def afterAll(): Unit =
    //Files.deleteIfExists(Paths.get(inTestFilePathName(filePathName)))
    println(filePathName)

