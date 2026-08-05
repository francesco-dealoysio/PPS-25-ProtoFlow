package pkg.a.gui.structures

import pkg.b.logic.Registration
import pkg.a.gui.text.UiText.Validation.Registration.*

class RegistrationViewModel:
  def validate(request: Registration): Seq[String] =
    Seq(
      validateRequired(Name, request.getName),
      validateRequired(Surname, request.getSurname),
      validateEmail(request.getEmail),
      validateRequired(RequestedRole, request.getRole),
      validateRequired(Area, request.getArea),
      validateRequired(Assignment, request.getAssignment)
    ).flatten

  def isValid(request: Registration): Boolean =
    validate(request).isEmpty

  private def validateRequired(fieldName: String, value: String): Option[String] =
    if value.trim.isEmpty then Some(required(fieldName))
    else None

  private def validateEmail(email: String): Option[String] =
    val trimmedEmail = email.trim

    if trimmedEmail.isEmpty then
      Some(EmailRequired)
    else if !trimmedEmail.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$") then
      Some(EmailInvalid)
    else
      None
