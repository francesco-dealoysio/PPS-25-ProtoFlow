[Back to index](0-Indice.md) |
[Previous Chapter](5-Implementazione.md) |
[Next Chapter](7-Retrospettiva.md)
# 6. Testing

Nell'ambito del progetto sono state adottate quattro tipologie di test:

- Unit test
- Test di regressione
- Test funzionali / di accettazione condotti a fine sprint
- Test di accettazione sul prodotto finito, tramite casi d'uso

Unit Test: effettuati tramite JUnit4, a copertura delle entità di dominio (package `pkg.b.logic`), dei servizi applicativi (`pkg.a.gui.services`), dei validator (`pkg.a.gui.validators`) e delle utility di supporto (`pkg.c.data`, `pkg.d.util`), per un totale di 39 classi di test.

Test di regressione: tutte le classi di test sono raccolte nella suite `AllTestsSuite`, eseguita automaticamente ad ogni push sul branch `develop` tramite una GitHub Action dedicata, che inizializza l'ambiente ProtoFlow e lancia l'intera suite con `sbt test`.

Test Funzionali / Accettazione
I test funzionali eseguiti al termine di ogni sprint allo scopo di verificare la correttezza delle implementazioni effettuate sono stati condotti secondo quanto specificato nelle sezioni “Done definition” contenute nelle User Story.

Redazione di Casi d’Uso relativi alle attività che l’utente finale deve essere in grado di 
svolgere attraverso l’applicazione. I Casi d’uso sono utilizzati dagli sviluppatori e dagli utenti finali
per verificare l’esistenza e correttezza delle funzionalità previste.

<p style="text-align: justify;">
Al termine di ogni sprint è stata effettuata una verifica dello stato di completamento delle funzionalità previste
per lo sprint e descritte dalle User Story contenute nello Sprint Backlog e, per quelle realizzate, è stata accertata
la correttezza di esecuzione e la conformità a quanto previsto dai requisiti.
</p>

<p style="text-align: justify;">
In sostanza, è stata eseguita l'applicazione seguendo i percorsi dettati dai casi d'uso specificati nelle sezioni
Done Definition delle User Story, sezioni da considerare quali surrogato di test di accettazione realizzati ad hoc.
In questa fase, essendo in realta previsto un incontro, in genere di circa 4 ore, tra Scrum Team e Stakeholder, i
componenti del team hanno indossato sia il cappello di sviluppatore, che dimostra quanto realizzato, che quello di
Product Owner e di End User, che esaminano in modo critico il comportamento e la correttezza di quanto prodotto.

Dalla attività di revisione sono emersi di volta in volta per i diversi sprint i seguenti elementi:
- requisiti da inserire nello sprint successivo, in quanto non realizzati nello sprint corrente;
- difetti derivanti dai comportamenti anomali riscontrati durante la revisione e da correggere nello sprint successivo;
- richieste di modifica dei requisiti sorte osservando l'applicativo in esecuzione.
</p>

<p style="text-align: justify;">
Le richieste di varianti a volte, dopo l'analisi d'impatto sul progetto, hanno comportato la revisione e 
l'adeguamento sia dei requisiti (User Story) che del Product Backlog.
</p>

[Back to index](0-Indice.md) |
[Previous Chapter](5-Implementazione.md) |
[Next Chapter](7-Retrospettiva.md)