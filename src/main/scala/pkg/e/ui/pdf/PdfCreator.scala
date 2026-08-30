package pkg.e.ui.pdf

import org.apache.pdfbox.pdmodel.common.*
import org.apache.pdfbox.pdmodel.font.PDType1Font
import pkg.d.util.DateTime.currentDateTime
import pkg.d.util.Util.inPrintsFilePathName
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream

import java.awt.Color

object PdfCreator:
  
  case class Font(fontType: PDType1Font, fontSize: Float)

  case class Rect(
                   var xPos: Float,
                   var yPos: Float,
                   var width: Float,
                   var height: Float,
                   var borderColor: Color = Color.BLACK,
                   var fillColor: Color = Color.WHITE
  )

  enum HorizontalAlignment:
    case LEFT
    case RIGHT
    case CENTER

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

  private val margin = 50f // threshold
  private val marginTop = 50f
  private val marginLeft = 40f
  private val marginRight = 40f
  private val marginBottom = 50f

  private val padding = 5f
  private val paddingTop = 15f
  private val paddingLeft = 15f
  private val paddingRight = 15f
  private val paddingBottom = 15f

  private val distanceFromTitle = 40f

  private val lineSpacing = 25f

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

    writePageNumbers()

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
    val rectHeight = 20f
    val rectWidth = 150f
    val xRectOffset = marginLeft
    var yRectOffset = startY - fontBody.fontSize - marginTop - distanceFromTitle

    fields.foreach(field =>

      if yRectOffset < margin then
        content.close()
        page = new PDPage(PDRectangle.A4)
        document.addPage(page)
        content = new PDPageContentStream(document, page)
        yRectOffset = startY - fontBody.fontSize - marginTop - distanceFromTitle
        writeHeader()
        writeTitle()
        writeFooter()
        yRectOffset = yRectOffset - lineSpacing

      var rect = Rect(xRectOffset, yRectOffset, rectWidth, rectHeight, fillColor = Color.DARK_GRAY)
      drawRect(rect)
      writeTextInRect(field._1, rect, HorizontalAlignment.RIGHT, Color.WHITE)

      rect.xPos = rect.xPos + rectWidth
      rect.fillColor = Color.WHITE
      rect.width = pageWidth - rect.width - marginLeft - marginRight
      drawRect(rect)
      writeTextInRect(field._2, rect, HorizontalAlignment.LEFT)

      yRectOffset = yRectOffset - lineSpacing

    )

  private def drawRect(rect: Rect): Unit =
    /* draw background colored rectangle */
    content.setNonStrokingColor(rect.fillColor)
    content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
    content.fill()
    /* draw border */
    content.setStrokingColor(rect.borderColor)
    content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
    content.stroke()

  private def writeTextInRect(text: String, rect: Rect, align: HorizontalAlignment, color: Color = Color.BLACK): Unit =
    val textWidth = fontBody.fontType.getStringWidth(text) / 1000 * fontBody.fontSize
    val textHeight = fontBody.fontType.getFontDescriptor.getCapHeight / 1000 * fontBody.fontSize
    var xTextOffset: Float = 0
    var yTextOffset: Float = rect.yPos - rect.height + (rect.height - textHeight) / 2

    content.setNonStrokingColor(color)

    align match
      case HorizontalAlignment.LEFT =>
        xTextOffset = rect.xPos + padding
      case HorizontalAlignment.RIGHT =>
        xTextOffset = rect.xPos + rect.width - textWidth - padding
      case HorizontalAlignment.CENTER =>
        xTextOffset = rect.xPos + (rect.width - textWidth) / 2
      case _ => ()

    writeToContent(text, xTextOffset, yTextOffset, fontBody)

  private def writeFooter(): Unit =
    var xOffset = startX + paddingLeft
    val yOffset = startY - pageHeight + paddingBottom
    writeToContent(currentDateTime, xOffset, yOffset, fontFooter)

    val textWidth = fontFooter.fontType.getStringWidth(pageNumber.toString) / 1000 * fontFooter.fontSize
    xOffset = (pageWidth - textWidth) / 2

  private def writeToContent(text: String, col: Float, row: Float, font: Font): Unit =
    content.beginText()
    content.setFont(font.fontType, font.fontSize)
    content.newLineAtOffset(col, row)
    content.showText(text)
    content.endText()

  private def writePageNumbers(): Unit =
    val textWidth = fontFooter.fontType.getStringWidth(pageNumber.toString) / 1000 * fontFooter.fontSize
    val xOffset = (pageWidth - textWidth) / 2
    val yOffset = startY - pageHeight + paddingBottom

    val nPages = document.getPages.getCount

    for (pageNumber <- 1 to nPages) {
      page = document.getPage(pageNumber-1)
      content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)
      writeToContent(pageNumber.toString + " di " + document.getPages.getCount, xOffset, yOffset, fontFooter)
      content.stroke()
      content.close()
    }

  private def shape(label: String): String = (" ".repeat(15) + label + ": ").takeRight(15)

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
  /*
    PdfCreator.createPdf(inPrintsFilePathName("SchedaAccount.pdf"), "Scheda Account Utente", fields)
    PdfViewer.viewPdf(inPrintsFilePathName("SchedaAccount.pdf"))
  */

  // test multipagina
  import scala.collection.mutable.ArrayBuffer
  val items: ArrayBuffer[(String, String)] = ArrayBuffer.empty
  for (i <- 1 to 100) { items += (("Label"+i, "Value"+i)) }
  val multiPages: Seq[(String, String)] = items.toSeq

  PdfCreator.createPdf(inPrintsFilePathName("PaginaMultipla.pdf"), "Scheda Pagina Multipla", multiPages)
  PdfViewer.viewPdf(inPrintsFilePathName("PaginaMultipla.pdf"))
