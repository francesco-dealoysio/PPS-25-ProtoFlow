package pkg.d.util

import alice.tuprolog.{Prolog, SolveInfo, Struct, Term, Theory}

import scala.language.implicitConversions

object PrologEngine:

  given Conversion[String, Term] = Term.createTerm(_)

  def fromTheory(theoryText: String): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(theoryText))
    (goal: Term) =>
      new Iterable[Term]:
        override def iterator: Iterator[Term] = new Iterator[Term]:
          var solution: SolveInfo = engine.solve(goal)
          var exhausted = false
          override def hasNext: Boolean = !exhausted && solution.isSuccess
          override def next(): Term =
            val result = solution.getSolution
            if solution.hasOpenAlternatives then solution = engine.solveNext()
            else exhausted = true
            result
      .to(LazyList)

  def arg(solvedGoal: Term, i: Int): Term =
    solvedGoal.asInstanceOf[Struct].getArg(i).getTerm

  def listElements(list: Term): List[Term] =
    val elements = scala.collection.mutable.ListBuffer.empty[Term]
    val it = list.asInstanceOf[Struct].listIterator()
    while it.hasNext do elements += it.next()
    elements.toList