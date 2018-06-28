package io.mateu.erp.client.booking;

import io.mateu.erp.model.booking.*;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.importing.TransferAutoImport;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.client.MDDMenu;
import io.mateu.ui.mdd.shared.ERPService;

import java.time.LocalDate;
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

        m.add(new MDDAction("Quotation requests", QuotationRequest.class));

        m.add(new MDDAction("Bookings", Booking.class));

        m.add(new MDDAction("Parts", BookingPart.class));

        m.add(new AbstractAction("Pickup confirmation") {
            @Override
            public void run() {
                MateuUI.openView(new PickupConfirmationView(), isModifierPressed());
            }
        });

        return m;
    }
}
