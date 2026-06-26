package pkg.b.logic

import java.nio.file.{Files, Paths}
import pkg.c.data.*

object Init

  def init: Unit =

    // File di configurazione
    val fs = java.io.File.separator
    val propsFile = "protoflow.properties"
    if (Files.notExists(Paths.get(propsFile)))
      createPropsFile(propsFile, " ProtoFlow Configuration")
      setPropsFileProperty(propsFile, "base.folder", "protoflow")
      setPropsFileProperty(propsFile, "database.folder", "protoflow/database")

    // Struttura per lo sviluppo
    createDirectory("protoflow" + fs + "archivio" + fs + "presidenza")
    createDirectory("protoflow" + fs + "archivio" + fs + "segreteria")
    createDirectory("protoflow" + fs + "archivio" + fs + "amministrazione")
    createDirectory("protoflow" + fs + "archivio" + fs + "personale")
    createDirectory("protoflow" + fs + "database")
    createDirectory("protoflow" + fs + "log")

    // Struttura per la documentazione
    createDirectory("protoflow" + fs + "documentazione")
    createDirectory("protoflow" + fs + "documentazione" + fs + "releases")
    createDirectory("protoflow" + fs + "documentazione" + fs + "releases" + fs + "jar")
    createDirectory("protoflow" + fs + "documentazione" + fs + "releases" + fs + "relazione")
    createDirectory("protoflow" + fs + "documentazione" + fs + "sources")
    createDirectory("protoflow" + fs + "documentazione" + fs + "requisiti e analisi")
    createDirectory("protoflow" + fs + "documentazione" + fs + "requisiti e analisi" + fs + "UserStories")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 0")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 1")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 2")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 3")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 4")
    createDirectory("protoflow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 5")

    // Requisiti e analisi
    var path = "protoFlow" + fs + "documentazione" + fs + "requisiti e analisi"
    createFile(path + fs + "RBS", "")
    createFile(path + fs + "WBS", "")

    path = "protoFlow" + fs + "documentazione" + fs + "requisiti e analisi" + fs + "UserStories"
    createFile(path + fs + "US-01 (RF) - Portale", "")

    // Processo
    path = "protoFlow" + fs + "documentazione" + fs + "process"
    createFile(path + fs + "Meeting_iniziale", "")
    createFile(path + fs + "Product_Backlog", "")

    path = "protoFlow" + fs + "documentazione" + fs + "process" + fs + "sprint" + fs + "sprint 0"
    createFile(path + fs + "1 Sprint Planning Meeting", "")
    createFile(path + fs + "2 Sprint Backlog", "")
    createFile(path + fs + "3 User Story Tasks", "")
    createFile(path + fs + "4 Daily Scrum", "")
    createFile(path + fs + "5 Sprint Review", "")
    createFile(path + fs + "6 Sprint Retrospective", "")

    // Template Relazione
    path = "protoFlow" + fs + "documentazione" + fs + "releases" + fs + "relazione"
    val content =
      "   Relazione di progetto\n" +
      "\n1. Processo di sviluppo" +
      "\n2. Requirement specification" +
      "\n   1) requisiti di business" +
      "\n   2) modello di dominio" +
      "\n   3) requisiti funzionali" +
      "\n      3.1) utente" +
      "\n      3.2) sistema" +
      "\n   4) requisiti non funzionali" +
      "\n   5) requisiti di implementazione" +
      "\n3. Design architetturale" +
      "\n4. Design di dettaglio" +
      "\n5. Implementazione" +
      "\n      5.1) Sezione descrittiva studente 1" +
      "\n      5.2) Sezione descrittiva studente 2" +
      "\n      5.3) Sezione descrittiva studente 3" +
      "\n6. Testing" +
      "\n7. Retrospettiva"

    createFile(path + fs + "0. Indice.MD", content)
    createFile(path + fs + "1. Processo di sviluppo.MD", "")
    createFile(path + fs + "2. Requirement specification.MD", "")
    createFile(path + fs + "3. Design architetturale.MD", "")
    createFile(path + fs + "4. Design di dettaglio.MD", "")
    createFile(path + fs + "5. Implementazione.MD", "")
    createFile(path + fs + "6. Testing.MD", "")
    createFile(path + fs + "7. Retrospettiva.MD", "")

  @main def tryInit: Unit =
    init
