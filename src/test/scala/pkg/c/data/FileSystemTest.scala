package pkg.c.data

import org.junit.*
import org.junit.Assert.*
import pkg.d.util.Util.inTestFilePathName
import java.nio.file.{Files, Path, Paths}
import scala.io.Source

class FileSystemTest:

  private val fs = java.io.File.separator
  private val dirPath: Path = Paths.get(inTestFilePathName("dir"))
  private val filePath: Path = Paths.get(inTestFilePathName("dir") + fs + "file")

  @Before
  def setUp(): Unit =
    Files.deleteIfExists(filePath)
    Files.deleteIfExists(dirPath)

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(filePath)
    Files.deleteIfExists(dirPath)

  @Test
  def testCreateDirectory(): Unit =
    FileSystem.createDirectory(inTestFilePathName("dir"))
    assertTrue(Files.exists(dirPath))

  @Test
  def testCreateFile(): Unit =
    FileSystem.createDirectory(inTestFilePathName("dir"))
    FileSystem.createFile(inTestFilePathName("dir") + fs + "file", "pippo")
    assertTrue(Files.exists(filePath))

  @Test
  def testCreateExistingDirectory(): Unit =
    FileSystem.createDirectory(inTestFilePathName("dir"))
    Files.createFile(Paths.get(inTestFilePathName("dir") + fs + "file"))
    FileSystem.createDirectory(inTestFilePathName("dir"))
    assertTrue(Files.exists(filePath))

  @Test
  def testCreateExistingFile(): Unit =
    FileSystem.createDirectory(inTestFilePathName("dir"))
    FileSystem.createFile(inTestFilePathName("dir") + fs + "file", "pippo")
    FileSystem.createFile(inTestFilePathName("dir") + fs + "file", "pluto")

    val source = Source.fromFile(inTestFilePathName("dir") + fs + "file")
    val content = source.getLines().toList.head
    source.close()

    assertEquals("pippo", content)

  @Test
  def testCreateFileInNonExistentDirectory(): Unit =
    FileSystem.createFile(inTestFilePathName("dir") + fs + "file", "pippo")
    assertFalse(Files.exists(filePath))
