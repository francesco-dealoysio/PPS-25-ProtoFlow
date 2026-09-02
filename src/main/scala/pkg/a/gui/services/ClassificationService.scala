package pkg.a.gui.services

import pkg.b.logic.Classification
import pkg.d.util.IdGen
import pkg.d.util.Util.inIdsFilePathName

class ClassificationService:

  private val classificationLogic = new Classification()

  /**
   * Genera l'id e salva una nuova classifica.
   */
  def addClassification(classification: String, description: String): Either[String, Classification] =

    val newClassification =
      Classification(
        id = IdGen(inIdsFilePathName("classificationId")),
        classification = classification,
        description = description
      )

    if classificationLogic.recordInsert(newClassification) then Right(newClassification)
    else Left("Errore durante l'inserimento della classifica")
