package pkg.a.gui

import org.junit.*
import org.junit.Assert.*
import pkg.a.gui.structures.ClassificationViewModel
import pkg.b.logic.Classification
import pkg.a.gui.text.UiText.Validation.Classification.*

class ClassificationViewModelTest:

  private val viewModel = ClassificationViewModel()

  @Test
  def testValidFormAndEmptyFields(): Unit =
    assertValid(validForm())
    assertInvalid(validForm().copy(classification = "   "))(ClassificationRequired)
    assertInvalid(validForm().copy(description = ""))(DescriptionRequired)

  @Test
  def testMultipleErrorsWhenFormIsEmpty(): Unit =
    assertInvalid(Classification())(ClassificationRequired, DescriptionRequired)

  @Test
  def testDuplicateClassificationCaseInsensitiveAndTrimmed(): Unit =
    assertInvalid(validForm().copy(classification = "Amministrazione"))(DuplicateClassification)
    assertInvalid(validForm().copy(classification = "  amministrazione  "))(DuplicateClassification)

  @Test
  def testDuplicateClassificationOnEdit(): Unit =
    val updated = Classification(classification = "Amministrazione", description = "Descrizione")
    assertValid(updated, currentId = Some("1"))
    assertInvalid(updated.copy(classification = "Personale"), currentId = Some("1"))(DuplicateClassification)

  // Test Helpers
  private def assertValid(classification: Classification, currentId: Option[String] = None): Unit =
    val errors = viewModel.validate(classification, existingClassifications, currentId)
    assertTrue(errors.isEmpty)
    assertTrue(viewModel.isValid(classification, existingClassifications, currentId))

  private def assertInvalid(classification: Classification, currentId: Option[String] = None)(expectedErrors: String*): Unit =
    val errors: Seq[String] = viewModel.validate(classification, existingClassifications, currentId)
    assertEquals(expectedErrors.size, errors.size)
    expectedErrors.foreach(error => assertTrue(errors.contains(error)))
    assertFalse(viewModel.isValid(classification, existingClassifications, currentId))

  private def validForm(): Classification =
    Classification(classification = "Informatica", description = "Gestione servizi")

  private def existingClassifications: Seq[Classification] = Seq(
    Classification(id = "1", classification = "Amministrazione", description = "Gestione amministrativa"),
    Classification(id = "2", classification = "Personale", description = "Gestione del personale")
  )