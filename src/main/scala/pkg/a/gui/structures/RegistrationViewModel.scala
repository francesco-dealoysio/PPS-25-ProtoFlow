package pkg.a.gui.structures

import pkg.b.logic.Registration

class RegistrationViewModel:
  def validate(request: Registration): Seq[String] =
    Seq(
      validateRequired("Nome", request.getName),
      validateRequired("Cognome", request.getSurname),
      validateEmail(request.getEmail),
      validateRequired("Ruolo richiesto", request.getRole),
      validateRequired("Area/Settore di appartenenza", request.getArea),
      validateRequired("Incarico", request.getAssignment)
    ).flatten

  def isValid(request: Registration): Boolean =
    validate(request).isEmpty

  private def validateRequired(fieldName: String, value: String): Option[String] =
    if value.trim.isEmpty then Some(s"Il campo '$fieldName' è obbligatorio.")
    else None

  private def validateEmail(email: String): Option[String] =
    val trimmedEmail = email.trim

    if trimmedEmail.isEmpty then
      Some("Il campo 'Indirizzo email' è obbligatorio.")
    else if !trimmedEmail.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$") then
      Some("L'indirizzo email non ha un formato valido.")
    else
      None
