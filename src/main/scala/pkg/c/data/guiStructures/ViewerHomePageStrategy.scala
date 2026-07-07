package pkg.c.data.guiStructures

object ViewerHomePageStrategy extends HomePageStrategy:

  override val role: Role =
    Role.Viewer

  override val pageTitle: String =
    "Homepage Utente"

  override val roleDescription: String =
    "Viewer"

  override val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Visualizzazione Protocollazioni", MenuAction.VisualizzazioneProtocollazioni),
      MenuItem("Logout", MenuAction.Logout)
    )


object OperatorHomePageStrategy extends HomePageStrategy:

  override val role: Role =
    Role.Operator

  override val pageTitle: String =
    "Homepage Operatore"

  override val roleDescription: String =
    "Operatore Protocollo"

  override val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Prese in carico", MenuAction.PreseInCarico),
      MenuItem("Protocollazione", MenuAction.Protocollo),
      MenuItem("Archiviazione", MenuAction.Archiviazione),
      MenuItem("Logout", MenuAction.Logout)
    )


object AdminHomePageStrategy extends HomePageStrategy:

  override val role: Role =
    Role.Admin

  override val pageTitle: String =
    "Homepage Amministratore"

  override val roleDescription: String =
    "Amministratore"

  override val menuItems: Seq[MenuItem] =
    Seq(
      MenuItem("Dashboard", MenuAction.Dashboard),
      MenuItem("Profilo", MenuAction.Profilo),
      MenuItem("Statistiche", MenuAction.Statistiche),
      MenuItem("Log", MenuAction.Log),
      MenuItem("Controllo Gestione", MenuAction.ControlloGestione),
      MenuItem("Registrazioni", MenuAction.Registrazioni),
      MenuItem("Account Utenti", MenuAction.AccountUtenti),
      MenuItem("Ruoli", MenuAction.Ruoli),
      MenuItem("Classifiche", MenuAction.Classifiche),
      MenuItem("Logout", MenuAction.Logout)
    )