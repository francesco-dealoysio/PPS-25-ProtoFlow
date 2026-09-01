# 2. Requirement specification

## 2.1 Requisiti di business

<a id="context-anchor"></a>
### Contesto

Il progetto nasce dal Project Overview Statement "Gestione Protocollo UNUCI", che inquadra un'organizzazione (l'Unione Nazionale Ufficiali in Congedo d'Italia) priva di uno strumento informatico per la protocollazione della corrispondenza. 
La gestione attuale è interamente manuale: la corrispondenza che transita per la Segreteria viene catalogata secondo una classificazione per settore, protocollata su registro cartaceo, poi spedita/smistata o archiviata fisicamente. 
Parte della corrispondenza, inoltre, bypassa la Segreteria arrivando o partendo direttamente dalle singole unità organizzative, sfuggendo così a qualunque tracciamento.

### Problemi individuati

Dalla situazione descritta derivano sei problemi espliciti: mancanza di controllo centralizzato sulla posta, ridotta condivisione delle informazioni tra chi ne avrebbe necessità, protocollazione omessa per parte della corrispondenza, archiviazione solo parziale, difficoltà di ricerca e recupero delle informazioni, lentezza delle operazioni di protocollazione.

### Obiettivo e opportunità

L'obiettivo dichiarato è disporre di *"un sistema applicativo efficiente ed user-friendly che informatizzi e potenzi le attività normalmente svolte dagli operatori per gestire la protocollazione della corrispondenza"*, con attenzione all'interoperabilità con altri sistemi. Da questo derivano gli obiettivi operativi: un'applicazione i cui operatori accedano a funzionalità differenziate in base al proprio profilo; gestione centralizzata e storicizzata delle informazioni con tracciamento delle transazioni; meccanismi di ricerca, estrazione e reportistica sui dati.

Il documento di partenza fissa anche criteri di successo quantitativi (es. velocizzare del 50% l'acquisizione e gestione delle informazioni, rendere completamente tracciabili le comunicazioni). Si tratta di target dichiarati nella fase di analisi iniziale: il progetto, per la sua natura di esercitazione universitaria con un singolo gruppo utente di prova, non prevede una misurazione empirica di questi indicatori — non c'è una base storica ("come si lavorava prima") con cui confrontare i tempi del nuovo sistema. Vengono riportati per completezza rispetto al documento di analisi, non come requisiti verificati.

### Vincoli e rischi individuati in fase di analisi

- l'impiego di tecnologie avanzate comporta un impegno realizzativo significativo, e richiede competenze di un profilo tecnico elevato per un gruppo di soli tre sviluppatori;
- la disponibilità del personale operativo (utenti finali) a collaborare con il team di sviluppo non è garantita — rischio non direttamente verificabile nell'ambito di un progetto d'esame, dove gli "utenti finali" reali non esistono;
- l'adozione del sistema comporterebbe, in un contesto reale, una modifica dei processi operativi correnti.

## 2.2 Modello di dominio

Il dominio del sistema ruota attorno al **ciclo di vita di un documento**, dalla presa in carico all'archiviazione, e agli **attori** che lo attraversano. Il modello implementato riflette direttamente questa struttura: dieci entità, tutte persistite come record XML tramite un contratto comune (`Entity`: `getRecords`, `getRecordsByFilter`, `getRecordById`, `recordInsert`, `recordUpdate`, `recordDelete`).

**Entità anagrafiche**
- `Account` — un utente del sistema (dati anagrafici, ruolo, area, username, password cifrata);
- `Role` — un ruolo applicativo (codice, nome, descrizione);
- `Classification` — una classificazione/area organizzativa usata per catalogare i documenti.

**Processo di accreditamento**
- `Registration` — una richiesta di registrazione al sistema, con uno stato (`Pending`/`Approved`/`Rejected`) e i dati dell'esito (chi l'ha elaborata, quando, con quale motivazione in caso di rifiuto).

