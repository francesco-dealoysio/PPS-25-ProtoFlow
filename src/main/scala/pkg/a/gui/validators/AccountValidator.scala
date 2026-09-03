package pkg.a.gui.validators

import pkg.a.gui.text.UiText.Validation.Account.*
import pkg.b.logic.Account

class AccountValidator:

  private val emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$".r
  private val MinimumAdminAccounts = 1

  def validate(account: Account, rawPassword: String, existingAccounts: Seq[Account], currentAccountId: Option[String] = None, requirePassword: Boolean = true): Seq[String] =
    Seq(
      validateRequired(SurnameRequired, account.getSurname),
      validateRequired(NameRequired, account.getName),
      validateEmail(account.getEmail),
      validateRequired(RoleRequired, account.getRole),
      validateLastAdminRole(account, existingAccounts, currentAccountId),
      validateRequired(UsernameRequired, account.getUsername),
      if requirePassword then validateRequired(PasswordRequired, rawPassword) else None,
      validateUniqueUsername(account.getUsername, existingAccounts, currentAccountId)
    ).flatten

  def isValid(account: Account, rawPassword: String, existingAccounts: Seq[Account], currentAccountId: Option[String] = None, requirePassword: Boolean = true): Boolean =
    validate(account, rawPassword, existingAccounts, currentAccountId, requirePassword).isEmpty

  def validateProfile(email: String): Seq[String] =
    Seq(validateEmail(email)).flatten

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    if value.trim.isEmpty then Some(errorMessage)
    else None

  private def validateEmail(value: String): Option[String] =
    val normalized = value.trim
    if normalized.isEmpty then Some(EmailRequired)
    else if emailPattern.matches(normalized) then None
    else Some(EmailInvalid)

  private def validateUniqueUsername(username: String, existingAccounts: Seq[Account], currentAccountId: Option[String]): Option[String] =
    val normalizedUsername = username.trim

    if normalizedUsername.isEmpty then
      None
    else
      val duplicateExists =
        existingAccounts.exists: existing =>
          !currentAccountId.contains(existing.getId) &&
            existing.getUsername.trim == normalizedUsername

      if duplicateExists then Some(DuplicateUsername)
      else None

  private def validateLastAdminRole(account: Account, existingAccounts: Seq[Account], currentAccountId: Option[String]): Option[String] =
    val editingLastAdmin =
      currentAccountId
        .flatMap: id =>
          existingAccounts.find(_.getId == id)
        .exists: existing =>
          existing.getRole.equalsIgnoreCase("admin") &&
            existingAccounts.count(_.getRole.equalsIgnoreCase("admin")) == MinimumAdminAccounts

    if editingLastAdmin &&
      !account.getRole.equalsIgnoreCase("admin")
    then
      Some(LastAdminRoleChange)
    else
      None