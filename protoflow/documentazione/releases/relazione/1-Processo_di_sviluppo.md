# 1. Processo di sviluppo

## 1.1 Metodologia
<p style="text-align: justify;">
Il gruppo ha adottato il processo di sviluppo Agile <b>Scrum</b>, articolato in sprint di durata orientativa pari
a una settimana, in conformità a quanto consigliato nel punto P8 delle regole d'esame del corso.
</p>

### <p style="color: red;">(tbd) Meeting iniziale (tbd)</p>

Ogni sprint ha seguito la struttura classica del seguente esempio relativo allo Sprint S1:

- **[Sprint Planning Meeting S1](../../process/sprint/Sprint_S1/01_SPRINT_PLANNING_MEETING_S1.pdf "Apertura file pdf")**
- **[Daily Scrum S1](../../process/sprint/Sprint_S1/04_DAILY_SCRUM_S1.pdf "Apertura file pdf")**
- **[Sprint Review S1](../../process/sprint/Sprint_S1/05_SPRINT_REVIEW_S1.pdf "Apertura file pdf")**

### **Sprint Planning Meeting**
<p style="text-align: justify;">
All'inizio di ogni sprint è stato tenuto un incontro via Teams/Meet finalizzato alla definizione degli obiettivi ed
alla selezione dei requisiti, assegnando a questi ultimi le opportune priorità di realizzazione, rispettivamente da
realizzare ed sviluppare durante lo sprint, nonché alla scomposizione delle User Story in task ed alla individuazione 
delle soluzioni di massima da adottare per implementarli. 
</p>

<p style="text-align: justify;">
Le suddette attività sono state condotte sulla base della documentazione relativa ai requisiti predisposta nelle 
precedenti fasi di raccolta, analisi e specifica degli stessi . Nello specifico il Project Overview Statement (POS), 
la Requirement Breakdown Structure (RBS), il Product Backlog (PB), le User Story (US) e la Work Breakdown Structure
(WBS), quest'ultima predisposta per accogliere i task individuati durante lo sviluppo.
Oltre alla predetta documentazione, dal secondo sprint in poi in alcuni casi si è tenuto conto delle user story e
dei task non completati negli sprint precedenti, degli eventuali difetti riscontrati durante le sprint review,
nonché delle eventuali modifiche dei requisiti stabilite in itinere.
</p>

- **[Project Overview Statement](../../process/POS.pdf "Apertura file pdf")**
- **[User Story](../../requisiti_e_analisi/UserStories/ "Apertura directory")**
- **[Requirement Breakdown Structure](../../requisiti_e_analisi/RBS.pdf "Apertura file pdf")**
- **[Product Backlog](../../process/Product_Backlog.pdf "Apertura file pdf")**
- **[Work Breakdown Structure](../../process/wBS.pdf "Apertura file pdf")**

<p style="text-align: justify;">
Successivamente alla selezione dei requisiti, si è proceduto all'analisi di ciascuna User story ed alla scomposizione
della stessa in task. Per ogni task si cercato di individuare una soluzione di massima da adottare per la sua 
realizzazione ed infine è stato redatto un token corrispondente al task.
Il <b>token</b> associato ad un task è un file, che oltre a contenere la descrizione e le indicazioni utili a comprendere
cosa deve essere realizzato, costituisce uno strumento condiviso per la coordinazione ed il monitoraggio delle
attività da espletare. I token, infatti, sono stati collocati in una struttura a directory realizzata su un
dispositivo di rete (NAS) accessibile da tutti. Ogni componente del team ha potuto prendere in carico un task
anteponendo al nome del token l'iniziale del proprio nome, spostare il token attraverso specifiche directory
in funzione dello stato di avanzamento della attività (todo, in progress o done) e compilare il token con le
informazioni relative al tempo di realizzazione.
</p>

<p style="text-align: justify;">
Nell'ultima fase del meeting sono state effettuate le seguenti attività ed è stata prodotta la documentazione
relativa allo sprint.
- Preparazione del documento Sprint Planning Meeting relativo all'incontro
- Preparazione del file Sprint_BackLog contenente le descrizioni relative alle US selezionate
- Preparazione del file User_Story_Task contenente l'elenco dei task invividuati per ogni User Story
- Preparazione del file Daily_Scrum deputato a tenere traccia quatidiana dello dello stato di avanzamento dello sprint
- Preparazione del file Sprint_Review da utilizzare al termine dello sprinto per il resoconto delle attività
- Inserimento dei file pdf relativi alle US selezionate nella directory 'Task_User_Story/User_Story'
- Inserimento dei token relativi ai task nella directory 'Task_User_Story'
</p>

