package pkg.d.util

import org.openpdf.text.*
import org.openpdf.text.pdf.*
import java.io.{File, FileOutputStream}
import scala.util.Using
import scala.xml.{Elem, XML}

object XmlToPdf:

  private val printsFolder = new File(System.getProperty("user.dir"), "protoflow/prints")

  case class SummaryPrintData(
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

  def printList(
                 xmlPath: String,
                 pdfFileName: String,
                 title: String,
                 fields: Seq[String] = Seq.empty,
                 recordIds: Seq[String] = Seq.empty,
                 openAfterCreation: Boolean = true
               ): Boolean =

    val records = selectRecords(loadRecords(xmlPath), recordIds)
    if records.isEmpty then
      false
    else
      val columns = columnsOf(records, fields)
      val rows = rowsOf(records, columns)
      createPdf(
        pdfFileName = pdfFileName,
        title = title,
        landscape = true,
        openAfterCreation = openAfterCreation
      ): document =>

        document.add(tableOf(columns, rows))

  def printDetails(
                    xmlPath: String,
                    recordId: String,
                    pdfFileName: String,
                    title: String,
                    additionalFields: Seq[(String, String)] = Seq.empty,
                    openAfterCreation: Boolean = true
                  ): Boolean =
    val record =
      loadRecords(xmlPath).find: record =>
          (record \ "id").text.trim == recordId

    record match
      case None =>
        false

      case Some(selected) =>
        createPdf(
          pdfFileName = pdfFileName,
          title = title,
          landscape = false,
          openAfterCreation = openAfterCreation
        ): document =>
          val table = new PdfPTable(2)

          table.setWidthPercentage(100f)
          table.setWidths(Array(1.5f, 4f))

          val xmlFields =
            selected.child.collect:
              case element: Elem =>
                element.label -> element.text.trim

          (xmlFields ++ additionalFields).foreach: (field, value) =>
            table.addCell(headerCell(field))
            table.addCell(value)

          document.add(table)

  def printSections(pdfFileName: String, title: String, sections: Seq[(String, Seq[String], Seq[Seq[String]])], openAfterCreation: Boolean = true): Boolean =
    if sections.forall(_._3.isEmpty) then
      false
    else
      createPdf(
        pdfFileName = pdfFileName,
        title = title,
        landscape = false,
        openAfterCreation = openAfterCreation
      ): document =>
        sections.foreach: (sectionTitle, headers, rows) =>
          if sectionTitle.nonEmpty then
            addTitle(document, sectionTitle)

          document.add(tableOf(headers, rows))

  def printDocumentManagementSummary(pdfFileName: String, data: SummaryPrintData, openAfterCreation: Boolean = true): Boolean =
    if data.phaseHeaders.isEmpty || data.phaseRows.exists(_.size != data.phaseHeaders.size) then
      false
    else
      createPdf(
        pdfFileName = pdfFileName,
        title = data.reportTitle,
        landscape = false,
        addDefaultTitle = false,
        pageEvent = Some(
          new SummaryFooter(
            generatedByLabel = data.generatedByLabel,
            generatedBy = data.generatedBy,
            pageLabel = data.pageLabel
          )
        ),
        bottomMargin = 55f,
        openAfterCreation = openAfterCreation
      ): document =>

        addSummaryHeader(
          document = document,
          applicationTitle = data.applicationTitle,
          reportTitle = data.reportTitle,
          generatedAtLabel = data.generatedAtLabel,
          generatedAt = data.generatedAt,
          logoResourcePath = data.logoResourcePath
        )

        addSectionTitle(document, data.documentDataSectionTitle)

        val documentTable = new PdfPTable(2)
        documentTable.setWidthPercentage(100f)
        documentTable.setWidths(Array(1.7f, 4f))
        documentTable.addCell(headerCell(data.documentCodeLabel))
        documentTable.addCell(data.documentCode)
        documentTable.addCell(headerCell(data.classificationLabel))
        documentTable.addCell(data.classification)
        document.add(documentTable)
        addSectionTitle(document, data.phasesSectionTitle)

        val phasesTable = tableOf(data.phaseHeaders, data.phaseRows)
        if data.phaseHeaders.size == 4 then
          phasesTable.setWidths(Array(1.6f, 1.7f, 1.3f, 2.4f))

        document.add(phasesTable)

  private def createPdf(
                         pdfFileName: String,
                         title: String,
                         landscape: Boolean,
                         addDefaultTitle: Boolean = true,
                         pageEvent: Option[PdfPageEventHelper] = None,
                         bottomMargin: Float = 36f,
                         openAfterCreation: Boolean = true
                       )(content: Document => Unit): Boolean =

    try
      printsFolder.mkdirs()

      val fileName = if pdfFileName.toLowerCase.endsWith(".pdf") then pdfFileName else s"$pdfFileName.pdf"
      val pdfFile = new File(printsFolder, fileName)
      val pageSize = if landscape then PageSize.A4.rotate() else PageSize.A4
      val document = new Document(pageSize, 36f, 36f, 36f, bottomMargin)
      Using.resource(new FileOutputStream(pdfFile)): os =>
        val writer = PdfWriter.getInstance(document, os)

        pageEvent.foreach: event =>
          writer.setPageEvent(event)

        document.open()

        try
          if addDefaultTitle then
            addTitle(document, title)

          content(document)
        finally
          document.close()

      if openAfterCreation then
        openPdf(pdfFile)

      true

    catch
      case exception: Exception =>
        println(s"Errore durante la creazione del PDF: ${exception.getMessage}")
        false

  private def addSummaryHeader(
                                document: Document,
                                applicationTitle: String,
                                reportTitle: String,
                                generatedAtLabel: String,
                                generatedAt: String,
                                logoResourcePath: String
                              ): Unit =

    val header = new PdfPTable(2)
    header.setWidthPercentage(100f)
    header.setWidths(Array(1f, 5f))
    header.setSpacingAfter(18f)
    val logoCell = new PdfPCell()
    logoCell.setBorder(Rectangle.NO_BORDER)
    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE)
    Option(getClass.getResource(logoResourcePath)).foreach: logoUrl =>
      val logo = Image.getInstance(logoUrl)
      logo.scaleToFit(50f, 50f)
      logoCell.addElement(logo)

    header.addCell(logoCell)

    val textCell = new PdfPCell()
    textCell.setBorder(Rectangle.NO_BORDER)
    val applicationParagraph = new Paragraph(applicationTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f))
    applicationParagraph.setSpacingAfter(4f)
    val reportParagraph = new Paragraph(reportTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13f))
    reportParagraph.setSpacingAfter(4f)

    val generatedAtParagraph = new Paragraph(s"$generatedAtLabel: $generatedAt", FontFactory.getFont(FontFactory.HELVETICA, 9f))
    textCell.addElement(applicationParagraph)
    textCell.addElement(reportParagraph)
    textCell.addElement(generatedAtParagraph)
    header.addCell(textCell)
    document.add(header)

  private def addSectionTitle(document: Document, title: String): Unit =
    val paragraph = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f))
    paragraph.setSpacingBefore(12f)
    paragraph.setSpacingAfter(7f)
    document.add(paragraph)

  private class SummaryFooter(generatedByLabel: String, generatedBy: String, pageLabel: String) extends PdfPageEventHelper:

    override def onEndPage(writer: PdfWriter, document: Document): Unit =
      val footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8f)
      val y = document.bottom() - 20f
      ColumnText.showTextAligned(
        writer.getDirectContent,
        Element.ALIGN_LEFT,
        new Phrase(s"$generatedByLabel: $generatedBy", footerFont), document.left(), y, 0f
      )

      ColumnText.showTextAligned(
        writer.getDirectContent,
        Element.ALIGN_RIGHT,
        new Phrase(s"$pageLabel ${writer.getPageNumber}", footerFont), document.right(), y, 0f
      )

  private def loadRecords(xmlPath: String): Seq[Elem] =
    (XML.loadFile(xmlPath) \\ "record")
      .collect:
        case element: Elem =>
          element

  private def addTitle(document: Document, title: String): Unit =
    val paragraph = new Paragraph(title)
    paragraph.setAlignment(Element.ALIGN_CENTER)
    paragraph.setSpacingAfter(15f)
    document.add(paragraph)

  private def headerCell(text: String): PdfPCell =
    val cell = new PdfPCell(new Phrase(text))
    cell.setGrayFill(0.85f)
    cell

  private def openPdf(file: File): Unit =
    try
      PdfViewer.open(file)
    catch
      case exception: Exception =>
        println(s"Impossibile aprire il PDF: ${exception.getMessage}")

  private def selectRecords(records: Seq[Elem], recordIds: Seq[String]): Seq[Elem] =
    if recordIds.isEmpty then
      records
    else
      records.filter: record =>
        recordIds.contains((record \ "id").text.trim)

  private def columnsOf(records: Seq[Elem], fields: Seq[String]): Seq[String] =
    val availableColumns =
      records
        .flatMap:
          _.child.collect:
            case element: Elem => element.label
        .distinct

    if fields.nonEmpty then
      fields.filter(availableColumns.contains)
    else
      availableColumns

  private def rowsOf(records: Seq[Elem], columns: Seq[String]): Seq[Seq[String]] =
    records.map: record =>
      columns.map: column =>
        (record \ column).text.trim

  private def tableOf(headers: Seq[String], rows: Seq[Seq[String]]): PdfPTable =
    val table = new PdfPTable(headers.size)
    table.setWidthPercentage(100f)
    headers.foreach: header =>
      table.addCell(headerCell(header))

    rows.foreach: row =>
      row.foreach: value =>
        table.addCell(value)

    table