package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.validation.RegistrationValidator
import pkg.b.logic.Registration

class RegistrationValidatorTest:

  private val validator = RegistrationValidator()

  @Test
  def testValidRegistrationRequest(): Unit =
    assertValid(validRequest())
    assertValid(validRequest().copy(phone = ""))

  @Test
  def testRequiredFieldsValidation(): Unit =
    assertInvalid(validRequest().copy(name = ""))("Il campo 'Nome' è obbligatorio.")
    assertInvalid(validRequest().copy(surname = ""))("Il campo 'Cognome' è obbligatorio.")
    assertInvalid(validRequest().copy(email = ""))("Il campo 'Indirizzo email' è obbligatorio.")
    assertInvalid(validRequest().copy(role = ""))("Il campo 'Ruolo richiesto' è obbligatorio.")
    assertInvalid(validRequest().copy(area = ""))("Il campo 'Area/Settore di appartenenza' è obbligatorio.")
    assertInvalid(validRequest().copy(assignment = ""))("Il campo 'Incarico' è obbligatorio.")

  @Test
  def testInvalidEmailFormat(): Unit =
    assertInvalid(validRequest().copy(email = "email-non-valida"))("L'indirizzo email non ha un formato valido.")

  @Test
  def testMultipleRequiredFieldsReturnAllErrors(): Unit =
    val emptyRequest = validRequest().copy(name = "", surname = "", email = "", role = "", area = "", assignment = "")
    assertInvalid(emptyRequest)(
      "Il campo 'Nome' è obbligatorio.",
      "Il campo 'Cognome' è obbligatorio.",
      "Il campo 'Indirizzo email' è obbligatorio.",
      "Il campo 'Ruolo richiesto' è obbligatorio.",
      "Il campo 'Area/Settore di appartenenza' è obbligatorio.",
      "Il campo 'Incarico' è obbligatorio."
    )

  // Test Helpers
  private def assertValid(request: Registration): Unit =
    val errors = validator.validate(request)
    assertTrue(errors.isEmpty)
    assertTrue(validator.isValid(request))

  private def assertInvalid(request: Registration)(expectedErrors: String*): Unit =
    val errors = validator.validate(request)
    assertEquals(expectedErrors.size, errors.size)
    expectedErrors.foreach(error => assertTrue(errors.contains(error)))
    assertFalse(validator.isValid(request))

  private def validRequest(): Registration =
    Registration(
      id = "test-request-1", name = "Mario", surname = "Rossi", email = "mario.rossi@email.it",
      phone = "3331234567", role = "Operatore Protocollo", area = "Urbanistica",
      assignment = "Addetto protocollo", date = "2026-07-10 10:00:00.00", state = "Pending"
    )