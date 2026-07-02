package pkg.c.data

enum MenuAction:
  case Dashboard
  case Profilo
  case VisualizzazioneProtocollazioni
  case PreseInCarico
  case Protocollo
  case Archiviazione
  case Statistiche
  case Log
  case ControlloGestione
  case Registrazioni
  case AccountUtenti
  case Ruoli
  case Classifiche
  case Logout

case class MenuItem(label: String, action: MenuAction)

case class HomePageConfig(applicationTitle: String,
                           pageTitle: String,
                           role: Role,
                           menuItems: Seq[MenuItem])

object HomePageConfig:

  private val applicationTitle = "ProtoFlow"

  def forRole(role: Role): HomePageConfig =
    role match

      case Role.Viewer =>
        HomePageConfig(
          applicationTitle = applicationTitle,
          pageTitle = "Homepage Utente",
          role = Role.Viewer,
          menuItems = Seq(
            MenuItem("Dashboard", MenuAction.Dashboard),
            MenuItem("Profilo", MenuAction.Profilo),
            MenuItem("Visualizzazione Protocollazioni", MenuAction.VisualizzazioneProtocollazioni),
            MenuItem("Logout", MenuAction.Logout)
          )
        )

      case Role.Operator =>
        HomePageConfig(
          applicationTitle = applicationTitle,
          pageTitle = "Homepage Operatore",
          role = Role.Operator,
          menuItems = Seq(
            MenuItem("Dashboard", MenuAction.Dashboard),
            MenuItem("Profilo", MenuAction.Profilo),
            MenuItem("Prese in carico", MenuAction.PreseInCarico),
            MenuItem("Protocollazione", MenuAction.Protocollo),
            MenuItem("Archiviazione", MenuAction.Archiviazione),
            MenuItem("Logout", MenuAction.Logout)
          )
        )

      case Role.Admin =>
        HomePageConfig(
          applicationTitle = applicationTitle,
          pageTitle = "Homepage Amministratore",
          role = Role.Admin,
          menuItems = Seq(
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
        )