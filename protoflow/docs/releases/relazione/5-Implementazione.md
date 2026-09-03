[Back to index](0-Indice.md) |
[Previous Chapter](4-Design_di_dettaglio.md) |
[Next Chapter](6-Testing.md)
# 5. Implementazione

## 5.1 Sezione descrittiva studente DE ALOYSIO Francesco
<p style="text-align: justify;">
Nell’ambito della architettura di alto livello scelta, articolata nei seguenti livelli:
</p>

- front end (pkg.a.gui): presentazione ed interazione degli utenti
- logica applicativa (pkg.b.logic): interfaccia tra front end e back end
- back end (pck.d.data): gestione dei dati persistenti

<p style="text-align: justify;">
i moduli applicativi da me sviluppati in autonomia afferiscono a tutte e tre le suddette aree, ma la maggiorparte del software si
colloca nel livello di logica applicativa ed in quello dei dati, oltre ad alcune utility contenute nel package pkg.d.util.
</p>

<p style="text-align: justify;">
Inoltre, nel package pkg.e.ui, a scopo meramente didattico, ho sviluppato una versione alternativa delle gui, non utilizzata
nell’ambito dell’applicativo, basata su una gerarchia di traits (diagramma delle classi allegato).

Nel dettaglio di seguito il riepilogo delle attività effettuate:
</p>

- Struttura a directory ‘protoflow’ deputata alla memorizzazione dei file prodotti ed utilizzati dall’applicativo (database, ids, log, ecc…);
- Meccanismo di gestione della configurazione tramite il file protoflow.properties;
- Configurazione del meccanismo di utilizzo delle risorse (src.main.resources e src.test.resources);
- Sviluppo dei moduli applicativi;
- Realizzazione delle classi di Test dei moduli.

Elenco dei moduli sviluppati:

<table>
  <tr style="font-weight: normal; font-size: 22px;"><th colspan="3">Moduli sviluppati in src.main</th></tr>
  <tr style="text-align: center;"><td>Package</td><td>Modulo</td><td>Descrizione</td></tr>
  <tr><td rowspan="10">pck.b.logic</td><td>Entity.scala</td><td>Trait ... inserire la descrizione relativa al modulo .......................................</td></tr>
  <tr><td>Account.scala</td><td></td></tr>
  <tr><td>Role.scala</td><td></td></tr>
  <tr><td>Classification.scala</td><td></td></tr>
  <tr><td>Registration.scala</td><td></td></tr>
  <tr><td>DocumentLog.scala</td><td></td></tr>
  <tr><td>ErrorLog.scala</td><td></td></tr>
  <tr><td>LoadedDocument.scala</td><td></td></tr>
  <tr><td>Init.scala</td><td></td></tr>
  <tr><td>StartData.scala</td><td></td></tr>
  <tr><td rowspan="5">pck.b.logic.pdf</td><td>PdfCreator.scala</td><td></td></tr>
  <tr><td>PdfVerifier.scala</td><td></td></tr>
  <tr><td>PdfDefaultViewer.scala</td><td></td></tr>
  <tr><td>PdfPrinter.scala</td><td></td></tr>
  <tr><td>PdfViewer.scala</td><td></td></tr>
  <tr><td rowspan="3">pck.c.data</td><td>FileSystem.scala</td><td></td></tr>
  <tr><td>Properties.scala</td><td></td></tr>
  <tr><td>Xml.scala</td><td></td></tr>
  <tr><td rowspan="4">pck.d.util</td><td>Logger.scala</td><td></td></tr>
  <tr><td>IdGen.scala</td><td></td></tr>
  <tr><td>Util.scala</td><td></td></tr>
  <tr><td>Filters.scala</td><td></td></tr>
  <tr><td rowspan="4">pck.e.ui.traits</td><td>GUI.scala</td><td></td></tr>
  <tr><td>Homepage.scala</td><td></td></tr>
  <tr><td>Management.scala</td><td></td></tr>
  <tr><td>Operation.scala</td><td></td></tr>
  <tr><td rowspan="3">pck.e.ui.homepages</td><td>AdminHomepage.scala</td><td></td></tr>
  <tr><td>OperatorHomepage.scala</td><td></td></tr>
  <tr><td>ViewerHomepage.scala</td><td></td></tr>
  <tr><td rowspan="1">pck.e.ui.management</td><td>Account.management.scala</td><td></td></tr>
  <tr><td rowspan="3">pck.e.ui.operations</td><td>Login.scala</td><td></td></tr>
  <tr><td>AccountAdd.scala</td><td></td></tr>
  <tr><td>DocumentLoad.scala</td><td></td></tr>
  <tr><th colspan="3">Moduli sviluppati in src.test</th></tr>
  <tr style="text-align: center;"><td>Package</td><td>Modulo</td><td>Descrizione</td></tr>
  <tr><td rowspan="7">pck.b.logic</td><td>AccountTest.scala</td><td></td></tr>
  <tr><td>RoleTest.scala</td><td></td></tr>
  <tr><td>ClassificationTest.scala</td><td></td></tr>
  <tr><td>RegistrationTest.scala</td><td></td></tr>
  <tr><td>DocumentLogTest.scala</td><td></td></tr>
  <tr><td>ErrorLogTest.scala</td><td></td></tr>
  <tr><td>LoadedDocumentTest.scala</td><td></td></tr>
  <tr><td rowspan="2">pck.c.data</td><td>PropertiesTest.scala</td><td></td></tr>
  <tr><td>XmlTest.scala</td><td></td></tr>
  <tr><td rowspan="1">pck.d.util</td><td>FiltersTest.scala</td><td></td></tr>
  <tr><td rowspan="1">pck</td><td>AllTestsSuite.scala</td><td></td></tr>
</table>

## 5.2 Sezione descrittiva studente PISU Roberto


## 5.3 Sezione descrittiva studente TESTA Thomas


[Back to index](0-Indice.md) |
[Previous Chapter](4-Design_di_dettaglio.md) |
[Next Chapter](6-Testing.md)



