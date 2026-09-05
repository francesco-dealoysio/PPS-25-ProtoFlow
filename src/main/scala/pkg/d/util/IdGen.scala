package pkg.d.util

import pkg.d.util.Logger.logger
import pkg.d.util.Util.inIdsFilePathName
import sun.jvm.hotspot.HelloWorld.e

import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.util.Try

object IdGen:

  def apply(idFilePath: String, startValue: Int = 0): String  =

    var caller: java.lang.StackTraceElement = null
    try
      val stackTrace = Thread.currentThread().getStackTrace
      if stackTrace.length > 2 then
        caller = stackTrace(2)
      else
        println("Caller information not available.")
    catch
      case e: Exception =>
        println(s"Error retrieving caller info: ${e.getMessage}")
        logger(e)
    
    if Files.notExists(Paths.get(idFilePath)) then
      saveId(startValue.toString, idFilePath)

    loadId(idFilePath) match
      case Some(foundId) =>
        if caller.getFileName != "Init.scala" then
          saveId((foundId.toInt + 1).toString, idFilePath)
        else
          saveId((foundId.toInt).toString, idFilePath)
        foundId
      case None =>
        "Failed to find Id!"

  private def saveId(id: String, idFilePath: String): Boolean =
    Try {
      Files.writeString(
        Paths.get(idFilePath),
        id,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      )
    }.isSuccess

  private def loadId(idFilePath: String): Option[String] =
    if Files.exists(Paths.get(idFilePath)) then
      Try(Files.readString(Paths.get(idFilePath)).trim).toOption.filter(_.nonEmpty)
    else
      None