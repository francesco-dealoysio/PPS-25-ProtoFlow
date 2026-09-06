package pkg.a.gui.services

import pkg.b.logic.*
import pkg.d.util.DateTime

import scala.util.Try

object StatisticsService:

  case class MonthlyCount(yearMonth: String, count: Int)
  case class RoleCount(roleCode: String, roleName: String, count: Int)
  case class UserCount(username: String, count: Int)
  case class RegistrationsSummary(total: Int, approved: Int, rejected: Int)

  private val registeredDocumentLogic = RegisteredDocument()
  private val archivedDocumentLogic = ArchivedDocument()
  private val registrationLogic = Registration()
  private val roleLogic = Role()
  private val accessLogLogic = AccessLog()
  private val DefaultMonthsRange = 12

  def registeredDocumentsByMonth(lastNMonths: Int = DefaultMonthsRange, xmlFilePathName: String = ""): Seq[MonthlyCount] =
    val docs = fetch(xmlFilePathName)(registeredDocumentLogic.getRecords[RegisteredDocument](), registeredDocumentLogic.getRecords[RegisteredDocument])
    val dates = docs.map(_.getRegisteredDate)
    countByMonth(dates, lastNMonths)

  def archivedDocumentsByMonth(lastNMonths: Int = DefaultMonthsRange, xmlFilePathName: String = ""): Seq[MonthlyCount] =
    val docs = fetch(xmlFilePathName)(archivedDocumentLogic.getRecords[ArchivedDocument](), archivedDocumentLogic.getRecords[ArchivedDocument])
    val dates = docs.map(_.getArchivedDate)
    countByMonth(dates, lastNMonths)

  def processedRegistrations(xmlFilePathName: String = ""): RegistrationsSummary =
    val requests = fetch(xmlFilePathName)(registrationLogic.getRecords[Registration](), registrationLogic.getRecords[Registration])
    val approved = requests.count(_.getState == "Approved")
    val rejected = requests.count(_.getState == "Rejected")

    RegistrationsSummary(
      total = approved + rejected,
      approved = approved,
      rejected = rejected
    )

  def accessesByRole(accessLogFilePathName: String = "", roleFilePathName: String = ""): Seq[RoleCount] =
    val roleNames = fetch(roleFilePathName)(roleLogic.getRecords[Role](), roleLogic.getRecords[Role])
    val logs = fetch(accessLogFilePathName)(accessLogLogic.getRecords[AccessLog](), accessLogLogic.getRecords[AccessLog])

    logs
      .groupMapReduce(_.getRole)(_ => 1)(_ + _)
      .toSeq
      .map((roleCode, count) =>
        RoleCount(
          roleCode = roleCode,
          roleName = roleNameFor(roleCode, roleNames),
          count = count
        )
      )
      .sortBy(-_.count)

  def accessesByUser(xmlFilePathName: String = ""): Seq[UserCount] =
    val logs = fetch(xmlFilePathName)(accessLogLogic.getRecords[AccessLog](), accessLogLogic.getRecords[AccessLog])
    logs
      .groupMapReduce(_.getUsername)(_ => 1)(_ + _)
      .toSeq
      .map((username, count) => UserCount(username, count))
      .sortBy(-_.count)


  private def fetch[T](filePath: String)(defaultFetch: => Seq[T], customFetch: String => Seq[T]): Seq[T] =
    if filePath.isEmpty then defaultFetch else customFetch(filePath)

  private def roleNameFor(roleCode: String, roles: Seq[Role]): String =
    roles
      .find(_.getRole.equalsIgnoreCase(roleCode))
      .map(_.getName)
      .getOrElse(roleCode)

  private def countByMonth(dates: Seq[String], lastNMonths: Int): Seq[MonthlyCount] =
    dates
      .flatMap(yearMonthOf)
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .toSeq
      .map((yearMonth, count) => MonthlyCount(yearMonth, count))
      .sortBy(_.yearMonth)
      .takeRight(lastNMonths)

  private def yearMonthOf(date: String): Option[String] =
    Try {
      val parsed = DateTime.parseDate(date)
      f"${parsed.getYear}%04d-${parsed.getMonthValue}%02d"
    }.toOption