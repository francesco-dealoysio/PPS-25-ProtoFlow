package pkg.e.ui

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import pkg.d.util.Util.inPrintsFilePathName
import pkg.e.ui.PdfVerifier.isPdf

import java.awt.{BorderLayout, Color, Dimension, Frame}
import java.awt.{Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import java.awt.event.*
import javax.swing.{JFrame, SwingUtilities}
import javax.swing.*
import java.io.File

object PdfViewer:

  def viewPdf(pdfPathName: String): Unit =

    if !isPdf(pdfPathName) then
      println(s"'${pdfPathName}' does not exist or is not a valid PDF file.")
      System.exit(1)

    val pdfFile = File(pdfPathName)

    try
      val document = PDDocument.load(pdfFile)
      val renderer = PDFRenderer(document)

      val totalPages = document.getNumberOfPages
      var currentPage = 0
      var pageWidth = 1150f
      var pageHeight = 750f

      val zoomDefault = 100f
      var zoomDPI = zoomDefault
      val zoomMin = 80f
      val zoomMax = 200f
      val zoomInc = 10f

      var firstTime = true
      var minWidth = Int.MaxValue
      var minHeight = Int.MaxValue

      var pageCounterLabel = JLabel()

      val frame = JFrame(s"PDF Viewer - $pdfPathName")
      frame.setTitle(pdfPathName)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

      val imageLabel = JLabel()
      imageLabel.setHorizontalAlignment(SwingConstants.CENTER)

      val scrollPane = JScrollPane(imageLabel)
      scrollPane.setPreferredSize(Dimension(pageWidth.toInt, pageHeight.toInt))

      def renderPage(pageIndex: Int): Unit =
        if pageIndex >= 0 && pageIndex < totalPages then
          val image: BufferedImage = renderer.renderImageWithDPI(pageIndex, zoomDPI)

          if firstTime then
            val margin = 25
            minWidth = image.getWidth + margin
            minHeight = image.getHeight + margin
            firstTime = false

          if image.getWidth > frame.getWidth || image.getHeight > frame.getHeight then
            frame.setExtendedState(Frame.MAXIMIZED_BOTH)

          imageLabel.setIcon(ImageIcon(image))

          currentPage = pageIndex
          pageCounterLabel.setText(s" ${currentPage + 1} of $totalPages ")

        else
          println("Page index out of range.")

      val firstButton = JButton("|<")
      firstButton.addActionListener( _ => renderPage(0))
      firstButton.setToolTipText("First page")

      val prevButton = JButton("<")
      prevButton.addActionListener( _ => renderPage(currentPage - 1))
      prevButton.setToolTipText("Previous page")

      //pageCounterLabel.addActionListener( _ => renderPage(currentPage - 1))
      pageCounterLabel.setToolTipText("Go to page number")

      val nextButton = JButton(">")
      nextButton.addActionListener( _ => renderPage(currentPage + 1))
      nextButton.setToolTipText("Next page")

      val lastButton = JButton(">|")
      lastButton.addActionListener( _ => renderPage(totalPages - 1))
      lastButton.setToolTipText("Last page")

      val printButton = JButton("Print")
      printButton.addActionListener( _ => PdfPrinter.printPdf(pdfPathName))
      printButton.setToolTipText("Print file")

      val viewWithDefaultViewerButton = JButton("View")
      viewWithDefaultViewerButton.addActionListener( _ => PdfDefaultViewer.viewWithDefaultViewer(pdfPathName))
      viewWithDefaultViewerButton.setToolTipText("Open file with default application")

      val zoomOutButton = JButton("-")
      zoomOutButton.addActionListener( _ =>
        if zoomDPI > zoomMin then
          zoomDPI = zoomDPI - zoomInc
          renderPage(currentPage)
      )
      zoomOutButton.setToolTipText("Zoom Out")

      val zoomDefaultButton = JButton("100%")
      zoomDefaultButton.addActionListener( _ =>
        zoomDPI = zoomDefault
        renderPage(currentPage)
      )
      zoomDefaultButton.setToolTipText("Zoom 100%")

      val zoomInButton = JButton("+")
      zoomInButton.addActionListener( _ =>
        if zoomDPI < zoomMax then
          zoomDPI = zoomDPI + zoomInc
          renderPage(currentPage)
      )
      zoomInButton.setToolTipText("Zoom In")

      val buttonPanel = JPanel()
      buttonPanel.add(firstButton)
      buttonPanel.add(prevButton)
      buttonPanel.add(pageCounterLabel)
      buttonPanel.add(nextButton)
      buttonPanel.add(lastButton)
      buttonPanel.add(printButton)
      buttonPanel.add(viewWithDefaultViewerButton)
      buttonPanel.add(zoomOutButton)
      buttonPanel.add(zoomDefaultButton)
      buttonPanel.add(zoomInButton)

      frame.getContentPane.add(scrollPane, BorderLayout.CENTER)
      frame.getContentPane.add(buttonPanel, BorderLayout.SOUTH)

      renderPage(currentPage)

      frame.pack()
      frame.setMinimumSize(Dimension(minWidth, minHeight))
      frame.setMinimumSize(frame.getSize())
      frame.setLocationRelativeTo(null)
      frame.setVisible(true)

      frame.addWindowListener(new java.awt.event.WindowAdapter:
        override def windowClosing(e: java.awt.event.WindowEvent): Unit =
          document.close()
      )

    catch
      case ex: Exception =>
        println(s"Error loading PDF: ${ex.getMessage}")
        ex.printStackTrace()

  // not used
  private def detectOrientation(width: Float, height: Float, rotation: Int): String = {
    val rotated = rotation % 180 != 0
    val w = if (rotated) height else width
    val h = if (rotated) width else height
    if (w > h) "Landscape" else "Portrait"
  }

  // not used
  private def resizeImage(original: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage =
    val resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
    val g2d: Graphics2D = resized.createGraphics()
    try
      // Enable high-quality scaling
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null)
    finally
      g2d.dispose()
    resized

@main def tryPdfViewer: Unit =
  PdfViewer.viewPdf(inPrintsFilePathName("Intro.pdf"))