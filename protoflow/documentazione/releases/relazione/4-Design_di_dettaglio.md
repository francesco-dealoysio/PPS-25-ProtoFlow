# 4. Design di dettaglio

Questa sezione descrive le principali scelte di design adottate 
nell'implementazione dei diversi sottosistemi di ProtoFlow.

A differenza del design architetturale, che descrive la struttura
generale del sistema e le responsabilità dei principali package,
il design di dettaglio approfondisce l'organizzazione interna dei
componenti, le loro responsabilità e le collaborazioni tra le
principali astrazioni introdotte durante lo sviluppo.

La sezione è suddivisa in base alle principali aree sviluppate dai
membri del gruppo.

## 4.1 Interfaccia grafica e navigazione — Roberto Pisu
Questa sezione descrive la struttura dell'interfaccia grafica di ProtoFlow, concentrandosi in particolare sulla gestione della navigazione e sull'organizzazione delle schermate.

Molte viste dell'applicazione condividono elementi e comportamenti simili, come la struttura dei moduli d'inserimento dati (form), le schermate di gestione e le home page dedicate ai vari ruoli utente.

Per evitare di duplicare il codice e mantenere il progetto pulito, queste parti comuni sono state raggruppate in componenti riutilizzabili tramite trait, lasciando a moduli specifici il compito di gestire il passaggio da una schermata all'altra.

### 4.1.1 Astrazioni comuni dell'interfaccia grafica

Le diverse schermate dell'applicazione condividono numerosi elementi
grafici e comportamentali. Una loro implementazione indipendente avrebbe
portato alla duplicazione della stessa logica all'interno delle singole
view.

Per questo motivo sono state definite alcune astrazioni comuni tramite
trait Scala. Ogni trait raccoglie funzionalità appartenenti allo stesso
livello di responsabilità, mentre le view concrete mantengono solamente
la configurazione e il comportamento specifico della schermata.

In particolare, `Common` raccoglie i costruttori e i comportamenti
grafici più generali, mentre trait più specializzati, come `Management`,
`Root` e `HomePage`, aggiungono rispettivamente funzionalità dedicate
alle schermate di gestione e alla struttura delle homepage.

[INSERIRE DIAGRAMMA CLASSI DEI TRAIT + ALCUNE VIEWS]

### 4.1.2 Struttura della navigazione tra le view

La navigazione dell'interfaccia grafica è gestita mantenendo separate
le singole view dalla logica che determina il passaggio tra le schermate.

Le view rappresentano le diverse funzionalità dell'applicazione e
ricevono tramite callback le operazioni che devono essere eseguite in
seguito alle azioni dell'utente, come il salvataggio, l'uscita o la
selezione di un elemento.

In questo modo una view non deve conoscere direttamente la schermata
che la precede o quella che verrà visualizzata successivamente.

Le homepage dei diversi ruoli costituiscono il punto di coordinamento
dei principali flussi dell'interfaccia e associano le azioni disponibili
alle corrispondenti schermate.

Tra i flussi presenti nell'applicazione si possono distinguere, ad
esempio:

- apertura di una schermata di gestione dalla dashboard;
- passaggio dalla gestione alla schermata di inserimento;
- passaggio dalla gestione alla modifica o al dettaglio di un elemento;
- ritorno alla schermata di gestione;
- ritorno alla dashboard.

Questa organizzazione mantiene separata la responsabilità di
rappresentare una schermata dalla responsabilità di coordinare la
navigazione tra le diverse funzionalità dell'applicazione.

[INSERIRE DIAGRAMMA DELLA NAVIGAZIONE]

### 4.1.3 Gestione dei form e dello stato dei campi

Le schermate dedicate all'inserimento, alla modifica e alla
visualizzazione dei dati sono costruite a partire da un'astrazione
comune definita dal trait `Form`.

L'elemento centrale di questa struttura è `FormField`, che associa
a un controllo grafico le informazioni e le operazioni necessarie
alla gestione del relativo campo. Oltre al controllo ScalaFX,
vengono mantenuti il valore iniziale, il messaggio di errore e le
funzioni utilizzate per leggere e aggiornare il contenuto.

Grazie a questa rappresentazione uniforme, operazioni comuni come il
reset dei campi, il controllo delle modifiche effettuate e la
visualizzazione degli errori possono essere applicate
indipendentemente dal tipo concreto del controllo utilizzato.

Il trait mette inoltre a disposizione costruttori specifici per i
principali tipi di campo presenti nell'applicazione, come campi di
testo, aree di testo, password, selezioni tramite `ComboBox` e date.

La struttura complessiva delle schermate viene infine costruita tramite
`formPage`, che compone intestazione, contenuto del form, messaggi di
feedback e pulsanti di azione secondo una disposizione comune.

[INSERIRE DIAGRAMMA DI FORM, FORMFIELD E ALCUNE VIEW]

### 4.1.4 Struttura delle schermate di gestione e ricerca

Le schermate dedicate alla consultazione e alla gestione di insiemi di
elementi condividono una struttura comune basata principalmente su
tabelle, strumenti di ricerca e azioni applicabili agli elementi
visualizzati.

Per rappresentare questa tipologia di interfaccia è stato definito il
trait `Management`, che mette a disposizione componenti riutilizzabili
per la costruzione delle diverse schermate di gestione.

La visualizzazione principale è realizzata tramite `TableView`,
parametrizzata rispetto al tipo degli elementi mostrati. La struttura
delle colonne viene costruita separatamente dalla tabella, permettendo
alle singole view di specificare solamente quali proprietà dell'entità
devono essere visualizzate.

Le schermate possono inoltre affiancare alla tabella una sezione di
ricerca composta da controlli differenti in base ai dati trattati,
come campi testuali, selezioni tramite `ComboBox` o intervalli temporali
tramite `DatePicker`.

I dati mostrati dalla tabella sono mantenuti in collezioni osservabili,
in modo che l'interfaccia possa essere aggiornata quando cambia
l'insieme degli elementi risultanti dalla ricerca o dalle operazioni
effettuate dall'utente.

Le singole view specializzano quindi questa struttura definendo le
colonne necessarie, i criteri di ricerca e le azioni disponibili,
mantenendo comune la costruzione generale della schermata.

Questa organizzazione viene utilizzata nelle diverse funzionalità di
gestione dell'applicazione, tra cui la consultazione di account,
richieste, documenti e log.

[INSERIRE DIAGRAMMA DI MANAGEMENT + ALCUNE VIEW DI GESTIONE]

## 4.2 [Parte sviluppata da Francesco] — Francesco ...

...

## 4.3 [Parte sviluppata da Thomas] — Thomas ...

...