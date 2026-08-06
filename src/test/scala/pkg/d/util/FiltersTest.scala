package pkg.d.util

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.{LoadedDocument, DocumentLog}

import pkg.c.data.Xml.{cleanXmlFile, createEmptyXmlFile}
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

  private var empty: DocumentLog = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("test.xml")
    createEmptyXmlFile(xmlFilePathName, "test_records")

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

  @After
  def tearDown(): Unit = {
    //()
    Files.deleteIfExists(Paths.get(inTestFilePathName("test.xml")))
  }

  @Test
  def testFilterByIdNotFound: Unit =
    val expectedSequence = Seq(
      empty
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", "=", List("10"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), Seq.empty[DocumentLog])

  @Test
  def testFilterIDLessThan: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog5,
      documentLog6
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", "<", List("5"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterIDLessThanOrEqual: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog5,
      documentLog6
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", "<=", List("4"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterById: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog5
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", "=", List("3"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterIdGreaterThan: Unit =
    val expectedSequence = Seq(
      documentLog7,
      documentLog8
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", ">", List("5"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterIdGreaterThanOrEqualTo: Unit =
    val expectedSequence = Seq(
      documentLog4,
      documentLog7,
      documentLog8,
      documentLog9
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", ">=", List("5"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsIds: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3,
      documentLog5,
      documentLog6,
      documentLog8
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getDocumentId", "contains", List("3", "4", "7"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDate: Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog3
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", "=", List("2026-07-14"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateLessThan: Unit =
    val expectedSequence = Seq(
      documentLog1
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", "<", List("2026-07-14"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateLessThanOrEqual: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog2,
      documentLog3
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", "<=", List("2026-07-14"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateGreaterThan: Unit =
    val expectedSequence = Seq(
      documentLog8,
      documentLog9
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", ">=", List("2026-09-27"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByDateInterval: Unit =
    val expectedSequence = Seq(
      documentLog4,
      documentLog5,
      documentLog6
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", ">=", List("2026-07-15")),
        ("getProcessedDate", "<=", List("2026-08-12"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsDate: Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog3,
      documentLog8
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", "contains", List("   2026-07-14", "2026-09-28   "))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterByOperationType: Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog6,
      documentLog9
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getOperationType", "=", List("registering"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsOperationType: Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog5,
      documentLog6,
      documentLog9
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getOperationType", "contains", List("registering", "archiving"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterExcludesMultipleOperationType: Unit =
    val expectedSequence = Seq(
      documentLog2,
      documentLog6,
      documentLog9
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getOperationType", "!=", List("loaDing")),
        ("getOperationType", "!=", List("  archiviNG"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterContainsProcessedBy: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog3,
      documentLog4,
      documentLog6,
      documentLog7,
      documentLog9
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedBy", "contains", List("NeRI ", "  rOSsi"))
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)

  @Test
  def testFilterCombined1: Unit =
    val expectedSequence = Seq(
      documentLog7
    )
    val predicate = getDocumentLogPredicate(
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
  def testFilterCombined2: Unit =
    val expectedSequence = Seq(
      documentLog1,
      documentLog3,
      documentLog4,
      documentLog6,
      documentLog7
    )
    val predicate = getDocumentLogPredicate(
      List(
        ("getProcessedDate", ">=", List("2026-06-01")),
        ("getProcessedDate", "<=", List("2026-09-30")),
        ("getProcessedBy", "contains", List("Rossi", "Neri")),
        ("getOperationType", "contains", List("loading", "registering")),
      )
    )
    assertEquals(DocumentLog().getRecordsByFilter[DocumentLog](predicate, xmlFilePathName), expectedSequence)






