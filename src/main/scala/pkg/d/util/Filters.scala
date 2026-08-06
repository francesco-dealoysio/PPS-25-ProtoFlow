package pkg.d.util

object Filters

  private case class FilterCriteria(
                             field: String,
                             operator: String,
                             value: List[String]
                           )

  private case class CriteriaGroup(criteria: List[FilterCriteria], combineWithAnd: Boolean)

  private def buildDocumentLogPredicate(criteria: FilterCriteria): Any => Boolean = {
    import pkg.b.logic.DocumentLog

    criteria match

      case FilterCriteria("getDocumentId", "<", v) =>
        val threshold = v(0).trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt < threshold
          case _ => false

      case FilterCriteria("getDocumentId", "<=", v) =>
        val threshold = v(0).trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt <= threshold
          case _ => false

      case FilterCriteria("getDocumentId", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim == v(0)
          case _ => false

      case FilterCriteria("getDocumentId", ">", v) =>
        val threshold = v(0).trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt > threshold
          case _ => false

      case FilterCriteria("getDocumentId", ">=", v) =>
        val threshold = v(0).trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt >= threshold
          case _ => false

      case FilterCriteria("getDocumentId", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim).contains(r.getDocumentId.trim)
          case _ => false

      case FilterCriteria("getProcessedDate", "<", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim < v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", "<=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim <= v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim == v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", ">", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim > v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", ">=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim >= v(0)
          case _ => false

      case FilterCriteria("getProcessedDate", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim).contains(r.getProcessedDate.trim)
          case _ => false

      case FilterCriteria("getProcessedBy", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedBy.trim.toLowerCase == v(0).trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "!=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedBy.trim.toLowerCase != v(0).trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim.toLowerCase).contains(r.getProcessedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getOperationType", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getOperationType.trim.toLowerCase == v(0).trim.toLowerCase()
          case _ => false

      case FilterCriteria("getOperationType", "!=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getOperationType.trim.toLowerCase != v(0).trim.toLowerCase
          case _ => false

      case FilterCriteria("getOperationType", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim.toLowerCase).contains(r.getOperationType.trim.toLowerCase)
          case _ => false

      case _ => _ => false
  }

  private def combineDocumentLogPredicates(group: CriteriaGroup): Any => Boolean = {
    val preds = group.criteria.map(buildDocumentLogPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))
  }

  def getDocumentLogPredicate(criteria: List[(String, String, List[String])]): Any => Boolean = {
    val criteriaGroup = CriteriaGroup(
      criteria.map((f, o, v) => FilterCriteria(f, o, v)),
      combineWithAnd = true
    )
    combineDocumentLogPredicates(criteriaGroup)
  }

@main def tryFilters: Unit =
  println("Tested in FiltersTest")
  import pkg.b.logic.DocumentLog

  val predicate = getDocumentLogPredicate(
    List(
      ("getDocumentId", ">", List("3")),
      ("getDocumentId", "<", List("21")),
      ("getProcessedDate", ">=", List("2026-07-09")),
      ("getProcessedDate", "<=", List("2026-07-21")),
      ("getProcessedBy", "!=", List("Rossi")),
      ("getOperationType", "contains", List("loading", "archiving")),
    )
  )

  val result = DocumentLog().getRecordsByFilter[DocumentLog](predicate)
  result.foreach(r => println(r))