package pkg.b.logic

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Formato di data a larghezza fissa usato per salvare le date di Registration come stringa (ordinabile e riparsabile). */
object RegistrationDates:

  private val storageFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

  def now(): String =
    LocalDateTime.now().format(storageFormatter)

  def parse(value: String): LocalDateTime =
    LocalDateTime.parse(value, storageFormatter)
