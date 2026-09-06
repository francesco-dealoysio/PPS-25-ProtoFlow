package pkg.b.logic

import pkg.d.util.Util.cipher

import scala.xml.Elem

object StartData:

  private val passwd1 = cipher("topolino")
  private val passwd2 = cipher("tommy$123")
  private val passwd3 = cipher("robby$456")

  val accounts: Elem = <accounts>
    <record>
      <id>1</id>
      <surname>de aloysio</surname>
      <name>francesco</name>
      <email>francesco.dealoysio@studio.unibo.it</email>
      <phone>06/11111111</phone>
      <role>admin</role>
      <area>presidenza</area>
      <assignment>presidente</assignment>
      <username>frank</username>
      <password>{passwd1}</password>
    </record>
    <record>
      <id>2</id>
      <surname>testa</surname>
      <name>thomas</name>
      <email>thomas.testa@studio.unibo.it</email>
      <phone>0547/1111111</phone>
      <role>oper</role>
      <area>amministrazione</area>
      <assignment>tesoriere</assignment>
      <username>tommy</username>
      <password>{passwd2}</password>
    </record>
    <record>
      <id>3</id>
      <surname>pisu</surname>
      <name>roberto</name>
      <email>roberto.pisu@studio.unibo.it</email>
      <phone>0547/2222222</phone>
      <role>viewer</role>
      <area>personale</area>
      <assignment>capo ufficio</assignment>
      <username>robby</username>
      <password>{passwd3}</password>
    </record>
  </accounts>

  val roles: Elem = <roles>
    <record>
      <id>1</id>
      <role>admin</role>
      <name>Amministratore</name>
      <description>Attività di amministrazione del sistema</description>
    </record>
    <record>
      <id>2</id>
      <role>oper</role>
      <name>Operatore</name>
      <description>Attività di protocollazione</description>
    </record>
    <record>
      <id>3</id>
      <role>viewer</role>
      <name>Viewer</name>
      <description>Ricerca e visualizzazione nel cono di visibilità</description>
    </record>
  </roles>

  val classifications: Elem = <classifications>
    <record>
      <id>1</id>
      <classification>Presidenza</classification>
      <description>Direzione e organi istituzionali</description>
    </record>
    <record>
      <id>2</id>
      <classification>Segreteria</classification>
      <description>Protocollo e supporto operativo</description>
    </record>
    <record>
      <id>3</id>
      <classification>Amministrazione</classification>
      <description>Contabilità, bilancio e finanza</description>
    </record>
    <record>
      <id>4</id>
      <classification>Personale</classification>
      <description>Gestione risorse umane e paghe</description>
    </record>
    <record>
      <id>5</id>
      <classification>Materiali</classification>
      <description>Acquisti, magazzino e forniture</description>
    </record>
    <record>
      <id>6</id>
      <classification>Infrastrutture</classification>
      <description>Manutenzione sedi e logistica</description>
    </record>
    <record>
      <id>7</id>
      <classification>Addestramento</classification>
      <description>Formazione e corsi aggiornamento</description>
    </record>
    <record>
      <id>8</id>
      <classification>Informatica</classification>
      <description>Sistemi IT, reti e assistenza</description>
    </record>
  </classifications>