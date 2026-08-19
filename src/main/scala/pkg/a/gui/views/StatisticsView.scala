package pkg.a.gui.views

import pkg.a.gui.services.StatisticsService
import pkg.a.gui.services.StatisticsService.{MonthlyCount, RoleCount, UserCount}
import pkg.a.gui.traits.Management
import pkg.a.gui.text.UiText.Statistics as Text
import pkg.a.gui.text.UiText.Common.Buttons
import pkg.a.gui.text.UiStyles.Common.RootStyle
import pkg.a.gui.text.UiStyles.HomePage.{CardsContainerStyle, StatCardStyle, StatCardTitleStyle, StatCardValueStyle}
import pkg.d.util.XmlToPdf
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.control.{ScrollPane, TableView}
import scalafx.scene.layout.{BorderPane, ColumnConstraints, GridPane, HBox, Priority, VBox}

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
      val printed =
        XmlToPdf.printSections(
          pdfFileName = Text.PrintFileName,
          title = Text.PrintTitle,
          sections = Seq(
            (
              Text.RegistrationsTotal,
              Seq(Text.RegistrationsTotal, Text.RegistrationsApproved, Text.RegistrationsRejected),
              Seq(Seq(registrationsSummary.total.toString, registrationsSummary.approved.toString, registrationsSummary.rejected.toString))
            ),
            (
              Text.RegisteredByMonthTitle,
              Seq("Mese", "Conteggio"),
              registeredByMonth.map(c => Seq(c.yearMonth, c.count.toString))
            ),
            (
              Text.ArchivedByMonthTitle,
              Seq("Mese", "Conteggio"),
              archivedByMonth.map(c => Seq(c.yearMonth, c.count.toString))
            ),
            (
              Text.AccessesByRoleTitle,
              Seq("Ruolo", "Conteggio"),
              accessesByRole.map(c => Seq(c.roleName, c.count.toString))
            ),
            (
              Text.AccessesByUserTitle,
              Seq(Text.UserColumn, Text.AccessCountColumn),
              accessesByUser.map(c => Seq(c.username, c.count.toString))
            )
          )
        )

      result.show(
        if printed then Text.PrintSuccess else Text.PrintError,
        success = printed
      )

    val exitButton = closeButton(onExit)
    val printButton = secondaryButton(Buttons.Print, printStatistics)
    val actions = actionBar(Seq(exitButton, printButton))

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
