package pkg.e.ui.pdf

import pkg.d.util.Util.inPrintsFilePathName
import java.awt.Desktop
import java.io.File

object PdfDefaultViewer:

  def viewWithDefaultViewer(pdfPathName: String): Unit =
    
    val file = File(pdfPathName)

    if !file.exists() then
      println(s"Error: File '$pdfPathName' not found.")
      sys.exit(1)

    if !Desktop.isDesktopSupported then
      println("Error: Desktop API is not supported on this system.")
      sys.exit(1)

    try
      Desktop.getDesktop.open(file)
      println(s"Opening PDF: $pdfPathName")
    catch
      case e: Exception =>
        println(s"Failed to open PDF: ${e.getMessage}")

@main def tryPdfDefaultViewer(): Unit =
  PdfDefaultViewer.viewWithDefaultViewer(inPrintsFilePathName("AccountList.pdf"))