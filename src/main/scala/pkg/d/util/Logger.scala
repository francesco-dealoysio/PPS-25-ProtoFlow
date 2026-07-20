package pkg.d.util

import pkg.b.logic.ErrorLog
import pkg.d.util.Util.inLogFilePathName

object Logger:

  def getErrorLog(ex: Exception): ErrorLog =
    import pkg.d.util.Util.*
    val errorLog = ErrorLog()
    val topElement = ex.getStackTrace.headOption
    topElement match
      case Some(ste) =>
        errorLog.setId(IdGen(inIdsFilePathName("errorlogid")))
        errorLog.setDate(localDate)
        errorLog.setTime(localTime)
        errorLog.setClass(ste.getClassName.stripSuffix("$"))
        errorLog.setMethod(ste.getMethodName.stripSuffix("$1"))
        errorLog.setLine(ste.getLineNumber.toString)
        errorLog.setMessage(ex.getMessage)
        errorLog.setStack(indent(ex.getStackTrace))
    errorLog

  def logger(ex: Exception, console: Boolean = false): Unit =

    ErrorLog().recordInsert(getErrorLog(ex), inLogFilePathName("errors.xml"))

    if console then
      val methodName = ex.getStackTrace.headOption.map(_.getMethodName.stripSuffix("$1"))
      methodName match
        case Some(something) =>
          println(something)
        case _ =>
          println(s"Errore in ${methodName}: ${ex.getMessage}")

  private def indent(stackTrace: Array[StackTraceElement]): String =
    stackTrace
      .map(elem => s"at $elem".indent(6)).mkString("")
      //.map(elem => " ".repeat(6) + s"at $elem").mkString("\n")

