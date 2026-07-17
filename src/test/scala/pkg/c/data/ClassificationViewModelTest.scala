package pkg.c.data

import org.scalatest.funsuite.AnyFunSuite
import pkg.b.logic.Classification
import pkg.c.data.guiStructures.ClassificationViewModel

class ClassificationViewModelTest extends AnyFunSuite:

  private val viewModel = ClassificationViewModel()

  test("una classifica completa e corretta deve essere valida"):
    val classificationToValidate = validForm()

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.isEmpty)
    assert(viewModel.isValid(classificationToValidate, existingClassifications))

  test("il campo Classifica è obbligatorio"):
    val classificationToValidate = validForm().copy(classification = "")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.ClassificationRequiredError))
    assert(!viewModel.isValid(classificationToValidate, existingClassifications))

  test("il campo Classifica contenente solo spazi non è valido"):
    val classificationToValidate = validForm().copy(classification = "   ")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.ClassificationRequiredError))
    assert(!viewModel.isValid(classificationToValidate, existingClassifications))

  test("il campo Descrizione è obbligatorio"):
    val classificationToValidate = validForm().copy(description = "")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.DescriptionRequiredError))
    assert(!viewModel.isValid(classificationToValidate, existingClassifications))

  test("il campo Descrizione contenente solo spazi non è valido"):
    val classificationToValidate = validForm().copy(description = "   ")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.DescriptionRequiredError))
    assert(!viewModel.isValid(classificationToValidate, existingClassifications))

  test("se Classifica e Descrizione sono vuote vengono restituiti entrambi gli errori"):
    val classificationToValidate = Classification()

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.size == 2)
    assert(errors.contains(ClassificationViewModel.ClassificationRequiredError))
    assert(errors.contains(ClassificationViewModel.DescriptionRequiredError))
    assert(!viewModel.isValid(classificationToValidate, existingClassifications))

  test("una classifica già esistente non deve essere valida"):
    val classificationToValidate = validForm().copy(classification = "Amministrazione")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.DuplicateClassificationError))
    assert(!viewModel.isValid(classificationToValidate, existingClassifications))

  test("il controllo dei duplicati ignora maiuscole e minuscole"):
    val classificationToValidate = validForm().copy(classification = "AMMINISTRAZIONE")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.DuplicateClassificationError))

  test("il controllo dei duplicati ignora gli spazi iniziali e finali"):
    val classificationToValidate = validForm().copy(classification = "  Amministrazione  ")

    val errors = viewModel.validate(classificationToValidate, existingClassifications)

    assert(errors.contains(ClassificationViewModel.DuplicateClassificationError))

  test("durante la modifica la classifica non deve essere considerata duplicata di se stessa"):
    val classificationToValidate = Classification(
      classification = "Amministrazione",
      description = "Gestione amministrativa aggiornata"
    )

    val errors = viewModel.validate(
      classification = classificationToValidate,
      existingClassifications = existingClassifications,
      currentClassificationId = Some("1")
    )

    assert(errors.isEmpty)
    assert(viewModel.isValid(
      classification = classificationToValidate,
      existingClassifications = existingClassifications,
      currentClassificationId = Some("1")
    ))

  test("durante la modifica il nome di un'altra classifica deve essere considerato duplicato"):
    val classificationToValidate = Classification(
      classification = "Personale",
      description = "Descrizione aggiornata"
    )

    val errors = viewModel.validate(
      classification = classificationToValidate,
      existingClassifications = existingClassifications,
      currentClassificationId = Some("1")
    )

    assert(errors.contains(ClassificationViewModel.DuplicateClassificationError))
    assert(!viewModel.isValid(
      classification = classificationToValidate,
      existingClassifications = existingClassifications,
      currentClassificationId = Some("1")
    ))

  test("nextId restituisce 1 quando non esistono classifiche"):
    val id = viewModel.nextId(Seq.empty)

    assert(id == "1")

  test("nextId restituisce l'identificativo successivo a quello massimo"):
    val classifications = Seq(
      Classification(id = "1", classification = "Amministrazione", description = "Gestione amministrativa"),
      Classification(id = "4", classification = "Personale", description = "Gestione del personale"),
      Classification(id = "2", classification = "Informatica", description = "Gestione informatica")
    )

    val id = viewModel.nextId(classifications)

    assert(id == "5")

  test("nextId ignora gli identificativi non numerici"):
    val classifications = Seq(
      Classification(id = "1", classification = "Amministrazione", description = "Gestione amministrativa"),
      Classification(id = "abc", classification = "Personale", description = "Gestione del personale"),
      Classification(id = "5", classification = "Informatica", description = "Gestione informatica")
    )

    val id = viewModel.nextId(classifications)

    assert(id == "6")

  test("nextId restituisce 1 quando tutti gli identificativi non sono numerici"):
    val classifications = Seq(
      Classification(id = "abc", classification = "Amministrazione", description = "Gestione amministrativa"),
      Classification(id = "xyz", classification = "Personale", description = "Gestione del personale")
    )

    val id = viewModel.nextId(classifications)

    assert(id == "1")

  private def validForm(): Classification =
    Classification(
      classification = "Informatica",
      description = "Gestione dei servizi informatici"
    )

  private def existingClassifications: Seq[Classification] =
    Seq(
      Classification(id = "1", classification = "Amministrazione", description = "Gestione amministrativa"),
      Classification(id = "2", classification = "Personale", description = "Gestione del personale")
    )