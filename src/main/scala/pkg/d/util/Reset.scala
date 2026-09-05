package pkg.d.util

import pkg.d.util.Logger.logger
import pkg.d.util.Util._
import java.io.IOException
import java.nio.file.{DirectoryStream, Files, Path, Paths}
import scala.jdk.CollectionConverters.IterableHasAsScala

object Reset:

  def reset(): Unit =

    // customRules ?? se viene eliminato, chi lo ricrea?

    val files = Seq(
      // database
      inDatabaseFilePathName("accounts.xml"),
      inDatabaseFilePathName("roles.xml"),
      inDatabaseFilePathName("classifications.xml"),
      inDatabaseFilePathName("registrations.xml"),
      // database/documents
      inDocumentsFilePathName("loaded.xml"),
      inDocumentsFilePathName("registered.xml"),
      inDocumentsFilePathName("archived.xml"),
      // log
      inLogFilePathName("errors.xml"),
      inLogFilePathName("documentOperations.xml"),
      inLogFilePathName("accessLog.xml"),
      // ids
      inIdsFilePathName("accessLogId"),
      inIdsFilePathName("errorlogId"),
      inIdsFilePathName("accountId"),
      inIdsFilePathName("roleId"),
      inIdsFilePathName("classificationId"),
      //inIdsFilePathName("registrationId"),
      inIdsFilePathName("loadedDocumentId"),
      inIdsFilePathName("documentOperationLogId")
    )

    try
      files.foreach { filePathName =>
        val file = Paths.get(filePathName)

        if Files.exists(file) then {
          //println(s"File: ${file.getFileName} exists!")
          Files.delete(file)
          println(s"File: $file deleted!")
        } else
          println(s"File: $file doesn't exist!")
      }
    catch
      case ex: IOException =>
        System.err.println(s"Errore in Reset.scala!")
        logger(ex)

    deleteFilesInDirectory(inPrintsFilePathName(""))
    deleteFilesInDirectory(inTestFilePathName(""))

    def deleteFilesInDirectory(directoryPath: String): Unit =
      val dir: Path = Paths.get(directoryPath)

      if !Files.exists(dir) then
        //println(s"Directory does not exist: $directoryPath")
        return
      if !Files.isDirectory(dir) then
        println(s"Path is not a directory: $directoryPath")
        return

      try
        val stream: DirectoryStream[Path] = Files.newDirectoryStream(dir)
        try
          for entry <- stream.asScala do
            if Files.isRegularFile(entry) then
              try
                Files.delete(entry)
                println(s"Deleted file: $entry")
              catch
                case ex: IOException =>
                  println(s"Failed to delete file $entry: ${ex.getMessage}")
        finally
          stream.close()
      catch
        case ex: IOException =>
          println(s"Error accessing directory: ${ex.getMessage}")

@main def tryReset(): Unit =
  println("-".repeat(17) + "\nTest Reset.scala:\n" + "-".repeat(17))
  Reset.reset()

