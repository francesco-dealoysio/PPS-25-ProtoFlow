package pkg.a.gui.services

import pkg.b.logic.{AccessLog, ArchivedDocument, RegisteredDocument, Registration, Role}
import pkg.d.util.DateTime

import scala.util.Try

object StatisticsService:

  case class MonthlyCount(yearMonth: String, count: Int)
  case class RoleCount(roleCode: String, roleName: String, count: Int)
  case class UserCount(username: String, count: Int)
  case class RegistrationsSummary(total: Int, approved: Int, rejected: Int)

  def registeredDocumentsByMonth(lastNMonths: Int = 12, xmlFilePathName: String = ""): Seq[MonthlyCount] =
    val dates =
      registeredDocuments(xmlFilePathName).map(_.getRegisteredDate)
    countByMonth(dates, lastNMonths)

  def archivedDocumentsByMonth(lastNMonths: Int = 12, xmlFilePathName: String = ""): Seq[MonthlyCount] =
    val dates =
      archivedDocuments(xmlFilePathName).map(_.getArchivedDate)
    countByMonth(dates, lastNMonths)

  def processedRegistrations(xmlFilePathName: String = ""): RegistrationsSummary =
    val requests = registrations(xmlFilePathName)
    val approved = requests.count(_.getState == "Approved")
    val rejected = requests.count(_.getState == "Rejected")
    RegistrationsSummary(total = approved + rejected, approved = approved, rejected = rejected)

  def accessesByRole(accessLogFilePathName: String = "", roleFilePathName: String = ""): Seq[RoleCount] =
    val roleNames = roles(roleFilePathName)

    def roleNameFor(roleCode: String): String =
      roleNames
        .find(_.getRole.equalsIgnoreCase(roleCode))
        .map(_.getName)
        .getOrElse(roleCode)

    accessLogs(accessLogFilePathName)
      .groupBy(_.getRole)
      .toSeq
      .map((roleCode, logs) => RoleCount(roleCode, roleNameFor(roleCode), logs.size))
      .sortBy(-_.count)

  def accessesByUser(xmlFilePathName: String = ""): Seq[UserCount] =
    accessLogs(xmlFilePathName)
      .groupBy(_.getUsername)
      .toSeq
      .map((username, logs) => UserCount(username, logs.size))
      .sortBy(-_.count)

  private def registeredDocuments(xmlFilePathName: String): Seq[RegisteredDocument] =
    if xmlFilePathName.isEmpty then RegisteredDocument().getRecords[RegisteredDocument]()
    else RegisteredDocument().getRecords[RegisteredDocument](xmlFilePathName)

  private def archivedDocuments(xmlFilePathName: String): Seq[ArchivedDocument] =
    if xmlFilePathName.isEmpty then ArchivedDocument().getRecords[ArchivedDocument]()
    else ArchivedDocument().getRecords[ArchivedDocument](xmlFilePathName)

  private def registrations(xmlFilePathName: String): Seq[Registration] =
    if xmlFilePathName.isEmpty then Registration().getRecords[Registration]()
    else Registration().getRecords[Registration](xmlFilePathName)

  private def roles(xmlFilePathName: String): Seq[Role] =
    if xmlFilePathName.isEmpty then Role().getRecords[Role]()
    else Role().getRecords[Role](xmlFilePathName)

  private def accessLogs(xmlFilePathName: String): Seq[AccessLog] =
    if xmlFilePathName.isEmpty then AccessLog().getRecords[AccessLog]()
    else AccessLog().getRecords[AccessLog](xmlFilePathName)

  private def countByMonth(dates: Seq[String], lastNMonths: Int): Seq[MonthlyCount] =
    dates
      .flatMap(yearMonthOf)
      .groupBy(identity)
      .toSeq
      .map((yearMonth, occurrences) => MonthlyCount(yearMonth, occurrences.size))
      .sortBy(_.yearMonth)
      .takeRight(lastNMonths)

  private def yearMonthOf(date: String): Option[String] =
    Try(DateTime.parseDate(date))
      .toOption
      .map(parsed => f"${parsed.getYear}%04d-${parsed.getMonthValue}%02d")
