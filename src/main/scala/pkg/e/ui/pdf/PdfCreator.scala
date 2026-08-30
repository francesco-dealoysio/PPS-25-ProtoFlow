package pkg.e.ui.pdf

import org.apache.pdfbox.pdmodel.common.*
import org.apache.pdfbox.pdmodel.font.PDType1Font
//import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import pkg.d.util.DateTime.currentDateTime
import pkg.d.util.Util.inPrintsFilePathName
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream

/* (0,0) bottom-left */

object PdfCreator:
  
  case class Font(fontType: PDType1Font, fontSize: Float)

  private var pdfPathName: String = _
  private var title: String = _

  private val document: PDDocument = new PDDocument()
  private var page: PDPage = new PDPage(PDRectangle.A4)
  private var content: PDPageContentStream = new PDPageContentStream(document, page)

  private val fontType = PDType1Font.COURIER_BOLD

  private val fontHeader = Font(fontType, 6)
  private val fontTitle = Font(fontType, 18)
  private val fontBody = Font(fontType, 12)
  private val fontFooter = Font(fontType, 8)

  private val pageWidth = page.getMediaBox.getWidth
  private val pageHeight = page.getMediaBox.getHeight

  private val startX = 0
  private val startY = pageHeight

  private val marginTop = 50f
  private val marginLeft = 100f

  private val paddingTop = 15f
  private val paddingLeft = 15f
  private val paddingRight = 15f
  private val paddingBottom = 15f

  private val lineSpacing = 25f

  private val margin = 50f // threshold

  private var pageNumber = 1

  def createPdf(pdfPathName: String, title: String, fields: Seq[(String, String)]): Unit =

    this.pdfPathName = pdfPathName
    this.title = title

    document.addPage(page)

    writeHeader()

    writeTitle()

    writeFooter()

    writeBody(fields)

    content.close()

    // scritture numero di pagine
    println("Number of pages: " + document.getPages.getCount)
    page = document.getPage(0)
    content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)
    val textWidth = fontFooter.fontType.getStringWidth(pageNumber.toString) / 1000 * fontFooter.fontSize
    val xOffset = (pageWidth - textWidth) / 2
    val yOffset = startY - pageHeight + paddingBottom
    // implementare un ciclo
    writeToContent(pageNumber.toString + " of " + document.getPages.getCount, xOffset, yOffset, fontFooter)
    content.stroke()
    content.close()
    // fine scrittura

    document.save(pdfPathName)
    document.close()

  private def writeHeader(): Unit =
    val xOffset = startX + paddingLeft
    val yOffset = startY - fontHeader.fontSize - paddingTop
    writeToContent(pdfPathName, xOffset, yOffset, fontHeader)

  private def writeTitle(): Unit =
    val textWidth = fontTitle.fontType.getStringWidth(title) / 1000 * fontTitle.fontSize
    val xOffset = (pageWidth - textWidth) / 2
    val yOffset = startY - fontTitle.fontSize - marginTop
    writeToContent(title, xOffset, yOffset, fontTitle)
    
  private def writeBody(fields: Seq[(String, String)]): Unit =
    val xOffset = marginLeft
    var yOffset = startY - fontBody.fontSize - marginTop - 40f

    fields.foreach(field =>

      if yOffset < margin then
        content.close()
        page = new PDPage(PDRectangle.A4)
        document.addPage(page)
        content = new PDPageContentStream(document, page)
        yOffset = startY - fontBody.fontSize - marginTop - 40f
        writeHeader()
        writeTitle()
        pageNumber += 1
        writeFooter()
        yOffset = yOffset - lineSpacing

      writeToContent(shape(field._1) + field._2, xOffset, yOffset, fontBody)
      yOffset = yOffset - lineSpacing
    )

  private def writeFooter(): Unit =
    var xOffset = startX + paddingLeft
    val yOffset = startY - pageHeight + paddingBottom
    writeToContent(currentDateTime, xOffset, yOffset, fontFooter)
    val textWidth = fontFooter.fontType.getStringWidth(pageNumber.toString) / 1000 * fontFooter.fontSize
    xOffset = (pageWidth - textWidth) / 2
    writeToContent(pageNumber.toString, xOffset, yOffset, fontFooter)

  private def writeToContent(text: String, col: Float, row: Float, font: Font): Unit =
    content.beginText()
    content.setFont(font.fontType, font.fontSize)
    content.newLineAtOffset(col, row)
    content.showText(text)
    content.endText()

  private def shape(label: String): String = (" ".repeat(15) + label + ": ").takeRight(15)

  def getTextHeight(text: String, font: Font): Float =

    val maxGlyphHeight = text.map { ch =>
      val glyphBBox = font.fontType.getPath(ch.toString).getBounds2D
      glyphBBox.getHeight
    }.maxOption.getOrElse(0.0)

    val scaledHeight = (maxGlyphHeight / 1000) * font.fontSize
    scaledHeight.toFloat

@main def tryPdfCreator: Unit =
  import pkg.b.logic.Account

  val account: Account = Account().getRecordById("1")
  val fields = Seq(
    ("Id", account.getId),
    ("Cognome", account.getSurname),
    ("Nome", account.getName),
    ("Email", account.getEmail),
    ("Telefono", account.getPhone),
    ("Ruolo", account.getRole),
    ("Area", account.getArea),
    ("Incarico", account.getAssignment),
    ("Username", account.getUsername)
  )

  // test multipagina
  import scala.collection.mutable.ArrayBuffer
  val items: ArrayBuffer[(String, String)] = ArrayBuffer.empty
  for (i <- 1 to 100) { items += (("Label"+i, "Value"+i)) }
  val seq: Seq[(String, String)] = items.toSeq

  PdfCreator.createPdf(inPrintsFilePathName("SchedaAccount.pdf"), "Scheda Account Utente", seq)
  PdfViewer.viewPdf(inPrintsFilePathName("SchedaAccount.pdf"))