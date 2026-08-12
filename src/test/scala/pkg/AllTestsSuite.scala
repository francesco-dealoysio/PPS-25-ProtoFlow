package pkg

import org.junit.runner.RunWith
import org.junit.runners.Suite
import pkg.a.gui.{AccountViewModelTest, ClassificationViewModelTest, HomePageViewModelTest, RegistrationRequestServiceTest, RegistrationViewModelTest}
import pkg.b.logic.{AccountTest, ArchivedDocumentTest, ClassificationTest, DocumentLogTest, ErrorLogTest, LoadedDocumentTest, RegisteredDocumentTest, RegistrationTest, RoleTest}
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

    classOf[AccountViewModelTest],
    classOf[ClassificationViewModelTest],
    classOf[HomePageViewModelTest],
    classOf[RegistrationRequestServiceTest],
    classOf[RegistrationViewModelTest],

    classOf[PropertiesTest],
    classOf[XmlTest],

    classOf[DateTimeTest],
    classOf[FiltersTest],
    classOf[XmlToPdfTest]
  )
)

class AllTestsSuite