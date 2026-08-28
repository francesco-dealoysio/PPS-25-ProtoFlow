package pkg.e.ui

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.{ImageType, PDFRenderer}
import pkg.d.util.Util.inPrintsFilePathName

import java.awt.{BorderLayout, Cursor, Dimension, Graphics, Point}
import java.awt.event.{MouseAdapter, MouseEvent, MouseWheelEvent}
import javax.swing.*
import java.awt.image.BufferedImage
import scala.util.Using

object _PdfViewerApp:

  @main def tryPdfViewerApp(): Unit =
    val filePath = inPrintsFilePathName("Intro.pdf") // Change to your PDF path

    Using(PDDocument.load(java.io.File(filePath))) { document =>
      val renderer = PDFRenderer(document)
      val totalPages = document.getNumberOfPages

      var currentPage = 0
      var zoom = 1.0
      val minZoom = 0.25
      val maxZoom = 5.0

      var cachedImage: Option[BufferedImage] = None

      val pageLabel = new JLabel(s"Page ${currentPage + 1} / $totalPages")

      // Panel to render PDF page
      val pdfPanel = new JPanel(true):
        override def paintComponent(g: Graphics): Unit =
          super.paintComponent(g)
          if cachedImage.isEmpty then
            cachedImage = Some(renderer.renderImage(currentPage, zoom.toFloat, ImageType.RGB))
          val image = cachedImage.get
          g.drawImage(image, 0, 0, null)
          setPreferredSize(new Dimension(image.getWidth, image.getHeight))
          pageLabel.setText(s"Page ${currentPage + 1} / $totalPages")

      val scrollPane = new JScrollPane(pdfPanel)

      // Navigation and zoom actions
      def goPrev(): Unit =
        if currentPage > 0 then
          currentPage -= 1
          cachedImage = None
          pdfPanel.revalidate()
          pdfPanel.repaint()

      def goNext(): Unit =
        if currentPage < totalPages - 1 then
          currentPage += 1
          cachedImage = None
          pdfPanel.revalidate()
          pdfPanel.repaint()

      def zoomIn(): Unit =
        if zoom < maxZoom then
          zoom += 0.25
          cachedImage = None
          pdfPanel.revalidate()
          pdfPanel.repaint()

      def zoomOut(): Unit =
        if zoom > minZoom then
          zoom -= 0.25
          cachedImage = None
          pdfPanel.revalidate()
          pdfPanel.repaint()

      def fitToWidth(): Unit =
        val viewportWidth = scrollPane.getViewport.getWidth
        val pageImage = renderer.renderImage(currentPage, 1.0f, ImageType.RGB)
        zoom = viewportWidth.toDouble / pageImage.getWidth
        cachedImage = None
        pdfPanel.revalidate()
        pdfPanel.repaint()

      // Buttons
      val prevButton = new JButton("Prev"); prevButton.addActionListener(_ => goPrev())
      val nextButton = new JButton("Next"); nextButton.addActionListener(_ => goNext())
      val zoomInButton = new JButton("Zoom In"); zoomInButton.addActionListener(_ => zoomIn())
      val zoomOutButton = new JButton("Zoom Out"); zoomOutButton.addActionListener(_ => zoomOut())
      val fitWidthButton = new JButton("Fit Width"); fitWidthButton.addActionListener(_ => fitToWidth())

      // Toolbar
      val toolbar = new JPanel()
      toolbar.add(prevButton)
      toolbar.add(nextButton)
      toolbar.add(zoomInButton)
      toolbar.add(zoomOutButton)
      toolbar.add(fitWidthButton)
      toolbar.add(pageLabel)

      // Mouse wheel zoom
      pdfPanel.addMouseWheelListener((e: MouseWheelEvent) =>
        if e.isControlDown then
          if e.getWheelRotation < 0 then zoomIn() else zoomOut()
      )

      // Click-and-drag panning
      var dragStart: Option[Point] = None
      pdfPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
      pdfPanel.addMouseListener(new MouseAdapter:
        override def mousePressed(e: MouseEvent): Unit =
          dragStart = Some(e.getPoint)
        override def mouseReleased(e: MouseEvent): Unit =
          dragStart = None
      )
      pdfPanel.addMouseMotionListener(new MouseAdapter:
        override def mouseDragged(e: MouseEvent): Unit =
          dragStart.foreach { start =>
            val vp = scrollPane.getViewport
            val viewPos = vp.getViewPosition
            val dx = start.x - e.getX
            val dy = start.y - e.getY
            viewPos.translate(dx, dy)
            pdfPanel.scrollRectToVisible(new java.awt.Rectangle(viewPos, vp.getSize))
          }
      )

      // Frame setup
      val frame = new JFrame(s"PDF Viewer - $filePath")
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setLayout(new BorderLayout())
      frame.add(toolbar, BorderLayout.NORTH)
      frame.add(scrollPane, BorderLayout.CENTER)
      frame.setSize(1000, 800)
      frame.setLocationRelativeTo(null)
      frame.setVisible(true)

    }.recover {
      case ex: Exception =>
        println(s"Error loading PDF: ${ex.getMessage}")
    }
