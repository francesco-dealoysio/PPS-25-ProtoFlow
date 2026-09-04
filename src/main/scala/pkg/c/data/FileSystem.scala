package pkg.c.data

import pkg.d.util.Logger.logger

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

object FileSystem:

  def createDirectory(dirPath: String): Unit =
    val path = Paths.get(dirPath)
    try
      if (Files.notExists(path))
        Files.createDirectories(path) // crea anche le cartelle superiori mancanti
        println(s"Directory creata: ${path.toAbsolutePath}")
      else
        println(s"Directory già esistente: ${path.toAbsolutePath}")
    catch
      case e: IOException =>
        println(s"Errore in FileSystem.createDirectory")
        logger(e)

  def createFile(filePathName: String, content: String): Unit =
    val path = Paths.get(filePathName)
    try {
      
      if (Files.notExists(path.getParent))
        println(s"Pathname inesistente: ${path.getParent}")
        return

      if (Files.notExists(path))
        Files.write(path, content.getBytes(StandardCharsets.UTF_8))
        println(s"File creato: ${path.toAbsolutePath}")
      else
        println(s"File già esistente: ${path.toAbsolutePath}")
        
    } catch
      case e: IOException =>
        println(s"Errore in FileSystem.createFile")
        logger(e)

  def createDirectoryStructure(): Unit =
    val baseDir = getCurrentDirectory
    val structure = Seq(
      baseDir.resolve("protoflow/database"),
      baseDir.resolve("protoflow/database/documents"),
      baseDir.resolve("protoflow/ids"),
      baseDir.resolve("protoflow/log"),
      baseDir.resolve("protoflow/test"),
      baseDir.resolve("protoflow/prints")
    )

    structure.foreach { dir =>
      try
        if !Files.exists(dir) then
          Files.createDirectories(dir)
          println(s"Creata directory: $dir")
        else
          println(s"Directory già esistente: $dir")

      catch
        case ex: IOException =>
          System.err.println(s"Errore nella creazione di $dir: ${ex.getMessage}")
    }

  private def getCurrentDirectory: Path =
    try
      Paths.get(System.getProperty("user.dir")).toAbsolutePath.normalize()
    catch
      case ex: SecurityException =>
        throw new RuntimeException("Permesso negato per ottenere la directory corrente", ex)