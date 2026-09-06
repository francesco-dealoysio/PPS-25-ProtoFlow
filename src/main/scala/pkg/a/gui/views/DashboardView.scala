package pkg.a.gui.views

import pkg.a.gui.services.DocumentManagementControlService.{ManagedDocument, Stages}
import pkg.a.gui.text.UiStyles.Dashboard.*
import pkg.a.gui.text.UiText.Common.Documents.{NoDocuments, Fields as DocumentFields}
import pkg.a.gui.text.UiText.Common.Fields.Labels
import pkg.a.gui.text.UiText.DocumentManagementControl as ManagementText
import pkg.a.gui.traits.Management
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TableView
import scalafx.scene.layout.{HBox, VBox}

object DashboardView extends Management:

  private val RecentDocumentsLimit = 10

  case class DashboardCard(title: String, value: String, subtitle: String)

  def apply(title: String, cards: Seq[DashboardCard], documents: Seq[ManagedDocument]): VBox =

    new VBox:
      styleClass += DashboardContainerStyle
      children = Seq(
        fieldLabel(title, PageTitleStyle),
        createCards(cards),
        createDocumentsTable(documents)
      )

  private def createCards(cards: Seq[DashboardCard]): HBox =
    new HBox:
      styleClass += CardsContainerStyle
      children = cards.map(statCard)

  private def statCard(card: DashboardCard): VBox =

    new VBox:
      styleClass += StatCardStyle
      children = Seq(
        fieldLabel(card.title, StatCardTitleStyle),
        fieldLabel(card.value, StatCardValueStyle),
        fieldLabel(card.subtitle, StatCardSubtitleStyle)
      )

  private def createDocumentsTable(documents: Seq[ManagedDocument]): TableView[ManagedDocument] =

    val recentDocuments = ObservableBuffer(documents.reverse.take(RecentDocumentsLimit)*)
    val table = managementTable(recentDocuments, NoDocuments)
    table.columns ++= Seq(
      stringColumn[ManagedDocument](DocumentFields.ProtocolNumber): document =>
        if document.protocolNumber.nonEmpty then document.protocolNumber else document.id,
      stringColumn[ManagedDocument](DocumentFields.Subject)(_.subject),
      stringColumn[ManagedDocument](DocumentFields.Sender)(_.sender),
      stringColumn[ManagedDocument](Labels.Classification): document =>
        if document.classification.nonEmpty then document.classification else ManagementText.NotAvailable,
      stringColumn[ManagedDocument](ManagementText.StageColumn): document =>
        ManagementText.Stages.labelOf(document.stage),
      stringColumn[ManagedDocument](Labels.Date): document =>
        document.stage match
          case Stages.Archiving =>
            document.archivedDate
          case Stages.Registering =>
            document.registeredDate
          case _ =>
            document.loadedDate
    )

    table