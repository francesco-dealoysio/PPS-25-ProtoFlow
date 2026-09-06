package pkg.a.gui.validators

import pkg.a.gui.text.UiText.ArchivedDocuments.Errors as ArchiveErrors

import java.time.{LocalDate, LocalTime}
import scala.util.Try

class DocumentArchivingValidator:

  def validate(archivedDate: String, archivedTime: String, archivedBy: String, archiveLocation: String): Seq[String] =
    Seq(
      validateArchivedDate(archivedDate),
      validateArchivedTime(archivedTime),
      validateArchivedBy(archivedBy),
      validateArchiveLocation(archiveLocation)
    ).flatten

  def isValid(archivedDate: String, archivedTime: String, archivedBy: String, archiveLocation: String): Boolean =
    validate(archivedDate, archivedTime, archivedBy, archiveLocation).isEmpty

  private def validateArchivedDate(value: String): Option[String] =
    val trimmedValue = value.trim

    if trimmedValue.isEmpty then
      Some(ArchiveErrors.ArchivedDateRequired)
    else
      Option.when(Try(LocalDate.parse(trimmedValue)).isFailure)(ArchiveErrors.ArchivedDateInvalid)

  private def validateArchivedTime(value: String): Option[String] =
    val trimmedValue = value.trim

    if trimmedValue.isEmpty then
      Some(ArchiveErrors.ArchivedTimeRequired)
    else
      Option.when(Try(LocalTime.parse(trimmedValue)).isFailure)(ArchiveErrors.ArchivedTimeInvalid)

  private def validateArchivedBy(value: String): Option[String] =
    Option.when(value.trim.isEmpty)(ArchiveErrors.ArchivedByRequired)

  private def validateArchiveLocation(value: String): Option[String] =
    Option.when(value.trim.isEmpty)(ArchiveErrors.ArchiveLocationRequired)