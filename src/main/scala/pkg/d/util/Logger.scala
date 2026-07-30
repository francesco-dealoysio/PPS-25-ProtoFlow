package pkg.d.util

import pkg.b.logic.ErrorLog
import pkg.d.util.Util.inLogFilePathName
import java.nio.file.{Files, Path, Paths}

object Logger:

  private def logFilePath: Path =
    val configuredPath = Paths.get(inLogFilePathName("errors.xml"))
    val parent = configuredPath.getParent

    if parent != null && Files.exists(parent) then
      configuredPath
    else
      Paths
        .get(System.getProperty("java.io.tmpdir"))
        .resolve("protoflow")
        .resolve("log")
        .resolve("errors.xml")

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
      println(s"Errore durante il logging di un errore precedente " + s"(evitata ricorsione infinita): ${ex.getMessage}")
    else
      loggingInProgress.set(true)
      try
        import java.nio.file.{Files, Path, Paths}
        import pkg.c.data.Xml.createEmptyXmlFile

        val configuredPath = Paths.get(inLogFilePathName("errors.xml"))
        val logPath: Path =
          val parent = configuredPath.getParent

          if parent != null && Files.exists(parent) then
            configuredPath
          else
            Paths
              .get(System.getProperty("java.io.tmpdir"))
              .resolve("protoflow")
              .resolve("log")
              .resolve("errors.xml")

        Option(logPath.getParent).foreach: parent =>
          Files.createDirectories(parent)

        if Files.notExists(logPath) then
          createEmptyXmlFile(logPath.toString, "errors")
          ErrorLog().recordInsert(getErrorLog(ex), logPath.toString)

        if console then
          val methodName =
            ex.getStackTrace
              .headOption
              .map(_.getMethodName.stripSuffix("$1"))

          methodName match
            case Some(name) =>
              println(name)
            case None =>
              println(s"Errore: ${ex.getMessage}")
      finally
        loggingInProgress.set(false)

  private def indent(stackTrace: Array[StackTraceElement]): String =
    stackTrace
      .map(elem => s"at $elem".indent(6)).mkString("")
      //.map(elem => " ".repeat(6) + s"at $elem").mkString("\n")