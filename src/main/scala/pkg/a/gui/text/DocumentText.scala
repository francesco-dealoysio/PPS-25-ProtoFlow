package pkg.a.gui.text

object DocumentText:

  object LoadedDocuments:
    object DocumentTypes:
      val All = Seq("Plico", "Email", "Lettera")

    object Fields:
      val DocumentDate = "Data documento"
      val ProcessedBy = "Preso in carico da"
      val DocumentProtocol = "Protocollo mittente"
      val DocumentType = "Tipo documento"
      val Remarks = "Note"

    object Prompts:
      val DocumentProtocol = "Inserisci il protocollo del mittente"
      val DocumentType = "Seleziona il tipo di documento"
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
      val RegisteredDate = "Data protocollo"
      val RegisteredTime = "Ora protocollo"
      val RegisteredBy = "Operatore protocollatore"

    object Process:
      private val Success = "Documento protocollato correttamente."
      val Title = "Protocollazione documento"
      val Subtitle = "Visualizza i dati del documento, seleziona la classifica e conferma la protocollazione."
      val Error = "Errore durante la protocollazione del documento."
      val ClassificationRequired = "Seleziona una classifica."
      val SaveTitle = "Conferma protocollazione"
      val SaveHeader = "Confermi la protocollazione del documento selezionato?"

      def success(protocolNumber: String): String =
        s"$Success Numero di protocollo: $protocolNumber."

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
      val ArchiveLocation = "Inserisci la collocazione archivistica"

    object Errors:
      val ArchivedDateRequired = "La data di archiviazione è obbligatoria"
      val ArchivedTimeRequired = "L'ora di archiviazione è obbligatoria"
      val ArchivedByRequired = "L'operatore archiviatore è obbligatorio"
      val ArchivedDateInvalid = "La data di archiviazione non è valida"
      val ArchivedTimeInvalid = "L'ora di archiviazione non è valida"
      val ArchiveLocationRequired = "La collocazione archivistica è obbligatoria"

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

  object DocumentLogs:

    object Fields:
      val Id = "ID log"
      val DocumentId = "ID documento"
      val OperationType = "Operazione"
      val ProcessedDate = "Data operazione"
      val ProcessedTime = "Ora operazione"
      val ProcessedBy = "Operatore"

    object Prompts:
      val FromDate = "Data inizio"
      val ToDate = "Data fine"

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
      val AllOperations = "Tutte le operazioni"
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

  object DocumentManagementControl:
    val Title = "Controllo di Gestione Documenti"
    val Subtitle = "Visualizza lo stato di lavorazione di tutti i documenti nel sistema."
    val Empty = "Non sono presenti documenti nel sistema."
    val LoadError = "Errore durante il caricamento dei documenti."
    val IdColumn = "Protocollo"
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
