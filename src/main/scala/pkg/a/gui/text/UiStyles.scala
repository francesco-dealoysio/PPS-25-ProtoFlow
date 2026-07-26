package pkg.a.gui.text

object UiStyles:

  val FormField = "form-field"

  final case class Section(
                            root: String,
                            title: String,
                            subtitle: String,
                            message: String,
                            messageSuccess: String,
                            messageError: String,
                            table: String
                          )

  val Roles = Section(
    root = "roles-management-root",
    title = "roles-title",
    subtitle = "roles-subtitle",
    message = "roles-message",
    messageSuccess = "roles-message-success",
    messageError = "roles-message-error",
    table = "roles-table"
  )

  val Accounts = Section(
    root = "accounts-management-root",
    title = "accounts-title",
    subtitle = "accounts-subtitle",
    message = "accounts-message",
    messageSuccess = "accounts-message-success",
    messageError = "accounts-message-error",
    table = "accounts-table"
  )

  val Classifications = Section(
    root = "classifications-management-root",
    title = "classifications-title",
    subtitle = "classifications-subtitle",
    message = "classifications-message",
    messageSuccess = "classifications-message-success",
    messageError = "classifications-message-error",
    table = "classifications-table"
  )

  val Requests = Section(
    root = "requests-management-root",
    title = "requests-title",
    subtitle = "requests-subtitle",
    message = "requests-message",
    messageSuccess = "requests-message-success",
    messageError = "requests-message-error",
    table = "requests-table"
  )
