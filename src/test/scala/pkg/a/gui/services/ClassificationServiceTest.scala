package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Classification
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class ClassificationServiceTest:

  private var xmlFilePathName: String = _
  private var idFilePathName: String = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("classificationServiceTest.xml")
    idFilePathName = inTestFilePathName("classificationServiceId")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    Files.deleteIfExists(Paths.get(idFilePathName))

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(xmlFilePathName))
    Files.deleteIfExists(Paths.get(idFilePathName))

  @Test
  def testAddClassification(): Unit =
    val result = ClassificationService.addClassification(
      classification = "Riservato",
      description = "Documento ad accesso riservato",
      classificationsFilePathName = xmlFilePathName,
      classificationIdFilePathName = idFilePathName
    )
    assertTrue(result.isRight)
    val classification = result.toOption.get
    assertEquals("Riservato", classification.getClassification)
    assertEquals("Documento ad accesso riservato", classification.getDescription)
    assertEquals(classification, Classification().getRecordById[Classification](classification.getId, xmlFilePathName))

  @Test
  def testAddDuplicateClassification(): Unit =
    ClassificationService.addClassification(
      "Riservato",
      "Prima descrizione",
      xmlFilePathName,
      idFilePathName
    )
    val result = ClassificationService.addClassification(
      "Riservato",
      "Seconda descrizione",
      xmlFilePathName,
      idFilePathName
    )
    assertEquals(Left("Errore durante l'inserimento della classifica"), result)