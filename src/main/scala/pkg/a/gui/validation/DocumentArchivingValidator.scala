package pkg.a.gui.validation

import pkg.a.gui.text.UiText.ArchivedDocuments.Errors as ArchiveErrors

import java.time.{LocalDate, LocalTime}
import scala.util.Try

class DocumentArchivingValidator:

  def validate(archivedDate: String, archivedTime: String, archivedBy: String): Seq[String] =
    Seq(
      validateArchivedDate(archivedDate),
      validateArchivedTime(archivedTime),
      validateArchivedBy(archivedBy)
    ).flatten

  def isValid(archivedDate: String, archivedTime: String, archivedBy: String): Boolean =
    validate(archivedDate, archivedTime, archivedBy).isEmpty

  private def validateArchivedDate(value: String): Option[String] =
    val trimmedValue = value.trim

    if trimmedValue.isEmpty then
      Some(ArchiveErrors.ArchivedDateRequired)
    else if Try(LocalDate.parse(trimmedValue)).isFailure then
      Some(ArchiveErrors.ArchivedDateInvalid)
    else
      None

  private def validateArchivedTime(value: String): Option[String] =
    val trimmedValue = value.trim

    if trimmedValue.isEmpty then
      Some(ArchiveErrors.ArchivedTimeRequired)
    else if Try(LocalTime.parse(trimmedValue)).isFailure then
      Some(ArchiveErrors.ArchivedTimeInvalid)
    else
      None

  private def validateArchivedBy(value: String): Option[String] =
    if value.trim.isEmpty then
      Some(ArchiveErrors.ArchivedByRequired)
    else
      None