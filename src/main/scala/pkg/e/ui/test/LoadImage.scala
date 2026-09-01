package pkg.e.ui.test

import java.awt.image.BufferedImage

object LoadImage:

  private def loadImage(imagePath: String): BufferedImage =
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

  @main def tryUtil(): Unit =
    print("\nTest loadImage:\n\t")
    val image = loadImage("img/message.jpg")
