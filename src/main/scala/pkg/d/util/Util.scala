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

  /** Compute MD5 hash of a given string and return it as a hex string */
  def md5(text: String): String =
    import java.security.MessageDigest
    import scala.util.Try

    require(text != null, "Input text cannot be null")

    // Get MD5 digest instance
    val md = MessageDigest.getInstance("MD5")

    // Compute digest as byte array
    val digestBytes = md.digest(text.getBytes("UTF-8"))

    // Convert bytes to hex string
    digestBytes.map("%02x".format(_)).mkString

  @main def tryUtil: Unit =

    // test loadImage
    print("\nTest loadImage:\n\t")
    val image = loadImage("img/message.jpg")

    // test md5
    println("\nTest md5:")
    println("\t" + md5(""))
    println("\t" + md5("topolino"))
