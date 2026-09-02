# 5. Implementazione

Questa sezione raccoglie, per ciascun membro del gruppo, una descrizione delle parti di sistema di cui si è occupato in prima persona: le scelte implementative adottate, le motivazioni dietro tali scelte e le eventuali difficoltà incontrate durante lo sviluppo.

A differenza della sezione 4 (Design di dettaglio), che descrive l'organizzazione interna dei sottosistemi in modo tecnico e indipendente dall'autore, questa sezione ha un taglio più personale: ogni sottosezione è scritta dal componente del gruppo che ha realizzato quella parte.

## 5.1 Motore di autorizzazione Prolog, controllo di gestione e moduli di supporto — Thomas Testa

### 5.1.1 Motore di autorizzazione Prolog

Il requisito obbligatorio "utilizzo di regole logiche per la verifica delle autorizzazioni" e il relativo opzionale "personalizzazione delle regole organizzative tramite Prolog" sono le parti di cui mi sono occupato più a lungo e con la maggiore autonomia progettuale, in un branch dedicato (`feature/autorizzazione-prolog`) integrato successivamente in `develop`.

L'implementazione è divisa in due livelli:

- **`PrologEngine`** (`pkg.d.util`), l'unico file del progetto che importa `alice.tuprolog.*`. Incapsula il motore tuProlog dietro un'unica funzione `Term => LazyList[Term]`, seguendo il pattern Scala2P visto a lezione. Rispetto alla versione delle slide, l'iteratore che consuma le soluzioni controlla esplicitamente `hasOpenAlternatives` prima di richiamare `solveNext()`: senza questo controllo, `solveNext()` lancia un'eccezione (`NoMoreSolutionException`) invece di segnalare in modo pulito l'esaurimento delle soluzioni — un comportamento che ho scoperto testando goal con un numero finito di soluzioni, il caso più comune nell'uso reale.
- **`AuthorizationEngine`** (`pkg.b.logic`), che espone all'applicazione un'API Scala pura (`isAuthorized`, `canDeleteRole`, `canDeleteAccount`, `permittedActions`) senza mai far trapelare i tipi di tuProlog. Tutte le regole di autorizzazione sono definite in `authorization.pl` (fatti `can/2`, predicati `authorized/2`, `permitted_actions/2` con `findall`, `can_delete_role/1`, `can_delete_account/2`), caricata come risorsa.

Ho scelto di centralizzare la verifica in un unico varco applicativo, `HomePage.navigate`: ogni azione richiesta dal menu passa da lì e viene verificata con `AuthorizationEngine.isAuthorized` prima di essere eseguita, indipendentemente dalla view che l'ha originata. Le stesse regole Prolog vengono interrogate anche da `Role.recordDelete` e `Account.recordDelete` (per gli invarianti "non si elimina il ruolo admin" e "non si elimina l'ultimo amministratore"), così la policy resta definita in un unico punto anche per operazioni che non passano dal menu.

Per l'opzionale, ho aggiunto la possibilità per un amministratore di estendere le regole a runtime dalla GUI (`AuthorizationRuleAddView`, `AuthorizationRulesManagementView`): le regole aggiunte vengono asserite nel motore live (`assert`/`retract`) e persistite in un file separato, `customRules.pl`, mai unito alla teoria di base spedita con l'applicazione. Questo evita due problemi: che un aggiornamento dell'app cancelli le personalizzazioni di un cliente, e che una regola custom duplichi silenziosamente un permesso già concesso dalla teoria base — quest'ultimo un bug che ho effettivamente introdotto e corretto durante lo sviluppo, controllando `isAuthorized` (base + custom) invece della sola collezione delle regole custom prima di asserire.

La difficoltà maggiore non è stata tecnica ma di valutazione: dopo aver completato questa parte mi sono chiesto onestamente se Prolog avesse semplificato il codice rispetto all'equivalente in Scala. La risposta, discussa più nel dettaglio in sezione 7, è che lo ha fatto in modo netto solo per `permitted_actions/2` (il `findall` elimina la necessità di una struttura dati parallela per il menu dinamico); per un controllo puntuale come `authorized/2` l'equivalente Scala (una `Map[String, Set[MenuAction]]`) sarebbe stato altrettanto leggibile e più diretto. Il vantaggio reale, per come l'ho strutturato, è avere un unico posto dichiarativo in cui tutte le regole di autorizzazione sono leggibili e modificabili senza toccare il codice Scala.

