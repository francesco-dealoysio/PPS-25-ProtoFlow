package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.services.StatisticsService
import pkg.b.logic.*
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.inTestFilePathName

import java.nio.file.{Files, Paths}

class StatisticsServiceTest:

  private var registeredXmlFile: String = _
  private var archivedXmlFile: String = _
  private var registrationsXmlFile: String = _
  private var accessLogXmlFile: String = _
  private var rolesXmlFile: String = _

  @Before
  def setUp(): Unit =
    registeredXmlFile = inTestFilePathName("testStatsRegistered.xml")
    archivedXmlFile = inTestFilePathName("testStatsArchived.xml")
    registrationsXmlFile = inTestFilePathName("testStatsRegistrations.xml")
    accessLogXmlFile = inTestFilePathName("testStatsAccessLog.xml")
    rolesXmlFile = inTestFilePathName("testStatsRoles.xml")

    createEmptyXmlFile(registeredXmlFile, "testRecords")
    createEmptyXmlFile(archivedXmlFile, "testRecords")
    createEmptyXmlFile(registrationsXmlFile, "testRecords")
    createEmptyXmlFile(accessLogXmlFile, "testRecords")
    createEmptyXmlFile(rolesXmlFile, "testRecords")

    RegisteredDocument().recordInsert(RegisteredDocument(id = "1", registeredDate = "2026-06-10"), registeredXmlFile)
    RegisteredDocument().recordInsert(RegisteredDocument(id = "2", registeredDate = "2026-06-20"), registeredXmlFile)
    RegisteredDocument().recordInsert(RegisteredDocument(id = "3", registeredDate = "2026-07-05"), registeredXmlFile)
    RegisteredDocument().recordInsert(RegisteredDocument(id = "4", registeredDate = "non una data"), registeredXmlFile)

    ArchivedDocument().recordInsert(ArchivedDocument(id = "1", archivedDate = "2026-07-01"), archivedXmlFile)
    ArchivedDocument().recordInsert(ArchivedDocument(id = "2", archivedDate = "2026-07-15"), archivedXmlFile)

    Registration().recordInsert(Registration(id = "1", state = "Pending"), registrationsXmlFile)
    Registration().recordInsert(Registration(id = "2", state = "Approved"), registrationsXmlFile)
    Registration().recordInsert(Registration(id = "3", state = "Approved"), registrationsXmlFile)
    Registration().recordInsert(Registration(id = "4", state = "Rejected"), registrationsXmlFile)

    AccessLog().recordInsert(AccessLog(id = "1", username = "mrossi", role = "admin"), accessLogXmlFile)
    AccessLog().recordInsert(AccessLog(id = "2", username = "mrossi", role = "admin"), accessLogXmlFile)
    AccessLog().recordInsert(AccessLog(id = "3", username = "gbianchi", role = "oper"), accessLogXmlFile)

    Role().recordInsert(Role(id = "1", role = "admin", name = "Amministratore"), rolesXmlFile)
    Role().recordInsert(Role(id = "2", role = "oper", name = "Operatore"), rolesXmlFile)

  @After
  def tearDown(): Unit =
    Seq(registeredXmlFile, archivedXmlFile, registrationsXmlFile, accessLogXmlFile, rolesXmlFile)
      .foreach(file => Files.deleteIfExists(Paths.get(file)))

  @Test
  def testRegisteredDocumentsByMonthGroupsAndSkipsInvalidDates(): Unit =
    val result = StatisticsService.registeredDocumentsByMonth(xmlFilePathName = registeredXmlFile)
    assertEquals(Seq(("2026-06", 2), ("2026-07", 1)), result.map(c => (c.yearMonth, c.count)))

  @Test
  def testArchivedDocumentsByMonth(): Unit =
    val result = StatisticsService.archivedDocumentsByMonth(xmlFilePathName = archivedXmlFile)
    assertEquals(Seq(("2026-07", 2)), result.map(c => (c.yearMonth, c.count)))

  @Test
  def testRegisteredDocumentsByMonthLimitsToLastNMonths(): Unit =
    val result = StatisticsService.registeredDocumentsByMonth(lastNMonths = 1, xmlFilePathName = registeredXmlFile)
    assertEquals(Seq("2026-07"), result.map(_.yearMonth))

  @Test
  def testProcessedRegistrationsCountsOnlyApprovedAndRejected(): Unit =
    val summary = StatisticsService.processedRegistrations(registrationsXmlFile)
    assertEquals(3, summary.total)
    assertEquals(2, summary.approved)
    assertEquals(1, summary.rejected)

  @Test
  def testAccessesByRoleResolvesDisplayNameAndCounts(): Unit =
    val result = StatisticsService.accessesByRole(accessLogXmlFile, rolesXmlFile)
    assertEquals(Seq(("admin", "Amministratore", 2), ("oper", "Operatore", 1)), result.map(c => (c.roleCode, c.roleName, c.count)))

  @Test
  def testAccessesByUserCountsAndSortsDescending(): Unit =
    val result = StatisticsService.accessesByUser(accessLogXmlFile)
    assertEquals(Seq(("mrossi", 2), ("gbianchi", 1)), result.map(c => (c.username, c.count)))
