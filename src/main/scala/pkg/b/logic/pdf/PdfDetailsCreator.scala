package pkg.b.logic.pdf

import pkg.d.util.DateTime.currentDisplayDateTime
import pkg.d.util.Logger.logger
import org.apache.pdfbox.pdmodel.common.*
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.awt.Color
import java.nio.file.{Files, Paths}

object PdfDetailsCreator:
  
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

  private var document: PDDocument = _
  private var page: PDPage = _
  private var content: PDPageContentStream = _

  private val fontType = PDType1Font.COURIER_BOLD
  private val fontHeader = Font(fontType, 6)
  private val fontTitle = Font(fontType, 18)
  private val fontBody = Font(fontType, 12)
  private val fontFooter = Font(fontType, 8)

  private val pageWidth = PDRectangle.A4.getWidth
  private val pageHeight = PDRectangle.A4.getHeight

  private val startX = 0
  private val startY = pageHeight

  private val margin = 50f
  private val marginTop = 50f
  private val marginLeft = 40f
  private val marginRight = 40f
  private val marginBottom = 50f

  private val padding = 8f
  private val paddingTop = 15f
  private val paddingLeft = 15f
  private val paddingRight = 15f
  private val paddingBottom = 15f

  private val distanceFromTitle = 40f
  private val recordSpacing = 5f
  private val lineSpacing = 10f

  private val pageNumber = 1

  def createDetailsPdf(pdfPathName: String, title: String, fields: Seq[(String, String)]): Boolean =
    if pdfPathName.isBlank || Files.isDirectory(Paths.get(pdfPathName)) then
      false
    else
      try
        initializeDocument()
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
        true
      catch
        case exception: Exception =>
          logger(exception)
          false

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
    val textHeight = fontBody.fontType.getFontDescriptor.getCapHeight / 1000 * fontBody.fontSize
    var rectHeight = textHeight + padding * 2
    val rectWidth = 150f
    val xRectOffset = marginLeft
    var yRectOffset = startY - fontBody.fontSize - marginTop - distanceFromTitle

    fields.foreach(field =>
      val linesColumn1 = wrapText(field._1, rectWidth - padding).size
      val rectWidth2 = pageWidth - rectWidth - marginLeft - marginRight
      val linesColumn2 = wrapText(field._2, rectWidth2 - padding).size
      val maxLines = math.max(linesColumn1, linesColumn2)

      rectHeight = (textHeight + lineSpacing) * maxLines + padding * 2

      if yRectOffset < margin then
        content.close()
        page = new PDPage(PDRectangle.A4)
        document.addPage(page)
        content = new PDPageContentStream(document, page)
        yRectOffset = startY - fontBody.fontSize - marginTop - distanceFromTitle
        writeHeader()
        writeTitle()
        writeFooter()
        yRectOffset = yRectOffset - (rectHeight - recordSpacing)

      val rect = Rect(xRectOffset, yRectOffset, rectWidth, rectHeight, fillColor = Color.DARK_GRAY)
      drawRect(rect)
      writeTextInRect(field._1, rect, HorizontalAlignment.RIGHT, Color.WHITE)

      rect.xPos = rect.xPos + rectWidth
      rect.fillColor = Color.WHITE
      rect.width = pageWidth - rect.width - marginLeft - marginRight
      drawRect(rect)
      writeTextInRect(field._2, rect, HorizontalAlignment.LEFT)

      yRectOffset = yRectOffset - (rect.height + recordSpacing)

    )

  private def drawRect(rect: Rect): Unit =

    content.setNonStrokingColor(rect.fillColor)
    content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
    content.fill()

    content.setStrokingColor(rect.borderColor)
    content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
    content.stroke()

  private def writeTextInRect(text: String, rect: Rect, align: HorizontalAlignment, color: Color = Color.BLACK): Unit =
    val lines = wrapText(text, rect.width - padding)
    val textHeight = fontBody.fontType.getFontDescriptor.getCapHeight / 1000 * fontBody.fontSize
    var yTextOffset: Float = rect.yPos - rect.height + (rect.height - textHeight) / 2

    if lines.size > 1 then
      yTextOffset = rect.yPos - rect.height + (rect.height - textHeight) - lineSpacing

    var xTextOffset: Float = 0

    for line <- lines do

      val textWidth = fontBody.fontType.getStringWidth(line) / 1000 * fontBody.fontSize

      content.setNonStrokingColor(color)

      align match
        case HorizontalAlignment.LEFT =>
          xTextOffset = rect.xPos + padding
        case HorizontalAlignment.RIGHT =>
          xTextOffset = rect.xPos + rect.width - textWidth - padding
        case HorizontalAlignment.CENTER =>
          xTextOffset = rect.xPos + (rect.width - textWidth) / 2
        case null => ()

      writeToContent(line, xTextOffset, yTextOffset, fontBody)
      yTextOffset = yTextOffset - lineSpacing

  private def writeFooter(): Unit =
    var xOffset = startX + paddingLeft
    val yOffset = startY - pageHeight + paddingBottom
    writeToContent(currentDisplayDateTime, xOffset, yOffset, fontFooter)

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

  private def initializeDocument(): Unit =
    document = new PDDocument()
    page = new PDPage(PDRectangle.A4)
    content = new PDPageContentStream(document, page)

  def wrapText(text: String, maxWidth: Float): Seq[String] =
    val words = text.split("\\s+")
    val lines = scala.collection.mutable.ListBuffer[String]()
    val currentLine = new StringBuilder

    for word <- words do
      val testLine = if currentLine.isEmpty then word else currentLine.toString + " " + word
      val textWidth = fontBody.fontType.getStringWidth(testLine) / 1000 * fontBody.fontSize
      if textWidth <= maxWidth then
        currentLine.clear()
        currentLine.append(testLine)
        else
        lines += currentLine.toString()
        currentLine.clear()
        currentLine.append(word)

    if currentLine.nonEmpty then lines += currentLine.toString()
    lines.toSeq