### 5.1.2 Controllo di gestione documenti

Il modulo "Controllo di Gestione" (`DocumentManagementControlView`, `DocumentManagementControlService`, `DocumentManagementDetailsView`, `DocumentManagementSummaryView`) offre una vista unificata sullo stato di lavorazione di un documento, indipendentemente dallo stadio del ciclo di vita in cui si trova (preso in carico, protocollato o archiviato).

La parte più delicata è stata l'aggregazione: `LoadedDocument`, `RegisteredDocument` e `ArchivedDocument` sono tre entità indipendenti, ciascuna persistita nel proprio file XML, non collegate da ereditarietà. Ho evitato un vero merge/join tra le tre collezioni sfruttando un invariante già garantito dal resto del sistema: quando un documento avanza di stadio, il record dello stadio precedente viene eliminato (lo fanno `LoadedDocumentService`/`ArchivedDocumentService`), quindi un dato id esiste sempre in un solo file alla volta. `DocumentManagementControlService.getManagedDocuments` si limita quindi a mappare ciascuna collezione in un view model comune (`ManagedDocument`) e a concatenare i risultati, ordinandoli per id — molto più semplice di un join, ma corretto solo perché quell'invariante è rispettata a monte.

`DocumentManagementSummaryView` ricostruisce invece lo storico cronologico delle fasi di un singolo documento leggendo `DocumentLog` e ordinandolo con una chiave composita (fase, poi id del log), per gestire correttamente più operazioni dello stesso tipo nello stesso giorno.

### 5.1.3 Statistiche di utilizzo del sistema

`StatisticsService` aggrega i dati di utilizzo (documenti protocollati/archiviati per mese, accessi per ruolo e per utente, esito delle richieste di registrazione) leggendo direttamente le entità di dominio, senza un database relazionale con `GROUP BY` a disposizione. L'ho tenuto deliberatamente privo di dipendenze da ScalaFX, cosa che ha permesso di testarlo con JUnit senza dover avviare il toolkit grafico — l'unico dei moduli che ho scritto per cui esiste una suite di test dedicata al livello di servizio.

Un dettaglio a cui ho dedicato attenzione è la gestione delle date non valide: `yearMonthOf` prova a interpretare la data di un documento e, se fallisce, scarta silenziosamente quel record invece di far fallire l'intera pagina statistiche — una scelta di robustezza rispetto a dati "sporchi", coerente col fatto che la persistenza su XML non impone alcun controllo di formato in scrittura.

### 5.1.4 Gestione delle richieste di registrazione e protocollazione

Su `RegistrationRequestService` ho implementato il flusso di approvazione di una richiesta di registrazione: generazione di username univoco e password temporanea, creazione dell'account con password cifrata (SHA3-512), e solo in caso di successo l'aggiornamento dello stato della richiesta. Non essendoci transazioni atomiche tra due file XML indipendenti, in caso di fallimento nell'aggiornamento della richiesta il servizio esegue un rollback esplicito cancellando l'account appena creato, per evitare account "orfani" non riconducibili a nessuna richiesta approvata.

Ho inoltre curato la protocollazione dei documenti (`RegisteredDocument`, `RegisteredDocumentDetailsView`) e, in una fase successiva di consolidamento del progetto, una serie di correzioni trasversali individuate testando manualmente l'applicazione: la perdita del campo note durante l'archiviazione (un campo semplicemente dimenticato nella costruzione del documento archiviato), l'assenza di uno scroll nelle schermate di form/dettaglio più lunghe (che tagliava fuori dalla vista i pulsanti di azione), e alcune duplicazioni di regole di business già presenti nel motore di autorizzazione ma reimplementate localmente in due view di gestione.

## 5.2 Sezione descrittiva studente 2

*[da completare]*

## 5.3 Sezione descrittiva studente 3

*[da completare]*
