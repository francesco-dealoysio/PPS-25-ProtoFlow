[Back to index](0-Indice.md) |
[Previous Chapter](4-Design_di_dettaglio.md) |
[Next Chapter](6-Testing.md)
# 5. Implementazione

Questa sezione raccoglie, per ciascun membro del gruppo, una descrizione delle parti di sistema di cui si è occupato in prima persona: le scelte implementative adottate, le motivazioni dietro tali scelte e le eventuali difficoltà incontrate durante lo sviluppo.

A differenza della sezione 4 (Design di dettaglio), che descrive l'organizzazione interna dei sottosistemi in modo tecnico e indipendente dall'autore, questa sezione ha un taglio più personale: ogni sottosezione è scritta dal componente del gruppo che ha realizzato quella parte.

## 5.1 Sezione descrittiva studente DE ALOYSIO Francesco
<p style="text-align: justify;">
Nell’ambito della architettura di alto livello scelta, articolata nei seguenti livelli:
</p>

- front end (pkg.a.gui): presentazione ed interazione degli utenti
- logica applicativa (pkg.b.logic): interfaccia tra front end e back end
- back end (pck.d.data): gestione dei dati persistenti

<p style="text-align: justify;">
i moduli applicativi da me sviluppati in autonomia afferiscono a tutte e tre le suddette aree, ma la maggiorparte del software si
colloca nel livello di logica applicativa ed in quello dei dati, oltre ad alcune utility contenute nel package pkg.d.util.
</p>

<p style="text-align: justify;">
Inoltre, nel package pkg.e.ui, a scopo meramente didattico, ho sviluppato una versione alternativa delle gui, non utilizzata
nell’ambito dell’applicativo, basata su una gerarchia di traits (diagramma delle classi allegato).

Nel dettaglio di seguito il riepilogo delle attività effettuate:
</p>

- Struttura a directory ‘protoflow’ deputata alla memorizzazione dei file prodotti ed utilizzati dall’applicativo (database, ids, log, ecc…);
- Meccanismo di gestione della configurazione tramite il file protoflow.properties;
- Configurazione del meccanismo di utilizzo delle risorse (src.main.resources e src.test.resources);
- Sviluppo dei moduli applicativi;
- Realizzazione delle classi di Test dei moduli.

Elenco dei moduli sviluppati:

