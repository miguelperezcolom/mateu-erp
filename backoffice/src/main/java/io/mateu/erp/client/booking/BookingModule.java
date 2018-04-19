package io.mateu.erp.client.booking;

import io.mateu.erp.model.booking.*;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.mdd.client.MDDAction;

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
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDAction("Price requests", PriceRequest.class));

        m.add(new MDDAction("Bookings", Booking.class));

        m.add(new MDDAction("Quotation requests", QuotationRequest.class));

        m.add(new MDDAction("All services", Service.class));

        m.add(new MDDAction("Purchase orders", PurchaseOrder.class));

        m.add(new MDDAction("Free text only", FreeTextService.class));

        m.add(new MDDAction("Generics only", GenericService.class));

        m.add(new MDDAction("Hotels only", HotelService.class));

        m.add(new MDDAction("Transfers only", TransferService.class));

        m.add(new AbstractAction("Transfers summary") {
            @Override
            public void run() {
                MateuUI.openView(new TransfersSummaryView(), isModifierPressed());
            }
        });

        m.add(new MDDAction("Mapping", TransferPointMapping.class));

        m.add(new AbstractAction("Import pickup times") {
            @Override
            public void run() {
                MateuUI.openView(new PickupTimeImportingView(), isModifierPressed());
            }
        });

        m.add(new AbstractAction("Pickup confirmation") {
            @Override
            public void run() {
                MateuUI.openView(new PickupConfirmationView(), isModifierPressed());
            }
        });

        return m;
    }
}
