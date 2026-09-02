package pkg.a.gui.services

import pkg.b.logic.Classification
import pkg.d.util.IdGen
import pkg.d.util.Util.{inDatabaseFilePathName, inIdsFilePathName}

object ClassificationService:

  private val classificationLogic = new Classification()

  def addClassification(
                         classification: String,
                         description: String,
                         classificationsFilePathName: String = inDatabaseFilePathName("classifications.xml"),
                         classificationIdFilePathName: String = inIdsFilePathName("classificationId")
                       ): Either[String, Classification] =

    val newClassification =
      Classification(
        id = IdGen(classificationIdFilePathName),
        classification = classification,
        description = description
      )

    if classificationLogic.recordInsert(newClassification, classificationsFilePathName) then
      Right(newClassification)
    else
      Left("Errore durante l'inserimento della classifica")