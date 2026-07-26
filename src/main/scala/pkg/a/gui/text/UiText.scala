package pkg.a.gui.text

object UiText:

  object Common:
    val RequiredMarker = " *"

    object Buttons:
      val Add = "Aggiungi"
      val Edit = "Modifica"
      val Delete = "Elimina"
      val Save = "Salva"
      val Reset = "Ripristina"
      val Clear = "Pulisci"
      val Close = "Chiudi"
      val Exit = "Esci"
      val Print = "Stampa"
      val PrintList = "Stampa elenco"
      val Refresh = "Aggiorna"
      val Approve = "Approva"
      val Reject = "Rifiuta"
      val Process = "Elabora"
      val Login = "Accedi"
      val RequestRegistration = "Richiedi registrazione"

  object Fields:
    object Labels:
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

      def required(label: String): String =
        s"$label${Common.RequiredMarker}"

    object Prompts:
      val Name = "Inserisci il nome"
      val Surname = "Inserisci il cognome"
      val Email = "Inserisci l'email"
      val Phone = "Inserisci il telefono"
      val Role = "Inserisci il ruolo"
      val SelectRole = "Seleziona il ruolo"
      val Description = "Inserisci la descrizione"
      val Area = "Inserisci l'area"
      val Assignment = "Inserisci la mansione"
      val Username = "Inserisci lo username"
      val Password = "Inserisci la password"

  object Roles:
    object Add:
      val Title = "Aggiunta ruolo"
      val Subtitle = "Inserisci i dati del nuovo ruolo."
      val Success = "Ruolo inserito correttamente."
      val Error = "Errore durante l'inserimento del ruolo."

    object Edit:
      val Title = "Modifica ruolo"
      val Subtitle = "Modifica i dati del ruolo selezionato."
      val Success = "Ruolo modificato correttamente."
      val Error = "Errore durante la modifica del ruolo."

    object Management:
      val Title = "Gestione ruoli"
      val Empty = "Non sono presenti ruoli nel sistema."
      val SelectToEdit = "Seleziona un ruolo da modificare."
      val SelectToDelete = "Seleziona un ruolo da eliminare."

      def deleted(role: String): String =
        s"Il ruolo '$role' è stato eliminato correttamente."

  object Accounts:
    object Add:
      val Title = "Aggiunta account"
      val Subtitle = "Inserisci i dati del nuovo account."
      val Success = "Account inserito correttamente."
      val Error = "Errore durante l'inserimento dell'account."

    object Edit:
      val Title = "Modifica account"
      val Subtitle = "Modifica i dati dell'account selezionato."
      val Success = "Account modificato correttamente."
      val Error = "Errore durante la modifica dell'account."

    object Management:
      val Title = "Gestione Account Utente"
      val Empty = "Non sono presenti account nel sistema."
      val LoadError = "Errore durante il caricamento degli account."
      val SelectToEdit = "Seleziona un account da modificare."
      val SelectToDelete = "Seleziona un account da eliminare."
      val DeleteTitle = "Eliminazione account"
      val DeleteConfirmation =
        "Confermi l'eliminazione dell'account selezionato?"
      val DeleteError =
        "Non è stato possibile eliminare l'account."

      def deleted(username: String): String =
        s"L'account '$username' è stato eliminato correttamente."

  object Login:
    val ApplicationTitle = "ProtoFlow"
    val ApplicationSubtitle = "Enterprise Document Protocol System"
    val EmptyCredentials = "Inserisci username e password."
    val InvalidCredentials =
      "Accesso negato. Username o password non corretti."

    def unknownRole(role: String): String =
      s"Errore applicativo: ruolo '$role' non riconosciuto."

  object Menu:
    val Dashboard = "Dashboard"
    val Profile = "Profilo"
    val Statistics = "Statistiche"
    val Log = "Log"
    val ManagementControl = "Controllo Gestione"
    val Registrations = "Registrazioni"
    val UserAccounts = "Account Utenti"
    val Roles = "Ruoli"
    val Classifications = "Classifiche"
    val Protocols = "Visualizzazione Protocollazioni"
    val Logout = "Logout"