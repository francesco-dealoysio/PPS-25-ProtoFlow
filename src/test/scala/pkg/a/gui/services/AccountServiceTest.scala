package pkg.a.gui.services

import org.junit.*
import org.junit.Assert.*
import pkg.b.logic.Account
import pkg.c.data.Xml.createEmptyXmlFile
import pkg.d.util.Util.{cipher, inTestFilePathName}

import java.nio.file.{Files, Paths}

class AccountServiceTest:

  private var xmlFilePathName: String = _
  private var idFilePathName: String = _

  @Before
  def setUp(): Unit =
    xmlFilePathName = inTestFilePathName("accountServiceTest.xml")
    idFilePathName = inTestFilePathName("accountServiceId")
    createEmptyXmlFile(xmlFilePathName, "test_records")
    Files.deleteIfExists(Paths.get(idFilePathName))

  @After
  def tearDown(): Unit =
    Files.deleteIfExists(Paths.get(xmlFilePathName))
    Files.deleteIfExists(Paths.get(idFilePathName))

  @Test
  def testAddAccount(): Unit =
    val result = AccountService.addAccount(
      surname = "Rossi",
      name = "Mario",
      email = "mario.rossi@email.it",
      phone = "3331234567",
      role = "viewer",
      area = "Personale",
      assignment = "Impiegato",
      username = "mrossi",
      cipheredPassword = cipher("password"),
      accountsFilePathName = xmlFilePathName,
      accountIdFilePathName = idFilePathName
    )

    assertTrue(result.isRight)
    val account = result.toOption.get
    assertEquals("Rossi", account.getSurname)
    assertEquals("Mario", account.getName)
    assertEquals("mrossi", account.getUsername)
    assertEquals(account, Account().getRecordById[Account](account.getId, xmlFilePathName))

  @Test
  def testAddAccountDuplicateUsername(): Unit =
    AccountService.addAccount(
      "Rossi",
      "Mario",
      "mario.rossi@email.it",
      "3331234567",
      "viewer",
      "Personale",
      "Impiegato",
      "mrossi",
      cipher("password"),
      xmlFilePathName,
      idFilePathName
    )

    val result = AccountService.addAccount(
      "Bianchi",
      "Luigi",
      "luigi.bianchi@email.it",
      "3337654321",
      "viewer",
      "Segreteria",
      "Impiegato",
      "mrossi",
      cipher("password2"),
      xmlFilePathName,
      idFilePathName
    )

    assertEquals(Left("Errore durante l'inserimento dell'account"), result)