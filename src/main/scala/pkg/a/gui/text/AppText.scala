package pkg.a.gui.text

import pkg.a.gui.structures.MenuAction

object AppText:

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
    val NewAssignment = "Presa in carico"
    val DocumentsToRegister = "Documenti da protocollare"
    val DocumentsToArchive = "Documenti da archiviare"
    val ArchivedDocuments = "Documenti archiviati"
    val AuthorizationRules = "Autorizzazioni"
    
    val labels: Map[MenuAction, String] = Map(
      MenuAction.Profilo -> Profile,
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

  object HomePages:
    val AdminTitle = "Homepage Amministratore"
    val OperatorTitle = "Homepage Operatore"
    val ViewerTitle = "Homepage Viewer"

  object Dashboards:

    object Admin:
      val TotalDocumentsTitle = "Documenti totali"
      val TotalDocumentsSubtitle = "Documenti nel sistema"
      val PendingRequestsTitle = "Richieste pendenti"
      val PendingRequestsSubtitle = "Richieste da elaborare"
      val RegisteredAccountsTitle = "Account registrati"
      val RegisteredAccountsSubtitle = "Utenti presenti"
      val ArchivedDocumentsTitle = "Documenti archiviati"
      val ArchivedDocumentsSubtitle = "Documenti completati"

    object Operator:
      val ToRegisterTitle = "Da protocollare"
      val ToRegisterSubtitle = "Documenti in attesa"
      val ToArchiveTitle = "Da archiviare"
      val ToArchiveSubtitle = "Documenti protocollati"
      val ArchivedTitle = "Archiviati"
      val ArchivedSubtitle = "Documenti completati"
      val TodayOperationsTitle = "Operazioni oggi"
      val TodayOperationsSubtitle = "Le tue attività"

    object Viewer:
      val AvailableDocumentsTitle = "Documenti disponibili"
      val AvailableDocumentsSubtitle = "Documenti della tua area"
      val ArchivedTodayTitle = "Archiviati oggi"
      val ArchivedTodaySubtitle = "Nuovi documenti"
      val ArchivedThisMonthTitle = "Archiviati questo mese"
      val ArchivedThisMonthSubtitle = "Documenti del mese"
      val AreaTitle = "Area"
      val AreaSubtitle = "Area di appartenenza"

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
      val LastAdminRoleChange = "Non è possibile modificare il ruolo dell'ultimo amministratore."

    object Classification:
      val ClassificationRequired = "Il campo Classifica è obbligatorio."
      val DescriptionRequired = "Il campo Descrizione è obbligatorio."
      val DuplicateClassification = "Esiste già una classifica con questo nome."

    object Role:
      val RoleRequired = "Il campo Ruolo è obbligatorio."
      val NameRequired = "Il campo Nome ruolo è obbligatorio."
      val DescriptionRequired = "Il campo Descrizione è obbligatorio."
      val DuplicateRole = "Esiste già questo ruolo."
      val DuplicateRoleName = "Esiste già un ruolo con questo nome."

    object LoadedDocument:
      val DocumentDateRequired = "Il campo Data documento è obbligatorio."
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
