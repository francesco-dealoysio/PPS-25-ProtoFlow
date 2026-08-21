package pkg.d.util

object Filters:

  private case class FilterCriteria(
                             field: String,
                             operator: String,
                             value: List[String]
                           )

  private case class CriteriaGroup(criteria: List[FilterCriteria], combineWithAnd: Boolean)

  private def buildDocumentOperationsLogPredicate(criteria: FilterCriteria): Any => Boolean = {
    import pkg.b.logic.DocumentLog

    criteria match

      case FilterCriteria("getDocumentId", "<", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt < threshold
          case _ => false

      case FilterCriteria("getDocumentId", "<=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt <= threshold
          case _ => false

      case FilterCriteria("getDocumentId", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim == v.head
          case _ => false

      case FilterCriteria("getDocumentId", ">", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt > threshold
          case _ => false

      case FilterCriteria("getDocumentId", ">=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) => obj match
          case r: DocumentLog => r.getDocumentId.trim.toInt >= threshold
          case _ => false

      case FilterCriteria("getDocumentId", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim).contains(r.getDocumentId.trim)
          case _ => false

      case FilterCriteria("getProcessedDate", "<", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim < v.head
          case _ => false

      case FilterCriteria("getProcessedDate", "<=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim <= v.head
          case _ => false

      case FilterCriteria("getProcessedDate", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim == v.head
          case _ => false

      case FilterCriteria("getProcessedDate", ">", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim > v.head
          case _ => false

      case FilterCriteria("getProcessedDate", ">=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedDate.trim >= v.head
          case _ => false

      case FilterCriteria("getProcessedDate", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim).contains(r.getProcessedDate.trim)
          case _ => false

      case FilterCriteria("getProcessedBy", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedBy.trim.toLowerCase == v.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "!=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getProcessedBy.trim.toLowerCase != v.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim.toLowerCase).contains(r.getProcessedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getOperationType", "=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getOperationType.trim.toLowerCase == v.head.trim.toLowerCase()
          case _ => false

      case FilterCriteria("getOperationType", "!=", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => r.getOperationType.trim.toLowerCase != v.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getOperationType", "contains", v) =>
        (obj: Any) => obj match
          case r: DocumentLog => v.map(_.trim.toLowerCase).contains(r.getOperationType.trim.toLowerCase)
          case _ => false

      case _ => _ => false
  }

  private def buildDocumentPredicate(criteria: FilterCriteria): Any => Boolean = {
    import pkg.b.logic.ArchivedDocument

    criteria match

      case FilterCriteria("getId", "<", v) =>
          val threshold = v.head.trim.toInt
          (obj: Any) =>
            obj match
              case r: ArchivedDocument => r.getId.trim.toInt < threshold
              case _ => false

      case FilterCriteria("getId", "<=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getId.trim.toInt <= threshold
            case _ => false

      case FilterCriteria("getId", "=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getId.trim == v.head
            case _ => false

      case FilterCriteria("getId", ">", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getId.trim.toInt > threshold
            case _ => false

      case FilterCriteria("getId", ">=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getId.trim.toInt >= threshold
            case _ => false

      case FilterCriteria("getId", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => v.map(_.trim).contains(r.getId.trim)
            case _ => false

      case FilterCriteria("getArchivedDate", "<", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedDate.trim < v.head
            case _ => false

      case FilterCriteria("getArchivedDate", "<=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedDate.trim <= v.head
            case _ => false

      case FilterCriteria("getArchivedDate", "=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedDate.trim == v.head
            case _ => false

      case FilterCriteria("getArchivedDate", ">", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedDate.trim > v.head
            case _ => false

      case FilterCriteria("getArchivedDate", ">=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedDate.trim >= v.head
            case _ => false

      case FilterCriteria("getArchivedDate", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => v.map(_.trim).contains(r.getArchivedDate.trim)
            case _ => false

      case FilterCriteria("getLoadedBy", "=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getLoadedBy.trim.toLowerCase == v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getLoadedBy", "!=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getLoadedBy.trim.toLowerCase != v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getLaodedBy", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => v.map(_.trim.toLowerCase).contains(r.getLoadedBy.trim.toLowerCase)
            case _ => false

      case FilterCriteria("getRegisteredBy", "=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getRegisteredBy.trim.toLowerCase == v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getRegisteredBy", "!=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getRegisteredBy.trim.toLowerCase != v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getRegisteredBy", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => v.map(_.trim.toLowerCase).contains(r.getRegisteredBy.trim.toLowerCase)
            case _ => false

      case FilterCriteria("getArchivedBy", "=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedBy.trim.toLowerCase == v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getArchivedBy", "!=", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getArchivedBy.trim.toLowerCase != v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getArchivedBy", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => v.map(_.trim.toLowerCase).contains(r.getArchivedBy.trim.toLowerCase)
            case _ => false

      case FilterCriteria("getSubject", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: ArchivedDocument => r.getSubject.trim.toLowerCase.contains(v.head.trim.toLowerCase)
            case _ => false

      case _ => _ => false
}

  private def buildRegisteredDocumentPredicate(criteria: FilterCriteria): Any => Boolean = {
    import pkg.b.logic.RegisteredDocument

    criteria match

      case FilterCriteria("getId", "<", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getId.trim.toInt < threshold
            case _ => false

      case FilterCriteria("getId", "<=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getId.trim.toInt <= threshold
            case _ => false

      case FilterCriteria("getId", "=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getId.trim == v.head
            case _ => false

      case FilterCriteria("getId", ">", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getId.trim.toInt > threshold
            case _ => false

      case FilterCriteria("getId", ">=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getId.trim.toInt >= threshold
            case _ => false

      case FilterCriteria("getId", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => v.map(_.trim).contains(r.getId.trim)
            case _ => false

      case FilterCriteria("getRegisteredDate", "<", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredDate.trim < v.head
            case _ => false

      case FilterCriteria("getRegisteredDate", "<=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredDate.trim <= v.head
            case _ => false

      case FilterCriteria("getRegisteredDate", "=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredDate.trim == v.head
            case _ => false

      case FilterCriteria("getRegisteredDate", ">", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredDate.trim > v.head
            case _ => false

      case FilterCriteria("getRegisteredDate", ">=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredDate.trim >= v.head
            case _ => false

      case FilterCriteria("getRegisteredDate", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => v.map(_.trim).contains(r.getRegisteredDate.trim)
            case _ => false

      case FilterCriteria("getRegisteredBy", "=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredBy.trim.toLowerCase == v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getRegisteredBy", "!=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getRegisteredBy.trim.toLowerCase != v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getRegisteredBy", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => v.map(_.trim.toLowerCase).contains(r.getRegisteredBy.trim.toLowerCase)
            case _ => false

      case FilterCriteria("getLoadedBy", "=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getLoadedBy.trim.toLowerCase == v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getLoadedBy", "!=", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getLoadedBy.trim.toLowerCase != v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getLoadedBy", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => v.map(_.trim.toLowerCase).contains(r.getLoadedBy.trim.toLowerCase)
            case _ => false

      case FilterCriteria("getSubject", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: RegisteredDocument => r.getSubject.trim.toLowerCase.contains(v.head.trim.toLowerCase)
            case _ => false

      case _ => _ => false
  }

  private def buildLoadedDocumentPredicate(criteria: FilterCriteria): Any => Boolean = {
    import pkg.b.logic.LoadedDocument

    criteria match

      case FilterCriteria("getId", "<", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getId.trim.toInt < threshold
            case _ => false

      case FilterCriteria("getId", "<=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getId.trim.toInt <= threshold
            case _ => false

      case FilterCriteria("getId", "=", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getId.trim == v.head
            case _ => false

      case FilterCriteria("getId", ">", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getId.trim.toInt > threshold
            case _ => false

      case FilterCriteria("getId", ">=", v) =>
        val threshold = v.head.trim.toInt
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getId.trim.toInt >= threshold
            case _ => false

      case FilterCriteria("getId", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => v.map(_.trim).contains(r.getId.trim)
            case _ => false

      case FilterCriteria("getProcessedDate", "<", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedDate.trim < v.head
            case _ => false

      case FilterCriteria("getProcessedDate", "<=", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedDate.trim <= v.head
            case _ => false

      case FilterCriteria("getProcessedDate", "=", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedDate.trim == v.head
            case _ => false

      case FilterCriteria("getProcessedDate", ">", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedDate.trim > v.head
            case _ => false

      case FilterCriteria("getProcessedDate", ">=", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedDate.trim >= v.head
            case _ => false

      case FilterCriteria("getProcessedDate", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => v.map(_.trim).contains(r.getProcessedDate.trim)
            case _ => false

      case FilterCriteria("getProcessedBy", "=", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedBy.trim.toLowerCase == v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getProcessedBy", "!=", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getProcessedBy.trim.toLowerCase != v.head.trim.toLowerCase
            case _ => false

      case FilterCriteria("getProcessedBy", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => v.map(_.trim.toLowerCase).contains(r.getProcessedBy.trim.toLowerCase)
            case _ => false

      case FilterCriteria("getSubject", "contains", v) =>
        (obj: Any) =>
          obj match
            case r: LoadedDocument => r.getSubject.trim.toLowerCase.contains(v.head.trim.toLowerCase)
            case _ => false

      case _ => _ => false
  }

  private def combineDocumentOperationsLogPredicates(group: CriteriaGroup): Any => Boolean = {
    val preds = group.criteria.map(buildDocumentOperationsLogPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))
  }

  def getDocumentOperationsLogPredicate(criteria: List[(String, String, List[String])]): Any => Boolean = {
    val criteriaGroup = CriteriaGroup(
      criteria.map((f, o, v) => FilterCriteria(f, o, v)),
      combineWithAnd = true
    )
    combineDocumentOperationsLogPredicates(criteriaGroup)
  }

  private def combineDocumentPredicates(group: CriteriaGroup): Any => Boolean = {
    val preds = group.criteria.map(buildDocumentPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))
  }

  def getDocumentPredicate(criteria: List[(String, String, List[String])]): Any => Boolean = {
    val criteriaGroup = CriteriaGroup(
      criteria.map((f, o, v) => FilterCriteria(f, o, v)),
      combineWithAnd = true
    )
    combineDocumentPredicates(criteriaGroup)
  }

  private def combineRegisteredDocumentPredicates(group: CriteriaGroup): Any => Boolean = {
    val preds = group.criteria.map(buildRegisteredDocumentPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))
  }

  def getRegisteredDocumentPredicate(criteria: List[(String, String, List[String])]): Any => Boolean = {
    val criteriaGroup = CriteriaGroup(
      criteria.map((f, o, v) => FilterCriteria(f, o, v)),
      combineWithAnd = true
    )
    combineRegisteredDocumentPredicates(criteriaGroup)
  }

  private def combineLoadedDocumentPredicates(group: CriteriaGroup): Any => Boolean = {
    val preds = group.criteria.map(buildLoadedDocumentPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))
  }

  def getLoadedDocumentPredicate(criteria: List[(String, String, List[String])]): Any => Boolean = {
    val criteriaGroup = CriteriaGroup(
      criteria.map((f, o, v) => FilterCriteria(f, o, v)),
      combineWithAnd = true
    )
    combineLoadedDocumentPredicates(criteriaGroup)
  }