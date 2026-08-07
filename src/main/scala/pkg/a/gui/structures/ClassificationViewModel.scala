package pkg.a.gui.structures

import pkg.b.logic.Classification
import pkg.a.gui.text.UiText.Validation.Classification.*

class ClassificationViewModel:

  def validate(classification: Classification, existingClassifications: Seq[Classification], currentClassificationId: Option[String] = None): Seq[String] =
    Seq(
      validateRequired(ClassificationRequired, classification.getClassification),
      validateRequired(DescriptionRequired, classification.getDescription),
      validateUniqueClassification(classification.getClassification, existingClassifications, currentClassificationId)
    ).flatten

  def isValid(classification: Classification, existingClassifications: Seq[Classification], currentClassificationId: Option[String] = None): Boolean =
    validate(classification, existingClassifications, currentClassificationId).isEmpty

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    if value.trim.isEmpty then Some(errorMessage)
    else None

  private def validateUniqueClassification(classificationName: String, existingClassifications: Seq[Classification], currentClassificationId: Option[String]): Option[String] =
    val normalizedName = classificationName.trim

    if normalizedName.isEmpty then
      None
    else
      val duplicateExists =
        existingClassifications.exists: existing =>
          !currentClassificationId.contains(existing.getId) &&
            existing.getClassification.trim.equalsIgnoreCase(normalizedName)

      if duplicateExists then Some(DuplicateClassification)
      else None