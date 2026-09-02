package pkg.a.gui.validators

import org.junit.Assert.*
import org.junit.Test
import pkg.a.gui.text.UiText.Validation.LoadedDocument.*
import pkg.b.logic.LoadedDocument

class LoadedDocumentValidatorTest:

  private val validator = LoadedDocumentValidator()

  @Test
  def testValidDocument(): Unit =
    val document = validDocument()
    assertTrue(validator.validate(document).isEmpty)
    assertTrue(validator.isValid(document))

  @Test
  def testRequiredFields(): Unit =
    val document = LoadedDocument()
    val errors = validator.validate(document)
    assertEquals(6, errors.size)
    assertTrue(errors.contains(DocumentDateRequired))
    assertTrue(errors.contains(DocumentProtocolRequired))
    assertTrue(errors.contains(DocumentTypeRequired))
    assertTrue(errors.contains(SenderRequired))
    assertTrue(errors.contains(RecipientRequired))
    assertTrue(errors.contains(SubjectRequired))
    assertFalse(validator.isValid(document))

  @Test
  def testBlankFieldIsInvalid(): Unit =
    val document = validDocument().copy(subject = "   ")
    val errors = validator.validate(document)
    assertEquals(1, errors.size)
    assertTrue(errors.contains(SubjectRequired))
    assertFalse(validator.isValid(document))

  private def validDocument(): LoadedDocument =
    LoadedDocument(
      documentDate = "2026-09-02",
      documentProtocol = "PROT-001",
      documentType = "Email",
      sender = "Mario Rossi",
      recipient = "Ufficio amministrazione",
      subject = "Richiesta documentazione"
    )