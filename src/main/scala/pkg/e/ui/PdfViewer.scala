package pkg.e.ui

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import pkg.d.util.Util.inPrintsFilePathName
import java.awt.{BorderLayout, Dimension}
import java.awt.image.BufferedImage
import javax.swing.*
import java.io.File

object PdfViewer:

  private def detectOrientation(width: Float, height: Float, rotation: Int): String = {
    val rotated = rotation % 180 != 0
    val w = if (rotated) height else width
    val h = if (rotated) width else height
    if (w > h) "Landscape" else "Portrait"
  }

  def viewPdf(pdfPathName: String): Unit =

    val pdfFile = File(pdfPathName)
    if !pdfFile.exists() || !pdfFile.isFile then
      println(s"Error: File '$pdfPathName' not found.")
      System.exit(1)

    try
      val document = PDDocument.load(pdfFile)
      val renderer = PDFRenderer(document)
      val totalPages = document.getNumberOfPages

      var pageWidth = 700f
      var pageHeight = 750f

      if totalPages > 0 then {
        val page = document.getPage(0)
        pageWidth = page.getMediaBox.getWidth
        pageHeight = page.getMediaBox.getHeight
        println("Width: " + pageWidth + ", Height: " + pageHeight)
        //page.getMediaBox
      }
/*
      val mb = page.getMediaBox
      val orientation = detectOrientation(mb.getWidth, mb.getHeight, page.getRotation)
*/
      // Swing components
      val frame = JFrame(s"PDF Viewer - $pdfPathName")
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

      val imageLabel = JLabel()
      imageLabel.setHorizontalAlignment(SwingConstants.CENTER)
      val scrollPane = JScrollPane(imageLabel)
      //scrollPane.setPreferredSize(Dimension(891, 595))
      scrollPane.setPreferredSize(Dimension(pageHeight.toInt, pageWidth.toInt))

      var currentPage = 0

      def renderPage(pageIndex: Int): Unit =
        if pageIndex >= 0 && pageIndex < totalPages then
          val image: BufferedImage = renderer.renderImageWithDPI(pageIndex, 100) // 150 DPI
          imageLabel.setIcon(ImageIcon(image))
          currentPage = pageIndex
          frame.setTitle(s"PDF Viewer - Page ${currentPage + 1} / $totalPages")
        else
          println("Page index out of range.")

      // Buttons
      //val firstButton = JButton("First")
      val firstButton = JButton("|<")
      firstButton.addActionListener(_ => renderPage(0))
      firstButton.setToolTipText("First page")

      //val prevButton = JButton("Previous")
      val prevButton = JButton("<")
      prevButton.addActionListener(_ => renderPage(currentPage - 1))
      prevButton.setToolTipText("Previous page")

      //val nextButton = JButton("Next")
      val nextButton = JButton(">")
      nextButton.addActionListener(_ => renderPage(currentPage + 1))
      nextButton.setToolTipText("Next page")

      //val lastButton = JButton("Last")
      val lastButton = JButton(">|")
      lastButton.addActionListener(_ => renderPage(totalPages - 1))
      lastButton.setToolTipText("Last page")

      val printButton = JButton("Print")
      printButton.addActionListener(_ => PdfPrinter.printPdf(pdfPathName))
      printButton.setToolTipText("Print file")

      val viewWithDefaultViewerButton = JButton("View")
      viewWithDefaultViewerButton.addActionListener(_ => PdfDefaultViewer.viewWithDefaultViewer(pdfPathName))
      viewWithDefaultViewerButton.setToolTipText("Open file with default application")

      val buttonPanel = JPanel()
      buttonPanel.add(firstButton)
      buttonPanel.add(prevButton)
      buttonPanel.add(nextButton)
      buttonPanel.add(lastButton)
      buttonPanel.add(printButton)
      buttonPanel.add(viewWithDefaultViewerButton)

      frame.getContentPane.add(scrollPane, BorderLayout.CENTER)
      frame.getContentPane.add(buttonPanel, BorderLayout.SOUTH)

      frame.pack()
      frame.setLocationRelativeTo(null)
      frame.setVisible(true)

      // Render first page
      renderPage(0)

      // Ensure document closes when window closes
      frame.addWindowListener(new java.awt.event.WindowAdapter:
        override def windowClosing(e: java.awt.event.WindowEvent): Unit =
          document.close()
      )

    catch
      case ex: Exception =>
        println(s"Error loading PDF: ${ex.getMessage}")
        ex.printStackTrace()

@main def tryPdfViewer: Unit =
  println("Test")
  PdfViewer.viewPdf(inPrintsFilePathName("Appo.pdf"))
