package pkg.d.util

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.{DocumentLog, ArchivedDocument}

import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class FiltersTest:

  private var xmlFilePathName: String = _

  private var documentLogs: Seq[DocumentLog] = _
  private var documentLog1: DocumentLog = _
  private var documentLog2: DocumentLog = _
  private var documentLog3: DocumentLog = _
  private var documentLog4: DocumentLog = _
  private var documentLog5: DocumentLog = _
  private var documentLog6: DocumentLog = _
  private var documentLog7: DocumentLog = _
  private var documentLog8: DocumentLog = _
  private var documentLog9: DocumentLog = _

  private var xmlFilePathName2: String = _

  private var archivedDocuments: Seq[ArchivedDocument] = _
  private var archivedDocument1: ArchivedDocument = _
  private var archivedDocument2: ArchivedDocument = _
  private var archivedDocument5: ArchivedDocument = _
  private var archivedDocument11: ArchivedDocument = _
  private var archivedDocument120: ArchivedDocument = _

  @Before
  def setUp(): Unit =

    xmlFilePathName = inTestFilePathName("testDocumentLog.xml")
    createEmptyXmlFile(xmlFilePathName, "testRecords")

    documentLog1 = DocumentLog("1", "3", "loading", "2026-07-10", "22:19:13.86", "Rossi")
    documentLog2 = DocumentLog("2", "3", "registering", "2026-07-14", "22:19:13.86", "Bianchi")
    documentLog3 = DocumentLog("3", "4", "loading", "2026-07-14", "22:19:13.86", "Neri")
    documentLog4 = DocumentLog("4", "5", "loading", "2026-07-16", "22:19:13.86", "Rossi")
    documentLog5 = DocumentLog("5", "3", "archiving", "2026-07-18", "22:19:13.86", "Bianchi")
    documentLog6 = DocumentLog("6", "4", "registering", "2026-08-11", "22:19:13.86", "Neri")
    documentLog7 = DocumentLog("7", "6", "loading", "2026-08-20", "22:19:13.86", "Neri")
    documentLog8 = DocumentLog("8", "7", "loading", "2026-09-28", "22:19:13.86", "Bianchi")
    documentLog9 = DocumentLog("9", "5", "registering", "2026-10-01", "22:19:13.86", "Rossi")

    documentLogs = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog4,
      documentLog5,
      documentLog6,
      documentLog7,
      documentLog8,
      documentLog9
    )

    documentLogs.foreach(r => DocumentLog().recordInsert[DocumentLog](r, xmlFilePathName) )

    // Test filter on ArchivedDocument
    xmlFilePathName2 = inTestFilePathName("testArchivedDocument.xml")
    createEmptyXmlFile(xmlFilePathName2, "testRecords")

    archivedDocument1 = ArchivedDocument(
      id = "1",
      subject = "Bollette",
      loadedBy = "Neri",
      protocolNumber = "2026/1/Amministrazione",
      registeredBy = "Neri",
      archivedDate = "2026-07-30",
      archivedBy = "Rossi"
    )

    archivedDocument2 = ArchivedDocument(
      id = "2",
      subject = "Documetazione caratteristica 2025",
      loadedBy = "Neri",
      protocolNumber = "2026/2/Personale",
      registeredBy = "Neri",
      archivedDate = "2026-08-14",
      archivedBy = "Neri"
    )

    archivedDocument5 = ArchivedDocument(
      id = "5",
      subject = "CUD 2025",
      loadedBy = "Bianchi",
      protocolNumber = "2026/5/Amministrazione",
      registeredBy = "Rossi",
      archivedDate = "2026-08-21",
      archivedBy = "Neri"
    )

    archivedDocument11 = ArchivedDocument(
      id = "11",
      subject = "Piano ferie",
      loadedBy = "Rossi",
      protocolNumber = "2026/11/Segreteria",
      registeredBy = "Bianchi",
      archivedDate = "2026-09-15",
      archivedBy = "Neri"
    )

    archivedDocument120 = ArchivedDocument(
      id = "120",
      subject = "Onorificenze",
      loadedBy = "Bianchi",
      protocolNumber = "2026/120/Presidenza",
      registeredBy = "Rossi",
      archivedDate = "2026-10-28",
      archivedBy = "Bianchi"
    )

    archivedDocuments = Seq(
      archivedDocument1,
      archivedDocument2,
      archivedDocument5,
      archivedDocument11,
      archivedDocument120
    )

    archivedDocuments.foreach(r => ArchivedDocument().recordInsert[ArchivedDocument](r, xmlFilePathName2))

  @After
  def tearDown(): Unit = {
    //()
    Files.deleteIfExists(Paths.get(inTestFilePathName("testDocumentLog.xml")))
    Files.deleteIfExists(Paths.get(inTestFilePathName("testArchivedDocument.xml")))
  }

  /**
   * Filter criteria test on DocumentLog
   */
  @Test
  def testFilterByIdNotFound(): Unit =
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", "=", List("10"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), Seq.empty[DocumentLog])

  @Test
  def testFilterIDLessThan(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog5,
      documentLog6
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", "<", List("5"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterIDLessThanOrEqual(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog5,
      documentLog6
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", "<=", List("4"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterById(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog5
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", "=", List("3"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterIdGreaterThan(): Unit =
    val expectedSequence = Seq(
      documentLog7,
      documentLog8
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", ">", List("5"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterIdGreaterThanOrEqualTo(): Unit =
    val expectedSequence = Seq(
      documentLog4,
      documentLog7,
      documentLog8,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", ">=", List("5"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsIds(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog5,
      documentLog6,
      documentLog8
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", "contains", List("3", "4", "7"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateLessThan(): Unit =
    val expectedSequence = Seq(
      documentLog1
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", "<", List("2026-07-14"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateLessThanOrEqual(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", "<=", List("2026-07-14"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDate(): Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog3
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", "=", List("2026-07-14"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateGreaterThanOrEqualTo(): Unit =
    val expectedSequence = Seq(
      documentLog7,
      documentLog8,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", ">=", List("2026-08-20"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateGreaterThan(): Unit =
    val expectedSequence = Seq(
      documentLog8,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", ">", List("2026-09-27"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateInterval(): Unit =
    val expectedSequence = Seq(
      documentLog4,
      documentLog5,
      documentLog6
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", ">=", List("2026-07-15")),
        ("getProcessedDate", "<=", List("2026-08-12"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsDate(): Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog3,
      documentLog8
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", "contains", List("   2026-07-14", "2026-09-28   "))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByOperationType(): Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog6,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getOperationType", "=", List("registering"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsOperationType(): Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog5,
      documentLog6,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getOperationType", "contains", List("registering", "archiving"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterExcludesMultipleOperationType(): Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog6,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getOperationType", "!=", List("loaDing")),
        ("getOperationType", "!=", List("  archiviNG"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsProcessedBy(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog3,
      documentLog4,
      documentLog6,
      documentLog7,
      documentLog9
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedBy", "contains", List("NeRI ", "  rOSsi"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterCombined1(): Unit =
    val expectedSequence = Seq(
      documentLog7
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getDocumentId", ">", List("3")),
        ("getDocumentId", "<=", List("7")),
        ("getProcessedDate", ">=", List("2026-07-16")),
        ("getProcessedDate", "<=", List("2026-08-21")),
        ("getProcessedBy", "=", List("neri")),
        ("getOperationType", "contains", List("loading", "archiving")),
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterCombined2(): Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog3,
      documentLog4,
      documentLog6,
      documentLog7
    )
    val predicate = getDocumentOperationsLogPredicate(
      List(
        ("getProcessedDate", ">=", List("2026-06-01")),
        ("getProcessedDate", "<=", List("2026-09-30")),
        ("getProcessedBy", "contains", List("Rossi", "Neri")),
        ("getOperationType", "contains", List("loading", "registering")),
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  /**
   * Test2 Filter criteria test on ArchivedDocument
   */
  @Test
  def test2FilterByIdNotFound(): Unit =
    val predicate = getDocumentPredicate(
      List(
        ("getId", "=", List("?"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), Seq.empty[ArchivedDocument])

  @Test
  def test2FilterIDLessThan(): Unit =
    val expectedSequence = Seq(
      archivedDocument1,
      archivedDocument2
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", "<", List("5"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterIDLessThanOrEqual(): Unit =
    val expectedSequence = Seq(
      archivedDocument1,
      archivedDocument2,
      archivedDocument5
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", "<=", List("5"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterById(): Unit =
    val expectedSequence = Seq(
      archivedDocument11
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", "=", List("11"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterIdGreaterThan(): Unit =
    val expectedSequence = Seq(
      archivedDocument11,
      archivedDocument120
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", ">", List("5"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterIdGreaterThanOrEqualTo(): Unit =
    val expectedSequence = Seq(
      archivedDocument5,
      archivedDocument11,
      archivedDocument120
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", ">=", List("5"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterContainsIds(): Unit =
    val expectedSequence = Seq(
      archivedDocument2,
      archivedDocument120
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", "contains", List("2", "0", "120"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  // new +++++++++++++++++++++++++++++++++++++++

  @Test
  def test2FilterByDateLessThan(): Unit =
    val expectedSequence = Seq(
      archivedDocument1,
      archivedDocument2
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", "<", List("2026-08-15"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterByDateLessThanOrEqual(): Unit =
    val expectedSequence = Seq(
      archivedDocument1,
      archivedDocument2,
      archivedDocument5
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", "<=", List("2026-08-22"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterByDate(): Unit =
    val expectedSequence = Seq(
      archivedDocument11
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", "=", List("2026-09-15"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterByDateGreaterThanOrEqualTo(): Unit =
    val expectedSequence = Seq(
      archivedDocument11,
      archivedDocument120
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", ">=", List("2026-09-10"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterByDateGreater(): Unit =
    val expectedSequence = Seq(
      archivedDocument5,
      archivedDocument11,
      archivedDocument120
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", ">", List("2026-08-20"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterByDateInterval(): Unit =
    val expectedSequence = Seq(
      archivedDocument2,
      archivedDocument5,
      archivedDocument11
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", ">=", List("2026-08-02")),
        ("getArchivedDate", "<=", List("2026-09-20"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterContainsDate(): Unit =
    val expectedSequence = Seq(
      archivedDocument2,
      archivedDocument11
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedDate", "contains", List("   2026-08-14", "2026-09-15   "))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterContainsArchivedBy(): Unit =
    val expectedSequence = Seq(
      archivedDocument1,
      archivedDocument120
    )
    val predicate = getDocumentPredicate(
      List(
        ("getArchivedBy", "contains", List("RoSSi ", "  Bianchi"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterByOperators(): Unit =
    val expectedSequence = Seq(
      archivedDocument11
    )
    val predicate = getDocumentPredicate(
      List(
        ("getLoadedBy", "=", List("RoSSi")),
        ("getRegisteredBy", "=", List("bianchi")),
        ("getArchivedBy", "=", List("NERI"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterTextInSubject(): Unit =
    val expectedSequence = Seq(
      archivedDocument2,
      archivedDocument5
    )
    val predicate = getDocumentPredicate(
      List(
        ("getSubject", "contains", List("2025"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterCombined1(): Unit =
    val expectedSequence = Seq(
      archivedDocument1
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", ">=", List("1")),
        ("getId", "<", List("110")),
        ("getArchivedDate", ">=", List("2026-06-01")),
        ("getArchivedDate", "<=", List("2026-09-10")),
        ("getArchivedBy", "=", List("Rossi"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)

  @Test
  def test2FilterCombined2(): Unit =
    val expectedSequence = Seq(
      archivedDocument2,
      archivedDocument5
    )
    val predicate = getDocumentPredicate(
      List(
        ("getId", ">=", List("2")),
        ("getId", "<", List("110")),
        ("getArchivedDate", ">=", List("2026-06-01")),
        ("getArchivedDate", "<=", List("2026-09-10"))
      )
    )
    assertEquals(ArchivedDocument().getRecordsByFilter[ArchivedDocument](predicate, xmlFilePathName2), expectedSequence)





