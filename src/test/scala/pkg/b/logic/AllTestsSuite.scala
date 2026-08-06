package pkg.b.logic

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.c.data.XmlTest
import pkg.d.util._

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(
  classOf[AccountTest],
  classOf[ClassificationTest],
  classOf[RegistrationTest],
  classOf[RoleTest],
  classOf[ErrorLogTest],
  classOf[XmlTest],
  classOf[LoadedDocumentTest],
  classOf[RegisteredDocumentTest],
  classOf[ArchivedDocumentTest],
  classOf[DocumentLogTest],
  classOf[FiltersTest]
))

class AllTestsSuite