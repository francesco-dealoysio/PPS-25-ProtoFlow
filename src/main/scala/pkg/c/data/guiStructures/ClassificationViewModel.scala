package pkg.c.data.guiStructures

import pkg.b.logic.Classification

object ClassificationViewModel:
  val ClassificationRequiredError =
    "Il campo Classifica è obbligatorio."

  val DescriptionRequiredError =
    "Il campo Descrizione è obbligatorio."

  val DuplicateClassificationError =
    "Esiste già una classifica con questo nome."

class ClassificationViewModel:
  import ClassificationViewModel.*

  def validate(
                classification: Classification,
                existingClassifications: Seq[Classification],
                currentClassificationId: Option[String] = None
              ): Seq[String] =
    Seq(
      validateRequired(
        ClassificationRequiredError,
        classification.getClassification
      ),
      validateRequired(
        DescriptionRequiredError,
        classification.getDescription
      ),
      validateUniqueClassification(
        classification.getClassification,
        existingClassifications,
        currentClassificationId
      )
    ).flatten

  def isValid(
               classification: Classification,
               existingClassifications: Seq[Classification],
               currentClassificationId: Option[String] = None
             ): Boolean =
    validate(
      classification,
      existingClassifications,
      currentClassificationId
    ).isEmpty

  def nextId(existingClassifications: Seq[Classification]): String =
    val maximumId =
      existingClassifications
        .flatMap(_.getId.toIntOption)
        .maxOption
        .getOrElse(0)

    (maximumId + 1).toString

  private def validateRequired(
                                errorMessage: String,
                                value: String
                              ): Option[String] =
    if value.trim.isEmpty then Some(errorMessage)
    else None

  private def validateUniqueClassification(
                                            classificationName: String,
                                            existingClassifications: Seq[Classification],
                                            currentClassificationId: Option[String]
                                          ): Option[String] =
    val normalizedName = classificationName.trim

    if normalizedName.isEmpty then
      None
    else
      val duplicateExists =
        existingClassifications.exists: existing =>
          !currentClassificationId.contains(existing.getId) &&
            existing.getClassification.trim.equalsIgnoreCase(normalizedName)

      if duplicateExists then Some(DuplicateClassificationError)
      else None