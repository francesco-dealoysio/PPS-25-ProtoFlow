package pkg.e.ui

import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.font.PDType1Font
import pkg.d.util.Util.inPrintsFilePathName

import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.common._
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
/*
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.File
*/
@main def createPdf(): Unit =

  val fontSize = 16f
  val fontType = PDType1Font.HELVETICA_BOLD

  val document = new PDDocument()

  val page = new PDPage(PDRectangle.A4)

  document.addPage(page)

  val text = "Elenco Account Utenti"

  val pageWidth = page.getMediaBox.getWidth

  val pageHeight = page.getMediaBox.getHeight

  val textWidth = fontType.getStringWidth(text) / 1000 * fontSize

  var col = 0

  val startX = (pageWidth - textWidth) / 2

  val topMargin = 40f

  var row = pageHeight - fontSize - topMargin

  val startY = row // vertical position (row)

  val marginTop = 20f // distance from top edge

  val contentStream = new PDPageContentStream(document, page)

  val fontTitolo = PDType1Font.HELVETICA_BOLD
  val fontCorpo = PDType1Font.HELVETICA

  contentStream.beginText()
  contentStream.setFont(fontTitolo, 16)

  // (0,0) bottom-left

  //var row = pageHeight - marginTop
  contentStream.newLineAtOffset(startX, startY)
  contentStream.showText("Elenco Account Utentix")
  contentStream.endText()

  contentStream.beginText()
  //contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14)
  contentStream.setFont(fontCorpo, 12)
  contentStream.newLineAtOffset(col, row - 30)
  contentStream.showText("Francesco de Aloysio")
  contentStream.endText()
  contentStream.close()

  document.save(inPrintsFilePathName("AccountList.pdf"))
  document.close()

  println("PDF created: AccountList.pdf")
