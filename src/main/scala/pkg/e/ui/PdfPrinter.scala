package pkg.e.ui

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.{Orientation, PDFPageable}
import javax.print.{PrintService, PrintServiceLookup}
import java.awt.print.PrinterJob
import javax.print.PrintServiceLookup
import javax.print.attribute.standard.PrinterName
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure, Using}
import pkg.d.util.Util.inPrintsFilePathName

object PdfPrinter:

  def printPdf(pdfPathName: String): Unit =

    val result = Using(PDDocument.load(new java.io.File(pdfPathName))) { document =>

      val job = PrinterJob.getPrinterJob

      val printerService = choosePrinterServiceUI() match
        case Some(service) =>
          println(service)
          job.setPrintService(service)
          val pageAble = new PDFPageable(document, Orientation.AUTO)
          job.setPageable(pageAble)
          job.print()
          println("PDF sent to printer successfully.")
        case None =>
          println("Print cancelled.")
          //job.setPrintService(PrintServiceLookup.lookupDefaultPrintService())
        case _ =>
          println("Failure")
          throw new IllegalArgumentException("Printer not found.")
    }

    result match
      case scala.util.Success(_) =>
        println("Program completed.")
      case scala.util.Failure(ex) =>
        println(s"Error printing PDF: ${ex.getMessage}")

  private def getAttributeSafe(service: javax.print.PrintService, timeout: FiniteDuration): Boolean =
    val future = Future {
      Try(service.getAttributes) match
        case Success(value) => true
        case Failure(_) => false
    }
    Try(Await.result(future, timeout)).isSuccess

  private def choosePrinterServiceUI(): Option[PrintService] =
    import javax.print.attribute.HashPrintRequestAttributeSet
    import javax.print.{DocFlavor, PrintServiceLookup, ServiceUI}
    //import javax.print.attribute.standard._

    val flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE
    val pras = new HashPrintRequestAttributeSet()
    val services = PrintServiceLookup.lookupPrintServices(flavor, pras)
    val defaultService = PrintServiceLookup.lookupDefaultPrintService()
    val activeServices = services.filter(service => getAttributeSafe(service, 1.second))

    ServiceUI.printDialog(null, 200, 200, activeServices, defaultService, flavor, pras) match
      case service if service != null =>
        Some(service)
      case null =>
        None

@main def tryPdfPrinter: Unit =
  PdfPrinter.printPdf(inPrintsFilePathName("AccountList.pdf"))