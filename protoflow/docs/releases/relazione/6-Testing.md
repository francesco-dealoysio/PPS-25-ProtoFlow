[Back to index](0-Indice.md) |
[Previous Chapter](5-Implementazione.md) |
[Next Chapter](7-Retrospettiva.md)
# 6. Testing

Nell'ambito del progetto sono state adottate quattro tipologie di test:

- Unit test
- Test di regressione
- Test funzionali / di accettazione condotti a fine sprint
- Test di accettazione sul prodotto finito, tramite casi d'uso

## 6.1 Unit test

Effettuati tramite JUnit4 durante il coding, contestualmente all'implementazione di ciascun modulo: a copertura delle entità di dominio (package `pkg.b.logic`), dei servizi applicativi (`pkg.a.gui.services`), dei validator (`pkg.a.gui.validators`) e delle utility di supporto (`pkg.c.data`, `pkg.d.util`), per un totale di 39 classi di test.

## 6.2 Test di regressione

Tutte le classi di test sono raccolte nella suite `AllTestsSuite`, il cui scopo è verificare che le funzionalità già implementate continuino a funzionare correttamente dopo ogni nuova modifica al codice. La suite viene eseguita automaticamente ad ogni push sul branch `develop` tramite una GitHub Action dedicata, che inizializza l'ambiente ProtoFlow e lancia l'intera suite con `sbt test`, oltre che manualmente in locale dallo sviluppatore con lo stesso comando.

## 6.3 Test Funzionali / di Accettazione

La verifica del lavoro svolto non è stata riservata solo al termine dello sprint: via via che una funzionalità veniva implementata, veniva anche testata manualmente per controllarne la correttezza, prima ancora di arrivare alla Sprint Review. I test funzionali veri e propri, eseguiti al termine di ogni sprint allo scopo di verificare la correttezza delle implementazioni effettuate, sono stati invece condotti secondo quanto specificato nelle sezioni “Done definition” contenute nelle User Story.

<p style="text-align: justify;">
Al termine di ogni sprint è stata effettuata una verifica dello stato di completamento delle funzionalità previste
per lo sprint e descritte dalle User Story contenute nello Sprint Backlog e, per quelle realizzate, è stata accertata
la correttezza di esecuzione e la conformità a quanto previsto dai requisiti.
</p>

<p style="text-align: justify;">
In sostanza, è stata eseguita l'applicazione seguendo i percorsi dettati dai casi d'uso specificati nelle sezioni
Done Definition delle User Story, sezioni da considerare quali surrogato di test di accettazione realizzati ad hoc.
In questa fase è previsto, in realtà, un incontro tra Scrum Team e Stakeholder, in genere di circa 4 ore.

Dalla attività di revisione sono emersi di volta in volta per i diversi sprint i seguenti elementi:
- requisiti da inserire nello sprint successivo, in quanto non realizzati nello sprint corrente;
- difetti derivanti dai comportamenti anomali riscontrati durante la revisione e da correggere nello sprint successivo;
- richieste di modifica dei requisiti sorte osservando l'applicativo in esecuzione.
</p>

<p style="text-align: justify;">
Le richieste di varianti a volte, dopo l'analisi d'impatto sul progetto, hanno comportato la revisione e 
l'adeguamento sia dei requisiti (User Story) che del Product Backlog.
</p>

## 6.4 Test di accettazione

- **[Casi d'Uso](../../process/use_case_test/ "Apertura directory")**

In questa fase sono stati eseguiti i test di accettazione sul prodotto finito, tramite la redazione di Casi d'Uso relativi alle attività che l'utente finale deve essere in grado di svolgere attraverso l'applicazione: casi d'uso che ripercorrono l'intera fase di sviluppo, prodotti dagli sviluppatori e utilizzati sia da questi ultimi sia dagli utenti finali per verificare l'esistenza e la correttezza delle funzionalità previste. Anche in questa fase i componenti del team hanno indossato un doppio ruolo: quello di sviluppatore, che dimostra quanto realizzato, e quello di End User, che esamina in modo critico il comportamento e la correttezza di quanto prodotto.

[Back to index](0-Indice.md) |
[Previous Chapter](5-Implementazione.md) |
[Next Chapter](7-Retrospettiva.md)