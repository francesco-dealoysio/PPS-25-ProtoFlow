package pkg.c.data

import org.junit.*
import org.junit.Assert.*

import java.io.{File, IOException}
import java.nio.file.{Files, Paths}
import java.util.Properties

class PropertiesTest:

  @Before
  private val propsFile: String = "testfile.properties"
  private val comment: String = "Configuration file"
  clearPropsFileProperties(propsFile)

  @Test
  def testCreatePropsFile: Unit =
    createPropsFile(propsFile, comment)
    assertTrue(File("testfile.properties").exists())

  @Test
  def testSetPropsFileProperty: Unit =
    val key: String = "classifica"
    val value: String = "amministrazione"
    setPropsFileProperty(propsFile: String, key: String, value: String)
    assertEquals("amministrazione", getPropsFileProperty(propsFile, key))

  @Test
  def testGetPropsFileProperty: Unit =
    val key: String = "classifica"
    val value: String = "amministrazione"
    setPropsFileProperty(propsFile: String, key: String, value: String)
    assertEquals("amministrazione", getPropsFileProperty(propsFile, key))
    assertEquals("<not set>", getPropsFileProperty(propsFile, "inesistente"))

  @Test
  def testRemovePropsFileProperty: Unit = {
    clearPropsFileProperties(propsFile)
    removePropsFileProperty(propsFile, "colore") // inesistente
    setPropsFileProperty(propsFile, "colore", "rosso")
    assertEquals("rosso", getPropsFileProperty(propsFile, "colore"))
    removePropsFileProperty(propsFile, "colore")
    assertEquals("<not set>", getPropsFileProperty(propsFile, "colore"))
  }

  @Test
  def testClearPropsFileProperties: Unit =
    setPropsFileProperty(propsFile, "colore", "rosso")
    setPropsFileProperty(propsFile, "auto", "topolina")
    assertEquals("rosso", getPropsFileProperty(propsFile, "colore"))
    assertEquals("topolina", getPropsFileProperty(propsFile, "auto"))
    clearPropsFileProperties(propsFile)
    assertEquals("<not set>", getPropsFileProperty(propsFile, "colore"))
    assertEquals("<not set>", getPropsFileProperty(propsFile, "auto"))

  @Test
  def testGetPropsFileProperties: Unit =
    setPropsFileProperty(propsFile, "colore", "rosso")
    setPropsFileProperty(propsFile, "auto", "topolina")
    val props = getPropsFileProperties(propsFile)
    assertEquals("rosso", props.getProperty("colore"))
    assertEquals("topolina", props.getProperty("auto"))

  @Test
  def testSetPropsFileProperties: Unit =
    val props = new Properties()
    props.setProperty("figura", "triangolo")
    props.setProperty("altezza", "10")
    setPropsFileProperties(propsFile, props)
    assertEquals("triangolo", getPropsFileProperty(propsFile, "figura"))
    assertEquals("10", getPropsFileProperty(propsFile, "altezza"))

