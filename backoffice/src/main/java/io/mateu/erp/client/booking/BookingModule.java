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
    public List<MenuEntry> getMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractAction("Budgets") {
            @Override
            public void run() {
            }
        });

        m.add(new AbstractAction("Groups") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.Booking", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Bookings") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.Booking", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Services") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.Service", new MDDCallback());
            }
        });

        m.add(new AbstractAction("Requests to suppliers") {
            @Override
            public void run() {
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData("io.mateu.erp.model.booking.ProviderRequest", new MDDCallback());
            }
        });

        return m;
    }
}
