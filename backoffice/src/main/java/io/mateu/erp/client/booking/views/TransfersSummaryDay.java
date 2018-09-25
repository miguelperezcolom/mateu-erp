package io.mateu.erp.client.booking.views;

import io.mateu.mdd.core.annotations.CellStyleGenerator;
import io.mateu.mdd.core.annotations.Ignored;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter@Setter
public class TransfersSummaryDay {

    @Ignored
    private LocalDate date;

    @CellStyleGenerator(WeekDayCellStyleGenerator.class)
    private String dateText;

    private String airport;

    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus shuttleIn;
    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus shuttleOut;


    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus privateIn;
    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus privateOut;


    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus executIn;
    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus executOut;


    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus incomIn;
    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus incomOut;


    @CellStyleGenerator(DayServiceStatusCellStyleGenerator.class)
    private DayServiceStatus p2pOut;

    private boolean PUInfd;


    /*
        l.add(new OutputColumn("col1", "Date", 120).setStyleGenerator(new CellStyleGenerator() {
        @Override
        public String getStyle(Object o) {
            String s = "" + o;
            String css = null;
            if (s.endsWith("SAT") || s.endsWith("SUN")) css = "warning";
            return css;
        }

        @Override
        public boolean isContentShown() {
            return true;
        }
    }));
        l.add(new OutputColumn("col2", "Airport", 120));
        l.add(new DataColumn("col3", "SHUTTLE In", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col4", "SHUTTLE Out", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col5", "PRIVATE In", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col6", "PRIVATE Out", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col7", "EXECUT In", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col8", "EXECUT Out", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col9", "INCOM In", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col10", "INCOM Out", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new DataColumn("col11", "P2P", 90) {
        @Override
        public void run(Data data) {
            linkedOn(getId(), data);
        }
    }.setAlignment(ColumnAlignment.RIGHT));
        l.add(new OutputColumn("col12", "PU Infd.", 120));

*/
}
