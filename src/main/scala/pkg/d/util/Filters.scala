package pkg.d.util

import pkg.b.logic.DocumentLog

object Filters

  @main def tryFilters: Unit =
    println("Test Filters")

    // filter fields
    val documentId = "10"
    val documentOperation = "loading"
    val operationDate = "2026-07-10"
    val lowerDate = "2026-07-11"
    val upperDate = "2026-07-12"
    val operator = "  NeRi    "

/*
    val fieldMap: Map[String, String] = Map(
      "getDocumentId" -> documentId.text.value.trim,
      "getOperationType" -> documentOperation.text.value.trim,
      "getProcessedDate" -> operationDate.text.value.trim,
      "getProcessedBy" -> operator.text.value.trim
    )
*/

    var op = "=="
    val varName = "documentId"
    var varValue = "10"
    val fieldName = "getDocumentId"

    // filter by specific id
    val predicate1: DocumentLog => Boolean = r =>
      r.getDocumentId == documentId

    // filter if contained in a list of values
    val allowedIds = List("10", "4")
    val predicate2: DocumentLog => Boolean = r =>
      allowedIds.contains(r.getDocumentId)

    // filter by multiple predicates
    val predicate3: DocumentLog => Boolean = r =>
      (r.getOperationType == "loading" || r.getOperationType == "registering") &&
      (r.getProcessedBy == "Rossi" || r.getProcessedBy == "Bianchi")

    // filter for date > of a specific date
    val predicate4: DocumentLog => Boolean = r =>
      r.getProcessedDate > operationDate

    // filter for date > of a specific date
    val predicate5: DocumentLog => Boolean = r =>
      r.getProcessedDate >= lowerDate.trim && r.getProcessedDate <= upperDate.trim

    // filter if date il contained in a list of dates
    val allowedDates = List("2025-07-10", "2023-12-25", "2024-05-01")
    val predicate6: DocumentLog => Boolean = r =>
      allowedDates.contains(r.getProcessedDate)

    // filter that excludes values
    val predicate7: DocumentLog => Boolean = r =>
      r.getProcessedBy.toLowerCase != operator.trim.toLowerCase

    val result = DocumentLog().getRecordsByFilter[DocumentLog](predicate7)

    result.foreach(r => println(r))


