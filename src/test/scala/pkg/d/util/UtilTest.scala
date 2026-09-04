package pkg.d.util

import org.junit.*
import org.junit.Assert.*
import pkg.c.data.FileSystem
import pkg.c.data.Properties.getPropsFileProperty
import pkg.d.util.Util.*
import java.nio.file.{Files, Paths}

class UtilTest:

  private val fs = java.io.File.separator
  private val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
  private val testFile = ".testfile"

  private val properties = Seq(
    "database",
    "documents",
    "log",
    "ids",
    "test",
    "prints"
  )

  @Before
  def setUp(): Unit = {
    properties.foreach { property =>
      val filePathName = getPropsFileProperty(baseFolder + fs + "protoflow.properties", property + ".folder") + fs + testFile
      FileSystem.createFile(filePathName, property)
    }
  }

  @After
  def tearDown(): Unit =
    properties.foreach { property =>
      val filePathName = getPropsFileProperty(baseFolder + fs + "protoflow.properties", property + ".folder") + fs + testFile
      Files.deleteIfExists(Paths.get(filePathName))
    }

  @Test
  def testInDatabaseFilePathName(): Unit =
      val readFile = Paths.get(inDatabaseFilePathName(testFile)).getFileName.toString
      assertEquals(readFile, testFile)
      
  @Test
  def testInDocumentsFilePathName(): Unit =
      val readFile = Paths.get(inDocumentsFilePathName(testFile)).getFileName.toString
      assertEquals(readFile, testFile)
      
  @Test
  def testInLogFilePathName(): Unit =
      val readFile = Paths.get(inLogFilePathName(testFile)).getFileName.toString
      assertEquals(readFile, testFile)
      
  @Test
  def testInIdsFilePathName(): Unit =
      val readFile = Paths.get(inIdsFilePathName(testFile)).getFileName.toString
      assertEquals(readFile, testFile)
      
  @Test
  def testInTestFilePathName(): Unit =
      val readFile = Paths.get(inTestFilePathName(testFile)).getFileName.toString
      assertEquals(readFile, testFile)
      
  @Test
  def testInPrintsFilePathName(): Unit =
      val readFile = Paths.get(inPrintsFilePathName(testFile)).getFileName.toString
      assertEquals(readFile, testFile)
      