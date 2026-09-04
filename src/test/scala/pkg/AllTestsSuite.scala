package pkg

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.a.gui.services.*
import pkg.a.gui.validators.*
import pkg.b.logic.pdf.*
import pkg.b.logic.*
import pkg.c.data.{FileSystemTest, PropertiesTest, XmlTest}
import pkg.d.util.{DateTimeTest, FiltersTest, IdGenTest}

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

    classOf[PdfDetailsCreatorTest],
    classOf[PdfTableCreatorTest],
    classOf[PdfSectionsCreatorTest],
    classOf[PdfDocumentSummaryCreatorTest],
    classOf[PdfVerifierTest]
  )
)

class AllTestsSuite