package pkg.b.logic

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.c.data.XmlTest

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(
  classOf[AccountTest],
  classOf[ClassificationTest],
  classOf[RegistrationTest],
  classOf[RoleTest],
  classOf[ErrorLogTest],
  classOf[XmlTest],
  classOf[LoadedDocumentTest],
  classOf[ArchivedDocumentTest]
))

class AllTestsSuite