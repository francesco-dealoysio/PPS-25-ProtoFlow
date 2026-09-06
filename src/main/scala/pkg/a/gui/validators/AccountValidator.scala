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
      Option.when(requirePassword)(validateRequired(PasswordRequired, rawPassword)).flatten,
      validateUniqueUsername(account.getUsername, existingAccounts, currentAccountId)
    ).flatten

  def isValid(account: Account, rawPassword: String, existingAccounts: Seq[Account], currentAccountId: Option[String] = None, requirePassword: Boolean = true): Boolean =
    validate(account, rawPassword, existingAccounts, currentAccountId, requirePassword).isEmpty

  def validateProfile(email: String): Seq[String] =
    Seq(validateEmail(email)).flatten

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    Option.when(value.trim.isEmpty)(errorMessage)

  private def validateEmail(value: String): Option[String] =
    val normalized = value.trim
    if normalized.isEmpty then Some(EmailRequired)
    else Option.unless(emailPattern.matches(normalized))(EmailInvalid)

  private def validateUniqueUsername(username: String, existingAccounts: Seq[Account], currentAccountId: Option[String]): Option[String] =
    val normalizedUsername = username.trim

    if normalizedUsername.isEmpty then
      None
    else
      val duplicateExists =
        existingAccounts.exists: existing =>
          !currentAccountId.contains(existing.getId) &&
            existing.getUsername.trim == normalizedUsername

      Option.when(duplicateExists)(DuplicateUsername)

  private def validateLastAdminRole(account: Account, existingAccounts: Seq[Account], currentAccountId: Option[String]): Option[String] =
    val editingLastAdmin =
      currentAccountId
        .flatMap: id =>
          existingAccounts.find(_.getId == id)
        .exists: existing =>
          existing.getRole.equalsIgnoreCase("admin") &&
            existingAccounts.count(_.getRole.equalsIgnoreCase("admin")) == MinimumAdminAccounts

    val isChangingLastAdmin = editingLastAdmin && !account.getRole.equalsIgnoreCase("admin")
    Option.when(isChangingLastAdmin)(LastAdminRoleChange)