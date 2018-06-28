package io.mateu.erp.client.booking;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.booking.BookingPart;
import io.mateu.erp.model.booking.QuotationRequest;
import io.mateu.erp.model.importing.TransferAutoImport;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ImportingModule extends AbstractModule {
    @Override
    public String getName() {
        return "Importing";
    }

    @Override
    public List<MenuEntry> buildMenu() {
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
}
