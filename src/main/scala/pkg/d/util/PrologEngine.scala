package pkg.d.util

import alice.tuprolog.{Prolog, Term, Theory}

import scala.language.implicitConversions

// Thin encapsulation of tuProlog: this is the only file in the project
// allowed to import alice.tuprolog.*, so callers only ever see Scala types.
object PrologEngine:

  given Conversion[String, Term] = Term.createTerm(_)

  // Loads a theory (a Prolog program, as text) and returns a function that,
  // given a goal, lazily yields all the terms it resolves to.
  def fromTheory(theoryText: String): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(theoryText))
    (goal: Term) =>
      new Iterable[Term]:
        override def iterator: Iterator[Term] = new Iterator[Term]:
          var solution = engine.solve(goal)
          var exhausted = false
          override def hasNext: Boolean = !exhausted && solution.isSuccess
          override def next(): Term =
            val result = solution.getSolution
            // solveNext() throws once there are no open alternatives left,
            // instead of returning a failed SolveInfo: guard it explicitly.
            if solution.hasOpenAlternatives then solution = engine.solveNext()
            else exhausted = true
            result
      .to(LazyList)
