package pkg.a.gui.structures

import pkg.b.logic.LoadedDocument
import pkg.a.gui.text.UiText.Validation.LoadedDocument.*

object LoadedDocumentViewModel:
  val DocumentDateRequiredError: String = DocumentDateRequired
  val DocumentTimeRequiredError: String  = DocumentTimeRequired
  val DocumentProtocolRequiredError: String  = DocumentProtocolRequired
  val DocumentTypeRequiredError: String  = DocumentTypeRequired
  val SenderRequiredError: String  = SenderRequired
  val RecipientRequiredError: String  = RecipientRequired
  val SubjectRequiredError: String  = SubjectRequired

class LoadedDocumentViewModel:

  import LoadedDocumentViewModel.*

  def validate(document: LoadedDocument): Seq[String] =
    Seq(
      validateRequired(DocumentDateRequiredError, document.getDocumentDate),
      validateRequired(DocumentTimeRequiredError, document.getDocumentTime),
      validateRequired(DocumentProtocolRequiredError, document.getDocumentProtocol),
      validateRequired(DocumentTypeRequiredError, document.getDocumentType),
      validateRequired(SenderRequiredError, document.getSender),
      validateRequired(RecipientRequiredError, document.getRecipient),
      validateRequired(SubjectRequiredError, document.getSubject)
    ).flatten

  def isValid(document: LoadedDocument): Boolean =
    validate(document).isEmpty

  private def validateRequired(errorMessage: String, value: String): Option[String] =
    if value.trim.isEmpty then
      Some(errorMessage)
    else
      None
