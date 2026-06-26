package pkg.c.data

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import java.io.IOException

object FileSystem

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

  @main def tryFileSystem: Unit =

    println("")
