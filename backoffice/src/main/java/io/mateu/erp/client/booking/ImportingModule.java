package io.mateu.erp.client.booking;

import io.mateu.erp.model.booking.Booking;
import io.mateu.erp.model.importing.TransferAutoImport;
import io.mateu.erp.model.importing.TransferBookingRequest;
import io.mateu.erp.model.importing.TransferImportTask;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.client.ERPServiceAsync;
import io.mateu.ui.mdd.client.MDDAction;
import io.mateu.ui.mdd.client.MDDCallback;
import io.mateu.ui.mdd.shared.ERPService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonia on 02/04/2017.
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
                ((ERPServiceAsync) MateuUI.create(ERPService.class)).getMetaData(null, "io.mateu.erp.model.importing.TransferImportTask", "io.mateu.erp.model.importing.TransferImportTask", null, new MDDCallback(new Data("modified_from", LocalDate.now(), "modified_to", LocalDate.now())));
            }
        });

        m.add(new MDDAction("Auto imports", TransferAutoImport.class));

        m.add(new MDDAction("Transfer requests", TransferBookingRequest.class));

        return m;
    }
}
