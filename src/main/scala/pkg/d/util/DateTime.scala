package pkg.d.util

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.StringProperty
import scalafx.util.Duration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTime:

  private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

  def currentDateTime: String =
    LocalDateTime
      .now()
      .format(dateTimeFormatter)

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
