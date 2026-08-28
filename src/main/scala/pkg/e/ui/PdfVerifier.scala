package pkg.e.ui

import pkg.d.util.Util.inPrintsFilePathName

import java.io.{File, FileInputStream, IOException}
import scala.util.{Try, Using}

object PdfVerifier:

  def isPdf(PdfPathName: String): Boolean =
    val pdfFile = File(PdfPathName)

    if !pdfFile.exists() || !pdfFile.isFile then
      println(s"File does not exist or is not a regular file: ${pdfFile.getPath}")
      return false

    val expectedHeader = "%PDF-".getBytes("US-ASCII")
    val buffer = new Array[Byte](expectedHeader.length)

    Try {
      Using.resource(new FileInputStream(PdfPathName)) { fis =>
        val bytesRead = fis.read(buffer)
        bytesRead == expectedHeader.length && buffer.sameElements(expectedHeader)
      }
    }.getOrElse(false)

  @main def tryPdfVerifier: Unit =
    val file = inPrintsFilePathName("falsoPdf.pdf")
    if isPdf(file) then
      println(s"'${file}' is a valid PDF file (header check passed).")
    else
      println(s"'${file}' is NOT a valid PDF file.")
