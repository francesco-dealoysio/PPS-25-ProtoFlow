package pkg.a.gui.structures

import pkg.b.logic.Account
import pkg.a.gui.text.UiText.Validation.Account.*

object AccountViewModel:
  val SurnameRequiredError: String = SurnameRequired
  val NameRequiredError: String = NameRequired
  val EmailRequiredError: String = EmailRequired
  val EmailInvalidError: String = EmailInvalid
  val RoleRequiredError: String = RoleRequired
  val UsernameRequiredError: String = UsernameRequired
  val PasswordRequiredError: String = PasswordRequired
  val DuplicateUsernameError: String = DuplicateUsername

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
      validateRequired(SurnameRequired, account.getSurname),
      validateRequired(NameRequired, account.getName),
      validateEmail(account.getEmail),
      validateRequired(RoleRequired, account.getRole),
      validateRequired(UsernameRequired, account.getUsername),
      if requirePassword then validateRequired(PasswordRequired, rawPassword) else None,
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

  def validateProfile(email: String): Seq[String] =
    Seq(validateEmail(email)).flatten

  private def validateRequired(
                                 errorMessage: String,
                                 value: String
                               ): Option[String] =
    if value.trim.isEmpty then Some(errorMessage)
    else None

  private def validateEmail(value: String): Option[String] =
    val normalized = value.trim

    if normalized.isEmpty then Some(EmailRequired)
    else if emailPattern.matches(normalized) then None
    else Some(EmailInvalid)

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

      if duplicateExists then Some(DuplicateUsername)
      else None
