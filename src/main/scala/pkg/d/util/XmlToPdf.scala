package pkg.d.util

import org.openpdf.text.*
import org.openpdf.text.pdf.*

import java.io.{File, FileOutputStream}
import scala.xml.{Elem, XML}

object XmlToPdf:

  private val printsFolder = new File(System.getProperty("user.dir"), "protoflow/prints")

  def printList(xmlPath: String, pdfFileName: String, title: String, fields: Seq[String] = Seq.empty, recordIds: Seq[String] = Seq.empty): Boolean =
    val allRecords = loadRecords(xmlPath)

    val records =
      if recordIds.nonEmpty then
        allRecords.filter: record =>
          recordIds.contains((record \ "id").text.trim)
      else
        allRecords

    if records.isEmpty then
      false
    else
      createPdf(pdfFileName, title, landscape = true): document =>
        val availableColumns =
          records
          .flatMap:
            _.child.collect:
              case element: Elem => element.label
          .distinct
        
        val columns =
          if fields.nonEmpty then
            fields.filter(availableColumns.contains)
          else
            availableColumns

        val table = new PdfPTable(columns.size)

        table.setWidthPercentage(100f)

        columns.foreach: column =>
          table.addCell(headerCell(column))

        records.foreach: record =>
          columns.foreach: column =>
            table.addCell(
              (record \ column).text.trim
            )

        document.add(table)

  def printDetails(xmlPath: String, recordId: String, pdfFileName: String, title: String): Boolean =
    val record =
      loadRecords(xmlPath)
        .find: record =>
          (record \ "id").text.trim == recordId

    record match
      case None =>
        false

      case Some(selected) =>
        createPdf(pdfFileName, title, landscape = false): document =>
          val table = new PdfPTable(2)

          table.setWidthPercentage(100f)
          table.setWidths(Array(1.5f, 4f))

          selected.child.collect:
            case element: Elem =>
              element.label -> element.text.trim
          .foreach: (field, value) =>
            table.addCell(headerCell(field))
            table.addCell(value)

          document.add(table)

  def printSections(pdfFileName: String, title: String, sections: Seq[(String, Seq[String], Seq[Seq[String]])]): Boolean =
    if sections.forall(_._3.isEmpty) then
      false
    else
      createPdf(pdfFileName, title, landscape = false): document =>
        sections.foreach: (sectionTitle, headers, rows) =>
          addTitle(document, sectionTitle)

          val table = new PdfPTable(headers.size)
          table.setWidthPercentage(100f)

          headers.foreach: header =>
            table.addCell(headerCell(header))

          rows.foreach: row =>
            row.foreach: cell =>
              table.addCell(cell)

          document.add(table)

  private def createPdf(pdfFileName: String, title: String, landscape: Boolean)(content: Document => Unit): Boolean =
    var document: Document = null

    try
      printsFolder.mkdirs()

      val fileName =
        if pdfFileName.toLowerCase.endsWith(".pdf") then
          pdfFileName
        else
          s"$pdfFileName.pdf"

      val pageSize = if landscape then PageSize.A4.rotate() else PageSize.A4

      document = new Document(pageSize)

      PdfWriter.getInstance(document, new FileOutputStream(new File(printsFolder, fileName)))

      document.open()
      addTitle(document, title)
      content(document)

      true

    catch
      case exception: Exception =>
        println(s"Errore durante la creazione del PDF: ${exception.getMessage}")
        false

    finally
      if document != null && document.isOpen then
        document.close()

  private def loadRecords(xmlPath: String): Seq[Elem] =
    (XML.loadFile(xmlPath) \\ "record").collect:
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