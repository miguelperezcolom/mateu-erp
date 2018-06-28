package io.mateu.erp.client.operations;

import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

import java.util.ArrayList;
import java.util.List;

public class HotelOperationsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Hotel";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Services", HotelService.class));

        m.add(new MDDAction("Purchase orders", PurchaseOrder.class));

        m.add(new MDDAction("Roomings", HotelService.class));

        return m;
    }
}
