package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.services.DocumentManagementControlService
import pkg.a.gui.services.DocumentManagementControlService.Stages
import pkg.b.logic.{ArchivedDocument, LoadedDocument, RegisteredDocument}
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class DocumentManagementControlServiceTest:

  private var loadedXmlFile: String = _
  private var registeredXmlFile: String = _
  private var archivedXmlFile: String = _

  @Before
  def setUp(): Unit =
    loadedXmlFile = inTestFilePathName("testManagementLoaded.xml")
    registeredXmlFile = inTestFilePathName("testManagementRegistered.xml")
    archivedXmlFile = inTestFilePathName("testManagementArchived.xml")

    createEmptyXmlFile(loadedXmlFile, "testRecords")
    createEmptyXmlFile(registeredXmlFile, "testRecords")
    createEmptyXmlFile(archivedXmlFile, "testRecords")

    LoadedDocument().recordInsert(
      LoadedDocument(id = "3", subject = "Bollette", processedDate = "2026-08-01", processedBy = "Neri"),
      loadedXmlFile
    )

    RegisteredDocument().recordInsert(
      RegisteredDocument(
        id = "1",
        subject = "Contratto",
        protocolNumber = "2026/1/Amministrazione",
        registeredDate = "2026-07-20",
        registeredBy = "Rossi",
        loadedBy = "Bianchi"
      ),
      registeredXmlFile
    )

    ArchivedDocument().recordInsert(
      ArchivedDocument(
        id = "2",
        subject = "Fattura",
        protocolNumber = "2026/2/Personale",
        registeredDate = "2026-07-10",
        registeredBy = "Rossi",
        archivedDate = "2026-07-25",
        archivedBy = "Verdi",
        archiveLocation = "Armadio 3"
      ),
      archivedXmlFile
    )

  @After
  def tearDown(): Unit =
    Seq(loadedXmlFile, registeredXmlFile, archivedXmlFile)
      .foreach(file => Files.deleteIfExists(Paths.get(file)))

  @Test
  def testGetManagedDocumentsMergesAllThreeStagesSortedById(): Unit =
    val result =
      DocumentManagementControlService.getManagedDocuments(
        loadedFilePathName = loadedXmlFile,
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    assertEquals(List("1", "2", "3"), result.map(_.id))

  @Test
  def testLoadedDocumentIsMappedToLoadingStage(): Unit =
    val result =
      DocumentManagementControlService.getManagedDocuments(
        loadedFilePathName = loadedXmlFile,
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    val loaded = result.find(_.id == "3").get
    assertEquals(Stages.Loading, loaded.stage)
    assertEquals("", loaded.protocolNumber)
    assertEquals("Neri", loaded.operator)

  @Test
  def testRegisteredDocumentIsMappedToRegisteringStage(): Unit =
    val result =
      DocumentManagementControlService.getManagedDocuments(
        loadedFilePathName = loadedXmlFile,
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    val registered = result.find(_.id == "1").get
    assertEquals(Stages.Registering, registered.stage)
    assertEquals("2026/1/Amministrazione", registered.protocolNumber)
    assertEquals("2026-07-20", registered.registeredDate)
    assertEquals("Rossi", registered.operator)
    assertEquals("", registered.archivedBy)

  @Test
  def testArchivedDocumentIsMappedToArchivingStage(): Unit =
    val result =
      DocumentManagementControlService.getManagedDocuments(
        loadedFilePathName = loadedXmlFile,
        registeredFilePathName = registeredXmlFile,
        archivedFilePathName = archivedXmlFile
      )

    val archived = result.find(_.id == "2").get
    assertEquals(Stages.Archiving, archived.stage)
    assertEquals("2026/2/Personale", archived.protocolNumber)
    assertEquals("Armadio 3", archived.archiveLocation)
    assertEquals("Verdi", archived.operator)
