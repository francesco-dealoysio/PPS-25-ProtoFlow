package pkg.a.gui.services

import pkg.b.logic.Account
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName

class AccountService:

  private val accountLogic = new Account()

  /**
   * Genera l'id e salva un nuovo account. I campi (incluso il ruolo risolto e la
   * password già cifrata) sono responsabilità del chiamante.
   */
  def addAccount(
                  surname: String,
                  name: String,
                  email: String,
                  phone: String,
                  role: String,
                  area: String,
                  assignment: String,
                  username: String,
                  cipheredPassword: String
                ): Either[String, Account] =

    val newAccount =
      Account(
        id = IdGen(inIdsFilePathName("accountId")),
        surname = surname,
        name = name,
        email = email,
        phone = phone,
        role = role,
        area = area,
        assignment = assignment,
        username = username,
        password = cipheredPassword
      )

    if accountLogic.recordInsert(newAccount) then Right(newAccount)
    else Left("Errore durante l'inserimento dell'account")
