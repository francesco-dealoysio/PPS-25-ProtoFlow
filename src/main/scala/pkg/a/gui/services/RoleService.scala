package pkg.a.gui.services

import pkg.b.logic.Role
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName

object  RoleService:

  private val roleLogic = new Role()
  
  def addRole(role: String, name: String, description: String): Either[String, Role] =

    val newRole =
      Role(
        id = IdGen(inIdsFilePathName("roleId")),
        role = role,
        name = name,
        description = description
      )

    if roleLogic.recordInsert(newRole) then Right(newRole)
    else Left("Errore durante l'inserimento del ruolo")