**Ciclo di vita del documento** — tre entità indipendenti, non legate da ereditarietà di classe, che rappresentano tre stadi successivi dello stesso documento: ogni stadio replica i campi dello stadio precedente e ne aggiunge di propri.
- `LoadedDocument` — il documento appena preso in carico (dati del documento, mittente/destinatario/oggetto, data e operatore che l'ha caricato);
- `RegisteredDocument` — lo stesso documento dopo la protocollazione (in più: numero di protocollo, data/ora/operatore della registrazione, classificazione assegnata);
- `ArchivedDocument` — lo stesso documento dopo l'archiviazione (in più: data/ora/operatore dell'archiviazione, collocazione archivistica).

**Log e tracciamento**
- `DocumentLog` — traccia ogni operazione compiuta su un documento (presa in carico, protocollazione, archiviazione), con tipo di operazione, data/ora e operatore;
- `AccessLog` — traccia gli accessi al sistema (utente, ruolo, data/ora);
- `ErrorLog` — traccia le eccezioni applicative, per diagnostica.

Uno schema completo delle entità e delle loro relazioni, insieme all'architettura degli altri livelli del sistema (interfaccia, servizi, motore di autorizzazione), è disponibile come materiale di supporto separato alla sezione 4 (Design di dettaglio).

## 2.3 Requisiti funzionali

### 2.3.1 Requisiti utente

I requisiti funzionali sono organizzati per attore, secondo la Requirement Breakdown Structure (RBS) prodotta in fase di analisi. Il sistema prevede tre ruoli:

| Ruolo | Descrizione | Attività principali |
|---|---|---|
| **Admin** | Gestore dell'applicazione | Gestione di utenti, ruoli e classifiche (CRUD); elaborazione delle richieste di registrazione; estrazione e analisi dei log; tutte le attività previste per il ruolo Oper |
| **Oper** | Operatore addetto alla protocollazione | Presa in carico, protocollazione e archiviazione dei documenti; tutte le attività previste per il ruolo Viewer, con visibilità globale sui documenti |
| **Viewer** | Utente in sola consultazione | Ricerca e consultazione dei documenti archiviati, con visibilità limitata alla propria area di appartenenza; stampa dei risultati |

*Nota sulla nomenclatura*: i documenti di analisi prodotti dal gruppo non sono coerenti tra loro sul nome del terzo ruolo — la RBS e il documento "Ruoli" lo chiamano **Viewer**, mentre il documento di specifica funzionale e il Product Backlog usano in alcuni punti **Reader**. Nell'implementazione il codice ruolo usato è `viewer`; questa relazione adotta quindi **Viewer** come nome definitivo, segnalando l'incoerenza nei documenti di partenza come un piccolo limite del processo di analisi (ripreso in sezione 7).

I 47 requisiti funzionali individuati in fase di analisi si raggruppano nelle seguenti aree:

- **Accesso e accreditamento** — portale, login, richiesta di registrazione (requisiti 1–3);
- **Homepage per ruolo** — una vista iniziale differenziata per Admin, Oper e Viewer (requisiti 4, 22, 26);
- **Gestione richieste di registrazione** *(Admin)* — elenco, elaborazione, approvazione/rifiuto (requisiti 7–8);
- **Gestione utenti** *(Admin)* — CRUD account, con relative stampe (requisiti 9–12, 40–41);
- **Gestione tabelle ausiliarie** *(Admin)* — CRUD ruoli e classifiche, con relative stampe (requisiti 14–21, 42–43);
- **Ciclo di vita del documento** *(Oper)* — presa in carico, protocollazione, archiviazione (requisiti 23–25);
- **Log delle operazioni** — visualizzazione elenco e scheda, log per singolo stadio del documento (requisiti 5–6, 27–29);
- **Ricerca** — per protocollo, data, intervallo temporale, classificazione, e loro combinazione, sia sui documenti sia sui log (requisiti 30–37);
- **Report e stampe** — elenco/scheda per documenti protocollati, log, utenti, ruoli, classifiche (requisiti 38–39, 45–46);
- **Controllo di gestione e sintesi** *(Admin)* — vista aggregata sui documenti nei tre stadi, riepilogo di un singolo documento, statistiche di utilizzo (requisiti 13, 44, 47).

