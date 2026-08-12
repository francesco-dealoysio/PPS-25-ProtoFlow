package pkg.d.util

import java.awt.image.BufferedImage

object Util:

  def loadImage(imagePath: String): BufferedImage =
    import java.io.IOException
    import javax.imageio.ImageIO

    val readStream = Option(getClass.getClassLoader.getResourceAsStream(imagePath))
    readStream match
      case Some(stream) =>
        try
          val image: BufferedImage = ImageIO.read(stream)
          if image != null then
            println(s"Image loaded successfully: ${image.getWidth}x${image.getHeight}")
            image
          else
            println("Failed to decode image (unsupported format or corrupted file).")
            null
        catch
          
          case e: IOException =>
            println(s"Error reading image: ${e.getMessage}")
            null
        finally {
          stream.close()
        }
      case _ =>
        println(s"Image not found in resources: $imagePath")
        null

  def cipher(text: String): String =
    import java.nio.charset.StandardCharsets
    import java.security.MessageDigest

    require(text != null, "Input text cannot be null")

    val digest = MessageDigest.getInstance("SHA3-512")
    val digestBytes =
      digest.digest(text.getBytes(StandardCharsets.UTF_8))

    digestBytes.map(byte => f"${byte & 0xff}%02x").mkString

  def inDatabaseFilePathName(fileName: String): String =
    inFolderFilePathName("database", fileName)

  def inDocumentsFilePathName(fileName: String) =
    inFolderFilePathName("documents", fileName)

  def inLogFilePathName(fileName: String) =
    inFolderFilePathName("log", fileName)

  def inIdsFilePathName(fileName: String) =
    inFolderFilePathName("ids", fileName)
    
  def inTestFilePathName(fileName: String) =
    inFolderFilePathName("test", fileName)

  def inFolderFilePathName(folder: String, fileName: String): String =
    import pkg.c.data.Xml.createEmptyXmlFile
    import pkg.c.data.Properties.getPropsFileProperty
    import java.nio.file.{Files, Paths}

    val fs = java.io.File.separator
    val baseFolder = System.getProperty("user.dir") + fs + "protoflow"
    getPropsFileProperty(baseFolder + fs + "protoflow.properties", folder + ".folder") + fs + fileName
/*
    val pathFileName = getPropsFileProperty(baseFolder + fs + "protoflow.properties", folder + ".folder") + fs + fileName
    if (Files.notExists(Paths.get(pathFileName)))
      createEmptyXmlFile(pathFileName, fileName.stripSuffix(".xml"))
    pathFileName
*/

  @main def tryUtil: Unit =
    import pkg.b.logic.ErrorLog.*
    import pkg.d.util.Util.*
    import pkg.d.util.Logger.*
    
    // test loadImage
    print("\nTest loadImage:\n\t")
    val image = loadImage("img/message.jpg")

    // test SHA3-512
    println("\nTest SHA3-512:")
    println("\t" + cipher(""))
    println("\t" + cipher("topolino"))


    // test inDatabaseFilePathName
    println("\nTest inDatabaseFilePathName")
    println("\tPath of " + "\"error.log\": \n\t" + inDatabaseFilePathName("error.log") + "\n")
    //println("\tPath of " + "\"protocols.xml\": \n\t" + inArchiveFilePathName("protocols.xml") + "\n")

    // test Logger
    def riskyFunction(): Unit =
      try
        throw new RuntimeException("Something went wrong!")
      catch
        case e: Exception =>
          logger(e, true)

    riskyFunction()