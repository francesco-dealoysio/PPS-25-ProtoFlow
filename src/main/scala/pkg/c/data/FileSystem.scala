package pkg.c.data

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
        System.err.println(s"Errore in createDirectory: ${e.getMessage}")

  def createFile(filePathName: String, content: String): Unit =
    val path = Paths.get(filePathName)
    try
      if (Files.notExists(path))
        Files.write(path, content.getBytes(StandardCharsets.UTF_8))
        println(s"File creato: ${path.toAbsolutePath}")
      else
        println(s"File già esistente: ${path.toAbsolutePath}")
    catch
      case e: IOException =>
        println(s"Errore in createFile: ${e.getMessage}")

  def getCurrentDirectory: Path =
    try
      Paths.get(System.getProperty("user.dir")).toAbsolutePath.normalize()
    catch
      case ex: SecurityException =>
        throw new RuntimeException("Permesso negato per ottenere la directory corrente", ex)
  
  def createDirectoryStructure: Unit =
    val baseDir = getCurrentDirectory
    val structure = Seq(
      baseDir.resolve("protoflow/database"),
      baseDir.resolve("protoflow/database/registrazioni"),
      baseDir.resolve("protoflow/log"),
      baseDir.resolve("protoflow/archivio/presidenza"),
      baseDir.resolve("protoflow/archivio/segreteria"),
      baseDir.resolve("protoflow/archivio/amministrazione"),
      baseDir.resolve("protoflow/archivio/personale"),
    )
  
    structure.foreach { dir =>
      try
        if (!Files.exists(dir)) {
          Files.createDirectories(dir)
          println(s"Creata directory: $dir")
        } else {
          println(s"Directory già esistente: $dir")
        }
      catch
        case ex: IOException =>
          System.err.println(s"Errore nella creazione di $dir: ${ex.getMessage}")
    }
  
  @main def tryFileSystem: Unit =

    println("")
