package pkg.d.util

import org.junit.*
import org.junit.Assert.*
import pkg.d.util.Util.inTestFilePathName
import java.nio.file.{Files, Paths}

class IdGenTest:

  private val IdFilePathName: String = inTestFilePathName("fileId")

  @Before
  def setUp(): Unit =
    Files.deleteIfExists(Paths.get(IdFilePathName))

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(IdFilePathName))

  @Test
  def testIdGenInit(): Unit =
    val id = IdGen(IdFilePathName)
    assertTrue(Files.exists(Paths.get(IdFilePathName)))
    assertEquals(id, "0")

  @Test
  def testIdGenInitFromStartValue(): Unit =
    val id = IdGen(IdFilePathName, 5)
    assertEquals(id, "5")

  @Test
  def testIdGenPickNextValue(): Unit =
    IdGen(IdFilePathName,5)
    val nextId = IdGen(IdFilePathName,5)
    assertEquals(nextId, "6")