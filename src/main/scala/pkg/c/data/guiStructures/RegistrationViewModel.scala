package pkg.c.data.guiStructures

class RegistrationViewModel:
  def validate(request: RegistrationRequest): Seq[String] =
    Seq(
      validateRequired("Nome", request.name),
      validateRequired("Cognome", request.surname),
      validateEmail(request.email),
      validateRequired("Ruolo richiesto", request.requestedRole),
      validateRequired("Area/Settore di appartenenza", request.area),
      validateRequired("Incarico", request.assignment)
    ).flatten

  def isValid(request: RegistrationRequest): Boolean =
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
