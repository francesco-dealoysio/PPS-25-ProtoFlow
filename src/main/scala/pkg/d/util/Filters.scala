package pkg.d.util

import pkg.b.logic.DocumentLog

object Filters

@main def tryFilters: Unit =

  case class FilterCriteria(
                             field: String,
                             operator: String,
                             value: List[String]
                           )

  case class CriteriaGroup(criteria: List[FilterCriteria], combineWithAnd: Boolean)

  def buildPredicate(criteria: FilterCriteria): Any => Boolean = {
    criteria match

      case FilterCriteria("getDocumentId", "<", v) =>
        val threshold = v(0).toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.toInt < threshold
          case _ => false

      case FilterCriteria("getDocumentId", "<=", v) =>
        val threshold = v(0).toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.toInt <= threshold
          case _ => false

      case FilterCriteria("getDocumentId", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId == v(0)
          case _ => false

      case FilterCriteria("getDocumentId", ">", v) =>
        val threshold = v(0).toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.toInt > threshold
          case _ => false

      case FilterCriteria("getDocumentId", ">=", v) =>
        val threshold = v(0).toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.toInt >= threshold
          case _ => false

      case FilterCriteria("getDocumentId", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.contains(r.getDocumentId)
          case _ => false

      case FilterCriteria("getProcessedDate", "<", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate < v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", "<=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate <= v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate == v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", ">", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate > v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", ">=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate >= v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.contains(r.getProcessedDate)
          case _ => false

      case FilterCriteria("getProcessedBy", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedBy.toLowerCase == v(0).trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "!=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedBy.toLowerCase != v(0).trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.contains(r.getProcessedBy)
          case _ => false

      case FilterCriteria("getOperationType", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getOperationType.toLowerCase == v(0).trim.toLowerCase()
          case _ => false

      case FilterCriteria("getOperationType", "!=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.contains(r.getOperationType)
          case _ => false

      case FilterCriteria("getOperationType", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.contains(r.getOperationType)
          case _ => false

      case _ => _ => false
  }

  def combinePredicates(group: CriteriaGroup): Any => Boolean = {
    val preds = group.criteria.map(buildPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))
  }

  val guiCriteria = CriteriaGroup(
    List(
      FilterCriteria("getDocumentId", ">", List("3")),
      FilterCriteria("getDocumentId", "<", List("21")),
      FilterCriteria("getProcessedDate", ">=", List("2026-07-09")),
      FilterCriteria("getProcessedDate", "<=", List("2026-07-21")),
      FilterCriteria("getProcessedBy", "!=", List("Rossi")),
      FilterCriteria("getOperationType", "contains", List("loading","archiving")),
    ),
    combineWithAnd = true
  )

  val predicate = combinePredicates(guiCriteria)

  val result = DocumentLog().getRecordsByFilter[DocumentLog](predicate)

  result.foreach(r => println(r))

/*
    // filter fields
    val documentId = "10"
    val documentOperation = "loading"
    val operationDate = "2026-07-10"
    val lowerDate = "2026-07-11"
    val upperDate = "2026-07-12"
    val operator = "  NeRi    "

    // OK filter by specific id
    val predicate1: DocumentLog => Boolean = r =>
      r.getDocumentId == documentId

    // OK filter if contained in a list of values
    val allowedIds = List("10", "4")
    val predicate2: DocumentLog => Boolean = r =>
      allowedIds.contains(r.getDocumentId)

    // OK filter by multiple predicates
    val predicate3: DocumentLog => Boolean = r =>
      (r.getOperationType == "loading" || r.getOperationType == "registering") &&
      (r.getProcessedBy == "Rossi" || r.getProcessedBy == "Bianchi")

    // OK filter for date > of a specific date
    val predicate4: DocumentLog => Boolean = r =>
      r.getProcessedDate > operationDate

    // OK filter date within a specific range
    val predicate5: DocumentLog => Boolean = r =>
      r.getProcessedDate >= lowerDate.trim && r.getProcessedDate <= upperDate.trim

    // OK filter if date il contained in a list of dates
    val allowedDates = List("2025-07-10", "2023-12-25", "2024-05-01")
    val predicate6: DocumentLog => Boolean = r =>
      allowedDates.contains(r.getProcessedDate)

    // OK filter that excludes values
    val predicate7: DocumentLog => Boolean = r =>
      r.getProcessedBy.toLowerCase != operator.trim.toLowerCase

    val result = DocumentLog().getRecordsByFilter[DocumentLog](predicate7)

    result.foreach(r => println(r))
*/

