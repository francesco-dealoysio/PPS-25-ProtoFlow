package pkg.a.gui

import org.junit.Assert.*
import org.junit.Test
import pkg.a.gui.text.UiText.ArchivedDocuments.Errors as ArchiveErrors
import pkg.a.gui.validation.DocumentArchivingValidator

class DocumentArchivingValidatorTest:

  private val validator = new DocumentArchivingValidator()

  @Test
  def testValidArchivingData(): Unit =
    val errors = validator.validate("2026-08-13", "12:30:00", "admin")
    assertTrue(errors.isEmpty)

  @Test
  def testMissingArchivedDate(): Unit =
    val errors = validator.validate("", "12:30:00", "admin")
    assertTrue(errors.contains(ArchiveErrors.ArchivedDateRequired))

  @Test
  def testMissingArchivedTime(): Unit =
    val errors = validator.validate("2026-08-13", "", "admin")
    assertTrue(errors.contains(ArchiveErrors.ArchivedTimeRequired))

  @Test
  def testMissingArchivedBy(): Unit =
    val errors = validator.validate("2026-08-13", "12:30:00", "")
    assertTrue(errors.contains(ArchiveErrors.ArchivedByRequired))

  @Test
  def testInvalidArchivedDate(): Unit =
    val errors = validator.validate("13/08/2026", "12:30:00", "admin")
    assertTrue(errors.contains(ArchiveErrors.ArchivedDateInvalid))

  @Test
  def testInvalidArchivedTime(): Unit =
    val errors = validator.validate("2026-08-13", "25:90:00", "admin")
    assertTrue(errors.contains(ArchiveErrors.ArchivedTimeInvalid))

  @Test
  def testIsValidReturnsTrueForValidData(): Unit =
    assertTrue(validator.isValid("2026-08-13", "12:30:00", "admin"))

  @Test
  def testIsValidReturnsFalseForInvalidData(): Unit =
    assertFalse(validator.isValid("", "12:30:00", "admin"))