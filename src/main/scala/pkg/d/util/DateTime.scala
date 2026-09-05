package pkg.d.util

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.StringProperty
import scalafx.util.Duration

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId}

object DateTime:

  private val ClockRefreshInterval = Duration(1000)
  private val romeZone: ZoneId = ZoneId.of("Europe/Rome")
  private val storageDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
  private val storageDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val storageTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS")
  private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

  def currentDisplayDateTime: String =
    LocalDateTime.now(romeZone).format(displayDateTimeFormatter)

  def currentStorageDateTime: String =
    LocalDateTime.now(romeZone).format(storageDateTimeFormatter)

  def localDate: String =
    LocalDate.now(romeZone).format(storageDateFormatter)

  def localTime: String =
    LocalTime.now(romeZone).format(storageTimeFormatter)

  def parseDateTime(value: String): LocalDateTime =
    LocalDateTime.parse(value, storageDateTimeFormatter)

  def parseDate(value: String): LocalDate =
    LocalDate.parse(value, storageDateFormatter)

  def displayDateTime(value: String): String =
    parseDateTime(value).format(displayDateTimeFormatter)

  def dynamicDateTimeProperty(): StringProperty =
    val dateTimeProperty = StringProperty(currentDisplayDateTime)

    val timeline =
      new Timeline:
        cycleCount = Timeline.Indefinite
        keyFrames = Seq(
          KeyFrame(
            time = ClockRefreshInterval,
            onFinished = _ => dateTimeProperty.value = currentDisplayDateTime
          )
        )

    timeline.play()

    dateTimeProperty