<p style="text-align: justify;">
A titolo di esempio, si riportano di seguito i link relativi alla documentazione prodotta per lo Sprint S1 
e quelli relativi alle directory nelle quali sono stati inseriti le User Story ed i token dei Task corrispondenti:
</p>

- **[Sprint Planning Meeting S1](../../process/sprint/Sprint_S1/01_SPRINT_PLANNING_MEETING_S1 "Apertura file pdf")**
- **[Sprint Backlog S1](../../process/sprint/Sprint_S1/02_SPRINT_BACKLOG_S1.pdf  "Apertura file pdf")**
- **[User Story Task S1](../../process/sprint/Sprint_S1/03_USER_STORY_TASK_S1.pdf  "Apertura file pdf")**
- **[Daily Scrum S1](../../process/sprint/Sprint_S1/04_DAILY_SCRUM_S1.pdf  "Apertura file pdf")**
- **[Sprint Review S1](../../process/sprint/Sprint_S1/05_SPRINT_REVIEW_S1.pdf  "Apertura file pdf")**
- **[User Story S1](../../process/sprint/Sprint_S1/TASK_USER_STORY_S1/USER_STORY/  "Apertura directory")**
- **[Task S1](../../process/sprint/Sprint_S1/TASK_USER_STORY_S1/Task_Done/  "Apertura directory")**

### **Daily Scrum**
Aggiornamento giornaliero per persona (attività svolta, attività da svolgere, problemi/esigenze);

### **Sprint Review**
<p style="text-align: justify;">
A fine sprint, con verifica dello stato di completamento degli elementi pianificati, della conformità
a quanto previsto dalle sezioni Done Definition delle User Story contenute nello Sprint Backlog tramite
test di accettazione (Use Case Test).
</p>

## 1.2 Team

Il gruppo è composto da tre persone:

- **Francesco de Aloysio**
- **Roberto Pisu**
- **Thomas Testa**

<p style="text-align: justify;">
Il ruolo di Scrum Master e quello di Product Owner non risultano assegnati a una persona specifica
(i relativi campi, presenti nei template di planning e di backlog, sono rimasti vuoti in tutti gli sprint).
Il gruppo ha operato più come un team auto-organizzato senza una separazione formale di questi due ruoli,
piuttosto che con un'assegnazione esplicita.
</p>

<p style="text-align: justify;">
Il lavoro è stato distribuito tra i tre membri del team sprint per sprint, in base a disponibilità e
priorità del momento, piuttosto che con un'assegnazione fissa di aree di competenza: nel corso del 
progetto ciascun componente del gruppo ha contribuito a più aspetti del sistema — analisi dei 
requisiti, logica di dominio, interfacce grafiche, funzionalità applicative — con una naturale
concentrazione, in alcuni sprint, su determinati task in base al carico di lavoro pianificato.
</p>

## 1.3 Strumenti a supporto del processo

- **GitHub**: versionamento del codice, gestione di branch per feature/refactoring, pull request;
- **GitHub Actions**: integrazione continua, con esecuzione automatica della suite di test ad ogni push sul branch `develop`;
- **IntelliJ IDEA**: ambiente di sviluppo per Scala/JUnit;
- **Microsoft PowerPoint**: presentazioni di Sprint Planning e Sprint Review;
- **Microsoft Excel**: Sprint Backlog e Product Backlog;
- **Microsoft Word**: Daily Scrum, User Story e relativa scomposizione in task;
- una condivisione su rete/cloud (NAS/Drive del gruppo) per la distribuzione di questi documenti tra i membri, separata dal repository Git.

## 1.4 Cronologia degli sprint

| Sprint | Periodo                 | Obiettivo principale |
|---|-------------------------|---|
| S0 | 26/06/2026 – 01/07/2026 | Avvio del progetto: ambiente di sviluppo, individuazione dei requisiti, RBS, Product Backlog, User Story, template della documentazione |
| S1 | 02/07/2026 – 09/07/2026 | Accesso al sistema (portale, login, richiesta di registrazione) e homepage per i tre ruoli utente |
| S2 | 10/07/2026 – 17/07/2026 | Gestione del processo di registrazione; CRUD di Account, Ruoli e Classifiche; modifica del profilo utente |
| S3 | 22/07/2026 – 30/07/2026 | Completamento della gestione Ruoli; ciclo di vita del documento (presa in carico, protocollazione, archiviazione) |
| S4 | 03/08/2026 – 11/08/2026 | Log degli eventi di protocollazione; funzionalità di ricerca; stampa di documenti e log |
| S5 | 12/08/2026 – ?/?/2026   | Controllo di gestione documentale, riepilogo documento, statistiche, dashboard per ruolo; redazione della documentazione di progetto |

