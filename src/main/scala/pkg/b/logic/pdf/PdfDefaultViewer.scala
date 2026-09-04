package pkg.b.logic.pdf

import java.awt.Desktop
import java.io.File

object PdfDefaultViewer:

  def viewWithDefaultViewer(pdfPathName: String): Unit =
    val file = File(pdfPathName)

    if !file.exists() then
      println(s"Error: File '$pdfPathName' not found.")
      return

    if !Desktop.isDesktopSupported then
      println("Error: Desktop API is not supported on this system.")
      return

    try
      Desktop.getDesktop.open(file)
      println(s"Opening PDF: $pdfPathName")
    catch
      case e: Exception =>
        println(s"Failed to open PDF: ${e.getMessage}")