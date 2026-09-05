package pkg.d.util

object Filters:

  private case class FilterCriteria(
                             field: String,
                             operatorecord: String,
                             value: List[String]
                           )

  private case class CriteriaGroup(criteria: List[FilterCriteria], combineWithAnd: Boolean)

  def getDocumentOperationsLogPredicate(criteria: List[(String, String, List[String])]): Any => Boolean =
    val criteriaGroup = CriteriaGroup(
      criteria.map((field, operator, value) => FilterCriteria(field, operator, value)),
      combineWithAnd = true
    )
    combineDocumentOperationsLogPredicates(criteriaGroup)

  def getDocumentPredicate(criteria: List[(String, String, List[String])]): Any => Boolean =
    val criteriaGroup = CriteriaGroup(
      criteria.map((field, operator, value) => FilterCriteria(field, operator, value)),
      combineWithAnd = true
    )
    combineDocumentPredicates(criteriaGroup)

  def getRegisteredDocumentPredicate(criteria: List[(String, String, List[String])]): Any => Boolean =
    val criteriaGroup = CriteriaGroup(
      criteria.map((field, operator, value) => FilterCriteria(field, operator, value)),
      combineWithAnd = true
    )
    combineRegisteredDocumentPredicates(criteriaGroup)

  def getLoadedDocumentPredicate(criteria: List[(String, String, List[String])]): Any => Boolean =
    val criteriaGroup = CriteriaGroup(
      criteria.map((field, operator, value) => FilterCriteria(field, operator, value)),
      combineWithAnd = true
    )
    combineLoadedDocumentPredicates(criteriaGroup)

  private def buildDocumentOperationsLogPredicate(criteria: FilterCriteria): Any => Boolean =
    import pkg.b.logic.DocumentLog

    criteria match

      case FilterCriteria("getDocumentId", "<", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: DocumentLog => record.getDocumentId.trim.toInt < threshold
          case _ => false
        }

      case FilterCriteria("getDocumentId", "<=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: DocumentLog => record.getDocumentId.trim.toInt <= threshold
          case _ => false
        }

      case FilterCriteria("getDocumentId", "=", value) =>
          case record: DocumentLog => record.getDocumentId.trim == value.head
          case _ => false

      case FilterCriteria("getDocumentId", ">", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: DocumentLog => record.getDocumentId.trim.toInt > threshold
          case _ => false
        }

      case FilterCriteria("getDocumentId", ">=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: DocumentLog => record.getDocumentId.trim.toInt >= threshold
          case _ => false
        }

      case FilterCriteria("getDocumentId", "contains", value) =>
          case record: DocumentLog => value.map(_.trim).contains(record.getDocumentId.trim)
          case _ => false

      case FilterCriteria("getProcessedDate", "<", value) =>
          case record: DocumentLog => record.getProcessedDate.trim < value.head
          case _ => false

      case FilterCriteria("getProcessedDate", "<=", value) =>
          case record: DocumentLog => record.getProcessedDate.trim <= value.head
          case _ => false

      case FilterCriteria("getProcessedDate", "=", value) =>
          case record: DocumentLog => record.getProcessedDate.trim == value.head
          case _ => false

      case FilterCriteria("getProcessedDate", ">", value) =>
          case record: DocumentLog => record.getProcessedDate.trim > value.head
          case _ => false

      case FilterCriteria("getProcessedDate", ">=", value) =>
          case record: DocumentLog => record.getProcessedDate.trim >= value.head
          case _ => false

      case FilterCriteria("getProcessedDate", "contains", value) =>
          case record: DocumentLog => value.map(_.trim).contains(record.getProcessedDate.trim)
          case _ => false

      case FilterCriteria("getProcessedBy", "=", value) =>
          case record: DocumentLog => record.getProcessedBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "!=", value) =>
          case record: DocumentLog => record.getProcessedBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "contains", value) =>
          case record: DocumentLog => value.map(_.trim.toLowerCase).contains(record.getProcessedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getOperationType", "=", value) =>
          case record: DocumentLog => record.getOperationType.trim.toLowerCase == value.head.trim.toLowerCase()
          case _ => false

      case FilterCriteria("getOperationType", "!=", value) =>
          case record: DocumentLog => record.getOperationType.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getOperationType", "contains", value) =>
          case record: DocumentLog => value.map(_.trim.toLowerCase).contains(record.getOperationType.trim.toLowerCase)
          case _ => false

      case _ => _ => false

  private def buildDocumentPredicate(criteria: FilterCriteria): Any => Boolean =
    import pkg.b.logic.ArchivedDocument

    criteria match

      case FilterCriteria("getId", "<", value) =>
          val threshold = value.head.trim.toInt
          {
            case record: ArchivedDocument => record.getId.trim.toInt < threshold
            case _ => false
          }

      case FilterCriteria("getId", "<=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: ArchivedDocument => record.getId.trim.toInt <= threshold
          case _ => false
        }

      case FilterCriteria("getId", "=", value) =>
          case record: ArchivedDocument => record.getId.trim == value.head
          case _ => false

      case FilterCriteria("getId", ">", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: ArchivedDocument => record.getId.trim.toInt > threshold
          case _ => false
        }

      case FilterCriteria("getId", ">=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: ArchivedDocument => record.getId.trim.toInt >= threshold
          case _ => false
        }

      case FilterCriteria("getId", "contains", value) =>
          case record: ArchivedDocument => value.map(_.trim).contains(record.getId.trim)
          case _ => false

      case FilterCriteria("getArchivedDate", "<", value) =>
          case record: ArchivedDocument => record.getArchivedDate.trim < value.head
          case _ => false

      case FilterCriteria("getArchivedDate", "<=", value) =>
          case record: ArchivedDocument => record.getArchivedDate.trim <= value.head
          case _ => false

      case FilterCriteria("getArchivedDate", "=", value) =>
          case record: ArchivedDocument => record.getArchivedDate.trim == value.head
          case _ => false

      case FilterCriteria("getArchivedDate", ">", value) =>
          case record: ArchivedDocument => record.getArchivedDate.trim > value.head
          case _ => false

      case FilterCriteria("getArchivedDate", ">=", value) =>
          case record: ArchivedDocument => record.getArchivedDate.trim >= value.head
          case _ => false

      case FilterCriteria("getArchivedDate", "contains", value) =>
          case record: ArchivedDocument => value.map(_.trim).contains(record.getArchivedDate.trim)
          case _ => false

      case FilterCriteria("getLoadedBy", "=", value) =>
          case record: ArchivedDocument => record.getLoadedBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getLoadedBy", "!=", value) =>
          case record: ArchivedDocument => record.getLoadedBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getLoadedBy", "contains", value) =>
          case record: ArchivedDocument => value.map(_.trim.toLowerCase).contains(record.getLoadedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getRegisteredBy", "=", value) =>
          case record: ArchivedDocument => record.getRegisteredBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getRegisteredBy", "!=", value) =>
          case record: ArchivedDocument => record.getRegisteredBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getRegisteredBy", "contains", value) =>
          case record: ArchivedDocument => value.map(_.trim.toLowerCase).contains(record.getRegisteredBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getArchivedBy", "=", value) =>
          case record: ArchivedDocument => record.getArchivedBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getArchivedBy", "!=", value) =>
          case record: ArchivedDocument => record.getArchivedBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getArchivedBy", "contains", value) =>
          case record: ArchivedDocument => value.map(_.trim.toLowerCase).contains(record.getArchivedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getSubject", "contains", value) =>
          case record: ArchivedDocument => record.getSubject.trim.toLowerCase.contains(value.head.trim.toLowerCase)
          case _ => false

      case _ => _ => false

  private def buildRegisteredDocumentPredicate(criteria: FilterCriteria): Any => Boolean =
    import pkg.b.logic.RegisteredDocument

    criteria match

      case FilterCriteria("getId", "<", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: RegisteredDocument => record.getId.trim.toInt < threshold
          case _ => false
        }

      case FilterCriteria("getId", "<=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: RegisteredDocument => record.getId.trim.toInt <= threshold
          case _ => false
        }

      case FilterCriteria("getId", "=", value) =>
          case record: RegisteredDocument => record.getId.trim == value.head
          case _ => false

      case FilterCriteria("getId", ">", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: RegisteredDocument => record.getId.trim.toInt > threshold
          case _ => false
        }

      case FilterCriteria("getId", ">=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: RegisteredDocument => record.getId.trim.toInt >= threshold
          case _ => false
        }

      case FilterCriteria("getId", "contains", value) =>
          case record: RegisteredDocument => value.map(_.trim).contains(record.getId.trim)
          case _ => false

      case FilterCriteria("getRegisteredDate", "<", value) =>
          case record: RegisteredDocument => record.getRegisteredDate.trim < value.head
          case _ => false

      case FilterCriteria("getRegisteredDate", "<=", value) =>
          case record: RegisteredDocument => record.getRegisteredDate.trim <= value.head
          case _ => false

      case FilterCriteria("getRegisteredDate", "=", value) =>
          case record: RegisteredDocument => record.getRegisteredDate.trim == value.head
          case _ => false

      case FilterCriteria("getRegisteredDate", ">", value) =>
          case record: RegisteredDocument => record.getRegisteredDate.trim > value.head
          case _ => false

      case FilterCriteria("getRegisteredDate", ">=", value) =>
          case record: RegisteredDocument => record.getRegisteredDate.trim >= value.head
          case _ => false

      case FilterCriteria("getRegisteredDate", "contains", value) =>
          case record: RegisteredDocument => value.map(_.trim).contains(record.getRegisteredDate.trim)
          case _ => false

      case FilterCriteria("getRegisteredBy", "=", value) =>
          case record: RegisteredDocument => record.getRegisteredBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getRegisteredBy", "!=", value) =>
          case record: RegisteredDocument => record.getRegisteredBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getRegisteredBy", "contains", value) =>
          case record: RegisteredDocument => value.map(_.trim.toLowerCase).contains(record.getRegisteredBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getLoadedBy", "=", value) =>
          case record: RegisteredDocument => record.getLoadedBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getLoadedBy", "!=", value) =>
          case record: RegisteredDocument => record.getLoadedBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getLoadedBy", "contains", value) =>
          case record: RegisteredDocument => value.map(_.trim.toLowerCase).contains(record.getLoadedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getSubject", "contains", value) =>
          case record: RegisteredDocument => record.getSubject.trim.toLowerCase.contains(value.head.trim.toLowerCase)
          case _ => false

      case _ => _ => false

  private def buildLoadedDocumentPredicate(criteria: FilterCriteria): Any => Boolean =
    import pkg.b.logic.LoadedDocument

    criteria match

      case FilterCriteria("getId", "<", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: LoadedDocument => record.getId.trim.toInt < threshold
          case _ => false
        }

      case FilterCriteria("getId", "<=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: LoadedDocument => record.getId.trim.toInt <= threshold
          case _ => false
        }

      case FilterCriteria("getId", "=", value) =>
          case record: LoadedDocument => record.getId.trim == value.head
          case _ => false

      case FilterCriteria("getId", ">", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: LoadedDocument => record.getId.trim.toInt > threshold
          case _ => false
        }

      case FilterCriteria("getId", ">=", value) =>
        val threshold = value.head.trim.toInt
        {
          case record: LoadedDocument => record.getId.trim.toInt >= threshold
          case _ => false
        }

      case FilterCriteria("getId", "contains", value) =>
          case record: LoadedDocument => value.map(_.trim).contains(record.getId.trim)
          case _ => false

      case FilterCriteria("getProcessedDate", "<", value) =>
          case record: LoadedDocument => record.getProcessedDate.trim < value.head
          case _ => false

      case FilterCriteria("getProcessedDate", "<=", value) =>
          case record: LoadedDocument => record.getProcessedDate.trim <= value.head
          case _ => false

      case FilterCriteria("getProcessedDate", "=", value) =>
          case record: LoadedDocument => record.getProcessedDate.trim == value.head
          case _ => false

      case FilterCriteria("getProcessedDate", ">", value) =>
          case record: LoadedDocument => record.getProcessedDate.trim > value.head
          case _ => false

      case FilterCriteria("getProcessedDate", ">=", value) =>
          case record: LoadedDocument => record.getProcessedDate.trim >= value.head
          case _ => false

      case FilterCriteria("getProcessedDate", "contains", value) =>
          case record: LoadedDocument => value.map(_.trim).contains(record.getProcessedDate.trim)
          case _ => false

      case FilterCriteria("getProcessedBy", "=", value) =>
          case record: LoadedDocument => record.getProcessedBy.trim.toLowerCase == value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "!=", value) =>
          case record: LoadedDocument => record.getProcessedBy.trim.toLowerCase != value.head.trim.toLowerCase
          case _ => false

      case FilterCriteria("getProcessedBy", "contains", value) =>
          case record: LoadedDocument => value.map(_.trim.toLowerCase).contains(record.getProcessedBy.trim.toLowerCase)
          case _ => false

      case FilterCriteria("getSubject", "contains", value) =>
          case record: LoadedDocument => record.getSubject.trim.toLowerCase.contains(value.head.trim.toLowerCase)
          case _ => false

      case _ => _ => false

  private def combineDocumentOperationsLogPredicates(group: CriteriaGroup): Any => Boolean =
    val preds = group.criteria.map(buildDocumentOperationsLogPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))

  private def combineDocumentPredicates(group: CriteriaGroup): Any => Boolean =
    val preds = group.criteria.map(buildDocumentPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))

  private def combineRegisteredDocumentPredicates(group: CriteriaGroup): Any => Boolean =
    val preds = group.criteria.map(buildRegisteredDocumentPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))

  private def combineLoadedDocumentPredicates(group: CriteriaGroup): Any => Boolean =
    val preds = group.criteria.map(buildLoadedDocumentPredicate)
    if group.combineWithAnd then
      obj => preds.forall(_(obj))
    else
      obj => preds.exists(_(obj))