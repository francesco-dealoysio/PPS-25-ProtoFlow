package pkg.a.gui.structures

import pkg.b.logic.Role
import pkg.a.gui.text.UiText.Validation.Role.*

class RoleViewModel:

  def validate(role: Role, existingRoles: Seq[Role], currentRoleId: Option[String] = None): Seq[String] =
    Seq(
      validateRequired(RoleRequired, role.getRole),
      validateRequired(DescriptionRequired, role.getDescription),
      validateUniqueRole(role.getRole, existingRoles, currentRoleId)
    ).flatten

  def isValid(role: Role, existingRoles: Seq[Role], currentRoleId: Option[String] = None): Boolean =
    validate(
      role,
      existingRoles,
      currentRoleId
    ).isEmpty

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    if value.trim.isEmpty then
      Some(errorMessage)
    else
      None

  private def validateUniqueRole(roleName: String, existingRoles: Seq[Role], currentRoleId: Option[String]): Option[String] =

    val normalizedName = roleName.trim

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
