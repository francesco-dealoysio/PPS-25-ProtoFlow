package pkg.b.logic.pdf

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.{Orientation, PDFPageable}
import java.awt.print.PrinterJob
import java.awt.{Dimension, Toolkit}
import javax.print.PrintService
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.{DocFlavor, PrintServiceLookup, ServiceUI}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try, Using}

object PdfPrinter:

  def printPdf(pdfPathName: String): Unit =

    val result = Using(PDDocument.load(new java.io.File(pdfPathName))) { document =>

      val job = PrinterJob.getPrinterJob

      val printerService: Unit = choosePrinterServiceUI() match
        case Some(service) =>
          println(service)
          job.setPrintService(service)
          val pageAble = new PDFPageable(document, Orientation.AUTO)
          job.setPageable(pageAble)
          job.print()
          println("PDF sent to printer successfully.")
        case None =>
          println("Print cancelled.")
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

  private def printWithFuture(pdfPathName: String): Future[Unit] =
    Future {
      Using.resource(PDDocument.load(java.io.File(pdfPathName))) { doc =>
        val job = PrinterJob.getPrinterJob
        job.setPageable(PDFPageable(doc))
        job.print()
      }
    }

  private def choosePrinterServiceUI(): Option[PrintService] =

    val flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE
    val pras = new HashPrintRequestAttributeSet()
    val services = PrintServiceLookup.lookupPrintServices(flavor, pras)
    val defaultService = PrintServiceLookup.lookupDefaultPrintService()
    val activeServices = services.filter(service => getAttributeSafe(service, 500.millisecond))

    val screenSize: Dimension = Toolkit.getDefaultToolkit.getScreenSize
    val dialogWidth = 500
    val dialogHeight = 400
    val centerX = (screenSize.width - dialogWidth) / 2
    val centerY = (screenSize.height - dialogHeight) / 2

    ServiceUI.printDialog(null, centerX, centerY, activeServices, defaultService, flavor, pras) match
      case service if service != null =>
        Some(service)
      case null =>
        None