package pkg.a.gui.views

import pkg.a.gui.services.StatisticsService
import pkg.a.gui.services.StatisticsService.{MonthlyCount, RoleCount, UserCount}
import pkg.a.gui.text.UiStyles.Common.RootStyle
import pkg.a.gui.text.UiStyles.HomePage.{CardsContainerStyle, StatCardStyle, StatCardTitleStyle, StatCardValueStyle}
import pkg.a.gui.text.UiText.Statistics as Text
import pkg.a.gui.traits.Management
import pkg.b.logic.pdf.{PdfSectionsCreator, PdfViewer}
import pkg.b.logic.pdf.PdfSectionsCreator.Section
import pkg.d.util.Util.inPrintsFilePathName
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.control.{ScrollPane, TableView}
import scalafx.scene.layout.*

object StatisticsView extends Management:

  private val ChartCardStyle = "chart-card"
  private val StatsChartStyle = "stats-chart"
  private val CardHeight = 300.0

  def apply(onExit: () => Unit = () => ()): BorderPane =
    
    val result = createResultMessage()

    val header = titleBox(Text.Title, Text.Subtitle)

    val registrationsSummary = StatisticsService.processedRegistrations()

    val registrationsCards =
      new HBox:
        styleClass += CardsContainerStyle
        children = Seq(
          statCard(Text.RegistrationsTotal, registrationsSummary.total.toString),
          statCard(Text.RegistrationsApproved, registrationsSummary.approved.toString),
          statCard(Text.RegistrationsRejected, registrationsSummary.rejected.toString)
        )

    val registeredByMonth = StatisticsService.registeredDocumentsByMonth()
    val archivedByMonth = StatisticsService.archivedDocumentsByMonth()
    val accessesByRole = StatisticsService.accessesByRole()
    val accessesByUser = StatisticsService.accessesByUser()

    val registeredChart = monthlyBarChart(Text.RegisteredByMonthTitle, registeredByMonth, "chart-registered")
    val archivedChart = monthlyBarChart(Text.ArchivedByMonthTitle, archivedByMonth, "chart-archived")
    val roleChart = roleBarChart(Text.AccessesByRoleTitle, accessesByRole, "chart-role")

    val userTable = managementTable(ObservableBuffer(accessesByUser*), Text.AccessesByUserTitle)
    userTable.columns ++= Seq(
      stringColumn[UserCount](Text.UserColumn, Some(220))(_.username),
      stringColumn[UserCount](Text.AccessCountColumn, Some(150))(_.count.toString)
    )

    val chartsGrid =
      squareGrid(
        chartCard(registeredChart),
        chartCard(archivedChart),
        chartCard(roleChart),
        tableCard(Text.AccessesByUserTitle, userTable)
      )

    def printStatistics(): Unit =
      val pdfPath = inPrintsFilePathName(s"${Text.PrintFileName}.pdf")

      val sections =
        Seq(
          Section(
            title = Text.RegistrationsTotal,
            headers = Seq(
              Text.RegistrationsTotal,
              Text.RegistrationsApproved,
              Text.RegistrationsRejected
            ),
            rows = Seq(
              Seq(
                registrationsSummary.total.toString,
                registrationsSummary.approved.toString,
                registrationsSummary.rejected.toString
              )
            )
          ),
          Section(
            title = Text.RegisteredByMonthTitle,
            headers = Seq("Mese", "Conteggio"),
            rows =
              registeredByMonth.map: count =>
                Seq(count.yearMonth, count.count.toString),
            columnWeights = Seq(2f, 1f)
          ),
          Section(
            title = Text.ArchivedByMonthTitle,
            headers = Seq("Mese", "Conteggio"),
            rows =
              archivedByMonth.map: count =>
                Seq(count.yearMonth, count.count.toString),
            columnWeights = Seq(2f, 1f)
          ),
          Section(
            title = Text.AccessesByRoleTitle,
            headers = Seq("Ruolo", "Conteggio"),
            rows =
              accessesByRole.map: count =>
                Seq(count.roleName, count.count.toString),
            columnWeights = Seq(2f, 1f)
          ),
          Section(
            title = Text.AccessesByUserTitle,
            headers = Seq(Text.UserColumn, Text.AccessCountColumn),
            rows =
              accessesByUser.map: count =>
                Seq(count.username, count.count.toString),
            columnWeights = Seq(2f, 1f)
          )
        )

      val printed =
        PdfSectionsCreator.createSectionsPdf(
          pdfPathName = pdfPath,
          title = Text.PrintTitle,
          sections = sections
        )

      if printed then
        PdfViewer.viewPdf(pdfPath)

      result.show(
        if printed then Text.PrintSuccess
        else Text.PrintError,
        success = printed
      )

    val actions = actionBar(Seq(closeButton(onExit), printButton(printStatistics)))

    val contentBox =
      new VBox:
        spacing = 18
        padding = Insets(20)
        children = Seq(
          header,
          registrationsCards,
          chartsGrid,
          result.label,
          actions
        )

    new BorderPane:
      styleClass += RootStyle
      center =
        new ScrollPane:
          fitToWidth = true
          content = contentBox

  private def statCard(title: String, value: String): VBox =
    new VBox:
      prefWidth = 190
      styleClass += StatCardStyle
      children = Seq(
        fieldLabel(title, StatCardTitleStyle),
        fieldLabel(value, StatCardValueStyle)
      )

  private def squareGrid(topLeft: VBox, topRight: VBox, bottomLeft: VBox, bottomRight: VBox): GridPane =
    val equalColumn =
      new ColumnConstraints():
        percentWidth = 50
        hgrow = Priority.Always

    Seq(topLeft, topRight, bottomLeft, bottomRight).foreach: card =>
      card.maxWidth = Double.MaxValue

    new GridPane:
      hgap = 18
      vgap = 18
      columnConstraints = Seq(equalColumn, equalColumn)
      add(topLeft, 0, 0)
      add(topRight, 1, 0)
      add(bottomLeft, 0, 1)
      add(bottomRight, 1, 1)

  private def chartCard(chart: BarChart[String, Number]): VBox =
    VBox.setVgrow(chart, Priority.Always)
    new VBox:
      prefHeight = CardHeight
      minHeight = CardHeight
      styleClass += ChartCardStyle
      children = Seq(chart)

  private def tableCard(cardTitle: String, table: TableView[UserCount]): VBox =
    VBox.setVgrow(table, Priority.Always)
    new VBox:
      prefHeight = CardHeight
      minHeight = CardHeight
      spacing = 8
      styleClass += ChartCardStyle
      children = Seq(fieldLabel(cardTitle), table)

  private def monthlyBarChart(chartTitle: String, counts: Seq[MonthlyCount], accentStyle: String): BarChart[String, Number] =
    val series = new XYChart.Series[String, Number]():
      data = ObservableBuffer(counts.map(c => XYChart.Data[String, Number](c.yearMonth, c.count.toDouble)) *)

    val xAxis = new CategoryAxis():
      label = "Mese"

    val yAxis = new NumberAxis():
      label = "Documenti"

    new BarChart[String, Number](xAxis, yAxis):
      title = chartTitle
      legendVisible = false
      animated = false
      categoryGap = 20
      prefHeight = 260
      styleClass ++= Seq(StatsChartStyle, accentStyle)
      data = series

  private def roleBarChart(chartTitle: String, counts: Seq[RoleCount], accentStyle: String): BarChart[String, Number] =
    val series = new XYChart.Series[String, Number]():
      data = ObservableBuffer(counts.map(c => XYChart.Data[String, Number](c.roleName, c.count.toDouble)) *)

    val xAxis = new CategoryAxis():
      label = "Ruolo"

    val yAxis = new NumberAxis():
      label = "Accessi"

    new BarChart[String, Number](xAxis, yAxis):
      title = chartTitle
      legendVisible = false
      animated = false
      categoryGap = 20
      prefHeight = 260
      styleClass ++= Seq(StatsChartStyle, accentStyle)
      data = series
