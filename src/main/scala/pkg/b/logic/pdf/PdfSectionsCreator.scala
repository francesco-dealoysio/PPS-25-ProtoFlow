package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import pkg.d.util.DateTime.currentDisplayDateTime
import pkg.d.util.Logger.logger
import java.awt.Color
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ListBuffer

object PdfSectionsCreator:

  case class Section(
                      title: String,
                      headers: Seq[String],
                      rows: Seq[Seq[String]],
                      columnWeights: Seq[Float] = Seq.empty
                    )

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

  def createSectionsPdf(pdfPathName: String, title: String, sections: Seq[Section]): Boolean =
    if invalidInput(pdfPathName, sections) then false
    else
      try
        createParentDirectory(pdfPathName)
        new SectionsWriter(pdfPathName, title, sections).create()
        true
      catch
        case exception: Exception =>
          logger(exception)
          false

  private def invalidInput(pdfPathName: String, sections: Seq[Section]): Boolean =
    pdfPathName.isBlank ||
      Files.isDirectory(Paths.get(pdfPathName)) ||
      sections.isEmpty ||
      sections.forall(_.rows.isEmpty) ||
      sections.exists(invalidSection)

  private def invalidSection(section: Section): Boolean =
    section.headers.isEmpty || section.rows.exists(_.size != section.headers.size)

  private def createParentDirectory(pdfPathName: String): Unit =
    Option(Paths.get(pdfPathName).getParent).foreach(parent => Files.createDirectories(parent))

  private final class SectionsWriter(pdfPathName: String, title: String, sections: Seq[Section]):

    private val pageSize = PDRectangle.A4
    private val document = new PDDocument()

    private var page = new PDPage(pageSize)
    document.addPage(page)

    private var content = new PDPageContentStream(document, page)

    private val fontHeader = Font(PDType1Font.COURIER_BOLD, 6f)
    private val fontTitle = Font(PDType1Font.COURIER_BOLD, 18f)
    private val fontSectionTitle = Font(PDType1Font.COURIER_BOLD, 11f)
    private val fontTableHeader = Font(PDType1Font.COURIER_BOLD, 8f)
    private val fontTableBody = Font(PDType1Font.COURIER, 8f)
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
    private val sectionTitleSpacing = 10f
    private val sectionSpacing = 20f
    private val lineSpacing = 2f

    def create(): Unit =
      try
        writePageFrame()
        writeSections()
        content.close()
        writePageNumbers()
        document.save(pdfPathName)
      finally document.close()

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

    private def writeSections(): Unit =
      var currentY = sectionsStartY
      sections.foreach: section =>
        currentY = writeSection(section, currentY)

    private def writeSection(section: Section, startY: Float): Float =
      val columnWidths = resolveColumnWidths(section.headers.size, section.columnWeights)
      var currentY = ensureSectionFits(section, columnWidths, startY)
      currentY = writeSectionHeader(section, columnWidths, currentY)
      currentY = writeSectionRows(section, columnWidths, currentY)
      currentY - sectionSpacing

    private def ensureSectionFits(section: Section, columnWidths: Seq[Float], currentY: Float): Float =
      val requiredHeight = sectionInitialHeight(section, columnWidths)
      if currentY - requiredHeight < marginBottom then startNewPage()
      else currentY

    private def sectionInitialHeight(section: Section, columnWidths: Seq[Float]): Float =
      val titleHeight = if section.title.nonEmpty then fontSectionTitle.fontSize + sectionTitleSpacing else 0f
      val headerHeight = tableRowHeight(section.headers, columnWidths, fontTableHeader)
      val firstRowHeight = section.rows.headOption
        .map(row => tableRowHeight(row, columnWidths, fontTableBody))
        .getOrElse(0f)

      titleHeight + headerHeight + firstRowHeight

    private def writeSectionHeader(section: Section, columnWidths: Seq[Float], startY: Float): Float =
      val afterTitle = if section.title.nonEmpty then writeSectionTitle(section.title, startY) else startY
      writeTableHeader(section.headers, columnWidths, afterTitle)

    private def writeSectionTitle(sectionTitle: String, y: Float): Float =
      val xOffset = (pageWidth - textWidth(sectionTitle, fontSectionTitle)) / 2
      val yOffset = y - fontSectionTitle.fontSize
      writeToContent(sectionTitle, xOffset, yOffset, fontSectionTitle)
      yOffset - sectionTitleSpacing

    private def writeTableHeader(headers: Seq[String], columnWidths: Seq[Float], y: Float): Float =
      val height = tableRowHeight(headers, columnWidths, fontTableHeader)
      writeRow(values = headers, columnWidths = columnWidths, y = y, height = height, header = true)
      y - height

    private def writeSectionRows(section: Section, columnWidths: Seq[Float], startY: Float): Float =
      var currentY = startY
      section.rows.foreach: row =>
        val rowHeight = tableRowHeight(row, columnWidths, fontTableBody)
        if currentY - rowHeight < marginBottom then
          currentY = startContinuationPage(section, columnWidths)

        writeRow(values = row, columnWidths = columnWidths, y = currentY, height = rowHeight, header = false)
        currentY -= rowHeight
      currentY

    private def startContinuationPage(section: Section, columnWidths: Seq[Float]): Float =
      val newPageY = startNewPage()
      writeSectionHeader(section, columnWidths, newPageY)

    private def startNewPage(): Float =
      content.close()
      page = new PDPage(pageSize)
      document.addPage(page)
      content = new PDPageContentStream(document, page)
      writePageFrame()
      sectionsStartY

    private def writeRow(
                          values: Seq[String],
                          columnWidths: Seq[Float],
                          y: Float,
                          height: Float,
                          header: Boolean
                        ): Unit =
      val font = if header then fontTableHeader else fontTableBody
      val fillColor = if header then Color.DARK_GRAY else Color.WHITE
      val textColor = if header then Color.WHITE else Color.BLACK
      val alignment = if header then HorizontalAlignment.Center else HorizontalAlignment.Left

      var x = marginLeft
      values.zip(columnWidths).foreach: (value, width) =>
        val rect = Rect(xPos = x, yPos = y, width = width, height = height, fillColor = fillColor)
        drawRect(rect)
        writeTextInRect(value, rect, font, alignment, textColor)
        x += width

    private def tableRowHeight(values: Seq[String], columnWidths: Seq[Float], font: Font): Float =
      val maxLines = values.zip(columnWidths)
        .map: (value, width) =>
          wrapText(value, width - cellPadding * 2, font).size
        .max
      maxLines * lineHeight(font) + cellPadding * 2

    private def drawRect(rect: Rect): Unit =
      content.setNonStrokingColor(rect.fillColor)
      content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
      content.fill()

      content.setStrokingColor(rect.borderColor)
      content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
      content.stroke()

    private def writeTextInRect(
                                 text: String,
                                 rect: Rect,
                                 font: Font,
                                 alignment: HorizontalAlignment,
                                 color: Color
                               ): Unit =
      val lines = wrapText(text, rect.width - cellPadding * 2, font)
      val textHeight = font.fontType.getFontDescriptor.getCapHeight / 1000 * font.fontSize
      val blockHeight = textHeight + (lines.size - 1) * lineHeight(font)

      var yOffset = rect.yPos - (rect.height - blockHeight) / 2 - textHeight

      lines.foreach: line =>
        val xOffset = alignment match
          case HorizontalAlignment.Left   => rect.xPos + cellPadding
          case HorizontalAlignment.Center => rect.xPos + (rect.width - textWidth(line, font)) / 2

        content.setNonStrokingColor(color)
        writeToContent(line, xOffset, yOffset, font)
        yOffset -= lineHeight(font)

    private def resolveColumnWidths(columnCount: Int, weights: Seq[Float]): Seq[Float] =
      val availableWidth = pageWidth - marginLeft - marginRight
      val validWeights = weights.size == columnCount && weights.forall(_ > 0)

      if validWeights then weightedColumnWidths(availableWidth, weights)
      else equalColumnWidths(availableWidth, columnCount)

    private def weightedColumnWidths(availableWidth: Float, weights: Seq[Float]): Seq[Float] =
      val totalWeight = weights.sum
      weights.map(weight => availableWidth * weight / totalWeight)

    private def equalColumnWidths(availableWidth: Float, columnCount: Int): Seq[Float] =
      Seq.fill(columnCount)(availableWidth / columnCount)

    private def wrapText(text: String, maxWidth: Float, font: Font): Seq[String] =
      val words = Option(text).getOrElse("").trim.split("\\s+").filter(_.nonEmpty).toSeq

      if words.isEmpty then Seq("")
      else
        val tokens = words.flatMap(splitLongWord(_, maxWidth, font))
        buildLines(tokens, maxWidth, font)

    private def splitLongWord(word: String, maxWidth: Float, font: Font): Seq[String] =
      if textWidth(word, font) <= maxWidth then Seq(word)
      else splitWord(word, maxWidth, font)

    private def splitWord(word: String, maxWidth: Float, font: Font): Seq[String] =
      val parts = ListBuffer[String]()
      val current = new StringBuilder

      word.foreach: character =>
        if current.nonEmpty && textWidth(current.toString + character, font) > maxWidth then
          parts += current.toString
          current.clear()
        current.append(character)

      if current.nonEmpty then parts += current.toString
      parts.toSeq

    private def buildLines(tokens: Seq[String], maxWidth: Float, font: Font): Seq[String] =
      val lines = ListBuffer[String]()
      var currentLine = ""

      tokens.foreach: token =>
        val candidate = if currentLine.isEmpty then token else s"$currentLine $token"
        if textWidth(candidate, font) <= maxWidth then currentLine = candidate
        else
          if currentLine.nonEmpty then lines += currentLine
          currentLine = token

      if currentLine.nonEmpty then lines += currentLine
      lines.toSeq

    private def writeToContent(text: String, x: Float, y: Float, font: Font): Unit =
      content.beginText()
      content.setFont(font.fontType, font.fontSize)
      content.newLineAtOffset(x, y)
      content.showText(Option(text).getOrElse(""))
      content.endText()

    private def writePageNumbers(): Unit =
      val totalPages = document.getNumberOfPages
      (0 until totalPages).foreach(index => writePageNumber(index, totalPages))

    private def writePageNumber(pageIndex: Int, totalPages: Int): Unit =
      val currentPage = document.getPage(pageIndex)
      val pageContent = new PDPageContentStream(
        document,
        currentPage,
        PDPageContentStream.AppendMode.APPEND,
        true
      )

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

    private def lineHeight(font: Font): Float =
      font.fontSize + lineSpacing

    private def sectionsStartY: Float =
      pageHeight - fontTitle.fontSize - marginTop - distanceFromTitle