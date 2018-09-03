package io.mateu.erp.client.booking;


import io.mateu.erp.server.booking.BookingServiceImpl;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.app.MDDCallback;
import io.mateu.mdd.core.interfaces.RpcCrudView;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 15/4/17.
 */
@Getter@Setter
public class TransfersSummaryView implements RpcCrudView<TransfersSummaryView, TransfersSummaryDay, Void> {

    private LocalDate start = LocalDate.now();

    private LocalDate finish = LocalDate.now().plusMonths(6);


    @Action
    public void informPickups(List<TransfersSummaryDay> selection) throws Throwable {
        new BookingServiceImpl().informPickupTime(MDD.getUserData(), selection);
    }

/*
    @Override
    public List<AbstractColumn> createColumns() {
        List<AbstractColumn> l = new ArrayList<>();
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
        return l;
    }
    */

/*
    private void linkedOn(String id, Data data) {
        Data d = new Data();
        d.set("start_from", data.get("_id"));
        d.set("start_to", data.get("_id"));
        int colNo = Integer.parseInt(id.replaceAll("col", ""));
        switch (colNo) {
            case 3:
                d.set("transferType", new Pair("SHUTTLE", "SHUTTLE"));
                d.set("direction", new Pair("INBOUND", "INBOUND"));
                break;
            case 4:
                d.set("transferType", new Pair("SHUTTLE", "SHUTTLE"));
                d.set("direction", new Pair("OUTBOUND", "OUTBOUND"));
                break;
            case 5:
                d.set("transferType", new Pair("PRIVATE", "PRIVATE"));
                d.set("direction", new Pair("INBOUND", "INBOUND"));
                break;
            case 6:
                d.set("transferType", new Pair("PRIVATE", "PRIVATE"));
                d.set("direction", new Pair("OUTBOUND", "OUTBOUND"));
                break;
            case 7:
                d.set("transferType", new Pair("EXECUTIVE", "EXECUTIVE"));
                d.set("direction", new Pair("INBOUND", "INBOUND"));
                break;
            case 8:
                d.set("transferType", new Pair("EXECUTIVE", "EXECUTIVE"));
                d.set("direction", new Pair("OUTBOUND", "OUTBOUND"));
                break;
            case 9:
                d.set("transferType", new Pair("INCOMING", "INCOMING"));
                d.set("direction", new Pair("INBOUND", "INBOUND"));
                break;
            case 10:
                d.set("transferType", new Pair("INCOMING", "INCOMING"));
                d.set("direction", new Pair("OUTBOUND", "OUTBOUND"));
                break;
            case 11:
                d.set("direction", new Pair("POINTTOPOINT", "POINTTOPOINT"));
                break;
        }
        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(null, "io.mateu.erp.model.file.transfer.TransferService", "io.mateu.erp.model.file.transfer.TransferService", null, new MDDCallback(d));
    }
    */


    @Override
    public Object deserializeId(String s) {
        return null;
    }

    @Override
    public boolean isAddEnabled() {
        return false;
    }

    @Override
    public List<TransfersSummaryDay> rpc(TransfersSummaryView filters, int offset, int limit) throws Throwable {
        return new BookingServiceImpl().getTransferSummary(filters, offset, limit);
    }

    @Override
    public int gatherCount(TransfersSummaryView filters) throws Throwable {
        return new BookingServiceImpl().getTransferSummaryCount(filters);
    }
}
