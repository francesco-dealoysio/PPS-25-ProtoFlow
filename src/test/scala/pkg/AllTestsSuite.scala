package pkg

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.a.gui.{AccountValidatorTest, ClassificationValidatorTest, DocumentManagementControlServiceTest, RegistrationRequestServiceTest, RegistrationValidatorTest, DocumentArchivingValidatorTest, StatisticsServiceTest}
import pkg.b.logic.{AccessLogTest, AccountTest, ArchivedDocumentTest, AuthorizationEngineTest, ClassificationTest, DocumentLogTest, ErrorLogTest, LoadedDocumentTest, RegisteredDocumentTest, RegistrationTest, RoleTest}
import pkg.c.data.{PropertiesTest, XmlTest}
import pkg.d.util.{DateTimeTest, FiltersTest, XmlToPdfTest}

@RunWith(classOf[Suite])
@Suite.SuiteClasses(
  Array(
    classOf[AccountTest],
    classOf[ClassificationTest],
    classOf[RegistrationTest],
    classOf[RoleTest],
    classOf[ErrorLogTest],
    classOf[LoadedDocumentTest],
    classOf[RegisteredDocumentTest],
    classOf[ArchivedDocumentTest],
    classOf[DocumentLogTest],
    classOf[AccessLogTest],
    classOf[AuthorizationEngineTest],

    classOf[AccountValidatorTest],
    classOf[ClassificationValidatorTest],
    classOf[RegistrationRequestServiceTest],
    classOf[RegistrationValidatorTest],
    classOf[DocumentArchivingValidatorTest],
    classOf[StatisticsServiceTest],
    classOf[DocumentManagementControlServiceTest],

    classOf[PropertiesTest],
    classOf[XmlTest],

    classOf[DateTimeTest],
    classOf[FiltersTest],
    classOf[XmlToPdfTest]
  )
)

class AllTestsSuite