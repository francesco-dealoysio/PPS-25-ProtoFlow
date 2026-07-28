package pkg.d.util

import pkg.b.logic.ErrorLog
import pkg.d.util.Util.inLogFilePathName

object Logger:

  // Evita che un errore avvenuto durante il logging stesso (es. scrittura di errors.xml)
  // inneschi una ricorsione infinita richiamando logger() su se stesso.
  private val loggingInProgress = new ThreadLocal[Boolean]:
    override def initialValue(): Boolean = false

  def getErrorLog(ex: Exception): ErrorLog =
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

  def logger(ex: Exception, console: Boolean = false): Unit =
    if loggingInProgress.get() then
      println(s"Errore durante il logging di un errore precedente (evitata ricorsione infinita): ${ex.getMessage}")
    else
      loggingInProgress.set(true)
      try
        import java.nio.file.{Files, Paths}
        import pkg.c.data.Xml.createEmptyXmlFile

        if (Files.notExists(Paths.get(inLogFilePathName("errors.xml"))))
          createEmptyXmlFile(inLogFilePathName("errors.xml"), "errors")

        ErrorLog().recordInsert(getErrorLog(ex), inLogFilePathName("errors.xml"))

        if console then
          val methodName = ex.getStackTrace.headOption.map(_.getMethodName.stripSuffix("$1"))
          methodName match
            case Some(something) =>
              println(something)
            case _ =>
              println(s"Errore in ${methodName}: ${ex.getMessage}")
      finally
        loggingInProgress.set(false)

  private def indent(stackTrace: Array[StackTraceElement]): String =
    stackTrace
      .map(elem => s"at $elem".indent(6)).mkString("")
      //.map(elem => " ".repeat(6) + s"at $elem").mkString("\n")