<table>
  <tr style="font-weight: normal; font-size: 20px;"><th colspan="3">Moduli sviluppati in src.main</th></tr>
  <tr style="text-align: center;"><td>Package</td><td>Modulo</td><td>Descrizione</td></tr>
  <tr><td rowspan="10">pck.b.logic</td><td>Entity.scala</td><td>Trait per la modellazione delle entità del dominio, le sue 
    funzionalità vengono utilizzate, per il tramite delle entità concrete, dai moduli del front end, prevalentemente
    dalle gui.</td></tr>
  <tr><td>Account.scala</td><td>Entità di modellazione degli account degli utenti del sistema.</td></tr>
  <tr><td>Role.scala</td><td>Entità di modellazione dei ruoli associati agli utenti: Amministratore, Operatore o 
    Utente in sola consultazione.</td></tr>
  <tr><td>Classification.scala</td><td>Entità di modellazione delle aree/settori dell'organizzazione, utilizzate per 
    descrivere l'area di assegnazione di un utente, per generare i protocolli e per definire le posizioni di archivio.</td></tr>
  <tr><td>Registration.scala</td><td>Entità di modellazione delle richieste di registazione per l'accreditamento al sistema
    presentate attraverso la pagina di login dagli utenti non accreditati al sistema.</td></tr>
  <tr><td>DocumentLog.scala</td><td>Entità di modellazione dei log relativi alle operazioni effettuate sulla corrispondenza: 
    prese in carico, protocollazioni ed archiviazioni.</td></tr>
  <tr><td>ErrorLog.scala</td><td>Entità di modellazione dei log relativi agli errori intercettati dall'applicazione.</td></tr>
  <tr><td>LoadedDocument.scala</td><td>Entità di modellazione dei documenti presi in carico dall'operatore.</td></tr>
  <tr><td>Init.scala</td><td>Funzione chiamata all'avvio dell'applicazione che, se non già esistenti, crea ed inizializza 
    nel filesystem la struttura ed i file necessari per il funzionamento del programma, ovvero file di configurazione,
    directory, file xml, e file preposti a contenere gli id delle diverse entità.</td></tr>
  <tr><td>StartData.scala</td><td>Contiene le strutture di tipo Elem utilizzate dalla funzione init per inizializzare i
    file accounts.xml, roles.xml e classifications.xml con le infomnazioni minimali necessarie al funzionamento dell' 
    applicazione.</td></tr>
  <tr><td rowspan="5">pck.b.logic.pdf</td><td>PdfCreator.scala</td><td>Genera, siulla base dell'input, un file pdf
    corrispondente alla scheda relativa al record di una entità (es. Scheda Account).</td></tr>
  <tr><td>PdfVerifier.scala</td><td>Preposto alla verifica il formato di un presunto file pdf, viene utilizzato dai
    moduli che manipolano i file pdf.</td></tr>
  <tr><td>PdfDefaultViewer.scala</td><td>Cerca ed apre il visualizzatore di default presente sul sistema operativo
    ospite per visualizzare un file pdf. Può essere utilizzato dalla gui di PdfViewer su comando dell'utente.</td></tr>
  <tr><td>PdfPrinter.scala</td><td>Permette di selezionare una delle stampanti (print services) presenti e disponibili
    sul sistema operativo ospite e di avviare la stampa di un file pdf. Può essere utilizzato dalla gui di PdfViewer su comando 
    dell'utente.</td></tr>
  <tr><td>PdfViewer.scala</td><td>Consente di selezionare e visualizzare un file in formato pdf.</td></tr>
  <tr><td rowspan="3">pck.c.data</td><td>FileSystem.scala</td><td>Contiene le funzionalità necessarie alla creazione
    dei file e delle directory nel filesystem.</td></tr>
  <tr><td>Properties.scala</td><td>Contiene le funzionalità per la gestione di un file di proprietà, in particolare
    viene utilizzato per creare e manipolare il file protoflow.properties contenente la configurazione del sistema.</td></tr>
  <tr><td>Xml.scala</td><td>Contiene tutte le funzionalità necessarie a gestire dei file xml utilizzati per la
    memorizzazione persistente dei dati in sostituzione di un database. In particolare il modulo le funzioni
    per la creazione dei file xml, per inserimento, modifica e rimozione dei record dai file e per le interrogazioni.
    Le sue funzioni vengono utilizzate dai moduli del livello di logica applicativa, ovvero dalle entità.</td></tr>
  <tr><td rowspan="4">pck.d.util</td><td>Logger.scala</td><td>Contiene le funzioni per estrarre le informazioni
    dall'oggetto eccezione ricevuto in argomento e per generare un record di log ed inserirlo in error.xml.
    Viene utilizzato in maniera massiva da tutti i moduli che intercettano le eccezioni.</td></tr>
  <tr><td>IdGen.scala</td><td>Consente di creare ed inizializzare i file contenenti gli id relativi alle varie
    entità e di generare gli id in fese di creazione dei record.</td></tr>
  <tr><td>Util.scala</td><td>Contiene la funzione di cifratura cipher, utilizzata per criptare le password, ed una
    serie di funzioni, apparentemente ridondanti, ma utili, che consentono di indirizzare i file ubicati nelle varie
    directory utilizzate dall'applicazione.</td></tr>
  <tr><td>Filters.scala</td><td>Contiene le funzionalità idonee a generare predicati in base ai criteri passati
    in argomento, tali predicati sono utilizzati dai moduli che chiamano le funzioni deputate a filtrare un insieme 
    di record (es. getRecordsByFilter[DocumentLog](predicate, xmlFilePathName). In particolare tali funzionalità
    sono impiegare nelle sezioni di ricerca delle maschere di gestione.</td></tr>
  <tr><td rowspan="4">pck.e.ui.traits</td><td>GUI.scala</td><td>Trait contenente gli elementi basici utilizzati da tutte le
    gui del sistema, gli elementi grafici ed i comportamenti. In particolare l'intestazione ed il piè di pagina.
    </td></tr>
  <tr><td>Homepage.scala</td><td>Trait contenente gli elementi utilizzati da tutte le maschere homepage, gli elementi
    grafici ed i comportamenti. Questo trait estende GUI ed aggiunge alle maschere la gestione del menù.</td></tr>
  <tr><td>Management.scala</td><td>Trait contenente gli elementi utilizzati da tutte le maschere di gestione, gli elementi
    grafici ed i comportamenti. Questo trait estende GUI ed aggiunge alle maschere la gestione di una griglia (datagrid)
    per mostrare i record e la barra degli strumenti utilizzati da questo tipo di maschera.</td></tr>
  <tr><td>Operation.scala</td><td>Trait contenente gli elementi utilizzati da tutte le maschere relative alle operazioni
    sulle entità (aggiunta, modifica, eliminazione, ecc...), sia gli elementi grafici che i comportamenti. Questo trait
    estende GUI ed aggiunge alle maschere un'area per i dati del record e la barra degli strumenti utilizzati da questo
    tipo di maschera.</td></tr>
  <tr><td rowspan="3">pck.e.ui.homepages</td><td>AdminHomepage.scala</td><td>Homepage associata agli utenti aventi il
    ruolo Amministratore.</td></tr>
  <tr><td>OperatorHomepage.scala</td><td>Homepage associata agli utenti aventi il ruolo Operatore, ovvero gli operatori
    di protocollo  deputati a gestire la procollazione (presa in carico, protocollazione, archiviazione e consultazione)
    .</td></tr>
  <tr><td>ViewerHomepage.scala</td><td>Homepage associata agli utenti aventi il ruolo Viewer, ovvero gli utenti che
    possono soltanto consultare.</td></tr>
  <tr><td rowspan="1">pck.e.ui.management</td><td>Account.management.scala</td><td>Maschera di gestione degli Account.</td></tr>
  <tr><td rowspan="3">pck.e.ui.operations</td><td>Login.scala</td><td>Maschera di login, per accedere al sistema o per
    effettuare una richiesta di registrazione.</td></tr>
  <tr><td>AccountAdd.scala</td><td>Maschera per effettuare l'aggiunta di un Account, attivabile dalla masche di gestione
    Account.</td></tr>
  <tr><td>DocumentLoad.scala</td><td>Maschera che consente all'operatore di protocollo di acquisire nel sistema le 
    informazioni di un documento e di prenderlo in carico.</td></tr>
  <tr style="font-weight: normal; font-size: 20px;"><th colspan="3">Moduli sviluppati in src.test</th></tr>
  <tr style="text-align: center;"><td>Package</td><td>Modulo</td><td>Descrizione</td></tr>
  <tr><td rowspan="7">pck.b.logic</td><td>AccountTest.scala</td><td>Classe di test utilizzata per testare il modulo
    Account.scala.</td></tr>
  <tr><td>RoleTest.scala</td><td>Classe di test utilizzata per testare il modulo Role.scala.</td></tr>
  <tr><td>ClassificationTest.scala</td><td>Classe di test utilizzata per testare il modulo Classification.scala.</td></tr>
  <tr><td>RegistrationTest.scala</td><td>Classe di test utilizzata per testare il modulo Registration.scala.</td></tr>
  <tr><td>DocumentLogTest.scala</td><td>Classe di test utilizzata per testare il modulo DocumentLog.scala.</td></tr>
  <tr><td>ErrorLogTest.scala</td><td>Classe di test utilizzata per testare il modulo ErrorLog.scala.</td></tr>
  <tr><td>LoadedDocumentTest.scala</td><td>Classe di test utilizzata per testare il modulo LoadDocument.scala.</td></tr>
  <tr><td rowspan="3">pck.c.data</td><td>FileSystemTest.scala</td><td>Classe di test utilizzata per testare il modulo 
    FileSystem.scala.</td></tr>
  <tr><td>PropertiesTest.scala</td><td>Classe di test utilizzata per testare il modulo Properties.scala.</td></tr>
  <tr><td>XmlTest.scala</td><td>Classe di test utilizzata per testare il modulo Xml.scala.</td></tr>
  <tr><td rowspan="3">pck.d.util</td><td>FiltersTest.scala</td><td>Classe di test utilizzata per testare il modulo 
    Filters.scala.</td></tr>
  <tr><td>IdGenTest.scala</td><td>Classe di test utilizzata per testare il modulo IdGen.scala.</td></tr>
  <tr><td>UtilTest.scala</td><td>Classe di test utilizzata per testare il modulo Util.scala.</td></tr>
  <tr><td rowspan="1">pck</td><td>AllTestsSuite.scala</td><td>Suite di test che esegue tutti i test definiti nelle 
    classi di test. Utilizzata per il test di regressione.</td></tr>
</table>

## 5.2 Interfaccia grafica, gestione documentale e moduli di supporto — Roberto Pisu

Una parte consistente del mio lavoro ha riguardato l'interfaccia grafica dell'applicazione e l'implementazione dei flussi attraverso cui i diversi utenti interagiscono con il sistema. Mi sono occupato in particolare della gestione di ruoli e classifiche, dell'archiviazione e consultazione dei documenti archiviati, della gestione dei log, delle funzionalità di stampa e di diverse schermate trasversali, come homepage, dashboard e modifica del profilo.

Con l'aumento delle funzionalità, una parte importante del lavoro è diventata inoltre il refactoring dell'infrastruttura GUI. Molte schermate implementavano infatti comportamenti molto simili — form, tabelle, filtri, validazione e navigazione — che ho progressivamente centralizzato in componenti comuni, cercando di mantenere nelle singole view solamente la logica specifica della funzionalità rappresentata.

### 5.2.1 Interfaccia grafica e navigazione

I requisiti relativi alle homepage dei diversi utenti, alla modifica del profilo e alle numerose schermate di gestione hanno portato alla realizzazione di un numero elevato di view ScalaFX.

Nelle prime versioni ogni homepage gestiva direttamente l'istanziazione delle schermate successive e le callback necessarie per ritornare alla pagina precedente. Questa soluzione funzionava inizialmente, ma con l'aumento delle funzionalità produceva homepage sempre più estese e numerose sequenze di navigazione quasi identiche.

Ho quindi contribuito a separare progressivamente la navigazione dalla costruzione delle singole view attraverso `AppNavigator`, `HomeNavigator` e `NavigationFlows`. Le schermate ricevono callback come `onExit`, `onSaved`, `onAdd` o `onEdit`, senza conoscere direttamente la view che verrà mostrata successivamente.

In `NavigationFlows` ho inoltre raccolto alcuni flussi ricorrenti, come gestione → inserimento → gestione e gestione → modifica → gestione. L'utilizzo di funzioni di ordine superiore e genericità permette così di descrivere una sola volta comportamenti riutilizzati da entità differenti.

Un problema emerso durante l'implementazione dei form riguardava inoltre la possibilità di abbandonare una schermata dopo aver modificato dei campi, perdendo silenziosamente i dati inseriti. Per questo ho introdotto il controllo delle modifiche non salvate: il form mantiene il valore iniziale dei campi e il navigator può verificare la presenza di modifiche prima di consentire l'uscita dalla schermata.

### 5.2.2 Accesso al sistema e richiesta di registrazione

I requisiti relativi all'accesso al sistema e alla richiesta di registrazione sono stati tra i primi su cui ho lavorato e hanno costituito anche il punto di partenza per buona parte dell'infrastruttura GUI sviluppata successivamente.

Per la richiesta di registrazione ho realizzato `RegistrationView`, occupandomi della costruzione del form, della raccolta dei dati e del feedback fornito all'utente. Inizialmente parte dei controlli sui campi era contenuta direttamente nella view; con l'evoluzione del progetto questi controlli sono stati progressivamente spostati in `RegistrationValidator`, permettendo alla schermata di concentrarsi sulla sola interazione con l'utente. Tra i controlli aggiunti successivamente rientra anche la validazione del numero di telefono.

Mi sono occupato inoltre del flusso di autenticazione attraverso `LoginView` e `LoginService`. Una delle prime versioni della schermata interrogava direttamente la logica necessaria alla verifica delle credenziali; ho successivamente introdotto `LoginService` per separare l'interazione grafica dall'accesso ai dati e dalla verifica dell'account. Questa scelta è stata poi mantenuta anche nel resto dell'interfaccia, dove le view comunicano prevalentemente con servizi dedicati invece di accedere direttamente alle entità persistite.

Durante il consolidamento del sistema ho inoltre sostituito l'uso di MD5 con SHA3-512 per la gestione delle password, aggiornando in modo coerente i punti nei quali le credenziali vengono create o verificate. L'obiettivo era evitare che operazioni differenti, come autenticazione, creazione di un account e approvazione di una richiesta, utilizzassero strategie di hashing diverse.

Queste prime schermate sono state anche uno dei principali casi che hanno motivato la successiva introduzione del trait `Form`: molte delle funzionalità realizzate inizialmente in modo specifico per login e registrazione sono state infatti generalizzate e riutilizzate negli altri form dell'applicazione.

### 5.2.3 Homepage, dashboard e modifica del profilo

Un'altra parte di cui mi sono occupato fin dalle prime fasi del progetto riguarda le homepage dei diversi utenti e, successivamente, le dashboard personalizzate previste per i diversi ruoli.

La prima implementazione utilizzava una homepage generale configurata in base al tipo di utente. In una fase iniziale avevo adottato una soluzione basata sul pattern Strategy per separare le configurazioni dei diversi profili; con l'aumento delle funzionalità e il successivo refactoring della GUI questa struttura è stata progressivamente sostituita dalle attuali `HomePageAdminView`, `HomePageOperView` e `HomePageViewerView`, che condividono il comportamento comune definito nel trait `HomePage`.

Una difficoltà concreta era evitare che la differenziazione tra i ruoli portasse a tre copie quasi identiche della stessa interfaccia. Ho quindi mantenuto nel trait comune la costruzione degli elementi condivisi — struttura della pagina, menu, footer e comportamento generale — lasciando alle singole homepage principalmente la definizione delle funzionalità specifiche disponibili per quel profilo.

Successivamente ho implementato anche le dashboard (`DashboardAdminView`, `DashboardOperView`, `DashboardViewerView`) a partire da una struttura comune, `DashboardView`. Anche in questo caso ho cercato di evitare tre implementazioni indipendenti: il contenitore e il comportamento condiviso rimangono comuni, mentre ogni ruolo fornisce solamente le informazioni che hanno effettivamente significato per il proprio utilizzo del sistema.

Mi sono inoltre occupato della modifica del profilo personale. La stessa `AccountEditView` utilizzata dall'amministratore per la gestione degli account viene adattata al caso dell'utente autenticato, limitando le operazioni consentite sul proprio profilo. Ho collegato questa funzionalità direttamente al footer della homepage, rendendo il nome dell'utente il punto di accesso alla schermata di modifica.

Questa parte ha richiesto particolare attenzione alla distinzione tra riuso dell'interfaccia e autorizzazioni: riutilizzare la stessa view non deve infatti implicare che un utente possa modificare gli stessi campi disponibili all'amministratore. Un errore di questo tipo è emerso durante il testing ed è stato successivamente corretto restringendo le operazioni disponibili nel caso di modifica del proprio profilo.

### 5.2.4 Gestione di ruoli e classifiche

La gestione amministrativa di ruoli e classifiche è una delle funzionalità di cui mi sono occupato maggiormente. Ho implementato le relative schermate di gestione, inserimento e modifica (`RoleManagementView`, `RoleAddView`, `RoleEditView`, `ClassificationManagementView`, `ClassificationAddView`, `ClassificationEditView`) e parte dei componenti di supporto utilizzati da queste view.

Queste schermate hanno evidenziato molto presto una forte duplicazione. I form contenevano sempre la stessa struttura di campi, errori e pulsanti, mentre le pagine di gestione ripetevano la costruzione di tabelle, colonne, selezione degli elementi e azioni CRUD.

Da questa esperienza sono nati i trait `Form` e `Management`. `FormField` consente di trattare in modo uniforme controlli ScalaFX differenti, associando al controllo le operazioni necessarie per leggerne il valore, modificarlo e gestirne gli errori. Il trait `Management` raccoglie invece operazioni comuni alle schermate basate su tabelle, come la costruzione delle colonne, la gestione della selezione e il caricamento sicuro dei dati.

Durante questo refactoring ho cercato di evitare che i trait comuni diventassero semplicemente contenitori di codice generico. Ho mantenuto quindi al loro interno solamente i comportamenti realmente condivisi, lasciando alle singole view la gestione delle regole specifiche del dominio.

### 5.2.5 Archiviazione dei documenti

Il requisito relativo all'archiviazione dei documenti è stato uno dei blocchi funzionali principali di cui mi sono occupato. Ho implementato `ArchivedDocument`, il relativo servizio, le schermate per l'archiviazione e la successiva consultazione dei documenti archiviati.

Ho scelto di mantenere nel documento archiviato anche le informazioni accumulate nelle fasi precedenti del ciclo di vita. Un `ArchivedDocument` contiene quindi i dati originali del documento, quelli della presa in carico e della protocollazione, oltre alle informazioni introdotte al momento dell'archiviazione.

La parte più delicata dell'implementazione è stata il passaggio da documento protocollato a documento archiviato. L'operazione modifica due file XML indipendenti: viene prima creato il record nell'archivio e poi eliminato quello presente tra i documenti protocollati.

Non essendoci transazioni atomiche, ho gestito esplicitamente il caso di fallimento: se l'inserimento nell'archivio riesce ma la cancellazione del documento protocollato fallisce, il nuovo record viene eliminato nuovamente dall'archivio. Questo rollback evita che lo stesso documento possa risultare contemporaneamente presente in due stadi differenti del ciclo di vita.

In una successiva fase di consolidamento ho inoltre uniformato l'identificativo del documento lungo tutti gli stadi, mantenendo lo stesso `id` dalla presa in carico fino all'archiviazione. Questa scelta ha semplificato sia la ricerca dei documenti sia la ricostruzione del loro storico.

La consultazione dei documenti archiviati è stata inoltre adattata al profilo Viewer. In questo caso non era sufficiente riutilizzare direttamente la schermata amministrativa: i documenti mostrati devono rispettare il cono di visibilità dell'utente. Ho quindi applicato il filtro sulla classificazione associata al Viewer prima di fornire gli elementi alla schermata di gestione, in modo che la view riceva solamente i documenti effettivamente consultabili dall'utente autenticato.

Ho preferito applicare questo controllo prima della visualizzazione invece di limitarmi a disabilitare le azioni nella GUI: un documento non accessibile non viene quindi semplicemente reso non selezionabile, ma non entra proprio nell'insieme dei dati presentati all'utente.

### 5.2.6 Gestione e ricerca dei log

I requisiti relativi alla consultazione e alla ricerca dei log hanno portato alla realizzazione di `DocumentLogManagementView` e `DocumentLogDetailsView`.

Una delle difficoltà principali è stata la gestione dei filtri. I log possono essere ricercati secondo criteri differenti, come tipo di operazione, intervallo temporale, identificativo del documento o operatore, e questi criteri possono essere combinati.

Invece di implementare una funzione diversa per ogni combinazione, la schermata traduce lo stato dei controlli grafici in una sequenza di criteri e costruisce a partire da questi il predicato utilizzato per filtrare i record. L'aggiornamento dei risultati viene collegato direttamente ai controlli della GUI, così una modifica del filtro provoca automaticamente il ricalcolo della tabella.

Questa funzionalità è stata anche uno dei casi che hanno motivato il successivo refactoring del trait `Management`, perché gran parte del comportamento sviluppato per i log poteva essere riutilizzato nelle altre schermate di gestione dei documenti.

### 5.2.7 Gestione dei documenti PDF

Per supportare le funzionalità di stampa dell'applicazione è stato utilizzato il package `pkg.b.logic.pdf`, che raccoglie i componenti dedicati alla generazione e alla gestione dei documenti PDF.

Il mio intervento si è concentrato in particolare sulla realizzazione e sull'adattamento dei componenti necessari alle diverse tipologie di informazioni mostrate dalle GUI. Le schermate dell'applicazione possono infatti richiedere documenti con strutture differenti: schede relative a un singolo elemento, tabelle contenenti più record, report suddivisi in sezioni oppure riepiloghi più articolati.

Per la generazione delle schede di dettaglio viene utilizzato `PdfDetailsCreator`, che riceve il titolo del documento e una sequenza di coppie campo-valore. Questo componente permette di riutilizzare la stessa logica per elementi differenti, come account, documenti, richieste di registrazione e log.

Per gli elenchi è stato introdotto `PdfTableCreator`, che astrae la costruzione di un documento tabellare ricevendo le intestazioni, le righe da visualizzare e, quando necessario, la configurazione delle larghezze delle colonne. Le diverse schermate di gestione possono quindi preparare i propri dati e delegare al componente solamente la loro rappresentazione nel PDF.

Le statistiche richiedono invece la presenza di più gruppi di dati nello stesso documento. Per questo scopo è stato realizzato `PdfSectionsCreator`, che consente di costruire un PDF formato da più sezioni, ciascuna caratterizzata da un proprio titolo e da una propria tabella.

Un caso differente è rappresentato dal riepilogo relativo alla lavorazione di un documento. Per questa funzionalità è stato introdotto `PdfDocumentSummaryCreator`, responsabile della costruzione di un report composto da informazioni eterogenee che non possono essere rappresentate efficacemente attraverso una semplice tabella.

I generatori PDF lavorano esclusivamente sui dati ricevuti dai propri chiamanti e non dipendono direttamente dal sistema di persistenza. La responsabilità di recuperare e organizzare le informazioni rimane quindi nei servizi e nelle view, mentre il package PDF si occupa solamente della loro rappresentazione.

Questa separazione consente di mantenere i componenti più semplici, riutilizzabili e con responsabilità ben definite.

La visualizzazione del documento è gestita separatamente dalla sua generazione attraverso `PdfViewer`, che permette di mostrare il PDF prodotto e di accedere alle operazioni di stampa o di apertura tramite il visualizzatore predefinito del sistema.

### 5.2.8 Refactoring, validazione e test

Oltre alle singole funzionalità, una parte rilevante del mio contributo ha riguardato il consolidamento del codice sviluppato durante i vari sprint.

Le prime schermate contenevano molte regole di validazione direttamente nei gestori degli eventi grafici. Ho progressivamente spostato questi controlli in validator dedicati, in modo da separare le regole sui dati dalla costruzione della GUI e poterle testare indipendentemente da ScalaFX.

Un'attività simile ha riguardato la centralizzazione delle stringhe e degli stili grafici in `UiText` e `UiStyles`, evitando che testi, messaggi di errore e identificatori CSS fossero duplicati nelle singole view.

Ho inoltre lavorato sulla suite di test e sulla configurazione della Continuous Integration. Durante l'esecuzione automatica sono emersi problemi dovuti al fatto che diversi test utilizzavano gli stessi file XML e potevano interferire tra loro quando eseguiti in parallelo. La configurazione è stata quindi adattata per evitare queste interferenze.

Infine ho aggiunto una GitHub Action per l'esecuzione automatica dei test sul branch `develop`. Questo controllo si è rivelato particolarmente utile nella fase finale, in cui modifiche ai componenti GUI comuni potevano influenzare contemporaneamente numerose funzionalità dell'applicazione.

## 5.3 Motore di autorizzazione Prolog, controllo di gestione e moduli di supporto — Thomas Testa

### 5.3.1 Motore di autorizzazione Prolog

Il requisito obbligatorio "utilizzo di regole logiche per la verifica delle autorizzazioni" e il relativo opzionale "personalizzazione delle regole organizzative tramite Prolog" sono le parti di cui mi sono occupato più a lungo e con la maggiore autonomia progettuale, in un branch dedicato (`feature/autorizzazione-prolog`) integrato successivamente in `develop`.

L'implementazione è divisa in due livelli:

- **`PrologEngine`** (`pkg.d.util`), l'unico file del progetto che importa `alice.tuprolog.*`. Incapsula il motore tuProlog dietro un'unica funzione `Term => LazyList[Term]`, seguendo il pattern Scala2P visto a lezione. Rispetto alla versione delle slide, l'iteratore che consuma le soluzioni controlla esplicitamente `hasOpenAlternatives` prima di richiamare `solveNext()`: senza questo controllo, `solveNext()` lancia un'eccezione (`NoMoreSolutionException`) invece di segnalare in modo pulito l'esaurimento delle soluzioni — un comportamento che ho scoperto testando goal con un numero finito di soluzioni, il caso più comune nell'uso reale.
- **`AuthorizationEngine`** (`pkg.b.logic`), che espone all'applicazione un'API Scala pura (`isAuthorized`, `canDeleteRole`, `canDeleteAccount`, `permittedActions`) senza mai far trapelare i tipi di tuProlog. Tutte le regole di autorizzazione sono definite in `authorization.pl` (fatti `can/2`, predicati `authorized/2`, `permitted_actions/2` con `findall`, `can_delete_role/1`, `can_delete_account/2`), caricata come risorsa.

Ho scelto di centralizzare la verifica in un unico varco applicativo, `HomePage.navigate`: ogni azione richiesta dal menu passa da lì e viene verificata con `AuthorizationEngine.isAuthorized` prima di essere eseguita, indipendentemente dalla view che l'ha originata. Le stesse regole Prolog vengono interrogate anche da `Role.recordDelete` e `Account.recordDelete` (per gli invarianti "non si elimina il ruolo admin" e "non si elimina l'ultimo amministratore"), così la policy resta definita in un unico punto anche per operazioni che non passano dal menu.

Per l'opzionale, ho aggiunto la possibilità per un amministratore di estendere le regole a runtime dalla GUI (`AuthorizationRuleAddView`, `AuthorizationRulesManagementView`): le regole aggiunte vengono asserite nel motore live (`assert`/`retract`) e persistite in un file separato, `customRules.pl`, mai unito alla teoria di base spedita con l'applicazione. Questo evita due problemi: che un aggiornamento dell'app cancelli le personalizzazioni di un cliente, e che una regola custom duplichi silenziosamente un permesso già concesso dalla teoria base — quest'ultimo un bug che ho effettivamente introdotto e corretto durante lo sviluppo, controllando `isAuthorized` (base + custom) invece della sola collezione delle regole custom prima di asserire.

La difficoltà maggiore non è stata tecnica ma di valutazione: dopo aver completato questa parte mi sono chiesto onestamente se Prolog avesse semplificato il codice rispetto all'equivalente in Scala. La risposta, discussa più nel dettaglio in sezione 7, è che lo ha fatto in modo netto solo per `permitted_actions/2` (il `findall` elimina la necessità di una struttura dati parallela per il menu dinamico); per un controllo puntuale come `authorized/2` l'equivalente Scala (una `Map[String, Set[MenuAction]]`) sarebbe stato altrettanto leggibile e più diretto. Il vantaggio reale, per come l'ho strutturato, è avere un unico posto dichiarativo in cui tutte le regole di autorizzazione sono leggibili e modificabili senza toccare il codice Scala.

### 5.3.2 Controllo di gestione documenti

Il modulo "Controllo di Gestione" (`DocumentManagementControlView`, `DocumentManagementControlService`, `DocumentManagementDetailsView`, `DocumentManagementSummaryView`) offre una vista unificata sullo stato di lavorazione di un documento, indipendentemente dallo stadio del ciclo di vita in cui si trova (preso in carico, protocollato o archiviato).

La parte più delicata è stata l'aggregazione: `LoadedDocument`, `RegisteredDocument` e `ArchivedDocument` sono tre entità indipendenti, ciascuna persistita nel proprio file XML, non collegate da ereditarietà. Ho evitato un vero merge/join tra le tre collezioni sfruttando un invariante già garantito dal resto del sistema: quando un documento avanza di stadio, il record dello stadio precedente viene eliminato (lo fanno `LoadedDocumentService`/`ArchivedDocumentService`), quindi un dato id esiste sempre in un solo file alla volta. `DocumentManagementControlService.getManagedDocuments` si limita quindi a mappare ciascuna collezione in un view model comune (`ManagedDocument`) e a concatenare i risultati, ordinandoli per id — molto più semplice di un join, ma corretto solo perché quell'invariante è rispettata a monte.

`DocumentManagementSummaryView` ricostruisce invece lo storico cronologico delle fasi di un singolo documento leggendo `DocumentLog` e ordinandolo con una chiave composita (fase, poi id del log), per gestire correttamente più operazioni dello stesso tipo nello stesso giorno.

### 5.3.3 Statistiche di utilizzo del sistema

`StatisticsService` aggrega i dati di utilizzo (documenti protocollati/archiviati per mese, accessi per ruolo e per utente, esito delle richieste di registrazione) leggendo direttamente le entità di dominio, senza un database relazionale con `GROUP BY` a disposizione. L'ho tenuto deliberatamente privo di dipendenze da ScalaFX, cosa che ha permesso di testarlo con JUnit senza dover avviare il toolkit grafico — l'unico dei moduli che ho scritto per cui esiste una suite di test dedicata al livello di servizio.

Un dettaglio a cui ho dedicato attenzione è la gestione delle date non valide: `yearMonthOf` prova a interpretare la data di un documento e, se fallisce, scarta silenziosamente quel record invece di far fallire l'intera pagina statistiche — una scelta di robustezza rispetto a dati "sporchi", coerente col fatto che la persistenza su XML non impone alcun controllo di formato in scrittura.

### 5.3.4 Gestione delle richieste di registrazione e protocollazione

Su `RegistrationRequestService` ho implementato il flusso di approvazione di una richiesta di registrazione: generazione di username univoco e password temporanea, creazione dell'account con password cifrata (SHA3-512), e solo in caso di successo l'aggiornamento dello stato della richiesta. Non essendoci transazioni atomiche tra due file XML indipendenti, in caso di fallimento nell'aggiornamento della richiesta il servizio esegue un rollback esplicito cancellando l'account appena creato, per evitare account "orfani" non riconducibili a nessuna richiesta approvata.

Ho inoltre curato la protocollazione dei documenti (`RegisteredDocument`, `RegisteredDocumentDetailsView`) e, in una fase successiva di consolidamento del progetto, una serie di correzioni trasversali individuate testando manualmente l'applicazione: la perdita del campo note durante l'archiviazione (un campo semplicemente dimenticato nella costruzione del documento archiviato), l'assenza di uno scroll nelle schermate di form/dettaglio più lunghe (che tagliava fuori dalla vista i pulsanti di azione), e alcune duplicazioni di regole di business già presenti nel motore di autorizzazione ma reimplementate localmente in due view di gestione.

[Back to index](0-Indice.md) |
[Previous Chapter](4-Design_di_dettaglio.md) |
[Next Chapter](6-Testing.md)
