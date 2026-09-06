package pkg.b.logic

import alice.tuprolog.Term
import pkg.a.gui.structures.MenuAction
import pkg.d.util.PrologEngine
import pkg.d.util.PrologEngine.given
import pkg.d.util.Util.inDatabaseFilePathName

import java.nio.file.{Files, Paths}
import scala.collection.mutable
import scala.language.implicitConversions

object AuthorizationEngine:

  private val customRulesPath: String = inDatabaseFilePathName("customRules.pl")

  private val customRulePattern = """can\((\w+),\s*(\w+)\)\.""".r

  private def readCustomRulesText(): String =
    val file = Paths.get(customRulesPath)
    if Files.exists(file) then Files.readString(file) else ""

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

  private def toAtom(action: MenuAction): String =
    action.toString.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase

  def isAuthorized(role: String, action: MenuAction): Boolean =
    engine(s"authorized(${role.toLowerCase}, ${toAtom(action)})").nonEmpty

  def canDeleteRole(role: String): Boolean =
    engine(s"can_delete_role(${role.toLowerCase})").nonEmpty

  def canDeleteAccount(role: String, adminCount: Int): Boolean =
    engine(s"can_delete_account(${role.toLowerCase}, $adminCount)").nonEmpty

  private def fromAtom(atom: String): Option[MenuAction] =
    MenuAction.values.find(toAtom(_) == atom)

  def permittedActions(role: String): Seq[MenuAction] =
    engine(s"permitted_actions(${role.toLowerCase}, Actions)").headOption match
      case Some(solvedGoal) =>
        val actionsList = PrologEngine.arg(solvedGoal, 1)
        PrologEngine.listElements(actionsList).flatMap(term => fromAtom(term.toString))
      case None =>
        Seq.empty

  def listCustomRules(): Seq[(String, MenuAction)] =
    customRules.toSeq.flatMap { case (role, atom) => fromAtom(atom).map(role -> _) }

  def addCustomRule(role: String, action: MenuAction): Boolean =
    if isAuthorized(role, action) then false
    else
      val pair = (role.toLowerCase, toAtom(action))
      engine(s"assert(can(${pair._1}, ${pair._2}))").nonEmpty
      customRules += pair
      writeCustomRulesFile()
      true

  def removeCustomRule(role: String, action: MenuAction): Boolean =
    val pair = (role.toLowerCase, toAtom(action))
    if !customRules.contains(pair) then false
    else
      engine(s"retract(can(${pair._1}, ${pair._2}))").nonEmpty
      customRules -= pair
      writeCustomRulesFile()
      true
