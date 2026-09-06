package pkg.a.gui.text

object CommonText:

  object Common:
    private val RequiredMarker = " *"
    private val UserIcon = "👤"
    val ApplicationName = "ProtoFlow"
    val MenuIcon = "☰"

    def headerUserInfo(username: String, roleName: String): String =
      s"$username\n$roleName"

    def footerUserInfo(username: String, roleName: String): String =
      s"$UserIcon $username ($roleName)"

    object Dialogs:
      object Logout:
        val Title = "Conferma logout"
        val Header = "Vuoi uscire da ProtoFlow?"
        val Content = "La sessione corrente verrà terminata."

      object UnsavedChanges:
        val Title = "Modifiche non salvate"
        val Header = "Vuoi uscire senza salvare?"
        val Content = "Le informazioni inserite o modificate non verranno mantenute."

      object Denied:
        val Title = "Azione non consentita"
        val Header = "Non hai i permessi per questa azione."
        val Content = "Il tuo ruolo non è autorizzato a eseguire questa operazione."

    object Documents:
      val NoDocuments = "Nessun documento disponibile."

      object Fields:
        val Id = "Id"
        val ProtocolNumber = "Numero protocollo"
        val Sender = "Mittente"
        val Recipient = "Destinatario"
        val Subject = "Oggetto"

    object WindowTitles:
      val Login = s"${Common.ApplicationName} - Login"
      val Registration = s"${Common.ApplicationName} - Registrazione"
      val Home = s"${Common.ApplicationName} - Homepage"

    object Buttons:
      val Add = "Aggiungi"
      val Edit = "Modifica"
      val Delete = "Elimina"
      val Save = "Salva"
      val Reset = "Reset"
      val Close = "Chiudi"
      val Print = "Stampa"
      val Refresh = "Aggiorna"
      val Approve = "Approva"
      val Reject = "Rifiuta"
      val Process = "Elabora"
      val Register = "Protocolla"
      val Archive = "Archivia"
      val ResetFilter = "Azzera filtri"
      val Login = "Accedi"
      val RequestRegistration = "Richiedi registrazione"

    object Fields:
      object Labels:
        val Id = "Id"
        val Name = "Nome"
        val Surname = "Cognome"
        val Email = "Email"
        val Phone = "Telefono"
        val Role = "Ruolo"
        val Description = "Descrizione"
        val Area = "Area"
        val Assignment = "Mansione"
        val Username = "Username"
        val Password = "Password"
        val Classification = "Classifica"
        val Date = "Data"
        val RoleName = "Nome ruolo"
        val Action = "Azione"

        def required(label: String): String =
          s"$label$RequiredMarker"

      object Prompts:
        val Name = "Inserisci il nome"
        val Surname = "Inserisci il cognome"
        val Email = "Inserisci l'email"
        val Phone = "Inserisci il telefono"
        val Role = "Inserisci il ruolo"
        val SelectRole = "Seleziona il ruolo"
        val SelectAction = "Seleziona l'azione"
        val Description = "Inserisci la descrizione"
        val Area = "Inserisci l'area"
        val Assignment = "Inserisci la mansione"
        val Username = "Inserisci l' username"
        val Password = "Inserisci la password"
        val Classification = "Inserisci la classifica"
        val KeepPassword = "Lascia vuoto per non modificare la password"
        val RoleName = "Inserisci il nome del ruolo"
