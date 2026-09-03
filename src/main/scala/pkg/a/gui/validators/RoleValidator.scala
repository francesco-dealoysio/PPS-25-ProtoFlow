package pkg.a.gui.validators

import pkg.a.gui.text.UiText.Validation.Role.*
import pkg.b.logic.Role

class RoleValidator:

  def validate(role: Role, existingRoles: Seq[Role], currentRoleId: Option[String] = None): Seq[String] =
    Seq(
      validateRequired(RoleRequired, role.getRole),
      validateRequired(DescriptionRequired, role.getDescription),
      validateRequired(NameRequired, role.getName),
      validateUniqueRole(role.getRole, existingRoles, currentRoleId),
      validateUniqueName(role.getName, existingRoles, currentRoleId)
    ).flatten

  def isValid(role: Role, existingRoles: Seq[Role], currentRoleId: Option[String] = None): Boolean =
    validate(role, existingRoles, currentRoleId).isEmpty

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    if value.trim.isEmpty then
      Some(errorMessage)
    else
      None

  private def validateUniqueRole(role: String, existingRoles: Seq[Role], currentRoleId: Option[String]): Option[String] =

    val normalizedName = role.trim

    if normalizedName.isEmpty then
      None
    else
      val duplicateExists =
        existingRoles.exists: existingRole =>
          !currentRoleId.contains(existingRole.getId) &&
            existingRole
              .getRole
              .trim
              .equalsIgnoreCase(normalizedName)

      if duplicateExists then
        Some(DuplicateRole)
      else
        None

  private def validateUniqueName(roleName: String, existingRoles: Seq[Role], currentRoleId: Option[String]): Option[String] =
    val normalizedName = roleName.trim
    if normalizedName.isEmpty then
      None
    else
      val duplicateExists =
        existingRoles.exists: existingRole =>
          !currentRoleId.contains(existingRole.getId) &&
            existingRole.getName.trim.equalsIgnoreCase(normalizedName)

      if duplicateExists then
        Some(DuplicateRoleName)
      else
        None