package io.mateu.erp.client.operations;

import io.mateu.erp.client.booking.TransfersSummaryView;
import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.transfer.TransferPointMapping;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.server.booking.BookingServiceImpl;
import io.mateu.mdd.core.app.*;

import java.util.ArrayList;
import java.util.List;

public class TransferOperationsModule extends AbstractModule {
    @Override
    public String getName() {
        return "Transfer";
    }

    @Override
    public List<MenuEntry> buildMenu() {
        List<MenuEntry> m = new ArrayList<>();

        m.add(new MDDOpenListViewAction("Transfers summary", TransfersSummaryView.class));

        m.add(new MDDOpenCRUDAction("Services", TransferService.class));

        m.add(new MDDOpenCRUDAction("Buses", PurchaseOrder.class));

        m.add(new MDDOpenCRUDAction("Mapping", TransferPointMapping.class));

        m.add(new MDDCallMethodAction("Import pickup times", BookingServiceImpl.class, "importPickupTimeExcel"));

        return m;
    }
}
