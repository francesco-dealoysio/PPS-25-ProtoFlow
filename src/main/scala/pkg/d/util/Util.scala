package pkg.d.util

import pkg.c.data.Properties.getPropsFileProperty

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object Util:

  def cipher(text: String): String =

    require(text != null, "Input text cannot be null")

    val digest = MessageDigest.getInstance("SHA3-512")
    val digestBytes =
      digest.digest(text.getBytes(StandardCharsets.UTF_8))

    digestBytes.map(byte => f"${byte & 0xff}%02x").mkString

  def inDatabaseFilePathName(fileName: String): String =
    inFolderFilePathName("database", fileName)

  def inDocumentsFilePathName(fileName: String): String =
    inFolderFilePathName("documents", fileName)

  def inLogFilePathName(fileName: String): String =
    inFolderFilePathName("log", fileName)

  def inIdsFilePathName(fileName: String): String =
    inFolderFilePathName("ids", fileName)
    
  def inTestFilePathName(fileName: String): String =
    inFolderFilePathName("test", fileName)
    
  def inPrintsFilePathName(fileName: String): String =
    inFolderFilePathName("prints", fileName)

  private def inFolderFilePathName(folder: String, fileName: String): String =
    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    
    getPropsFileProperty(baseFolder + fs + "protoflow.properties", folder + ".folder") + fs + fileName