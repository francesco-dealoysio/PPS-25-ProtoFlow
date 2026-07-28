package pkg.a.gui.structures

import pkg.b.logic.LoadedDocument

object LoadedDocumentViewModel:
  val DocumentDateRequiredError = "Il campo Data documento è obbligatorio."
  val DocumentTimeRequiredError = "Il campo Ora documento è obbligatorio."
  val DocumentProtocolRequiredError = "Il campo Protocollo mittente è obbligatorio."
  val DocumentTypeRequiredError = "Il campo Tipo documento è obbligatorio."
  val SenderRequiredError = "Il campo Mittente è obbligatorio."
  val RecipientRequiredError = "Il campo Destinatario è obbligatorio."
  val SubjectRequiredError = "Il campo Oggetto è obbligatorio."

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
