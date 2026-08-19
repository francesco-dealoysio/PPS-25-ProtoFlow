package pkg

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.a.gui.{AccountValidatorTest, ClassificationValidatorTest, HomePageViewModelTest, RegistrationRequestServiceTest, RegistrationValidatorTest, DocumentArchivingValidatorTest, StatisticsServiceTest}
import pkg.b.logic.{AccessLogTest, AccountTest, ArchivedDocumentTest, ClassificationTest, DocumentLogTest, ErrorLogTest, LoadedDocumentTest, RegisteredDocumentTest, RegistrationTest, RoleTest}
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

    classOf[AccountValidatorTest],
    classOf[ClassificationValidatorTest],
    classOf[HomePageViewModelTest],
    classOf[RegistrationRequestServiceTest],
    classOf[RegistrationValidatorTest],
    classOf[DocumentArchivingValidatorTest],
    classOf[StatisticsServiceTest],

    classOf[PropertiesTest],
    classOf[XmlTest],

    classOf[DateTimeTest],
    classOf[FiltersTest],
    classOf[XmlToPdfTest]
  )
)

class AllTestsSuite