can(admin, profilo).
can(admin, statistiche).
can(admin, log).
can(admin, controllo_gestione).
can(admin, registrazioni).
can(admin, account_utenti).
can(admin, ruoli).
can(admin, classifiche).
can(admin, gestione_autorizzazioni).

can(oper, profilo).
can(oper, nuova_presa_in_carico).
can(oper, documenti_da_protocollare).
can(oper, documenti_da_archiviare).
can(oper, documenti_archiviati).

can(viewer, profilo).
can(viewer, visualizzazione_archiviazioni).

authorized(Role, Action) :- can(Role, Action).

permitted_actions(Role, Actions) :- findall(Action, can(Role, Action), Actions).

can_delete_role(Role) :- Role \= admin.

can_delete_account(Role, _) :- Role \= admin.
can_delete_account(admin, AdminCount) :- AdminCount > 1.
