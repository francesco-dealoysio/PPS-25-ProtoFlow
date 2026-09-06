package pkg.a.gui.services

import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.{inDatabaseFilePathName, inIdsFilePathName}

object  RoleService:

  private val roleLogic = new Role()

  def addRole(
               role: String,
               name: String,
               description: String,
               rolesFilePathName: String = inDatabaseFilePathName("roles.xml"),
               roleIdFilePathName: String = inIdsFilePathName("roleId")
             ): Either[String, Role] =

    val newRole =
      Role(
        id = IdGen(roleIdFilePathName),
        role = role,
        name = name,
        description = description
      )

    if roleLogic.recordInsert(newRole, rolesFilePathName) then Right(newRole)
    else Left("Errore durante l'inserimento del ruolo")
