package pkg.b.logic

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.structures.MenuAction

class AuthorizationEngineTest:

  @Test
  def adminCanManageRolesAccountsAndClassifications(): Unit =
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.Ruoli))
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.AccountUtenti))
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.Classifiche))
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.Statistiche))
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.Log))
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.ControlloGestione))
    assertTrue(AuthorizationEngine.isAuthorized("admin", MenuAction.Registrazioni))

  @Test
  def adminCannotDoOperatoreOrViewerActions(): Unit =
    assertFalse(AuthorizationEngine.isAuthorized("admin", MenuAction.NuovaPresaInCarico))
    assertFalse(AuthorizationEngine.isAuthorized("admin", MenuAction.VisualizzazioneArchiviazioni))

  @Test
  def operCanHandleTheDocumentLifecycle(): Unit =
    assertTrue(AuthorizationEngine.isAuthorized("oper", MenuAction.NuovaPresaInCarico))
    assertTrue(AuthorizationEngine.isAuthorized("oper", MenuAction.DocumentiDaProtocollare))
    assertTrue(AuthorizationEngine.isAuthorized("oper", MenuAction.DocumentiDaArchiviare))
    assertTrue(AuthorizationEngine.isAuthorized("oper", MenuAction.DocumentiArchiviati))

  @Test
  def operCannotDoAdminActions(): Unit =
    assertFalse(AuthorizationEngine.isAuthorized("oper", MenuAction.Ruoli))
    assertFalse(AuthorizationEngine.isAuthorized("oper", MenuAction.AccountUtenti))

  @Test
  def viewerCanOnlySeeArchivedDocuments(): Unit =
    assertTrue(AuthorizationEngine.isAuthorized("viewer", MenuAction.VisualizzazioneArchiviazioni))
    assertFalse(AuthorizationEngine.isAuthorized("viewer", MenuAction.DocumentiDaProtocollare))
    assertFalse(AuthorizationEngine.isAuthorized("viewer", MenuAction.Ruoli))

  @Test
  def unknownRoleIsDeniedByDefault(): Unit =
    assertFalse(AuthorizationEngine.isAuthorized("guest", MenuAction.Dashboard))
    assertFalse(AuthorizationEngine.isAuthorized("guest", MenuAction.Ruoli))

  @Test
  def adminRoleCanNeverBeDeleted(): Unit =
    assertFalse(AuthorizationEngine.canDeleteRole("admin"))
    assertTrue(AuthorizationEngine.canDeleteRole("oper"))
    assertTrue(AuthorizationEngine.canDeleteRole("viewer"))

  @Test
  def lastAdminAccountCannotBeDeleted(): Unit =
    assertFalse(AuthorizationEngine.canDeleteAccount("admin", 1))
    assertTrue(AuthorizationEngine.canDeleteAccount("admin", 2))
    assertTrue(AuthorizationEngine.canDeleteAccount("oper", 1))

  @Test
  def permittedActionsMatchesIsAuthorizedForEveryRole(): Unit =
    val roles = Seq("admin", "oper", "viewer")
    for role <- roles; action <- MenuAction.values do
      assertEquals(
        s"mismatch for $role/$action",
        AuthorizationEngine.isAuthorized(role, action),
        AuthorizationEngine.permittedActions(role).contains(action)
      )

  @Test
  def permittedActionsIsEmptyForUnknownRole(): Unit =
    assertTrue(AuthorizationEngine.permittedActions("guest").isEmpty)
