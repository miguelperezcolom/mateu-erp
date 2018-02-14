package io.mateu.erp.client.booking;

import io.mateu.erp.shared.booking.BookingService;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.DateField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.ColumnAlignment;
import io.mateu.ui.core.client.components.fields.grids.columns.DataColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.OutputColumn;
import io.mateu.ui.core.client.views.AbstractListView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.CellStyleGenerator;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 15/4/17.
 */
public class TransfersSummaryView extends AbstractListView {

    @Override
    public Data initializeData() {
        Data d = super.initializeData();
        d.set("start", LocalDate.now());
        d.set("finish", LocalDate.now().plusMonths(6));
        return d;
    }


    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> l = super.createActions();

        l.add(new AbstractAction("Inform pickups") {
            @Override
            public void run() {
                ((BookingServiceAsync)MateuUI.create(BookingService.class)).informPickupTime(MateuUI.getApp().getUserData(), getSelection(), new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        super.onSuccess(result);
                        search();
                    }
                });
            }
        });

        return l;
    }


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
        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(null, "io.mateu.erp.model.booking.transfer.TransferService", "io.mateu.erp.model.booking.transfer.TransferService", null, new MDDCallback(d));
    }

    @Override
    public void rpc(Data data, AsyncCallback<Data> callback) {
        ((BookingServiceAsync)MateuUI.create(BookingService.class)).getTransferSummary(getForm().getData(), callback);
    }

    @Override
    public String getTitle() {
        return "Transfers summary";
    }

    @Override
    public void build() {
        add(new DateField("start", "From")).add(new DateField("finish", "To"));
    }
}