### Sprint 0 — Avvio (26/06/2026 – 01/07/2026)
<p style="text-align: justify;">
Sprint organizzativo, privo di User Story applicative: preparazione dell'ambiente di sviluppo (progetto IntelliJ, Scala,
JUnit, repository GitHub condiviso), individuazione dei requisiti a partire dal Project Overview Statement, redazione
della Requirement Breakdown Structure e del Product Backlog, stesura delle User Story, predisposizione dei template 
della documentazione di progetto. Le sei attività pianificate risultano tutte completate a fine sprint.
</p>

### Sprint 1 (02/07/2026 – 09/07/2026)
<p style="text-align: justify;">
Obiettivo: gestione dell'accesso al sistema e preparazione delle homepage per i diversi profili utente. Sono state
realizzate le sei User Story pianificate (Portale, Login, Richiesta di Registrazione, e le tre Homepage per i ruoli
Amministratore/Operatore/Viewer), tutte segnate come completate nella Sprint Review. Una nota di Daily Scrum (03/07)
segnala una decisione tecnica: le maschere di inserimento dati devono essere generali e i dati persistenti devono risiedere in file XML — scelta di persistenza confermata e mantenuta per l'intero progetto.
</p>

### Sprint 2 (10/07/2026 – 17/07/2026)
<p style="text-align: justify;">
Obiettivo: gestione del processo di registrazione e CRUD delle entità Account, Ruolo e Classifica, oltre alla modifica
del profilo utente. La maggior parte dei task pianificati risulta completata; le GUI relative alla gestione dei Ruoli
e alla modifica del profilo utente sono state esplicitamente rinviate allo sprint successivo (la logica di backend di
queste funzionalità era invece già completa), mentre le funzioni di backend collegate risultano completate in questo
sprint.
</p>

### Sprint 3 (22/07/2026 – 30/07/2026)
<p style="text-align: justify;">
Obiettivo: completamento della gestione Ruoli (le GUI rimaste in sospeso dallo Sprint 2) e avvio del ciclo di vita
del documento protocollato: presa in carico, protocollazione e archiviazione, con le relative entità e interfacce.
La GUI di modifica del profilo utente è rimasta l'unico task non completato a fine sprint, rinviato allo Sprint 4.
</p>

### Sprint 4 (03/08/2026 – 11/08/2026)
<p style="text-align: justify;">
Obiettivo: log degli eventi legati alla protocollazione (presa in carico, protocollazione, archiviazione), 
funzionalità di ricerca sui documenti e sui log, stampa di documenti e log. Diverse sezioni GUI di ricerca sono 
rimaste non completate a fine sprint, pur essendo completata la logica di filtro sottostante; la GUI di modifica 
del profilo utente, riportata da Sprint 3, è stata completata in questo sprint.
</p>

### Sprint 5 (12/08/2026 – ?/08/2026)
<p style="text-align: justify;">
Ultimo sprint del piano originale. Obiettivo duplice: da un lato il completamento delle ultime funzionalità
applicative (controllo di gestione documentale, riepilogo di un singolo documento, statistiche, dashboard per
ruolo, visualizzazione dei documenti archiviati per il ruolo Viewer); dall'altro l'avvio della stesura della 
documentazione di progetto finale (i sette documenti che compongono questa stessa relazione). Le User Story
applicative risultano tutte completate; tra i documenti di relazione, a fine sprint solo quello di "Requirement
specification" risultava ancora in corso secondo il Daily Scrum, mentre la Sprint Review li segna tutti come
completati — a indicare che il lavoro è stato concluso a ridosso della chiusura dello sprint.
</p>

## 1.5 Lavoro successivo allo Sprint 5
<p style="text-align: justify;">
Dopo la chiusura dello sprint pianificato, il gruppo ha dedicato un'ulteriore fase di lavoro all'implementazione
del requisito obbligatorio "utilizzo di regole logiche per la verifica delle autorizzazioni" e dell'opzionale
"personalizzazione delle regole organizzative tramite Prolog", non ancora completati a fine Sprint 5. Il lavoro
è stato svolto su un branch dedicato (`feature/autorizzazione-prolog`), con verifica tramite la suite di test
esistente ad ogni passaggio, e successivamente integrato in `develop`. I dettagli tecnici sono discussi nelle
sezioni 3, 4 e 6.
</p>

## 1.6 Osservazioni sul processo
<p style="text-align: justify;">

</p>



