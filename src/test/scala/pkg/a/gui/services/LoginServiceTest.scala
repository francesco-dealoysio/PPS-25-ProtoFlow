package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.services.LoginService.LoginError
import pkg.b.logic.{Account, AccessLog, Role}
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.{cipher, inTestFilePathName}

import java.nio.file.{Files, Paths}

class LoginServiceTest:

  private var accountsFilePathName: String = _
  private var rolesFilePathName: String = _
  private var accessLogFilePathName: String = _
  private var accessLogIdFilePathName: String = _

  @Before
  def setUp(): Unit =
    accountsFilePathName = inTestFilePathName("loginAccountsTest.xml")
    rolesFilePathName = inTestFilePathName("loginRolesTest.xml")
    accessLogFilePathName = inTestFilePathName("loginAccessLogTest.xml")
    accessLogIdFilePathName = inTestFilePathName("loginAccessLogId")
    createEmptyXmlFile(accountsFilePathName, "test_records")
    createEmptyXmlFile(rolesFilePathName, "test_records")
    createEmptyXmlFile(accessLogFilePathName, "test_records")
    Files.deleteIfExists(Paths.get(accessLogIdFilePathName))

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(accountsFilePathName))
    Files.deleteIfExists(Paths.get(rolesFilePathName))
    Files.deleteIfExists(Paths.get(accessLogFilePathName))
    Files.deleteIfExists(Paths.get(accessLogIdFilePathName))

  @Test
  def testEmptyCredentials(): Unit =
    val result = login("", "")
    assertEquals(Left(LoginError.EmptyCredentials), result)

  @Test
  def testInvalidCredentials(): Unit =
    insertAccount()
    val result = login("mrossi", "password-sbagliata")
    assertEquals(Left(LoginError.InvalidCredentials), result)

  @Test
  def testUnknownRole(): Unit =
    insertAccount(role = "unknown")
    val result = login("mrossi", "password")
    assertEquals(Left(LoginError.UnknownRole("unknown")), result)

  @Test
  def testSuccessfulLogin(): Unit =
    val account = insertAccount()
    insertRole()
    val result = login("  mrossi  ", "  password  ")
    assertEquals(Right(account), result)
    val accessLogs = AccessLog().getRecords[AccessLog](accessLogFilePathName)
    assertEquals(1, accessLogs.size)
    assertEquals("mrossi", accessLogs.head.getUsername)
    assertEquals("viewer", accessLogs.head.getRole)

  private def login(username: String, password: String): Either[LoginError, Account] =
    LoginService.login(
      username,
      password,
      accountsFilePathName,
      rolesFilePathName,
      accessLogFilePathName,
      accessLogIdFilePathName
    )

  private def insertAccount(role: String = "viewer"): Account =
    val account = Account(
      id = "1",
      surname = "Rossi",
      name = "Mario",
      email = "mario.rossi@email.it",
      phone = "",
      role = role,
      area = "Segreteria",
      assignment = "Impiegato",
      username = "mrossi",
      password = cipher("password")
    )

    Account().recordInsert(account, accountsFilePathName)
    account

  private def insertRole(): Unit =
    Role().recordInsert(
      Role(
        id = "1",
        role = "viewer",
        name = "Viewer",
        description = "Visualizzazione documenti"
      ),
      rolesFilePathName
    )