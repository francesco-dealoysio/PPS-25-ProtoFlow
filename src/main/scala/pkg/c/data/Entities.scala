package pkg.c.data

trait Entity extends Product {}

case class Account(
                    id: String = "",
                    cognome: String = "",
                    nome: String = "",
                    email: String = "",
                    telefono: String = "",
                    ruolo: String = "",
                    area: String = "",
                    incarico: String = "",
                    username: String = "",
                    password: String = ""
                  ) extends Entity {
  def this() =
    this("", "", "", "", "", "", "", "", "", "")
}

case class Ruolo(
                  id: String = "",
                  ruolo: String = ""
                ) extends Entity {
  def this() =
    this("","")
}

case class Classifica(
                       id: String = "",
                       classifica: String = ""
                     ) extends Entity {
  def this() =
    this("","")
}
