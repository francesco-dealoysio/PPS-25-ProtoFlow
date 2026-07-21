package pkg.a.gui.structures

import pkg.b.logic.Account

object AccountViewModel:
  val SurnameRequiredError = "Il campo Cognome è obbligatorio."

  val NameRequiredError = "Il campo Nome è obbligatorio."

  val EmailRequiredError = "Il campo Email è obbligatorio."

  val EmailInvalidError = "Inserisci un indirizzo email valido."

  val RoleRequiredError = "Il campo Ruolo è obbligatorio."

  val UsernameRequiredError = "Il campo Username è obbligatorio."

  val PasswordRequiredError = "Il campo Password è obbligatorio."

  val DuplicateUsernameError = "Esiste già un account con questo username."

  val roles: Seq[String] = Seq("admin", "oper", "viewer")

  private val emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$".r

class AccountViewModel:
  import AccountViewModel.*

  def validate(
                account: Account,
                rawPassword: String,
                existingAccounts: Seq[Account],
                currentAccountId: Option[String] = None,
                requirePassword: Boolean = true
              ): Seq[String] =
    Seq(
      validateRequired(SurnameRequiredError, account.getSurname),
      validateRequired(NameRequiredError, account.getName),
      validateEmail(account.getEmail),
      validateRequired(RoleRequiredError, account.getRole),
      validateRequired(UsernameRequiredError, account.getUsername),
      if requirePassword then validateRequired(PasswordRequiredError, rawPassword) else None,
      validateUniqueUsername(
        account.getUsername,
        existingAccounts,
        currentAccountId
      )
    ).flatten

  def isValid(
               account: Account,
               rawPassword: String,
               existingAccounts: Seq[Account],
               currentAccountId: Option[String] = None,
               requirePassword: Boolean = true
             ): Boolean =
    validate(
      account,
      rawPassword,
      existingAccounts,
      currentAccountId,
      requirePassword
    ).isEmpty

  def nextId(existingAccounts: Seq[Account]): String =
    val maximumId =
      existingAccounts
        .flatMap(_.getId.toIntOption)
        .maxOption
        .getOrElse(0)

    (maximumId + 1).toString

  private def validateRequired(
                                 errorMessage: String,
                                 value: String
                               ): Option[String] =
    if value.trim.isEmpty then Some(errorMessage)
    else None

  private def validateEmail(value: String): Option[String] =
    val normalized = value.trim

    if normalized.isEmpty then Some(EmailRequiredError)
    else if emailPattern.matches(normalized) then None
    else Some(EmailInvalidError)

  private def validateUniqueUsername(
                                       username: String,
                                       existingAccounts: Seq[Account],
                                       currentAccountId: Option[String]
                                     ): Option[String] =
    val normalizedUsername = username.trim

    if normalizedUsername.isEmpty then
      None
    else
      val duplicateExists =
        existingAccounts.exists: existing =>
          !currentAccountId.contains(existing.getId) &&
            existing.getUsername.trim.equalsIgnoreCase(normalizedUsername)

      if duplicateExists then Some(DuplicateUsernameError)
      else None
