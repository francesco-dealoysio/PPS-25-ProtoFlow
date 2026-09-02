[Back to index](0-Indice.md) |
[Previous Chapter](3-Design_architetturale.md) |
[Next Chapter](5-Implementazione.md)
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


[Back to index](0-Indice.md) |
[Previous Chapter](3-Design_architetturale.md) |
[Next Chapter](5-Implementazione.md)