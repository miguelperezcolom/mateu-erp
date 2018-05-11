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

        m.add(new MDDAction("All services", Service.class));

        m.add(new MDDMenu("Free text", "Summary", FreeTextService.class, "Services", FreeTextService.class));

        m.add(new MDDMenu("Generic", "Summary", FreeTextService.class, "Services", GenericService.class));

        m.add(new MDDMenu("Hotel", "Summary", FreeTextService.class, "Services", HotelService.class, "Rooming", HotelService.class));

        m.add(new AbstractMenu("Transfers") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Transfers summary") {
                    @Override
                    public void run() {
                        MateuUI.openView(new TransfersSummaryView(), isModifierPressed());
                    }
                });

                m.add(new MDDAction("Services", TransferService.class));

                m.add(new MDDAction("Buses", PurchaseOrder.class));

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
        });

         m.add(new MDDAction("Purchase orders", PurchaseOrder.class));


        m.add(new AbstractMenu("Importing") {
            @Override
            public List<MenuEntry> getEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new AbstractAction("Importing Queue") {
                    @Override
                    public void run() {
                        ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(null, "io.mateu.common.model.importing.TransferImportTask", "io.mateu.common.model.importing.TransferImportTask", null, new MDDCallback(new Data("modified_from", LocalDate.now(), "modified_to", LocalDate.now()), isModifierPressed()));
                    }
                });

                m.add(new MDDAction("Auto imports", TransferAutoImport.class));

                m.add(new MDDAction("Transfer requests", TransferBookingRequest.class));

                return m;

            }
        });

        return m;
    }
}
