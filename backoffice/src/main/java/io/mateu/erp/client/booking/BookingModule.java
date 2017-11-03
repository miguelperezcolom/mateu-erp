package io.mateu.erp.client.booking;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class BookingModule extends AbstractModule {
    @Override
    public String getName() {
        return "Booking";
    }

    @Override
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Bookings") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.Booking", new MDDCallback());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("All services") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.Service", new MDDCallback());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Purchase orders") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.PurchaseOrder", new MDDCallback());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Generics only") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.generic.GenericService", new MDDCallback());
            }
        });

        m.add(new AbstractAction((MateuUI.getApp().getName().toLowerCase().contains("quoon"))?"Bookings":"Hotels only") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.hotel.HotelService", new MDDCallback());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Transfers only") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.transfer.TransferService", new MDDCallback());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Transfers summary") {
            @Override
            public void run() {
                MateuUI.openView(new TransfersSummaryView());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Mapping") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.transfer.TransferPointMapping", new MDDCallback());
            }
        });

        if (!MateuUI.getApp().getName().toLowerCase().contains("quoon")) m.add(new AbstractAction("Import pickup times") {
            @Override
            public void run() {
                MateuUI.openView(new PickupTimeImportingView());
            }
        });

//        m.add(new AbstractAction("Transfer operation") {
//            @Override
//            public void run() {
//                ((ERPServiceAsync) MateuUI.create(ERPService.class)).execute("io.mateu.erp.model.booking.transfer.TransferPointMapping", null, null, new ServerSideWizardCallback());
//            }
//        });

        return m;
    }
}
