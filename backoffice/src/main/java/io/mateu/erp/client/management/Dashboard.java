package io.mateu.erp.client.management;

import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.annotations.FullWidth;
import io.mateu.mdd.core.annotations.KPIInline;
import io.mateu.mdd.core.annotations.SameLine;
import io.mateu.mdd.core.annotations.Width;
import io.mateu.mdd.vaadinport.vaadin.components.charts.BarChart;
import io.mateu.mdd.vaadinport.vaadin.components.charts.LineChart;
import io.mateu.mdd.vaadinport.vaadin.components.charts.PieChart;
import io.mateu.mdd.vaadinport.vaadin.components.charts.RandomDataProvider;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Dashboard {

    @KPIInline(style = CSS.SUPERKPI)
    private long bookingsToday = 300;

    @KPIInline(style = CSS.SUPERKPI)
    @SameLine
    private double markupToday = 2500.45;

    @KPIInline(style = CSS.SUPERKPI)
    @SameLine
    private double totalRisk = 310468.68;


    @KPIInline(style = CSS.SUPERKPI)
    @SameLine
    private double totalDebt = 415847.47;


    private BarChart bookingsLastYear = new BarChart(new RandomDataProvider(2, 12));

    @SameLine
    private PieChart bookingsPerOffice = new PieChart(new RandomDataProvider(2, 4));


    private PieChart bookingsPerAgency = new PieChart(new RandomDataProvider(2, 12));

    @SameLine
    private LineChart warrantiesCoverage = new LineChart(new RandomDataProvider(7, 12));

    @FullWidth
    private LineChart salesLastYears = new LineChart(new RandomDataProvider(3, 12));

}
