package io.mateu.erp.client.booking;

import io.mateu.erp.client.booking.views.ArrivalView;
import io.mateu.erp.client.booking.views.FlightsView;
import io.mateu.erp.client.booking.views.GroupsView;
import io.mateu.erp.client.booking.views.PickupConfirmationView;
import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.File;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.mdd.core.app.*;

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

        m.add(new MDDOpenCRUDAction("Files", File.class));

        m.add(new MDDOpenCRUDAction("Bookings", Booking.class));

        m.add(new AbstractMenu("Queries") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Flights", FlightsView.class));

                m.add(new MDDOpenListViewAction("Groups", GroupsView.class));

                m.add(new MDDOpenListViewAction("Arrivals", ArrivalView.class));

                m.add(new MDDOpenListViewAction("Departures", ArrivalView.class));

                m.add(new MDDOpenListViewAction("Excursions", PickupConfirmationView.class));

                m.add(new MDDOpenListViewAction("Circuits", PickupConfirmationView.class));

                m.add(new MDDOpenListViewAction("Pax in destination", PickupConfirmationView.class));

                return m;
            }
        });

        m.add(new MDDOpenListViewAction("Pickup confirmation", PickupConfirmationView.class));


        return m;
    }
}
