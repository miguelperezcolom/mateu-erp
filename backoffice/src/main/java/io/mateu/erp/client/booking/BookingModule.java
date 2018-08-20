package io.mateu.erp.client.booking;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.BookingPart;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.mdd.core.app.AbstractModule;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MDDOpenListViewAction;
import io.mateu.mdd.core.app.MenuEntry;

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

        m.add(new MDDOpenCRUDAction("Quotation requests", QuotationRequest.class));

        m.add(new MDDOpenCRUDAction("Bookings", Booking.class));

        m.add(new MDDOpenCRUDAction("Parts", BookingPart.class));

        m.add(new MDDOpenListViewAction("Pickup confirmation", PickupConfirmationView.class));

        return m;
    }
}
