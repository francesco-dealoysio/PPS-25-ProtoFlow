package pkg.d.util

import org.scalatest.funsuite.AnyFunSuite
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId}
import scala.util.Try

class DateTimeTest extends AnyFunSuite:

  private val romeZone = ZoneId.of("Europe/Rome")
  private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
  private val storageDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS")
  private val storageDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val storageTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS")

  test("currentDateTime restituisce una stringa nel formato corretto"):
    val result = DateTime.currentDateTime
    assert(result.matches("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}:\d{2}"""))

  test("currentDateTime restituisce una data e un'ora valide"):
    val result = DateTime.currentDateTime
    val parsedResult = Try(LocalDateTime.parse(result, displayDateTimeFormatter))
    assert(parsedResult.isSuccess)

  test("currentDateTime restituisce un valore vicino all'ora corrente di Roma"):
    val before = LocalDateTime.now(romeZone)
    val result = LocalDateTime.parse(DateTime.currentDateTime, displayDateTimeFormatter)
    val after = LocalDateTime.now(romeZone)
    assert(!result.isBefore(before.minusSeconds(1)))
    assert(!result.isAfter(after.plusSeconds(1)))

  test("localDateTime restituisce una stringa nel formato corretto"):
    val result = DateTime.localDateTime
    assert(result.matches("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{2}"""))

  test("localDateTime restituisce una data e un'ora valide"):
    val result = DateTime.localDateTime
    val parsedResult = Try(LocalDateTime.parse(result, storageDateTimeFormatter))
    assert(parsedResult.isSuccess)

  test("localDateTime restituisce un valore vicino all'ora corrente di Roma"):
    val before = LocalDateTime.now(romeZone)
    val result = LocalDateTime.parse(DateTime.localDateTime, storageDateTimeFormatter)
    val after = LocalDateTime.now(romeZone)
    assert(!result.isBefore(before.minusSeconds(1)))
    assert(!result.isAfter(after.plusSeconds(1)))

  test("localDate restituisce una stringa nel formato corretto"):
    val result = DateTime.localDate
    assert(result.matches("""\d{4}-\d{2}-\d{2}"""))

  test("localDate restituisce una data valida"):
    val result = DateTime.localDate
    val parsedResult = Try(LocalDate.parse(result, storageDateFormatter))
    assert(parsedResult.isSuccess)

  test("localDate restituisce la data corrente di Roma"):
    val before = LocalDate.now(romeZone)
    val result = LocalDate.parse(DateTime.localDate, storageDateFormatter)
    val after = LocalDate.now(romeZone)
    assert(result == before || result == after)

  test("localTime restituisce una stringa nel formato corretto"):
    val result = DateTime.localTime
    assert(result.matches("""\d{2}:\d{2}:\d{2}\.\d{2}"""))

  test("localTime restituisce un orario valido"):
    val result = DateTime.localTime
    val parsedResult = Try(LocalTime.parse(result, storageTimeFormatter))
    assert(parsedResult.isSuccess)
