package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import pkg.d.util.DateTime.currentDisplayDateTime
import pkg.d.util.Logger.logger

import java.awt.Color
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ListBuffer

object PdfTableCreator:

  private case class Font(fontType: PDType1Font, fontSize: Float)

  private case class Rect(
                           xPos: Float,
                           yPos: Float,
                           width: Float,
                           height: Float,
                           borderColor: Color = Color.BLACK,
                           fillColor: Color = Color.WHITE
                         )

  private enum HorizontalAlignment:
    case Left, Center

  def createTablePdf(pdfPathName: String, title: String, headers: Seq[String], rows: Seq[Seq[String]], columnWeights: Seq[Float] = Seq.empty): Boolean =
    if invalidInput(pdfPathName, headers, rows) then
      false
    else
      try
        createParentDirectory(pdfPathName)
        new TableWriter(pdfPathName, title, headers, rows, columnWeights).create()
        true
      catch
        case exception: Exception =>
          logger(exception)
          false

  private def invalidInput(pdfPathName: String, headers: Seq[String], rows: Seq[Seq[String]]): Boolean =
    pdfPathName.isBlank ||
      Files.isDirectory(Paths.get(pdfPathName)) ||
      headers.isEmpty ||
      rows.isEmpty ||
      rows.exists(_.size != headers.size)

  private def createParentDirectory(pdfPathName: String): Unit =
    Option(Paths.get(pdfPathName).getParent)
      .foreach(parent => Files.createDirectories(parent))

  private final class TableWriter(
                                   pdfPathName: String,
                                   title: String,
                                   headers: Seq[String],
                                   rows: Seq[Seq[String]],
                                   columnWeights: Seq[Float]
                                 ):

    private val pageSize = new PDRectangle(PDRectangle.A4.getHeight, PDRectangle.A4.getWidth)
    private val document = new PDDocument()
    private var page = new PDPage(pageSize)
    document.addPage(page)
    private var content = new PDPageContentStream(document, page)
    private val fontHeader = Font(PDType1Font.COURIER_BOLD, 6f)
    private val fontTitle = Font(PDType1Font.COURIER_BOLD, 18f)
    private val fontTableHeader = Font(PDType1Font.COURIER_BOLD, 7f)
    private val fontTableBody = Font(PDType1Font.COURIER, 7f)
    private val fontFooter = Font(PDType1Font.COURIER_BOLD, 8f)
    private val pageWidth = pageSize.getWidth
    private val pageHeight = pageSize.getHeight

    private val marginTop = 50f
    private val marginLeft = 40f
    private val marginRight = 40f
    private val marginBottom = 50f

    private val paddingTop = 15f
    private val paddingLeft = 15f
    private val paddingBottom = 15f

    private val cellPadding = 4f
    private val distanceFromTitle = 35f
    private val lineSpacing = 2f

    private val columnWidths = resolveColumnWidths(headers.size, columnWeights)

    def create(): Unit =
      try
        writePageFrame()
        writeTable()
        content.close()
        writePageNumbers()
        document.save(pdfPathName)
      finally
        document.close()

    private def writePageFrame(): Unit =
      writeHeader()
      writeTitle()
      writeFooter()

    private def writeHeader(): Unit =
      val yOffset = pageHeight - fontHeader.fontSize - paddingTop
      writeToContent(pdfPathName, paddingLeft, yOffset, fontHeader)

    private def writeTitle(): Unit =
      val xOffset = (pageWidth - textWidth(title, fontTitle)) / 2
      val yOffset = pageHeight - fontTitle.fontSize - marginTop
      writeToContent(title, xOffset, yOffset, fontTitle)

    private def writeFooter(): Unit =
      writeToContent(currentDisplayDateTime, paddingLeft, paddingBottom, fontFooter)

    private def writeTable(): Unit =
      var currentY = writeHeaderRow(tableStartY)

      rows.foreach: row =>
        val rowHeight = tableRowHeight(row, fontTableBody)
        if currentY - rowHeight < marginBottom then
          currentY = startNewPage()

        writeRow(values = row, y = currentY, height = rowHeight, header = false)
        currentY -= rowHeight

    private def startNewPage(): Float =
      content.close()
      page = new PDPage(pageSize)
      document.addPage(page)
      content = new PDPageContentStream(document, page)
      writePageFrame()
      writeHeaderRow(tableStartY)

    private def writeHeaderRow(y: Float): Float =
      val height = tableRowHeight(headers, fontTableHeader)
      writeRow(values = headers, y = y, height = height, header = true)
      y - height

    private def writeRow(values: Seq[String], y: Float, height: Float, header: Boolean): Unit =
      val font = if header then fontTableHeader else fontTableBody
      val fillColor = if header then Color.DARK_GRAY else Color.WHITE
      val textColor = if header then Color.WHITE else Color.BLACK
      val alignment = if header then HorizontalAlignment.Center else HorizontalAlignment.Left
      var x = marginLeft

      values
        .zip(columnWidths)
        .foreach: (value, width) =>
          val rect = Rect(xPos = x, yPos = y, width = width, height = height, fillColor = fillColor)
          drawRect(rect)
          writeTextInRect(value, rect, font, alignment, textColor)
          x += width

    private def tableRowHeight(values: Seq[String], font: Font): Float =
      val maxLines =
        values.zip(columnWidths).map: (value, width) =>
            wrapText(
              value,
              width - cellPadding * 2,
              font
            ).size
          .max

      maxLines * lineHeight(font) + cellPadding * 2

    private def drawRect(rect: Rect): Unit =
      content.setNonStrokingColor(rect.fillColor)
      content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
      content.fill()
      content.setStrokingColor(rect.borderColor)
      content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
      content.stroke()

    private def writeTextInRect(text: String, rect: Rect, font: Font, alignment: HorizontalAlignment, color: Color): Unit =
      val lines = wrapText(text, rect.width - cellPadding * 2, font)
      var yOffset = rect.yPos - cellPadding - font.fontSize

      lines.foreach: line =>
        val xOffset =
          alignment match
            case HorizontalAlignment.Left =>
              rect.xPos + cellPadding

            case HorizontalAlignment.Center =>
              rect.xPos + (rect.width - textWidth(line, font)) / 2

        content.setNonStrokingColor(color)
        writeToContent(line, xOffset, yOffset, font)

        yOffset -= lineHeight(font)

    private def resolveColumnWidths(columnCount: Int, weights: Seq[Float]): Seq[Float] =
      val availableWidth = pageWidth - marginLeft - marginRight
      val validWeights = weights.size == columnCount && weights.forall(_ > 0)

      if validWeights then
        weightedColumnWidths(availableWidth, weights)
      else
        equalColumnWidths(availableWidth, columnCount)

    private def weightedColumnWidths(availableWidth: Float, weights: Seq[Float]): Seq[Float] =
      val totalWeight = weights.sum
      weights.map: weight =>
        availableWidth *
          weight /
          totalWeight

    private def equalColumnWidths(availableWidth: Float, columnCount: Int): Seq[Float] =
      Seq.fill(columnCount)(availableWidth / columnCount)

    private def wrapText(text: String, maxWidth: Float, font: Font): Seq[String] =
      val words = Option(text).getOrElse("").trim.split("\\s+").filter(_.nonEmpty).toSeq
      if words.isEmpty then
        Seq("")
      else
        val tokens = words.flatMap(splitLongWord(_, maxWidth, font))
        buildLines(tokens, maxWidth, font)

    private def splitLongWord(word: String, maxWidth: Float, font: Font): Seq[String] =
      if textWidth(word, font) <= maxWidth then
        Seq(word)
      else
        splitWord(word, maxWidth, font)

    private def splitWord(word: String, maxWidth: Float, font: Font): Seq[String] =
      val parts = ListBuffer[String]()
      val current = new StringBuilder

      word.foreach: character =>
        val candidate = current.toString + character

        if current.nonEmpty &&
          textWidth(candidate, font) > maxWidth
        then
          parts += current.toString
          current.clear()

        current.append(character)

      if current.nonEmpty then
        parts += current.toString

      parts.toSeq

    private def buildLines(tokens: Seq[String], maxWidth: Float, font: Font): Seq[String] =
      val lines = ListBuffer[String]()
      var currentLine = ""

      tokens.foreach: token =>
        val candidate =
          if currentLine.isEmpty then
            token
          else
            s"$currentLine $token"

        if textWidth(candidate, font) <= maxWidth then
          currentLine = candidate
        else
          if currentLine.nonEmpty then
            lines += currentLine

          currentLine = token

      if currentLine.nonEmpty then
        lines += currentLine

      lines.toSeq

    private def writeToContent(text: String, x: Float, y: Float, font: Font): Unit =
      content.beginText()
      content.setFont(font.fontType, font.fontSize)
      content.newLineAtOffset(x, y)
      content.showText(Option(text).getOrElse(""))
      content.endText()

    private def writePageNumbers(): Unit =
      val totalPages = document.getNumberOfPages
      (0 until totalPages).foreach: index =>
        writePageNumber(index, totalPages)

    private def writePageNumber(pageIndex: Int, totalPages: Int): Unit =
      val currentPage = document.getPage(pageIndex)
      val pageContent = new PDPageContentStream(document, currentPage, PDPageContentStream.AppendMode.APPEND, true)
      val text = s"${pageIndex + 1} di $totalPages"
      val xOffset = (pageWidth - textWidth(text, fontFooter)) / 2

      pageContent.beginText()
      pageContent.setFont(fontFooter.fontType, fontFooter.fontSize)
      pageContent.newLineAtOffset(xOffset, paddingBottom)
      pageContent.showText(text)
      pageContent.endText()
      pageContent.close()

    private def textWidth(text: String, font: Font): Float =
      font.fontType.getStringWidth(Option(text).getOrElse("")) / 1000 * font.fontSize

    private def lineHeight(font: Font): Float = font.fontSize + lineSpacing

    private def tableStartY: Float = pageHeight - fontTitle.fontSize - marginTop - distanceFromTitle