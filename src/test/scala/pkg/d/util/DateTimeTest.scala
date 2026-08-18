package pkg.d.util

import org.junit.*
import org.junit.Assert.*
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId}
import scala.util.Try

class DateTimeTest:

  private val romeZone = ZoneId.of("Europe/Rome")
  private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
  private val storageDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
  private val storageDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val storageTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS")

  @Test
  def testCurrentDateTimeFormat(): Unit =
    val result = DateTime.currentDateTime
    assertTrue(result.matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}:\d{2}"""))

  @Test
  def testCurrentDateTimeValid(): Unit =
    val result = DateTime.currentDateTime
    val parsedResult = Try(LocalDateTime.parse(result, displayDateTimeFormatter))
    assertTrue(parsedResult.isSuccess)

  @Test
  def testCurrentDateTimeCloseToRomeTime(): Unit =
    val before = LocalDateTime.now(romeZone)
    val result = LocalDateTime.parse(DateTime.currentDateTime, displayDateTimeFormatter)
    val after = LocalDateTime.now(romeZone)
    assertFalse(result.isBefore(before.minusSeconds(1)))
    assertFalse(result.isAfter(after.plusSeconds(1)))

  @Test
  def testLocalDateTimeFormat(): Unit =
    val result = DateTime.localDateTime
    assertTrue(result.matches("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{2}"""))

  @Test
  def testLocalDateTimeValid(): Unit =
    val result = DateTime.localDateTime
    val parsedResult = Try(LocalDateTime.parse(result, storageDateTimeFormatter))
    assertTrue(parsedResult.isSuccess)

  @Test
  def testLocalDateTimeCloseToRomeTime(): Unit =
    val before = LocalDateTime.now(romeZone)
    val result = LocalDateTime.parse(DateTime.localDateTime, storageDateTimeFormatter)
    val after = LocalDateTime.now(romeZone)
    assertFalse(result.isBefore(before.minusSeconds(1)))
    assertFalse(result.isAfter(after.plusSeconds(1)))

  @Test
  def testLocalDateFormat(): Unit =
    val result = DateTime.localDate
    assertTrue(result.matches("""\d{4}-\d{2}-\d{2}"""))

  @Test
  def testLocalDateValid(): Unit =
    val result = DateTime.localDate
    val parsedResult = Try(LocalDate.parse(result, storageDateFormatter))
    assertTrue(parsedResult.isSuccess)

  @Test
  def testLocalDateIsCurrentRomeDate(): Unit =
    val before = LocalDate.now(romeZone)
    val result = LocalDate.parse(DateTime.localDate, storageDateFormatter)
    val after = LocalDate.now(romeZone)
    assertTrue(result == before || result == after)

  @Test
  def testLocalTimeFormat(): Unit =
    val result = DateTime.localTime
    assertTrue(result.matches("""\d{2}:\d{2}:\d{2}\.\d{2}"""))

  @Test
  def testLocalTimeValid(): Unit =
    val result = DateTime.localTime
    val parsedResult = Try(LocalTime.parse(result, storageTimeFormatter))
    assertTrue(parsedResult.isSuccess)

  @Test
  def testParseDateTime(): Unit =
    val value = "2026-07-10 10:00:00.00"
    val result = DateTime.parseDateTime(value)
    assertEquals(LocalDateTime.of(2026, 7, 10, 10, 0, 0, 0), result)

  @Test
  def testParseDate(): Unit =
    val value = "2026-08-13"
    val result = DateTime.parseDate(value)
    assertEquals(LocalDate.of(2026, 8, 13), result)