package pkg.a.gui.text

object IdentityText:

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
      val PrintFileName = "elenco-ruoli"
      val AdminRoleDeleteError = "Non è possibile eliminare il ruolo di amministratore."
      val RoleInUseDeleteError = "Non è possibile eliminare un ruolo assegnato a un account o presente in una richiesta di registrazione in attesa."

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
      val PrintFileName = "elenco-classifiche"

      def deleted(classification: String): String =
        s"La classifica '$classification' è stata eliminata correttamente."

  object AuthorizationRules:

    object Add:
      val Title = "Aggiunta regola di autorizzazione"
      val Subtitle = "Concedi a un ruolo il permesso di eseguire un'azione, tramite una regola Prolog personalizzata."
      val Success = "Regola aggiunta correttamente."
      val AlreadyExists = "Questo ruolo ha già il permesso per questa azione."
      val RoleRequired = "Seleziona un ruolo."
      val ActionRequired = "Seleziona un'azione."

    object Management:
      val Title = "Gestione Autorizzazioni"
      val Subtitle = "Regole di autorizzazione personalizzate, aggiunte oltre a quelle di base del sistema."
      val Empty = "Non sono presenti regole personalizzate."
      val SelectToDelete = "Seleziona una regola da rimuovere."
      val DeleteTitle = "Rimozione regola"
      val DeleteConfirmation = "Confermi la rimozione della regola selezionata?"
      val DeleteError = "Non è stato possibile rimuovere la regola."
      val RoleColumn = "Ruolo"
      val ActionColumn = "Azione"

      def deleted(role: String, action: String): String =
        s"Regola rimossa: $role non può più eseguire '$action'."

  object Accounts:
    object Profile:
      val Title = "Profilo"
      val Subtitle = "Visualizza le informazioni del tuo account e modifica i dati personali."
      val AccountInfo = "Informazioni account"
      val EditableInfo = "Modifica dati personali"
      val Success = "Profilo modificato correttamente."
      val Error = "Errore durante la modifica del profilo."

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
      val Title = "Gestione Account Utenti"
      val Subtitle = "Visualizza, aggiungi, modifica ed elimina gli account degli utenti del sistema."
      val Empty = "Non sono presenti account nel sistema."
      val LoadError = "Errore durante il caricamento degli account."
      val SelectToEdit = "Seleziona un account da modificare."
      val SelectToDelete = "Seleziona un account da eliminare."
      val DeleteTitle = "Eliminazione account"
      val DeleteConfirmation = "Confermi l'eliminazione dell'account selezionato?"
      val DeleteError = "Non è stato possibile eliminare l'account."
      val LastAdminDeleteError = "Non è possibile eliminare l'ultimo account amministratore."
      val PrintTitle = "Elenco Account Utenti"
      val PrintFileName = "elenco-account"
      val PrintSuccess = "Elenco account stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa dell'elenco account."
      def deletedAccount(username: String): String =
        s"L'account '$username' è stato eliminato correttamente."

  object RegistrationRequests:
    object Management:
      val Title = "Gestione richieste registrazione"
      val Subtitle = "Visualizza le richieste di registrazione in attesa ed elaborale."
      val Empty = "Non sono presenti richieste di registrazione da elaborare."
      val LoadError = "Errore durante il caricamento delle richieste di registrazione."
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
      val TemporaryPasswordLabel = "Password temporanea"

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
    val ApplicationSubtitle = "Enterprise Document Protocol System"
    val EmptyCredentials = "Inserisci username e password."
    val InvalidCredentials = "Accesso negato. Username o password non corretti."

    def unknownRole(role: String): String =
      s"Errore applicativo: ruolo '$role' non riconosciuto."

  object Registration:
    val Title = "Registrazione"
    val Subtitle = "Compila il modulo per richiedere l'accreditamento al sistema ProtoFlow."
    val EmailPrompt = "nome.cognome@email.it"
    val RolePrompt = "Seleziona ruolo"
    val AreaPrompt = "Seleziona area"
    val AssignmentPrompt = "Inserisci incarico"

    val SubmitSuccess = "Richiesta presa in carico e salvata correttamente."
    val ValidationHeader = "Errori riscontrati:\n- "
    val ValidationSeparator = "\n- "

    object ExitDialog:
      val Content = "I dati inseriti nella richiesta di registrazione verranno persi."
