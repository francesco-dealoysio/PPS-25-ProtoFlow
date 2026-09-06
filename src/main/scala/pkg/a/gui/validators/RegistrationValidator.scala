package pkg.a.gui.validators

import pkg.a.gui.text.UiText.Validation.Registration.*
import pkg.b.logic.Registration

class RegistrationValidator:
  def validate(request: Registration): Seq[String] =
    Seq(
      validateRequired(Name, request.getName),
      validateRequired(Surname, request.getSurname),
      validateEmail(request.getEmail),
      validatePhone(request.getPhone),
      validateRequired(RequestedRole, request.getRole),
      validateRequired(Area, request.getArea),
      validateRequired(Assignment, request.getAssignment)
    ).flatten

  def isValid(request: Registration): Boolean =
    validate(request).isEmpty

  private def validateRequired(fieldName: String, value: String): Option[String] =
    Option.when(value.trim.isEmpty)(required(fieldName))

  private def validatePhone(phone: String): Option[String] =
    val trimmedPhone = phone.trim
    Option.when(trimmedPhone.nonEmpty && !trimmedPhone.matches("^\\d+$"))(PhoneInvalid)

  private def validateEmail(email: String): Option[String] =
    val trimmedEmail = email.trim

    if trimmedEmail.isEmpty then
      Some(EmailRequired)
    else
      Option.unless(trimmedEmail.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))(EmailInvalid)
