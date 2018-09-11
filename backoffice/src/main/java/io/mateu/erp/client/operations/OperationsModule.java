package io.mateu.erp.client.operations;

import io.mateu.erp.client.booking.TransfersSummaryView;
import io.mateu.erp.model.booking.ManagedEvent;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.freetext.FreeTextService;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.server.booking.BookingServiceImpl;
import io.mateu.mdd.core.app.*;

import java.util.ArrayList;
import java.util.List;

public class OperationsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Operations";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new AbstractMenu("Hotel") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Calendar", HotelServiceCalendar.class));

                m.add(new MDDOpenCRUDAction("Services", HotelService.class));

                m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

                m.add(new MDDOpenCRUDAction("Roomings", HotelService.class));

                return m;
            }
        });


        m.add(new AbstractMenu("Transfer") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Calendar", TransfersSummaryView.class));

                m.add(new MDDOpenCRUDAction("Services", TransferService.class));

                m.add(new MDDOpenCRUDAction("Buses", PurchaseOrder.class));

                m.add(new MDDOpenCRUDAction("Mapping", TransferPointMapping.class));

                m.add(new MDDCallMethodAction("Import pickup times", BookingServiceImpl.class, "importPickupTimeExcel"));

                return m;
            }
        });


        m.add(new AbstractMenu("Generic") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Calendar", GenericServiceCalendar.class));

                m.add(new MDDOpenCRUDAction("Services", GenericService.class));

                m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

                return m;
            }
        });

        m.add(new AbstractMenu("Free text") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Calendar", FreeTextServiceCalendar.class));

                m.add(new MDDOpenCRUDAction("Services", FreeTextService.class));

                m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

                return m;
            }
        });

        m.add(new AbstractMenu("Excursion") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Calendar", ExcursionCalendar.class));

                m.add(new MDDOpenCRUDAction("Events", ManagedEvent.class));

                m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

                return m;
            }
        });


        m.add(new AbstractMenu("Circuit") {
            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> m = new ArrayList<>();

                m.add(new MDDOpenListViewAction("Calendar", CircuitCalendar.class));

                m.add(new MDDOpenCRUDAction("Events", ManagedEvent.class));

                m.add(new MDDOpenCRUDAction("Purchase orders", PurchaseOrder.class));

                return m;
            }
        });


        return m;
    }
}
