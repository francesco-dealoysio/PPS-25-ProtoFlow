[Back to index](0-Indice.md) |
[Previous Chapter](6-Testing.md)
# 7. Retrospettiva

La Sprint Retrospective è, come previsto dalla metodologia Scrum, l'incontro che lo Scrum Team tiene in fase di Strint 
Review al termine di ogni Sprint con i seguenti scopi:

- esaminare l'andamento dello Sprint appena terminato in relazione a comportamenti delle persone, relazioni, processi e strumenti;
- evidenziare e discutere, oltre agli elementi andati a buon fine, anche e soprattutto le eventuali  problematiche e difficoltà che si sono presentate;
- individuare i correttivi e/o i miglioramenti da applicare alle modalità di lavoro per l'esecuzione degli Sprint successivi;
- redigere un piano per l'attuazione di tali miglioramenti.

## 7.1 Limiti del processo

Il gruppo non ha tenuto, per la maggior parte degli sprint, una sessione di Sprint Retrospective documentata in modo discorsivo: le sole eccezioni sono poche note isolate riportate nei Daily Scrum (ad esempio decisioni tecniche o rallentamenti puntuali). Questo limite del processo viene ripreso e discusso onestamente in questa sezione. In particolare:

- la disciplina di compilazione del Daily Scrum non è stata costante: negli Sprint 2, 3 e 5 diverse giornate risultano senza note compilate per uno o più membri, pur risultando lavoro effettivamente svolto (confermato dalla tabella riepilogativa dei task e dai file di implementazione prodotti);
- i ruoli di Scrum Master e Product Owner, previsti dal framework Scrum, non sono stati assegnati formalmente a una specifica persona nei documenti di sprint;
- le sessioni di Sprint Review si sono limitate, nella maggior parte dei casi, a una tabella di stato delle User Story pianificate, senza una sezione di retrospettiva discorsiva.

## 7.2 Retrospettiva a consuntivo

Non avendo svolto, sprint per sprint, una retrospettiva discorsiva secondo lo schema Scrum, il gruppo ripercorre qui, a consuntivo dell'intero progetto, gli elementi che quella sessione avrebbe dovuto raccogliere in itinere.

Elementi che sono andati bene:

- l'organizzazione del lavoro su branch dedicati per singola funzionalità (`feature/*`), integrati in `develop` tramite merge, ha permesso a più membri di lavorare in parallelo su parti diverse del sistema senza bloccarsi a vicenda;
- l'introduzione di una GitHub Action che esegue automaticamente l'intera suite di test ad ogni push su `develop` ha permesso di intercettare regressioni introdotte da modifiche successive, anche quando non notate manualmente da chi le aveva apportate;
- il refactoring dell'infrastruttura GUI condivisa (trait `Form`, `Management`, i navigator) è stato portato avanti in modo incrementale e collaborativo, riducendo nel tempo la duplicazione tra le numerose schermate del sistema.

Miglioramenti da adottare in un progetto successivo:

- assegnare formalmente, fin dal primo sprint, i ruoli di Scrum Master e Product Owner, invece di lasciarli impliciti;
- riservare, ad ogni Sprint Review, uno spazio esplicito e documentato alla retrospettiva discorsiva, oltre alla sola tabella di stato delle User Story;
- rendere più costante la compilazione del Daily Scrum da parte di tutti i membri, ad esempio con un promemoria di fine giornata o una checklist minima da compilare.

### Suggerimento
I progetti che, per il grado di incertezza sia dei requisiti che delle soluzioni, vengono gestiti attraverso la metodologia Agile Scrum prevedono che i componenti del
team di sviluppo possiedano elevate qualità tecnico-professionali, capacità organizzative, capacità di interazione sociale e un grado di maturità che gli consenta
in autonomia di scegliere e portare avanti le attività di propria competenza.

Normalmente la scelta della composizione del team è oggetto di attenta analisi e l'inizio del progetto è preceduto dalla cosiddetta fase di team building, anche
questa necessaria a verificare la bontà della scelta.

Ovviamente, nel caso del nostro progetto, finalizzato al sostenimento dell'esame, le suddette premesse non sono realizzabili.
Tuttavia, potrebbe essere utile, allo scopo di favorire la conoscenza, saggiare la compatibilità ed effettuare una fase di amalgama tra studenti chiamati a
sviluppare insieme il progetto finale d'esame, comporre i gruppi di lavoro sin dall'inizio del corso, affinché le persone lavorino insieme già dall'inizio 
per le attività dei laboratorio.

[Back to index](0-Indice.md) |
[Previous Chapter](6-Testing.md)
