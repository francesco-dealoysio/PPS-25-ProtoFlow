package pkg.b.logic

import alice.tuprolog.Term
import pkg.a.gui.structures.MenuAction
import pkg.d.util.PrologEngine
import pkg.d.util.PrologEngine.given
import pkg.d.util.Util.inDatabaseFilePathName

import java.nio.file.{Files, Paths}
import scala.collection.mutable
import scala.language.implicitConversions

// Authorization is decided by Prolog rules (src/main/resources/prolog/authorization.pl),
// not by Scala conditionals: this is the only call site the rest of the app needs.
object AuthorizationEngine:

  // Custom rules an admin adds at runtime (see addCustomRule/removeCustomRule) live in a
  // separate, writable file, so the base theory shipped with the app is never rewritten.
  private val customRulesPath: String = inDatabaseFilePathName("customRules.pl")

  private val customRulePattern = """can\((\w+),\s*(\w+)\)\.""".r

  private def readCustomRulesText(): String =
    val file = Paths.get(customRulesPath)
    if Files.exists(file) then Files.readString(file) else ""

  // (role, action atom) pairs currently customized, in the order they were added.
  private val customRules: mutable.LinkedHashSet[(String, String)] =
    val pairs = customRulePattern.findAllMatchIn(readCustomRulesText()).map(m => (m.group(1), m.group(2)))
    mutable.LinkedHashSet.from(pairs)

  private def writeCustomRulesFile(): Unit =
    val text = customRules.map { case (role, atom) => s"can($role, $atom).\n" }.mkString
    Files.writeString(Paths.get(customRulesPath), text)

  private val engine: Term => LazyList[Term] =
    val baseTheory =
      scala.io.Source.fromInputStream(getClass.getResourceAsStream("/prolog/authorization.pl")).mkString
    PrologEngine.fromTheory(baseTheory + "\n" + readCustomRulesText())

  // ControlloGestione -> controllo_gestione, matching the atoms used in authorization.pl.
  private def toAtom(action: MenuAction): String =
    action.toString.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase

  def isAuthorized(role: String, action: MenuAction): Boolean =
    engine(s"authorized(${role.toLowerCase}, ${toAtom(action)})").nonEmpty

  def canDeleteRole(role: String): Boolean =
    engine(s"can_delete_role(${role.toLowerCase})").nonEmpty

  def canDeleteAccount(role: String, adminCount: Int): Boolean =
    engine(s"can_delete_account(${role.toLowerCase}, $adminCount)").nonEmpty

  // controllo_gestione -> ControlloGestione, the inverse of toAtom.
  private def fromAtom(atom: String): Option[MenuAction] =
    MenuAction.values.find(toAtom(_) == atom)

  // The actions Role is authorized to perform, in the order declared in authorization.pl.
  def permittedActions(role: String): Seq[MenuAction] =
    engine(s"permitted_actions(${role.toLowerCase}, Actions)").headOption match
      case Some(solvedGoal) =>
        val actionsList = PrologEngine.arg(solvedGoal, 1)
        PrologEngine.listElements(actionsList).flatMap(term => fromAtom(term.toString))
      case None =>
        Seq.empty

  // The organization's own additions to the base theory, for display in the management view.
  def listCustomRules(): Seq[(String, MenuAction)] =
    customRules.toSeq.flatMap { case (role, atom) => fromAtom(atom).map(role -> _) }

  // Adds a custom can(role, action) rule: asserts it into the live theory and persists it.
  // Returns false without effect if this exact rule was already customized.
  def addCustomRule(role: String, action: MenuAction): Boolean =
    // Checked against isAuthorized (base + custom), not just customRules: otherwise
    // "customizing" a permission the role already has from the base theory would assert
    // a duplicate can/2 fact, and findall (permitted_actions) would return it twice.
    if isAuthorized(role, action) then false
    else
      val pair = (role.toLowerCase, toAtom(action))
      engine(s"assert(can(${pair._1}, ${pair._2}))").nonEmpty // force evaluation: assert is a side effect
      customRules += pair
      writeCustomRulesFile()
      true

  // Removes a previously-added custom rule. Rules from the base theory are never touched:
  // only pairs tracked in customRules can be retracted, so this can't strip a default permission.
  def removeCustomRule(role: String, action: MenuAction): Boolean =
    val pair = (role.toLowerCase, toAtom(action))
    if !customRules.contains(pair) then false
    else
      engine(s"retract(can(${pair._1}, ${pair._2}))").nonEmpty // force evaluation: retract is a side effect
      customRules -= pair
      writeCustomRulesFile()
      true
