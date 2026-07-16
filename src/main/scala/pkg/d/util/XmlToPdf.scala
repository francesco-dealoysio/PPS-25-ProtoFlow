package pkg.d.util

import org.openpdf.text.*
import org.openpdf.text.pdf.*

import java.io.{File, FileOutputStream}
import scala.xml.{Elem, XML}

object XmlToPdf:

  private val printsFolder =
    new File(
      System.getProperty("user.dir"),
      "src/main/resources/prints"
    )

  def print(xmlPath: String, pdfFileName: String, title: String = "Stampa dati"): Boolean =
    try
      val xml = XML.loadFile(xmlPath)

      val records =
        (xml \\ "record").collect:
          case element: Elem => element

      if records.isEmpty then
        false
      else
        val columns =
          records
            .flatMap:
              _.child.collect:
                case element: Elem =>
                  element.label
            .distinct

        if !printsFolder.exists() then
          printsFolder.mkdirs()

        val normalizedFileName =
          if pdfFileName.toLowerCase.endsWith(".pdf") then
            pdfFileName
          else
            s"$pdfFileName.pdf"

        val pdfFile =
          new File(
            printsFolder,
            normalizedFileName
          )

        val document = new Document(PageSize.A4.rotate())

        PdfWriter.getInstance(
          document,
          new FileOutputStream(pdfFile)
        )

        document.open()

        val titleParagraph = new Paragraph(title)
        titleParagraph.setAlignment(Element.ALIGN_CENTER)
        titleParagraph.setSpacingAfter(15f)
        document.add(titleParagraph)

        val table = new PdfPTable(columns.size)
        table.setWidthPercentage(100f)
        columns.foreach: column =>
          val cell =
            new PdfPCell(
              new Phrase(column)
            )
          cell.setGrayFill(0.85f)
          table.addCell(cell)

        records.foreach: record =>
          columns.foreach: column =>
            val value =
              (record \ column)
                .text
                .trim
            table.addCell(value)

        document.add(table)
        document.close()
        println(s"PDF creato in: ${pdfFile.getAbsolutePath}")
        true

    catch
      case exception: Exception =>
        println(s"Errore durante la creazione del PDF: ${exception.getMessage}")

        false