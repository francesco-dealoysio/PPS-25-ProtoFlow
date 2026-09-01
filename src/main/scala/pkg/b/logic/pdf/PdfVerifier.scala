package pkg.b.logic.pdf

import java.io.{File, FileInputStream}
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