package pkg.b.logic.pdf


import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage, PDPageContentStream}
import pkg.d.util.Logger.logger

import java.awt.Color
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ListBuffer
import scala.util.Using

object PdfDocumentSummaryCreator:

  case class SummaryData(
                          applicationTitle: String,
                          reportTitle: String,
                          generatedAtLabel: String,
                          generatedAt: String,
                          documentDataSectionTitle: String,
                          documentCodeLabel: String,
                          documentCode: String,
                          classificationLabel: String,
                          classification: String,
                          phasesSectionTitle: String,
                          phaseHeaders: Seq[String],
                          phaseRows: Seq[Seq[String]],
                          generatedByLabel: String,
                          generatedBy: String,
                          pageLabel: String,
                          logoResourcePath: String
                        )

  private case class Font(fontType: PDType1Font, fontSize: Float)

  private case class Rect(
                           xPos: Float,
                           yPos: Float,
                           width: Float,
                           height: Float,
                           fillColor: Color = Color.WHITE
                         )

  private enum HorizontalAlignment:
    case Left, Center

  def createSummaryPdf(pdfPathName: String, data: SummaryData): Boolean =
    if invalidInput(pdfPathName, data) then false
    else
      try
        createParentDirectory(pdfPathName)
        new SummaryWriter(pdfPathName, data).create()
        true
      catch
        case exception: Exception =>
          logger(exception)
          false

  private def invalidInput(pdfPathName: String, data: SummaryData): Boolean =
    pdfPathName.isBlank ||
      Files.isDirectory(Paths.get(pdfPathName)) ||
      data.phaseHeaders.isEmpty ||
      data.phaseRows.exists(_.size != data.phaseHeaders.size)

  private def createParentDirectory(pdfPathName: String): Unit =
    Option(Paths.get(pdfPathName).getParent)
      .foreach(parent => Files.createDirectories(parent))

  private final class SummaryWriter(pdfPathName: String, data: SummaryData):

    private val pageSize = PDRectangle.A4
    private val document = new PDDocument()

    private var page = new PDPage(pageSize)
    document.addPage(page)

    private var content = new PDPageContentStream(document, page)

    private val fontApplication = Font(PDType1Font.COURIER_BOLD, 16f)
    private val fontReport = Font(PDType1Font.COURIER_BOLD, 13f)
    private val fontInfo = Font(PDType1Font.COURIER, 9f)
    private val fontSection = Font(PDType1Font.COURIER_BOLD, 11f)
    private val fontTableHeader = Font(PDType1Font.COURIER_BOLD, 8f)
    private val fontTableBody = Font(PDType1Font.COURIER, 8f)
    private val fontFooter = Font(PDType1Font.COURIER, 8f)

    private val pageWidth = pageSize.getWidth
    private val pageHeight = pageSize.getHeight

    private val marginLeft = 40f
    private val marginRight = 40f
    private val marginTop = 40f
    private val marginBottom = 60f

    private val logoSize = 50f
    private val headerGap = 18f
    private val sectionSpacing = 12f
    private val cellPadding = 5f
    private val lineSpacing = 2f

    private val documentColumnWeights = Seq(1.7f, 4f)
    private val phaseColumnWeights = Seq(1.6f, 1.7f, 1.3f, 2.4f)

    def create(): Unit =
      try
        var currentY = writeSummaryHeader()
        currentY = writeDocumentSection(currentY)
        writePhasesSection(currentY)

        content.close()
        writeFooters()

        document.save(pdfPathName)
      finally document.close()

    private def writeSummaryHeader(): Float =
      writeLogo()

      val textX = marginLeft + logoSize + 15f
      var currentY = pageHeight - marginTop - fontApplication.fontSize

      writeToContent(data.applicationTitle, textX, currentY, fontApplication)
      currentY -= fontApplication.fontSize + 7f

      writeToContent(data.reportTitle, textX, currentY, fontReport)
      currentY -= fontReport.fontSize + 7f

      writeToContent(s"${data.generatedAtLabel}: ${data.generatedAt}", textX, currentY, fontInfo)

      pageHeight - marginTop - logoSize - headerGap

    private def writeLogo(): Unit =
      resourceBytes(data.logoResourcePath).foreach: bytes =>
        val image = PDImageXObject.createFromByteArray(document, bytes, "summary-logo")
        content.drawImage(image, marginLeft, pageHeight - marginTop - logoSize, logoSize, logoSize)

    private def resourceBytes(path: String): Option[Array[Byte]] =
      Option(getClass.getResourceAsStream(path)).map: stream =>
        Using.resource(stream)(_.readAllBytes())

    private def writeDocumentSection(startY: Float): Float =
      var currentY = writeSectionTitle(data.documentDataSectionTitle, startY)
      val widths = resolveColumnWidths(documentColumnWeights)

      currentY = writeDetailRow(data.documentCodeLabel, data.documentCode, widths, currentY)
      currentY = writeDetailRow(data.classificationLabel, data.classification, widths, currentY)

      currentY - sectionSpacing

    private def writeDetailRow(label: String, value: String, widths: Seq[Float], y: Float): Float =
      val values = Seq(label, value)
      val fonts = Seq(fontTableHeader, fontTableBody)
      val height = rowHeight(values, widths, fonts)

      writeCell(
        text = label,
        x = marginLeft,
        y = y,
        width = widths.head,
        height = height,
        font = fontTableHeader,
        fillColor = Color.DARK_GRAY,
        textColor = Color.WHITE,
        alignment = HorizontalAlignment.Left
      )

      writeCell(
        text = value,
        x = marginLeft + widths.head,
        y = y,
        width = widths(1),
        height = height,
        font = fontTableBody,
        fillColor = Color.WHITE,
        textColor = Color.BLACK,
        alignment = HorizontalAlignment.Left
      )

      y - height

    private def writePhasesSection(startY: Float): Unit =
      val widths = resolveColumnWidths(
        if data.phaseHeaders.size == phaseColumnWeights.size then phaseColumnWeights
        else Seq.fill(data.phaseHeaders.size)(1f)
      )

      var currentY = writeSectionTitle(data.phasesSectionTitle, startY)
      currentY = writePhaseHeader(widths, currentY)

      data.phaseRows.foreach: row =>
        val height = tableRowHeight(row, widths, fontTableBody)
        if currentY - height < marginBottom then
          currentY = startPhasesContinuationPage(widths)

        writeTableRow(values = row, widths = widths, y = currentY, height = height, header = false)
        currentY -= height

    private def startPhasesContinuationPage(widths: Seq[Float]): Float =
      startNewPage()
      var currentY = pageHeight - marginTop
      currentY = writeSectionTitle(data.phasesSectionTitle, currentY)
      writePhaseHeader(widths, currentY)

    private def startNewPage(): Unit =
      content.close()
      page = new PDPage(pageSize)
      document.addPage(page)
      content = new PDPageContentStream(document, page)

    private def writeSectionTitle(title: String, y: Float): Float =
      writeToContent(title, marginLeft, y - fontSection.fontSize, fontSection)
      y - fontSection.fontSize - 7f

    private def writePhaseHeader(widths: Seq[Float], y: Float): Float =
      val height = tableRowHeight(data.phaseHeaders, widths, fontTableHeader)
      writeTableRow(values = data.phaseHeaders, widths = widths, y = y, height = height, header = true)
      y - height

    private def writeTableRow(
                               values: Seq[String],
                               widths: Seq[Float],
                               y: Float,
                               height: Float,
                               header: Boolean
                             ): Unit =
      val font = if header then fontTableHeader else fontTableBody
      val fillColor = if header then Color.DARK_GRAY else Color.WHITE
      val textColor = if header then Color.WHITE else Color.BLACK
      val alignment = if header then HorizontalAlignment.Center else HorizontalAlignment.Left

      var x = marginLeft
      values.zip(widths).foreach: (value, width) =>
        writeCell(
          text = value,
          x = x,
          y = y,
          width = width,
          height = height,
          font = font,
          fillColor = fillColor,
          textColor = textColor,
          alignment = alignment
        )
        x += width

    private def writeCell(
                           text: String,
                           x: Float,
                           y: Float,
                           width: Float,
                           height: Float,
                           font: Font,
                           fillColor: Color,
                           textColor: Color,
                           alignment: HorizontalAlignment
                         ): Unit =
      val rect = Rect(xPos = x, yPos = y, width = width, height = height, fillColor = fillColor)
      drawRect(rect)
      writeTextInRect(text, rect, font, alignment, textColor)

    private def drawRect(rect: Rect): Unit =
      content.setNonStrokingColor(rect.fillColor)
      content.addRect(rect.xPos, rect.yPos - rect.height, rect.width, rect.height)
      content.fill()

      content.setStrokingColor(Color.BLACK)
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

    private def tableRowHeight(values: Seq[String], widths: Seq[Float], font: Font): Float =
      rowHeight(values, widths, Seq.fill(values.size)(font))

    private def rowHeight(values: Seq[String], widths: Seq[Float], fonts: Seq[Font]): Float =
      values.zip(widths).zip(fonts).map:
        case ((value, width), font) =>
          wrapText(value, width - cellPadding * 2, font).size * lineHeight(font) + cellPadding * 2
      .max

    private def resolveColumnWidths(weights: Seq[Float]): Seq[Float] =
      val availableWidth = pageWidth - marginLeft - marginRight
      val totalWeight = weights.sum
      weights.map(weight => availableWidth * weight / totalWeight)

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

    private def writeFooters(): Unit =
      val totalPages = document.getNumberOfPages
      (0 until totalPages).foreach(index => writeFooter(index))

    private def writeFooter(pageIndex: Int): Unit =
      val currentPage = document.getPage(pageIndex)
      val footerContent = new PDPageContentStream(
        document,
        currentPage,
        PDPageContentStream.AppendMode.APPEND,
        true
      )

      val y = 30f
      writeFooterText(footerContent, s"${data.generatedByLabel}: ${data.generatedBy}", marginLeft, y)

      val pageText = s"${data.pageLabel} ${pageIndex + 1}"
      val pageX = pageWidth - marginRight - textWidth(pageText, fontFooter)

      writeFooterText(footerContent, pageText, pageX, y)
      footerContent.close()

    private def writeFooterText(stream: PDPageContentStream, text: String, x: Float, y: Float): Unit =
      stream.beginText()
      stream.setFont(fontFooter.fontType, fontFooter.fontSize)
      stream.newLineAtOffset(x, y)
      stream.showText(text)
      stream.endText()

    private def textWidth(text: String, font: Font): Float =
      font.fontType.getStringWidth(Option(text).getOrElse("")) / 1000 * font.fontSize

    private def lineHeight(font: Font): Float =
      font.fontSize + lineSpacing