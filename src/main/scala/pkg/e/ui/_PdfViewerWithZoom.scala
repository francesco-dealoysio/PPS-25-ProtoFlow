package pkg.e.ui

//> using scala "3.3.7"
//> using lib "org.apache.pdfbox:pdfbox:2.0.30"

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.{PDFRenderer, ImageType}

import java.awt.{BorderLayout, Dimension, Graphics}
import java.awt.image.BufferedImage
import javax.swing._
import java.io.File

object PdfViewerApp3:

  @main def tryPdfViewerApp3: Unit =
    val fileChooser = new JFileChooser()
    fileChooser.setDialogTitle("Select a PDF file")
    if fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION then
      println("No file selected.")
      return

    val pdfFile = fileChooser.getSelectedFile
    if !pdfFile.exists() || !pdfFile.getName.toLowerCase.endsWith(".pdf") then
      println("Invalid PDF file.")
      return

    val document = PDDocument.load(pdfFile)
    val renderer = PDFRenderer(document)

    // State variables
    var currentPage = 0
    var zoom = 1.0

    // Panel to render the PDF page
    val pdfPanel = new JPanel():
      override def paintComponent(g: Graphics): Unit =
        super.paintComponent(g)
        val image: BufferedImage =
          renderer.renderImage(currentPage, zoom.toFloat, ImageType.RGB)
        g.drawImage(image, 0, 0, null)
        setPreferredSize(new Dimension(image.getWidth, image.getHeight))

    // Scroll pane for large pages
    val scrollPane = new JScrollPane(pdfPanel)

    // Navigation buttons
    val prevButton = new JButton("Previous")
    val nextButton = new JButton("Next")
    val zoomInButton = new JButton("Zoom In")
    val zoomOutButton = new JButton("Zoom Out")

    prevButton.addActionListener(_ =>
      if currentPage > 0 then
        currentPage -= 1
        pdfPanel.revalidate()
        pdfPanel.repaint()
    )

    nextButton.addActionListener(_ =>
      if currentPage < document.getNumberOfPages - 1 then
        currentPage += 1
        pdfPanel.revalidate()
        pdfPanel.repaint()
    )

    zoomInButton.addActionListener(_ =>
      zoom += 0.25
      pdfPanel.revalidate()
      pdfPanel.repaint()
    )

    zoomOutButton.addActionListener(_ =>
      if zoom > 0.25 then
        zoom -= 0.25
        pdfPanel.revalidate()
        pdfPanel.repaint()
    )

    // Control panel
    val controlPanel = new JPanel()
    controlPanel.add(prevButton)
    controlPanel.add(nextButton)
    controlPanel.add(zoomInButton)
    controlPanel.add(zoomOutButton)

    // Main frame
    val frame = new JFrame(s"PDF Viewer - ${pdfFile.getName}")
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setLayout(new BorderLayout())
    frame.add(scrollPane, BorderLayout.CENTER)
    frame.add(controlPanel, BorderLayout.SOUTH)
    frame.setSize(800, 600)
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
