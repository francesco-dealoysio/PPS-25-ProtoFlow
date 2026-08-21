package pkg.b.logic

import java.nio.file.{Files, Paths}
import pkg.c.data.*
import pkg.c.data.FileSystem.*
import pkg.c.data.Properties.*
import pkg.d.util.Util.*
import pkg.d.util.IdGen
import Xml.*
import DummyData.*

object Init:

  def init(): Unit =

    // Creazione struttura per i dati nel folder corrente
    createDirectoryStructure

    // Creazione file di configurazione nel folder corrente
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = baseFolder + fs + "database"
    val documentsFolder = baseFolder + fs + "database" + fs + "documents"
    val logFolder = baseFolder + fs + "log"
    val idFolder = baseFolder + fs + "ids"
    val testFolder = baseFolder + fs + "test"
    createPropsFile(baseFolder + fs + "protoflow.properties", " ProtoFlow Configuration")
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "base.folder", baseFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder", databaseFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "documents.folder", documentsFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "log.folder", logFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "ids.folder", idFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "test.folder", testFolder)

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
    createFile(path + fs + "Meeting Iniziale", "")
    createFile(path + fs + "Product Backlog", "")

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

    // Creazione dati fittizi

    if (Files.notExists(Paths.get(inDatabaseFilePathName("accounts.xml"))))
      saveXML(inDatabaseFilePathName("accounts.xml"), accounts)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("roles.xml"))))
      saveXML(inDatabaseFilePathName("roles.xml"), roles)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("classifications.xml"))))
      saveXML(inDatabaseFilePathName("classifications.xml"), classifications)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("registrations.xml"))))
      saveXML(inDatabaseFilePathName("registrations.xml"), registrations)

    if (Files.notExists(Paths.get(inLogFilePathName("errors.xml"))))
      createEmptyXmlFile(inLogFilePathName("errors.xml"), "errors")

    if (Files.notExists(Paths.get(inDocumentsFilePathName("loaded.xml"))))
      createEmptyXmlFile(inDocumentsFilePathName("loaded.xml"), "loaded")

    val documentOperationsPath = inLogFilePathName("documentOperations.xml")
    val documentOperationsFile = Paths.get(documentOperationsPath)
    
    if Files.notExists(documentOperationsFile) || Files.size(documentOperationsFile) == 0 then
      createEmptyXmlFile(documentOperationsPath, "operations")

    if (Files.notExists(Paths.get(inLogFilePathName("accessLog.xml"))))
      createEmptyXmlFile(inLogFilePathName("accessLog.xml"), "accessLog")

    // init ids
    IdGen(inIdsFilePathName("errorlogId"))
    IdGen(inIdsFilePathName("accountId"))
    IdGen(inIdsFilePathName("roleId"))
    IdGen(inIdsFilePathName("classificationId"))
    IdGen(inIdsFilePathName("registrationId"))
    IdGen(inIdsFilePathName("loadedDocumentId"))

  @main def tryInit(): Unit =
    init()  