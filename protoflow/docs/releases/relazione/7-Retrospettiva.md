[Back to index](0-Indice.md) |
[Previous Chapter](6-Testing.md)
# 7. Retrospettiva

La Sprint Retrospective è, nel framework Scrum, l'incontro che lo Scrum Team tiene al termine di ogni Sprint con i seguenti scopi:

- esaminare l'andamento dello Sprint appena terminato in relazione a comportamenti delle persone, relazioni, processi e strumenti;
- elencare i principali elementi che sono andati bene;
- individuare eventuali miglioramenti alle modalità di lavoro da implementare nello Sprint successivo;
- redigere un piano per l'attuazione di tali miglioramenti.

## 7.1 Limiti del processo

Il gruppo non ha tenuto, per la maggior parte degli sprint, una sessione di Sprint Retrospective documentata in modo discorsivo: le sole eccezioni sono poche note isolate riportate nei Daily Scrum (ad esempio decisioni tecniche o rallentamenti puntuali). Questo limite del processo viene ripreso e discusso onestamente in questa sezione. In particolare:

- la disciplina di compilazione del Daily Scrum non è stata costante: negli Sprint 2, 3 e 5 diverse giornate risultano senza note compilate per uno o più membri, pur risultando lavoro effettivamente svolto (confermato dalla tabella riepilogativa dei task e dai file di implementazione prodotti);
- i ruoli di Scrum Master e Product Owner, previsti dal framework Scrum, non sono mai stati assegnati formalmente a una persona nei documenti di sprint;
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

[Back to index](0-Indice.md) |
[Previous Chapter](6-Testing.md)
