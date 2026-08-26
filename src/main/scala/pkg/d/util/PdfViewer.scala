package pkg.d.util

import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer

import scalafx.scene.Scene
import scalafx.scene.control.ScrollPane
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.stage.Stage

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import javax.imageio.ImageIO

object PdfViewer:

  def open(file: File): Unit =
    val document = Loader.loadPDF(file)

    try
      val renderer = new PDFRenderer(document)

      val pages =
        (0 until document.getNumberOfPages).map: pageIndex =>
          val bufferedImage = renderer.renderImageWithDPI(pageIndex, 120)
          val output = new ByteArrayOutputStream()
          ImageIO.write(bufferedImage, "png", output)
          val image = new Image(new ByteArrayInputStream(output.toByteArray))
          new ImageView(image):
            preserveRatio = true
            fitWidth = 900
            smooth = true

      val pagesBox =
        new VBox:
          spacing = 15
          children = pages

      val scrollPane =
        new ScrollPane:
          content = pagesBox
          fitToWidth = true
          pannable = true

      val rootPane =
        new BorderPane:
          center = scrollPane

      new Stage:
        title = file.getName
        scene = new Scene(rootPane, 1000.0, 800.0)
      .show()      
          

    finally
      document.close()