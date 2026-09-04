package pkg

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.a.gui.services.{AccountServiceTest, ArchivedDocumentServiceTest, ClassificationServiceTest, DocumentManagementControlServiceTest, LoadedDocumentServiceTest, LoginServiceTest, RegistrationRequestServiceTest, RoleServiceTest, StatisticsServiceTest}
import pkg.a.gui.validators.{AccountValidatorTest, ClassificationValidatorTest, DocumentArchivingValidatorTest, LoadedDocumentValidatorTest, RegistrationValidatorTest, RoleValidatorTest}
import pkg.b.logic.{AccessLogTest, AccountTest, ArchivedDocumentTest, AuthorizationEngineTest, ClassificationTest, DocumentLogTest, ErrorLogTest, LoadedDocumentTest, RegisteredDocumentTest, RegistrationTest, RoleTest}
import pkg.c.data.{FileSystemTest, PropertiesTest, XmlTest}
import pkg.d.util.{DateTimeTest, FiltersTest, IdGenTest, XmlToPdfTest}
import pkg.c.data.{PropertiesTest, XmlTest}
import pkg.d.util.{DateTimeTest, FiltersTest}

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
    classOf[AccountServiceTest],
    classOf[ArchivedDocumentServiceTest],
    classOf[ClassificationServiceTest],
    classOf[LoadedDocumentServiceTest],
    classOf[LoginServiceTest],
    classOf[RoleServiceTest],
    classOf[LoadedDocumentValidatorTest],
    classOf[RoleValidatorTest],

    classOf[FileSystemTest],
    classOf[PropertiesTest],
    classOf[XmlTest],

    classOf[DateTimeTest],
    classOf[FiltersTest],
    classOf[IdGenTest],
    classOf[XmlToPdfTest]

    classOf[PdfDetailsCreatorTest],
    classOf[PdfTableCreatorTest],
    classOf[PdfSectionsCreatorTest],
    classOf[PdfDocumentSummaryCreatorTest],
    classOf[PdfVerifierTest]
  )
)

class AllTestsSuite