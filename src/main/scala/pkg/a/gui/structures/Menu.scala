package pkg.a.gui.structures

enum MenuAction:
  case Dashboard
  case Profilo
  case VisualizzazioneArchiviazioni

  case NuovaPresaInCarico
  case DocumentiDaProtocollare
  case DocumentiDaArchiviare
  case DocumentiArchiviati

  case Statistiche
  case Log
  case ControlloGestione
  case Registrazioni
  case AccountUtenti
  case Ruoli
  case Classifiche
  case GestioneAutorizzazioni

  case Logout

case class MenuItem(label: String, action: MenuAction)