package pkg.c.data

import org.scalatest.funsuite.AnyFunSuite
import pkg.a.gui.structures.ClassificationViewModel
import pkg.b.logic.Classification

class ClassificationViewModelTest extends AnyFunSuite:

  private val viewModel = ClassificationViewModel()
  
  test("validazione form: caso valido e campo vuoto/spazi"):
    assertValid(validForm())
    assertInvalid(validForm().copy(classification = "   "))(ClassificationViewModel.ClassificationRequiredError)
    assertInvalid(validForm().copy(description = ""))(ClassificationViewModel.DescriptionRequiredError)

  test("validazione form: errori multipli quando tutto è vuoto"):
    assertInvalid(Classification())(
      ClassificationViewModel.ClassificationRequiredError,
      ClassificationViewModel.DescriptionRequiredError
    )

  test("gestione duplicati: rileva duplicati (anche con spazi e case-insensitive)"):
    assertInvalid(validForm().copy(classification = "Amministrazione"))(ClassificationViewModel.DuplicateClassificationError)
    assertInvalid(validForm().copy(classification = "  amministrazione  "))(ClassificationViewModel.DuplicateClassificationError)

  test("gestione duplicati in modifica: permette se stesso, blocca altri"):
    val updated = Classification(classification = "Amministrazione", description = "Descrizione")
    assertValid(updated, currentId = Some("1"))
    assertInvalid(updated.copy(classification = "Personale"), currentId = Some("1"))(ClassificationViewModel.DuplicateClassificationError)

  test("nextId calcola il nuovo id numerico incrementale o fallback a 1"):
    val validSeq = Seq(
      Classification(id = "1", classification = "A", description = "D"),
      Classification(id = "4", classification = "P", description = "D")
    )
    val invalidSeq = Seq(Classification(id = "abc", classification = "A", description = "D"))

    assert(viewModel.nextId(Seq.empty) == "1")
    assert(viewModel.nextId(validSeq) == "5")
    assert(viewModel.nextId(invalidSeq) == "1")
  
  // Test Helpers
  private def assertValid(classification: Classification, currentId: Option[String] = None): Unit =
    val errors = viewModel.validate(classification, existingClassifications, currentId)
    assert(errors.isEmpty)
    assert(viewModel.isValid(classification, existingClassifications, currentId))

  private def assertInvalid(classification: Classification, currentId: Option[String] = None)(expectedErrors: String*): Unit =
    val errors: Seq[String] = viewModel.validate(classification, existingClassifications, currentId)
    assert(errors.size == expectedErrors.size)
    expectedErrors.foreach(err => assert(errors.contains(err)))
    assert(!viewModel.isValid(classification, existingClassifications, currentId))

  private def validForm(): Classification =
    Classification(classification = "Informatica", description = "Gestione servizi")

  private def existingClassifications: Seq[Classification] =
    Seq(
      Classification(id = "1", classification = "Amministrazione", description = "Gestione amministrativa"),
      Classification(id = "2", classification = "Personale", description = "Gestione del personale")
    )