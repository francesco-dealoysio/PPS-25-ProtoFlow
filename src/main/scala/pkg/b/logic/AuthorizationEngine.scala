package pkg.b.logic

import alice.tuprolog.Term
import pkg.a.gui.structures.MenuAction
import pkg.d.util.PrologEngine
import pkg.d.util.PrologEngine.given

import scala.language.implicitConversions

// Authorization is decided by Prolog rules (src/main/resources/prolog/authorization.pl),
// not by Scala conditionals: this is the only call site the rest of the app needs.
object AuthorizationEngine:

  private val engine: Term => LazyList[Term] =
    val theoryText =
      scala.io.Source.fromInputStream(getClass.getResourceAsStream("/prolog/authorization.pl")).mkString
    PrologEngine.fromTheory(theoryText)

  // ControlloGestione -> controllo_gestione, matching the atoms used in authorization.pl.
  private def toAtom(action: MenuAction): String =
    action.toString.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase

  def isAuthorized(role: String, action: MenuAction): Boolean =
    engine(s"authorized(${role.toLowerCase}, ${toAtom(action)})").nonEmpty

  def canDeleteRole(role: String): Boolean =
    engine(s"can_delete_role(${role.toLowerCase})").nonEmpty

  def canDeleteAccount(role: String, adminCount: Int): Boolean =
    engine(s"can_delete_account(${role.toLowerCase}, $adminCount)").nonEmpty
