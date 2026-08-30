package pkg.e.ui.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import pkg.d.util.Util.inPrintsFilePathName
import pkg.e.ui.pdf.PdfVerifier.isPdf
import pkg.e.ui.pdf.{PdfDefaultViewer, PdfPrinter}
import java.awt.event.*
import java.awt.image.BufferedImage
import java.awt.*
import java.io.File
import javax.swing.*
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
      val zoomMin = 60f
      val zoomMax = 200f
      val zoomInc = 10f

      var firstTime = true
      var minWidth = Int.MaxValue
      var minHeight = Int.MaxValue

      val buttonPanel = JPanel()

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

      def first(): Unit = renderPage(0)

      def prev(): Unit = renderPage(currentPage - 1)

      def next(): Unit = renderPage(currentPage + 1)

      def last(): Unit = renderPage(totalPages - 1)

      def print(): Unit = { val printFuture: Future[Unit] = Future(PdfPrinter.printPdf(pdfPathName)) }

      def view(): Unit = PdfDefaultViewer.viewWithDefaultViewer(pdfPathName)

      def zoomOut(): Unit =
        if zoomDPI > zoomMin then
          zoomDPI = zoomDPI - zoomInc
          renderPage(currentPage)

      def zoomNormal(): Unit =
        zoomDPI = zoomDefault
        renderPage(currentPage)

      def zoomIn(): Unit =
        if zoomDPI < zoomMax then
          zoomDPI = zoomDPI + zoomInc
          renderPage(currentPage)

      def makeButton(label: String, tip: String, panel: JPanel, action: () => Unit): JButton =
        val button: JButton = new JButton(label)
        button.setToolTipText(tip)
        button.addActionListener( _ =>
          action()
          frame.requestFocusInWindow()
        )
        panel.add(button)
        button

      val firstButton = makeButton("|<", "First page", buttonPanel, first)

      val prevButton = makeButton("<", "Previous page", buttonPanel, prev)

      buttonPanel.add(pageCounterLabel)

      val nextButton = makeButton(">", "Next page", buttonPanel, next)

      val lastButton = makeButton(">|", "Last page", buttonPanel, last)

      val printButton = makeButton("Print", "Print file", buttonPanel, print)

      val viewButton = makeButton("View", "Open file with default application", buttonPanel, view)

      val zoomOutButton = makeButton("-","Zoom Out", buttonPanel, zoomOut)

      val zoomNormalButton = makeButton("100%","Zoom 100%", buttonPanel, zoomNormal)

      val zoomInButton = makeButton("+", "Zoom In", buttonPanel, zoomIn)

      frame.getContentPane.add(scrollPane, BorderLayout.CENTER)
      frame.getContentPane.add(buttonPanel, BorderLayout.SOUTH)

      frame.setFocusable(true)
      frame.requestFocusInWindow()
      frame.addKeyListener(new KeyListener:
        override def keyPressed(e: KeyEvent): Unit =
          println(s"Key Pressed: ${KeyEvent.getKeyText(e.getKeyCode)}")
          e.getKeyCode match
            case KeyEvent.VK_PLUS | KeyEvent.VK_ADD => zoomIn()
            case KeyEvent.VK_MINUS | KeyEvent.VK_SUBTRACT => zoomOut()
            case _ => ()

        override def keyReleased(e: KeyEvent): Unit = ()
        override def keyTyped(e: KeyEvent): Unit = ()
      )

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

@main def tryPdfViewer: Unit =
  //PdfViewer.viewPdf(inPrintsFilePathName("Intro.pdf"))
  val fileChooser = new JFileChooser()
  fileChooser.setCurrentDirectory(File(inPrintsFilePathName("")))
  fileChooser.setDialogTitle("Select a PDF file")
  if fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION then
    println("No file selected.")
  else
    PdfViewer.viewPdf(fileChooser.getSelectedFile.getAbsolutePath)