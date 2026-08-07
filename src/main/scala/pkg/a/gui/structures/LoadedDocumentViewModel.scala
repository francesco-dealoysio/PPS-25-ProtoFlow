package pkg.a.gui.structures

import pkg.b.logic.LoadedDocument
import pkg.a.gui.text.UiText.Validation.LoadedDocument.*

class LoadedDocumentViewModel:

  def validate(document: LoadedDocument): Seq[String] =
    Seq(
      validateRequired(DocumentDateRequired, document.getDocumentDate),
      validateRequired(DocumentTimeRequired, document.getDocumentTime),
      validateRequired(DocumentProtocolRequired, document.getDocumentProtocol),
      validateRequired(DocumentTypeRequired, document.getDocumentType),
      validateRequired(SenderRequired, document.getSender),
      validateRequired(RecipientRequired, document.getRecipient),
      validateRequired(SubjectRequired, document.getSubject)
    ).flatten

  def isValid(document: LoadedDocument): Boolean =
    validate(document).isEmpty

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    if value.trim.isEmpty then
      Some(errorMessage)
    else
      None
