% can(Role, Action)
% relates a role with an action it is allowed to perform.
% Role and Action are always ground atoms: this is a plain fact table,
% mirroring pkg.b.logic.Role.role and pkg.a.gui.structures.MenuAction.

can(admin, statistiche).
can(admin, log).
can(admin, controllo_gestione).
can(admin, registrazioni).
can(admin, account_utenti).
can(admin, ruoli).
can(admin, classifiche).

can(oper, nuova_presa_in_carico).
can(oper, documenti_da_protocollare).
can(oper, documenti_da_archiviare).
can(oper, documenti_archiviati).

can(viewer, visualizzazione_archiviazioni).

% authorized(Role, Action)
% true if Role is allowed to perform Action.
% Kept as a separate rule (rather than querying can/2 directly) so that
% future refinements (role hierarchies, contextual checks) only change
% this one predicate, not every call site.
authorized(Role, Action) :- can(Role, Action).

% permitted_actions(Role, Actions)
% Actions is the list of every action Role is authorized to perform,
% in the order the can/2 facts for Role were declared above.
permitted_actions(Role, Actions) :- findall(Action, can(Role, Action), Actions).

% can_delete_role(Role)
% the admin role is a system invariant: it can never be removed,
% regardless of how many accounts currently use it.
can_delete_role(Role) :- Role \= admin.

% can_delete_account(Role, AdminCount)
% AdminCount is the number of admin accounts currently in the system.
% any non-admin account can always be deleted; an admin account can be
% deleted only if it is not the last one (i.e. AdminCount > 1).
can_delete_account(Role, _) :- Role \= admin.
can_delete_account(admin, AdminCount) :- AdminCount > 1.
