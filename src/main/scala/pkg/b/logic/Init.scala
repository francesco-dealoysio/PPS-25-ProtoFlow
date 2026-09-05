package pkg.b.logic

import pkg.b.logic.StartData.*
import pkg.c.data.FileSystem.*
import pkg.c.data.Properties.*
import pkg.c.data.Xml.*
import pkg.d.util.IdGen
import pkg.d.util.Util.*

import java.nio.file.{Files, Paths}

object Init:

  def init(): Unit =

    createDirectoryStructure()

    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    val databaseFolder = baseFolder + fs + "database"
    val documentsFolder = baseFolder + fs + "database" + fs + "documents"
    val logFolder = baseFolder + fs + "log"
    val idFolder = baseFolder + fs + "ids"
    val testFolder = baseFolder + fs + "test"
    val printFolder = baseFolder + fs + "prints"
    createPropsFile(baseFolder + fs + "protoflow.properties", " ProtoFlow Configuration")
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "base.folder", baseFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "database.folder", databaseFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "documents.folder", documentsFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "log.folder", logFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "ids.folder", idFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "test.folder", testFolder)
    setPropsFileProperty(baseFolder + fs + "protoflow.properties", "prints.folder", printFolder)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("accounts.xml"))))
      saveXML(inDatabaseFilePathName("accounts.xml"), accounts)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("roles.xml"))))
      saveXML(inDatabaseFilePathName("roles.xml"), roles)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("classifications.xml"))))
      saveXML(inDatabaseFilePathName("classifications.xml"), classifications)

    if (Files.notExists(Paths.get(inDatabaseFilePathName("registrations.xml"))))
      createEmptyXmlFile(inDatabaseFilePathName("registrations.xml"), "registrations")

    if (Files.notExists(Paths.get(inLogFilePathName("errors.xml"))))
      createEmptyXmlFile(inLogFilePathName("errors.xml"), "errors")

    if (Files.notExists(Paths.get(inDocumentsFilePathName("loaded.xml"))))
      createEmptyXmlFile(inDocumentsFilePathName("loaded.xml"), "loaded")

    if (Files.notExists(Paths.get(inLogFilePathName("documentOperations.xml"))))
      createEmptyXmlFile(inLogFilePathName("documentOperations.xml"), "operations")

    if (Files.notExists(Paths.get(inLogFilePathName("accessLog.xml"))))
      createEmptyXmlFile(inLogFilePathName("accessLog.xml"), "accessLog")

    IdGen.initialize(inIdsFilePathName("accessLogId"))
    IdGen.initialize(inIdsFilePathName("errorlogId"))
    IdGen.initialize(inIdsFilePathName("accountId"), 3)
    IdGen.initialize(inIdsFilePathName("roleId"), 3)
    IdGen.initialize(inIdsFilePathName("classificationId"), 8)
    IdGen.initialize(inIdsFilePathName("registrationId"))
    IdGen.initialize(inIdsFilePathName("loadedDocumentId"))
  
  @main def tryInit(): Unit =
    init()