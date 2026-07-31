package pkg.d.util

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.StringProperty
import scalafx.util.Duration

import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId}
import java.time.format.DateTimeFormatter

object DateTime:

  private val RomeZone: ZoneId = ZoneId.of("Europe/Rome")
  private val StorageDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
  private val StorageDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val StorageTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS")
  private val DisplayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

  def currentDateTime: String =
    LocalDateTime.now(RomeZone).format(DisplayDateTimeFormatter)

  def localDateTime: String =
    LocalDateTime.now(RomeZone).format(StorageDateTimeFormatter)

  def localDate: String =
    LocalDate.now(RomeZone).format(StorageDateFormatter)

  def localTime: String =
    LocalTime.now(RomeZone).format(StorageTimeFormatter)

  def dynamicDateTimeProperty(): StringProperty =
    val dateTimeProperty =
      StringProperty(currentDateTime)

    val timeline =
      new Timeline:
        cycleCount = Timeline.Indefinite
        keyFrames = Seq(
          KeyFrame(
            time = Duration(1000),
            onFinished = _ =>
              dateTimeProperty.value = currentDateTime
          )
        )

    timeline.play()

    dateTimeProperty
