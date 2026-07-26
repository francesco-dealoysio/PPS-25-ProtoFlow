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
      val Classification = "Classifica"
      val Date = "Data"

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
      val Classification = "Inserisci la classifica"
      val KeepPassword = "Lascia vuoto per non modificare la password"

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
      val Title = "Gestione Ruoli"
      val Subtitle = "Visualizza, aggiungi, modifica ed elimina i ruoli del sistema."
      val Empty = "Non sono presenti ruoli nel sistema."
      val LoadError = "Errore durante il caricamento dei ruoli."
      val SelectToEdit = "Seleziona un ruolo da modificare."
      val SelectToDelete = "Seleziona un ruolo da eliminare."
      val DeleteTitle = "Eliminazione ruolo"
      val DeleteConfirmation = "Confermi l'eliminazione del ruolo selezionato?"
      val DeleteError = "Non è stato possibile eliminare il ruolo."

      val PrintSuccess = "Elenco dei ruoli stampato correttamente nella cartella protoflow/prints."
      val PrintError = "Non è stato possibile stampare l'elenco dei ruoli."
      val PrintTitle = "Elenco Ruoli"

      def deleted(role: String): String =
        s"Il ruolo '$role' è stato eliminato correttamente."

  object Classifications:

    object Add:
      val Title = "Aggiunta classifica"
      val Subtitle = "Inserisci i dati della nuova classifica."
      val Success = "Classifica inserita correttamente."
      val Error = "Errore durante l'inserimento della classifica."

    object Edit:
      val Title = "Modifica classifica"
      val Subtitle = "Modifica i dati della classifica selezionata."
      val Success = "Classifica modificata correttamente."
      val Error = "Errore durante la modifica della classifica."

    object Management:
      val Title = "Gestione Classifiche"
      val Subtitle = "Visualizza e seleziona le classifiche utilizzate per la catalogazione dei documenti."
      val Empty = "Non sono presenti classifiche nel sistema."
      val LoadError = "Errore durante il caricamento delle classifiche."
      val SelectToEdit = "Seleziona una classifica da modificare."
      val SelectToDelete = "Seleziona una classifica da eliminare."
      val DeleteTitle = "Eliminazione classifica"
      val DeleteConfirmation = "Confermi l'eliminazione della classifica selezionata?"
      val DeleteError = "Non è stato possibile eliminare la classifica."

      val PrintEmpty = "Non sono presenti classifiche da stampare."
      val PrintSuccess = "Elenco delle classifiche stampato correttamente nella cartella protoflow/prints."
      val PrintError = "Non è stato possibile stampare l'elenco delle classifiche."
      val PrintTitle = "Elenco Classifiche"

      def deleted(classification: String): String =
        s"La classifica '$classification' è stata eliminata correttamente."

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
      val PrintSuccess = "Scheda account stampata correttamente in PDF."
      val PrintError = "Errore durante la stampa della scheda account."
      val PrintTitle = "Scheda Account Utente"

    object Management:
      val Title = "Gestione Account Utente"
      val Empty = "Non sono presenti account nel sistema."
      val LoadError = "Errore durante il caricamento degli account."
      val SelectToEdit = "Seleziona un account da modificare."
      val SelectToDelete = "Seleziona un account da eliminare."
      val DeleteTitle = "Eliminazione account"
      val DeleteConfirmation = "Confermi l'eliminazione dell'account selezionato?"
      val DeleteError = "Non è stato possibile eliminare l'account."

      def deleted(username: String): String =
        s"L'account '$username' è stato eliminato correttamente."

  object RegistrationRequests:
    object Management:
      val Title = "Gestione richieste registrazione"
      val Subtitle = "Visualizza le richieste di registrazione in attesa ed elaborale."
      val Empty = "Non sono presenti richieste di registrazione da elaborare."
      val SelectToProcess = "Seleziona una richiesta da elaborare."

      val PrintTitle = "Elenco Richieste di Registrazione da Elaborare"
      val PrintFileName = "richieste_registrazione_elenco"
      val PrintSuccess = "Elenco stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa dell'elenco (nessuna richiesta presente?)."

    object Process:
      val Title = "Elaborazione richiesta registrazione"
      val Subtitle = "Visualizza i dati della richiesta e approvala o rifiutala."
      val DetailsTitle = "Dettaglio richiesta"
      val MotivationLabel = "Motivazione rifiuto"
      val MotivationPrompt = "Obbligatoria per rifiutare la richiesta"
      val EmptyMotivationError = "Inserisci la motivazione del rifiuto."
      val RejectSuccess = "Richiesta rifiutata correttamente."
      val PrintSuccess = "Scheda stampata correttamente in PDF."
      val PrintError = "Errore durante la stampa della scheda."

      val RejectTitle = "Conferma rifiuto"
      val RejectHeader = "Rifiutare la richiesta selezionata?"

      val ApproveTitle = "Conferma approvazione"
      val ApproveHeader = "Approvare la richiesta selezionata?"

      val PrintFileNamePrefix = "richiesta"
      val PrintPendingTitle = "Scheda Richiesta di Registrazione"
      val PrintApprovedTitle = "Esito Richiesta di Registrazione - Approvata"
      val PrintRejectedTitle = "Esito Richiesta di Registrazione - Rifiutata"

      def approved(username: String, password: String): String =
        s"Richiesta approvata. Account creato con username '$username' e password temporanea '$password': comunicali al richiedente."

  object Login:
    val ApplicationTitle = "ProtoFlow"
    val ApplicationSubtitle = "Enterprise Document Protocol System"
    val EmptyCredentials = "Inserisci username e password."
    val InvalidCredentials = "Accesso negato. Username o password non corretti."

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
    val Assignments = "Prese in carico"
    val Archiving = "Archiviazione"
    val Logout = "Logout"

  object Registration:
    val Title = "Registrazione"
    val Subtitle = "Compila il modulo per richiedere l'accreditamento al sistema ProtoFlow."

    val NamePrompt = "Inserisci il nome"
    val SurnamePrompt = "Inserisci il cognome"
    val EmailPrompt = "nome.cognome@email.it"
    val PhonePrompt = "Inserisci il telefono"
    val RolePrompt = "Seleziona ruolo"
    val AreaPrompt = "Seleziona area"
    val AssignmentPrompt = "Inserisci incarico"

    val SubmitSuccess = "Richiesta presa in carico e salvata correttamente."
    val ValidationHeader = "Errori riscontrati:\n- "
    val ValidationSeparator = "\n- "

    object ExitDialog:
      val Title = "Modifiche non salvate"
      val Header = "Vuoi uscire senza salvare?"
      val Content = "I dati inseriti nella richiesta di registrazione verranno persi."