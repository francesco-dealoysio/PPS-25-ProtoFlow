package pkg.a.gui.validators

import pkg.a.gui.text.UiText.Validation.LoadedDocument.*
import pkg.b.logic.LoadedDocument

class LoadedDocumentValidator:

  def validate(document: LoadedDocument): Seq[String] =
    Seq(
      validateRequired(DocumentDateRequired, document.getDocumentDate),
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
