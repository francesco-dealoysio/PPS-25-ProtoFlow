package pkg.b.logic

import pkg.c.data.guiStructures.ClassificationRow

/**
 * Fornisce alla GUI i dati necessari per
 * visualizzare l'elenco delle classifiche.
 */
class ClassificationManagementService:

  private val classificationEntity =
    new Classification()

  def getClassifications: Seq[ClassificationRow] =
    classificationEntity
      .getRecords()
      .map: record =>
        ClassificationRow(
          id = record.getId,
          classification = record.getClassification,
          description = record.getDescription
        )
      .sortBy: row =>
        row.id.toIntOption.getOrElse(Int.MaxValue)