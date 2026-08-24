package pkg.a.gui.text

import pkg.a.gui.structures.MenuAction

object UiText:

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
      val Clear = "Pulisci"
      val Close = "Chiudi"
      val Exit = "Esci"
      val Print = "Stampa"
      val PrintList = "Stampa elenco"
      val Refresh = "Aggiorna"
      val Approve = "Approva"
      val Reject = "Rifiuta"
      val Process = "Elabora"
      val Register = "Protocolla"
      val Archive = "Archivia"
      val Search = "Cerca"
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
      val AdminRoleDeleteError = "Non è possibile eliminare il ruolo di amministratore."

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
      val Title = "Gestione Account Utente"
      val Subtitle = "Visualizza, aggiungi, modifica ed elimina gli account degli utenti del sistema."
      val Empty = "Non sono presenti account nel sistema."
      val LoadError = "Errore durante il caricamento degli account."
      val SelectToEdit = "Seleziona un account da modificare."
      val SelectToDelete = "Seleziona un account da eliminare."
      val DeleteTitle = "Eliminazione account"
      val DeleteConfirmation = "Confermi l'eliminazione dell'account selezionato?"
      val DeleteError = "Non è stato possibile eliminare l'account."
      val LastAdminDeleteError = "Non è possibile eliminare l'ultimo account amministratore."
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

  object LoadedDocuments:
    object Fields:
      val DocumentDate = "Data documento"
      val DocumentTime = "Ora documento"
      val ProcessedBy = "Preso in carico da"
      val DocumentProtocol = "Protocollo mittente"
      val DocumentType = "Tipo documento"
      val Remarks = "Note"

    object Prompts:
      val DocumentTime = "Inserisci l'ora del documento (HH:mm:ss)"
      val DocumentProtocol = "Inserisci il protocollo del mittente"
      val DocumentType = "Inserisci il tipo di documento"
      val Sender = "Inserisci il mittente"
      val Recipient = "Inserisci il destinatario"
      val Subject = "Inserisci l'oggetto"
      val Remarks = "Inserisci eventuali note"

    object Add:
      val Title = "Presa in carico documento"
      val Subtitle = "Inserisci i dati del documento da prendere in carico."
      val Success = "Documento preso in carico correttamente."
      val Error = "Errore durante la presa in carico del documento."

      val SaveTitle = "Conferma presa in carico"
      val SaveHeader = "Confermi la presa in carico del documento?"

    object Management:
      val Title = "Gestione documenti presi in carico"
      val Subtitle = "Visualizza i documenti presi in carico e avviane la protocollazione."
      val Empty = "Non sono presenti documenti presi in carico nel sistema."
      val LoadError = "Errore durante il caricamento dei documenti presi in carico."
      val SelectToDelete = "Seleziona un documento da eliminare."
      val SelectToRegister = "Seleziona un documento da protocollare."
      val DeleteTitle = "Eliminazione documento"
      val DeleteConfirmation = "Confermi l'eliminazione del documento selezionato? L'operazione non può essere annullata."
      val DeleteError = "Non è stato possibile eliminare il documento."
      val Deleted = "Il documento è stato eliminato correttamente."
      val AllOperators = "Tutti gli operatori"
      val NoFilterResults = "Nessun documento corrisponde ai filtri selezionati."

      val PrintTitle = "Elenco Documenti Presi in Carico"
      val PrintFileName = "documenti_presi_in_carico_elenco"
      val PrintSuccess = "Elenco stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa dell'elenco (nessun documento presente?)."

  object RegisteredDocuments:

    object Details:
      val Title = "Dettaglio documento protocollato"
      val Subtitle = "Visualizza tutte le informazioni del documento selezionato"
      val PrintTitle = "Scheda Documento Protocollato"
      val PrintFileNamePrefix = "documento_protocollato"
      val PrintSuccess = "Scheda del documento stampata correttamente nella cartella protoflow/prints."
      val PrintError = "Non è stato possibile stampare la scheda del documento."

    object Fields:
      val Type = "Tipo"
      val ProtocolledBy = "Protocollato da"
      val RegisteredDate = "Data protocollo"
      val RegisteredTime = "Ora protocollo"
      val RegisteredBy = "Operatore protocollatore"

    object Process:
      val Title = "Protocollazione documento"
      val Subtitle = "Verifica e correggi i dati del documento preso in carico, poi conferma la protocollazione."
      val Success = "Documento protocollato correttamente."
      val Error = "Errore durante la protocollazione del documento."

      val SaveTitle = "Conferma protocollazione"
      val SaveHeader = "Confermi la protocollazione del documento selezionato?"

    object Management:
      val Title = "Gestione documenti protocollati"
      val Subtitle = "Visualizza i documenti protocollati e avviane l'archiviazione."
      val Empty = "Non sono presenti documenti protocollati nel sistema."
      val LoadError = "Errore durante il caricamento dei documenti protocollati."
      val SelectToDelete = "Seleziona un documento da eliminare."
      val SelectToArchive = "Seleziona un documento da archiviare."
      val DeleteTitle = "Eliminazione documento"
      val DeleteConfirmation = "Confermi l'eliminazione del documento selezionato? L'operazione non può essere annullata."
      val DeleteError = "Non è stato possibile eliminare il documento."
      val Deleted = "Il documento è stato eliminato correttamente."
      val ArchiveNotYetAvailable = "La funzionalità di archiviazione non è ancora disponibile."
      val View = "Visualizza"
      val SelectToView = "Seleziona un documento da visualizzare."
      val AllOperators = "Tutti gli operatori"
      val NoFilterResults = "Nessun documento corrisponde ai filtri selezionati."

      val PrintTitle = "Elenco Documenti Protocollati"
      val PrintFileName = "documenti_protocollati_elenco"
      val PrintSuccess = "Elenco stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa dell'elenco (nessun documento presente?)."

  object ArchivedDocuments:

    object Details:
      val Title = "Dettaglio documento archiviato"
      val Subtitle = "Visualizza tutte le informazioni del documento selezionato"
      val PrintTitle = "Scheda Documento Archiviato"
      val PrintFileNamePrefix = "documento_archiviato"
      val PrintSuccess = "Scheda del documento stampata correttamente nella cartella protoflow/prints."
      val PrintError = "Non è stato possibile stampare la scheda del documento."

    object Fields:
      val ProtocolNumber = "Numero protocollo"
      val ArchivedDate = "Data archiviazione"
      val ArchivedTime = "Ora archiviazione"
      val ArchivedBy = "Operatore archiviatore"
      val ArchiveLocation = "Collocazione archivistica"

    object Prompts:
      val ArchivedTime = "Inserisci l'ora di archiviazione"
      val ArchivedDate = "Inserisci la data di archiviazione"
      val ArchivedBy = "Operatore archiviatore"
      val ArchiveLocation = "Inserisci la collocazione archivistica"

    object Errors:
      val ArchivedDateRequired = "La data di archiviazione è obbligatoria"
      val ArchivedTimeRequired = "L'ora di archiviazione è obbligatoria"
      val ArchivedByRequired = "L'operatore archiviatore è obbligatorio"
      val ArchivedDateInvalid = "La data di archiviazione non è valida"
      val ArchivedTimeInvalid = "L'ora di archiviazione non è valida"

    object Process:
      val Title = "Archiviazione documento"
      val Subtitle = "Conferma i dati e completa l'archiviazione del documento protocollato"
      val SaveTitle = "Conferma archiviazione"
      val SaveHeader = "Vuoi archiviare il documento selezionato?"
      val Success = "Documento archiviato correttamente"

    object Management:
      val Title = "Gestione documenti archiviati"
      val Subtitle = "Visualizza e consulta i documenti archiviati."
      val Empty = "Non sono presenti documenti archiviati nel sistema."
      val LoadError = "Errore durante il caricamento dei documenti archiviati."
      val View = "Visualizza"
      val SelectToView = "Seleziona un documento da visualizzare."
      val AllOperators = "Tutti gli operatori"
      val NoFilterResults = "Nessun documento corrisponde ai filtri selezionati."
      val PrintTitle = "Elenco Documenti Archiviati"
      val PrintFileName = "documenti_archiviati_elenco"
      val PrintSuccess = "Elenco stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa dell'elenco."

  object Login:
    val ApplicationSubtitle = "Enterprise Document Protocol System"
    val EmptyCredentials = "Inserisci username e password."
    val InvalidCredentials = "Accesso negato. Username o password non corretti."

    def unknownRole(role: String): String =
      s"Errore applicativo: ruolo '$role' non riconosciuto."

  object Menu:
    val Dashboard = "Dashboard"
    val Profile = "Profilo"
    val Protocols = "Protocollazioni"
    val Statistics = "Statistiche"
    val Log = "Log"
    val ManagementControl = "Controllo Gestione"
    val Registrations = "Registrazioni"
    val UserAccounts = "Account Utenti"
    val Roles = "Ruoli"
    val Classifications = "Classifiche"
    val NewAssignment = "Presa in carico"
    val DocumentsToRegister = "Documenti da protocollare"
    val DocumentsToArchive = "Documenti da archiviare"
    val ArchivedDocuments = "Documenti archiviati"
    val AuthorizationRules = "Autorizzazioni"

    // Label for each MenuAction whose presence in the menu is decided by
    // AuthorizationEngine.permittedActions, rather than hardcoded per view.
    val labels: Map[MenuAction, String] = Map(
      MenuAction.Statistiche -> Statistics,
      MenuAction.Log -> Log,
      MenuAction.ControlloGestione -> ManagementControl,
      MenuAction.Registrazioni -> Registrations,
      MenuAction.AccountUtenti -> UserAccounts,
      MenuAction.Ruoli -> Roles,
      MenuAction.Classifiche -> Classifications,
      MenuAction.NuovaPresaInCarico -> NewAssignment,
      MenuAction.DocumentiDaProtocollare -> DocumentsToRegister,
      MenuAction.DocumentiDaArchiviare -> DocumentsToArchive,
      MenuAction.DocumentiArchiviati -> ArchivedDocuments,
      MenuAction.GestioneAutorizzazioni -> AuthorizationRules,
      MenuAction.VisualizzazioneArchiviazioni -> ArchivedDocuments
    )
    val Logout = "Logout"

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

  object DocumentLogs:

    object Fields:
      val Id = "ID log"
      val DocumentId = "ID documento"
      val OperationType = "Operazione"
      val ProcessedDate = "Data operazione"
      val ProcessedTime = "Ora operazione"
      val ProcessedBy = "Operatore"

    object Operations:
      private val Loading = "loading"
      private val Registering = "registering"
      private val Archiving = "archiving"
      private val LoadingLabel = "Presa in carico"
      private val RegisteringLabel = "Protocollazione"
      private val ArchivingLabel = "Archiviazione"

      val values: Seq[(String, String)] = Seq(
        Loading -> LoadingLabel,
        Registering -> RegisteringLabel,
        Archiving -> ArchivingLabel
      )

      def labelOf(operationType: String): String =
        values
          .find(_._1 == operationType)
          .map(_._2)
          .getOrElse(operationType)

      def valueOf(label: String): Option[String] =
        values
          .find(_._2 == label)
          .map(_._1)

    object Management:
      val Title = "Gestione Log"
      val Subtitle = "Visualizza le operazioni effettuate sui documenti."
      val Empty = "Non sono presenti log relativi ai documenti."
      val AllOperations = "Tutte"
      val AllOperators = "Tutti gli operatori"
      val NoFilterResults = "Nessun log corrisponde ai filtri selezionati."
      val View = "Visualizza"
      val SelectToView = "Seleziona un log da visualizzare."
      val PrintTitle = "Elenco Log Operazioni Documenti"
      val PrintFileName = "log_operazioni_documenti_elenco"
      val PrintSuccess = "Elenco dei log stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa dell'elenco dei log."

    object Details:
      val Title = "Dettaglio log documento"
      val Subtitle = "Visualizza i dati del log selezionato."
      val PrintTitle = "Dettaglio Log Operazione Documento"
      val PrintSuccess = "Dettaglio del log stampato correttamente in PDF."
      val PrintError = "Errore durante la stampa del dettaglio del log."

  object Statistics:
    val Title = "Statistiche"
    val Subtitle = "Visualizza gli indicatori relativi all'utilizzo del sistema."

    val RegisteredByMonthTitle = "Documenti protocollati per mese"
    val ArchivedByMonthTitle = "Documenti archiviati per mese"
    val AccessesByRoleTitle = "Accessi per ruolo"
    val AccessesByUserTitle = "Accessi per utente"

    val RegistrationsTotal = "Richieste elaborate"
    val RegistrationsApproved = "Approvate"
    val RegistrationsRejected = "Rifiutate"

    val UserColumn = "Utente"
    val AccessCountColumn = "Numero accessi"

    val PrintTitle = "Statistiche Utilizzo Sistema"
    val PrintFileName = "statistiche_utilizzo_sistema"
    val PrintSuccess = "Statistiche stampate correttamente nella cartella protoflow/prints."
    val PrintError = "Non è stato possibile stampare le statistiche."

  object DocumentManagementControl:
    val Title = "Controllo di Gestione Documenti"
    val Subtitle = "Visualizza lo stato di lavorazione di tutti i documenti nel sistema."
    val Empty = "Non sono presenti documenti nel sistema."
    val LoadError = "Errore durante il caricamento dei documenti."
    val IdColumn = "Id/Protocollo"
    val ClassificationColumn = "Classifica"
    val RegisteredDateColumn = "Data di protocollazione"
    val OperatorColumn = "Operatore incaricato"
    val StageColumn = "Stato di lavorazione"
    val NotAvailable = "-"
    val ViewDetails = "Visualizza Dettaglio"
    val SelectToView = "Seleziona un documento da visualizzare."
    val DetailsTitle = "Dettaglio documento in gestione"
    val DetailsSubtitle = "Visualizza tutte le informazioni del documento selezionato."
    val Summary = "Riepilogo"
    val SelectToSummary = "Seleziona un documento per generare il riepilogo."
    val SummaryTitle = "Riepilogo Gestione Documento"
    val SummarySubtitle = "Visualizza le fasi di gestione del documento selezionato."
    val DocumentDataSection = "Dati identificativi del documento"
    val PhasesSection = "Fasi di gestione"
    val DocumentCodeField = "Id/Codice protocollo"
    val SummaryClassificationField = "Classifica"
    val PhaseColumn = "Fase"
    val DateTimeColumn = "Data e ora"
    val SummaryOperatorColumn = "Operatore"
    val OutcomeColumn = "Esito"
    val SummaryEmpty = "Non sono presenti fasi di gestione per il documento selezionato."
    val SummaryPrintFileName = "riepilogo_gestione_documento"
    val SummaryPrintSuccess = "Riepilogo stampato correttamente nella cartella protoflow/prints."
    val SummaryPrintError = "Non è stato possibile stampare il riepilogo."
    val SummaryGeneratedAt = "Data e ora di generazione"
    val SummaryGeneratedBy = "Utente"
    val SummaryPage = "Pagina"

    object Fields:
      val LoadedDate = "Data presa in carico"
      val LoadedTime = "Ora presa in carico"

    val PrintTitle = "Elenco Documenti in Gestione"
    val PrintFileName = "documenti_in_gestione_elenco"
    val PrintSuccess = "Elenco stampato correttamente nella cartella protoflow/prints."
    val PrintError = "Non è stato possibile stampare l'elenco."

    object Stages:
      private val Loading = "loading"
      private val Registering = "registering"
      private val Archiving = "archiving"
      private val LoadingLabel = "Presa in carico"
      private val RegisteringLabel = "Protocollato"
      private val ArchivingLabel = "Archiviato"

      private val values: Seq[(String, String)] = Seq(
        Loading -> LoadingLabel,
        Registering -> RegisteringLabel,
        Archiving -> ArchivingLabel
      )

      def labelOf(stage: String): String =
        values
          .find(_._1 == stage)
          .map(_._2)
          .getOrElse(stage)

  object Validation:
    object Account:
      val SurnameRequired = "Il campo Cognome è obbligatorio."
      val NameRequired = "Il campo Nome è obbligatorio."
      val EmailRequired = "Il campo Email è obbligatorio."
      val EmailInvalid = "Inserisci un indirizzo email valido."
      val RoleRequired = "Il campo Ruolo è obbligatorio."
      val UsernameRequired = "Il campo Username è obbligatorio."
      val PasswordRequired = "Il campo Password è obbligatorio."
      val DuplicateUsername = "Esiste già un account con questo username."

    object Classification:
      val ClassificationRequired = "Il campo Classifica è obbligatorio."
      val DescriptionRequired = "Il campo Descrizione è obbligatorio."
      val DuplicateClassification = "Esiste già una classifica con questo nome."

    object Role:
      val RoleRequired = "Il campo Ruolo è obbligatorio."
      val NameRequired = "Il campo Nome ruolo è obbligatorio."
      val DescriptionRequired = "Il campo Descrizione è obbligatorio."
      val DuplicateRole = "Esiste già un ruolo con questo nome."

    object LoadedDocument:
      val DocumentDateRequired = "Il campo Data documento è obbligatorio."
      val DocumentTimeRequired = "Il campo Ora documento è obbligatorio."
      val DocumentProtocolRequired = "Il campo Protocollo mittente è obbligatorio."
      val DocumentTypeRequired = "Il campo Tipo documento è obbligatorio."
      val SenderRequired = "Il campo Mittente è obbligatorio."
      val RecipientRequired = "Il campo Destinatario è obbligatorio."
      val SubjectRequired = "Il campo Oggetto è obbligatorio."

    object Registration:
      val Name = "Nome"
      val Surname = "Cognome"
      val RequestedRole = "Ruolo richiesto"
      val Area = "Area/Settore di appartenenza"
      val Assignment = "Incarico"
      val EmailRequired = "Il campo 'Indirizzo email' è obbligatorio."
      val EmailInvalid = "L'indirizzo email non ha un formato valido."
      val PhoneInvalid = "Il numero di telefono deve contenere solo numeri"

      def required(fieldName: String): String =
        s"Il campo '$fieldName' è obbligatorio."