Ogni requisito è ulteriormente dettagliato in una User Story dedicata, prodotta durante lo sprint di competenza e non riportata per esteso in questa sezione per brevità.

### 2.3.2 Requisiti di sistema

Oltre alle funzionalità direttamente azionate da un utente, il sistema deve garantire un insieme di comportamenti trasversali, non legati a una singola schermata:

- **verifica delle autorizzazioni tramite regole logiche**: ogni azione richiesta da un utente viene verificata contro un insieme di regole scritte in Prolog, non tramite condizionali sparsi nel codice applicativo — requisito obbligatorio della proposta di progetto, discusso in dettaglio nelle sezioni 3, 4 e 6;
- **persistenza e storicizzazione**: ogni entità del dominio viene salvata su file XML e non viene mai sovrascritta silenziosamente — le transizioni di stato (es. approvazione di una richiesta, avanzamento di un documento tra stadi) aggiornano il record esistente, mentre le operazioni sui documenti generano automaticamente una voce di log, indipendentemente dall'azione specifica richiesta dall'utente;
- **generazione di identificativi univoci**: ogni nuovo record riceve un identificativo generato dal sistema, non inseribile manualmente;
- **cifratura delle credenziali**: le password degli account non sono mai salvate in chiaro;
- **validazione dei dati in ingresso**: ogni form di inserimento/modifica valida i campi obbligatori e i vincoli di unicità (es. username, codice ruolo) prima di consentire il salvataggio.

## 2.4 Requisiti non funzionali

Dalla RBS, i requisiti non funzionali individuati in fase di analisi si raggruppano in quattro categorie:

- **Sicurezza** — autenticazione e profilazione dell'utente (l'accesso al sistema richiede credenziali valide, e ogni utente vede solo le funzionalità del proprio ruolo), un "cono di visibilità" limitato ai dati di propria competenza (es. il Viewer vede solo i documenti della propria area), cifratura delle credenziali, tracciamento degli accessi tramite log;
- **Scalabilità**;
- **Interoperabilità**;
- **Affidabilità**.

Le ultime tre categorie sono dichiarate nel documento di analisi originale ma non sono state tradotte in criteri di accettazione misurabili né verificate nel corso del progetto — è onesto segnalarlo come limite, coerente con la natura di esercitazione didattica del progetto piuttosto che di sistema in produzione. Il requisito di **Sicurezza**, al contrario, è verificabile direttamente nel codice: è il requisito non funzionale meglio coperto dal progetto, sia a livello di autenticazione/autorizzazione (si veda 2.3.2) sia di cifratura delle credenziali.

## 2.5 Requisiti di implementazione

Dai vincoli tecnologici individuati in fase di analisi (RBS, ramo Constraint) e dagli strumenti indicati nella proposta di progetto:

- **Linguaggio**: Scala 3, con build gestita tramite SBT;
- **Interfaccia grafica**: ScalaFX;
- **Persistenza**: file XML, uno per entità/collezione di record. È una scelta più semplice di quanto suggerito dagli obiettivi del POS originale, che parlava genericamente di una "base dati relazionale" — per un progetto di questa scala, e per restare aderente agli strumenti indicati nella proposta d'esame, il gruppo ha optato per la persistenza su file, senza un DBMS relazionale. È uno scostamento dal documento di analisi iniziale, non un'omissione: viene ripreso onestamente in sezione 7;
- **Programmazione logica**: Prolog (tuProlog 3.3.0), per le regole di autorizzazione — si veda la sezione 4;
- **Testing**: JUnit/ScalaTest, con suite eseguita automaticamente ad ogni push tramite GitHub Actions;
- **Versionamento e collaborazione**: Git/GitHub, con branch per feature e merge verso `develop`;
- **Ambiente di sviluppo**: IntelliJ IDEA;
- **Documentazione**: Markdown per la relazione, versionata nello stesso repository del codice;
- **Vincolo temporale**: consegna entro la scadenza d'esame, che ha condizionato le scelte di scope discusse nelle sezioni successive (in particolare gli opzionali non completati, si veda la Retrospettiva).
