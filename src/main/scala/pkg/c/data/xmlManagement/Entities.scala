package pkg.c.data.xmlManagement

import pkg.d.util.Util.md5

import scala.xml.Elem

object Entities:

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
                    ) {
    def this() =
      this("", "", "", "", "", "", "", "", "", "")
  }

  case class Ruolo(
                    id: String = "",
                    ruolo: String = ""
                  ) {
    def this() =
      this("","")
  }

  case class Classifica(
                         id: String = "",
                         classifica: String = ""
                       ) {
    def this() =
      this("","")
  }

  case class Registrazione (
                              id: String = "",
                              cognome: String = "",
                              nome: String = "",
                              email: String = "",
                              telefono: String = "",
                              ruolo: String = "",
                              area: String = "",
                              incarico: String = "",
                              data: String = "",
                              stato: String = "",
                              esito: String = "",
                              motivazione: String = ""
                            ) {
    def this() =
      this("", "", "", "", "", "", "", "", "", "", "", "")
  }

object DummyData:

  private val passwd1 = md5("topolino")
  private val passwd2 = md5("tommy$123")
  private val passwd3 = md5("robby$456")

  val accounts: Elem = <accounts>
    <record>
      <id>1</id>
      <cognome>de aloysio</cognome>
      <nome>francesco</nome>
      <email>francesco.dealoysio@studio.unibo.it</email>
      <telefono>06/11111111</telefono>
      <ruolo>admin</ruolo>
      <area>presidenza</area>
      <incarico>presidente</incarico>
      <username>frank</username>
      <password>{md5("topolino")}</password>
    </record>
    <record>
      <id>2</id>
      <cognome>testa</cognome>
      <nome>thomas</nome>
      <email>thomas.testa@studio.unibo.it</email>
      <telefono>0547/1111111</telefono>
      <ruolo>oper</ruolo>
      <area>amministrazione</area>
      <incarico>tesoriere</incarico>
      <username>tommy</username>
      <password>{passwd2}</password>
    </record>
    <record>
      <id>3</id>
      <cognome>pisu</cognome>
      <nome>roberto</nome>
      <email>roberto.pisu@studio.unibo.it</email>
      <telefono>0547/2222222</telefono>
      <ruolo>viewer</ruolo>
      <area>personale</area>
      <incarico>capo ufficio</incarico>
      <username>robby</username>
      <password>{passwd3}</password>
    </record>
  </accounts>

  val ruoli: Elem = <ruoli>
    <record>
      <id>1</id>
      <ruolo>admin</ruolo>
    </record>
    <record>
      <id>2</id>
      <ruolo>oper</ruolo>
    </record>
    <record>
      <id>3</id>
      <ruolo>viewer</ruolo>
    </record>
  </ruoli>

  val classifiche: Elem = <classifiche>
    <record>
      <id>1</id>
      <classifica>presidenza</classifica>
    </record>
    <record>
      <id>2</id>
      <classifica>segreteria</classifica>
    </record>
    <record>
      <id>3</id>
      <classifica>amministrazione</classifica>
    </record>
    <record>
      <id>4</id>
      <classifica>personale</classifica>
    </record>
    <record>
      <id>5</id>
      <classifica>materiali</classifica>
    </record>
    <record>
      <id>6</id>
      <classifica>infrastrutture</classifica>
    </record>
    <record>
      <id>7</id>
      <classifica>addestramento</classifica>
    </record>
    <record>
      <id>8</id>
      <classifica>informatica</classifica>
    </record>
  </classifiche>

  val registrazioni: Elem = <registrazioni>
    <record>
      <id>1</id>
      <cognome>rossi</cognome>
      <nome>mario</nome>
      <email>mario.rossi@alice.it</email>
      <telefono>06/1234567</telefono>
      <ruolo>viewer</ruolo>
      <area>personale</area>
      <incarico>addetto</incarico>
      <data>06/07/2026</data>
      <stato>evadere</stato>
      <esito></esito>
      <motivazione></motivazione>
    </record>
  </registrazioni>
