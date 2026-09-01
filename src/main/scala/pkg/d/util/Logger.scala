package pkg.d.util

import pkg.b.logic.ErrorLog
import pkg.d.util.Util.inLogFilePathName
import pkg.d.util.DateTime.{localDate, localTime}
import java.nio.file.{Files, Paths}
import pkg.c.data.Xml.createEmptyXmlFile

object Logger:

  def logger(ex: Exception, console: Boolean = false): Unit =
    if loggingInProgress.get() then
      println(s"Errore durante il logging di un errore precedente " + s"(evitata ricorsione infinita): ${ex.getMessage}")
    else
      loggingInProgress.set(true)
      try
        if (Files.notExists(Paths.get(inLogFilePathName("errors.xml"))))
          createEmptyXmlFile(inLogFilePathName("errors.xml"), "errors")

        ErrorLog().recordInsert(getErrorLog(ex), inLogFilePathName("errors.xml"))

        if console then
          val methodName = ex.getStackTrace.headOption.map(_.getMethodName.stripSuffix("$1"))

          methodName match
            case Some(name) => println(name)
            case None => println(s"Errore: ${ex.getMessage}")
      finally
        loggingInProgress.set(false)

  private val loggingInProgress = new ThreadLocal[Boolean]:
    override def initialValue(): Boolean = false

  private def getErrorLog(ex: Exception): ErrorLog =
    import pkg.d.util.Util.*
    val errorLog = ErrorLog()
    val topElement = ex.getStackTrace.headOption
    topElement match
      case Some(ste) =>
        errorLog.setId(IdGen(inIdsFilePathName("errorlogId")))
        errorLog.setDate(localDate)
        errorLog.setTime(localTime)
        errorLog.setClass(ste.getClassName.stripSuffix("$"))
        errorLog.setMethod(ste.getMethodName.stripSuffix("$1"))
        errorLog.setLine(ste.getLineNumber.toString)
        errorLog.setMessage(ex.getMessage)
        errorLog.setStack(indent(ex.getStackTrace))
      case None =>
        errorLog.setId(IdGen(inIdsFilePathName("errorlogId")))
        errorLog.setDate(localDate)
        errorLog.setTime(localTime)
        errorLog.setMessage(ex.getMessage)
    errorLog

  private def indent(stackTrace: Array[StackTraceElement]): String =
    stackTrace
      .map(elem => s"at $elem".indent(6)).mkString("")