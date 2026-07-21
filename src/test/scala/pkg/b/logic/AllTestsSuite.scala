package pkg.b.logic

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(
  classOf[AccountTest],
  classOf[ClassificationTest],
  classOf[RegistrationTest],
  classOf[RoleTest],
  classOf[ErrorLogTest]
))

class AllTestsSuite