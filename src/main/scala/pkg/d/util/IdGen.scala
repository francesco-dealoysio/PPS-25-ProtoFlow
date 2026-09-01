package pkg.d.util

import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.util.Try

object IdGen:

  def apply(idFilePath: String, startValue: Int = 0): String  =
    if Files.notExists(Paths.get(idFilePath)) then
      saveId(startValue.toString, idFilePath)

    loadId(idFilePath) match
      case Some(foundId) =>
        saveId((foundId.toInt + 1).toString, idFilePath)
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