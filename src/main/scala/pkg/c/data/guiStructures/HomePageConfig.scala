package pkg.c.data.guiStructures

import pkg.c.data.guiStructures.Role


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
                           roleDescription: String,
                           menuItems: Seq[MenuItem])

object HomePageConfig:

  private val applicationTitle = "ProtoFlow"

  private val strategies: Map[Role, HomePageStrategy] =
    Seq(
      ViewerHomePageStrategy,
      OperatorHomePageStrategy,
      AdminHomePageStrategy
    ).map(strategy => strategy.role -> strategy).toMap

  def forRole(role: Role): HomePageConfig =
    strategies(role).createConfig(applicationTitle)